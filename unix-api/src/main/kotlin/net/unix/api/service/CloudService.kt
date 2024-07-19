package net.unix.api.service

import net.unix.api.Serializable
import net.unix.api.group.CloudGroup
import net.unix.api.persistence.PersistentDataHolder
import java.io.File

/**
 * [CloudService]'s allow to start instances of [CloudGroup]
 */
interface CloudService : PersistentDataHolder, Serializable {

    /**
     * Service name
     */
    val name: String

    /**
     * Service group
     */
    val group: CloudGroup

    /**
     * Service folder
     */
    val dataFolder: File

    /**
     * Service executable file location
     */
    val core: File

    /**
     * Send command to service
     *
     * @param command Command line
     *
     * @return Command execution result
     */
    fun sendCommand(command: String): String

    companion object
}