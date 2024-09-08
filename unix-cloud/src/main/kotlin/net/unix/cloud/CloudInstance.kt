package net.unix.cloud

import net.unix.api.CloudAPI
import net.unix.api.CloudBuilder
import net.unix.api.LocationSpace
import net.unix.api.command.CommandDispatcher
import net.unix.cloud.command.aether.argument.CloudServiceArgument
import net.unix.cloud.command.aether.command
import net.unix.cloud.event.cloud.CloudStartEvent
import net.unix.api.group.CloudGroupManager
import net.unix.api.modification.extension.ExtensionManager
import net.unix.api.modification.module.ModuleManager
import net.unix.api.network.server.Server
import net.unix.api.scheduler.SchedulerManager
import net.unix.api.service.CloudService
import net.unix.api.service.CloudServiceManager
import net.unix.api.template.CloudTemplateManager
import net.unix.api.terminal.Terminal
import net.unix.api.terminal.logger.Logger
import net.unix.api.terminal.logger.LoggerFactory
import net.unix.cloud.CloudInstanceBuilder.Companion.builder
import net.unix.cloud.command.CloudCommandDispatcher
import net.unix.cloud.command.aether.AetherArgumentBuilder.Companion.argument
import net.unix.cloud.command.aether.AetherCommandBuilder
import net.unix.cloud.command.aether.AetherLiteralBuilder.Companion.literal
import net.unix.cloud.command.aether.get
import net.unix.cloud.group.BasicCloudGroupManager
import net.unix.cloud.modification.extension.CloudExtensionManager
import net.unix.cloud.modification.module.CloudModuleManager
import net.unix.cloud.scheduler.CloudSchedulerManager
import net.unix.cloud.service.BasicCloudServiceManager
import net.unix.cloud.template.BasicCloudTemplateManager
import net.unix.cloud.terminal.CloudJLineTerminal
import net.unix.cloud.terminal.logger.CloudLoggerFactory
import kotlin.system.exitProcess

fun main() {
    var builder: CloudBuilder = CloudInstance.builder()

    CloudStartEvent(builder).callEvent().also { builder = it.builder }

    builder.build()

//    AetherCommandBuilder("screen") // <- название команды
//        .then( // <- указываем какой-то аргумент
//            literal("toggle") // <- обязательный аргумент "toggle".   Полная запись: AetherLiteralBuilder.literal("toggle")
//                .then( // <- ещё один аргумент, который следует за "toggle"
//                    argument("service", CloudServiceArgument()) // <- CloudServiceArgument фильтрует ввод пользователя и 100% возвращает существующий объект CloudService
//                        .execute { // <- Вызывается при выполнении команды с аргументом toggle и service
//                            val service: CloudService = it["service"] // <- получаем аргумент CloudService по его имени из контекста команды
//
//                            println("Screen toggled to ${service.name}")
//                        }
//                )
//        )
//        .then( // <- указываем ещё какой-то аргумент
//            literal("switch") // <- Обязательный аргумент "switch"
//                .then(
//                    argument("service", CloudServiceArgument())
//                        .execute {
//                            val service: CloudService = it["service"]
//
//                            println("Screen switched to ${service.name}")
//                        }
//                )
//        )
//        .register() // <- регистрируем команду

    command("screen") {
        literal("toggle") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]

                    println("Screen toggled to ${service.name}")
                }
            }
        }
        literal("switch") {
            argument("service", CloudServiceArgument()) {
                execute {
                    val service: CloudService = it["service"]

                    println("Screen switched to ${service.name}")
                }
            }
        }
    }.register()
}

@Suppress("unused")
class CloudInstanceBuilder : CloudBuilder {

    private var locationSpace: LocationSpace? = null

    private var loggerFactory: LoggerFactory? = null
    private var logger: Logger? = null

    private var commandDispatcher: CommandDispatcher? = null
    private var terminal: Terminal? = null

    private var cloudTemplateManager: CloudTemplateManager? = null
    private var cloudGroupManager: CloudGroupManager? = null
    private var cloudServiceManager: CloudServiceManager? = null

    private var moduleManager: ModuleManager? = null
    private var extensionManager: ExtensionManager? = null

    private var schedulerManager: SchedulerManager? = null

    private var server: Server? = null

