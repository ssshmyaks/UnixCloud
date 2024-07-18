package net.unix.api.command.aether.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.unix.api.CloudAPI
import net.unix.api.command.aether.AetherArgument
import net.unix.api.command.aether.SyntaxExceptionBuilder
import net.unix.api.service.CloudService
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

/**
 * Command argument for [CloudService]
 */
class CloudServiceArgument : AetherArgument<CloudService>() {

    private var notFoundMessage = "CloudService not found"

    companion object {
        /**
         * Get [CloudService] from command context by argument name
         *
         * @param name Argument name
         *
         * @return Instance of [CloudService]
         *
         * @throws IllegalArgumentException If argument not found or is not [CloudService]
         */
        @Throws(IllegalArgumentException::class)
        fun CommandContext<*>.getCloudService(name: String): CloudService {
            return this.getArgument(name, CloudService::class.java)
        }
    }

    override fun parse(reader: StringReader): CloudService {
        val service = CloudAPI.instance.cloudServiceManager[reader.readString()]
            ?: throw SyntaxExceptionBuilder.exception(notFoundMessage, reader)

        return service
    }

    /**
     * Set your own not found message
     *
     * @param message Message text
     *
     * @return Current instance of [CloudServiceArgument]
     */
    fun notFound(message: String): CloudServiceArgument {
        this.notFoundMessage = message

        return this
    }

    override fun getExamples(): List<String> {
        return CloudAPI.instance.cloudServiceManager.services.map { it.name }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        CloudAPI.instance.cloudServiceManager.services.forEach {
            builder.suggest(it.name)
        }

        return builder.buildFuture()
    }
}