package net.unix.api.modification.extension

import net.unix.api.modification.ModificationManager
import net.unix.api.modification.exception.ModificationExistException
import net.unix.api.modification.exception.ModificationLoadException
import net.unix.api.modification.module.Module
import java.io.File

/**
 * Manager for [Extension]'s.
 */
interface ExtensionManager : ModificationManager {

    /**
     * List of all [Extension]'s.
     */
    val extensions: List<Extension>

    /**
     * Get [Extension] by name.
     *
     * @param name Extension name.
     *
     * @return Instance of [Extension] or null, if not founded.
     */
    override operator fun get(name: String): Extension?

    /**
     * Load all extensions from [folder].
     *
     * @param silent If true - all errors will be ignored, else it can throw exceptions.
     *
     * @return List of all loaded extensions.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If extension with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun loadAll(silent: Boolean): List<Extension>

    /**
     * Load [Extension] from file.
     *
     * @param file Extension file.
     *
     * @return Loaded extension.
     *
     * @throws ModificationLoadException Generic exception, may be corrupted file?
     * @throws ModificationExistException If extension with this name already loaded.
     */
    @Throws(ModificationLoadException::class, ModificationExistException::class)
    override fun load(file: File): Extension

    companion object

}