    companion object {
        fun CloudInstance.Companion.builder(): CloudInstanceBuilder {
            return CloudInstanceBuilder()
        }
    }

    override fun loggerFactory(factory: LoggerFactory): CloudInstanceBuilder {
        this.loggerFactory = factory

        return this
    }

    override fun globalLogger(logger: Logger): CloudInstanceBuilder {
        this.logger = logger

        return this
    }

    override fun terminal(terminal: Terminal): CloudInstanceBuilder {
        this.terminal = terminal

        return this
    }

    override fun commandDispatcher(dispatcher: CommandDispatcher): CloudInstanceBuilder {
        this.commandDispatcher = dispatcher

        return this
    }

    override fun commandTemplateManager(manager: CloudTemplateManager): CloudInstanceBuilder {
        this.cloudTemplateManager = manager

        return this
    }

    override fun cloudGroupManager(manager: CloudGroupManager): CloudInstanceBuilder {
        this.cloudGroupManager = manager

        return this
    }

    override fun cloudServiceManager(manager: CloudServiceManager): CloudInstanceBuilder {
        this.cloudServiceManager = manager

        return this
    }

    override fun moduleManager(manager: ModuleManager): CloudInstanceBuilder {
        this.moduleManager = manager

        return this
    }

    override fun extensionManager(manager: ExtensionManager): CloudInstanceBuilder {
        this.extensionManager = manager

        return this
    }

    override fun schedulerManager(manager: SchedulerManager): CloudInstanceBuilder {
        this.schedulerManager = manager

        return this
    }

    override fun server(server: Server): CloudInstanceBuilder {
        this.server = server

        return this
    }

    override fun locationSpace(space: LocationSpace): CloudInstanceBuilder {
        this.locationSpace = space
        
        return this
    }

    override fun build(): CloudInstance {
        return CloudInstance(
            schedulerManager,
            locationSpace,
            loggerFactory,
            logger,
            commandDispatcher,
            terminal,
            cloudTemplateManager,
            cloudGroupManager,
            cloudServiceManager,
            moduleManager,
            extensionManager,
            server
        )
    }
}

class CloudInstance(
    schedulerManager: SchedulerManager? = null,

    locationSpace: LocationSpace? = null,

    loggerFactory: LoggerFactory? = null,
    logger: Logger? = null,

    commandDispatcher: CommandDispatcher? = null,
    terminal: Terminal? = null,

    cloudTemplateManager: CloudTemplateManager? = null,
    cloudGroupManager: CloudGroupManager? = null,
    cloudServiceManager: CloudServiceManager? = null,

    moduleManager: ModuleManager? = null,
    extensionManager: ExtensionManager? = null,

    server: Server? = null
) : CloudAPI() {

    companion object {
        lateinit var instance: CloudAPI
    }

    init {
        instance = this
    }

    override val loggerFactory: LoggerFactory = loggerFactory ?: CloudLoggerFactory()
    override val logger: Logger = logger ?: this.loggerFactory.logger

    override val schedulerManager: SchedulerManager = schedulerManager ?: CloudSchedulerManager(this.logger)

    override val locationSpace: LocationSpace = locationSpace ?: CloudLocationSpace()

    override val commandDispatcher: CommandDispatcher = commandDispatcher ?: CloudCommandDispatcher
    override val terminal: Terminal = terminal ?: CloudJLineTerminal(" <white>Unix<gray>@<aqua>cloud<gray>:~<dark_gray># ", this.logger, this.commandDispatcher)

    override val cloudTemplateManager: CloudTemplateManager = cloudTemplateManager ?: BasicCloudTemplateManager
    override val cloudGroupManager: CloudGroupManager = cloudGroupManager ?: BasicCloudGroupManager
    override val cloudServiceManager: CloudServiceManager = cloudServiceManager ?: BasicCloudServiceManager

    override val moduleManager: ModuleManager = moduleManager ?: CloudModuleManager
    override val extensionManager: ExtensionManager = extensionManager ?: CloudExtensionManager

    override val server: Server = server ?: Server()

    init {
        Runtime.getRuntime().addShutdownHook(Thread { CloudInstanceShutdownHandler(this).run() })
        this.server.start(9191)
    }

    override fun shutdown() {
        exitProcess(0)
    }
}