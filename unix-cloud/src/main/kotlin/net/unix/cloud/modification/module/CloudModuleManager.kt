package net.unix.cloud.modification.module

import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import net.unix.api.modification.module.ModuleManager
import net.unix.cloud.CloudInstance
import java.io.File

object CloudModuleManager : ModuleManager {

    private val cachedModules = mutableMapOf<String, Module>()

    override var folder: File = CloudInstance.instance.locationSpace.module

    override val modules: List<Module>
        get() = cachedModules.values.toList()

    override fun get(name: String): Module? = cachedModules[name]

    override fun loadAll(silent: Boolean): List<Module> {
        val loaders = folder.listFiles()
            ?.filter { it.name.endsWith(".jar") }
            ?.map { CloudModuleLoader(it) }
            ?.filter { it.info != null }
            ?: run {
                if (!silent) throw IllegalArgumentException("File \"${folder.path}\" is not a folder!")
                else listOf()
            }

        return listOf()
    }

    override fun load(file: File): Module {
        val loader = CloudModuleLoader(file)

        val result = loader.load() ?: throw ModificationLoadException("Could not load ${file.name}. Corrupted file?")

        result.onLoad()

        return result
    }

    override fun unload(module: Module): Boolean = module.loader.unload()
    override fun reload(module: Module): Boolean = module.loader.reload()
}
