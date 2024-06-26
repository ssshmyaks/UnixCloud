package net.unix.api.template

import net.unix.api.builder.Builder
import net.unix.api.group.CloudFile
import net.unix.api.group.CloudGroupBuilder
import java.io.File

interface CloudTemplateBuilder : Builder<CloudTemplate> {

    fun folder(folder: File): CloudGroupBuilder
    fun file(vararg file: CloudFile)

    override fun build(): CloudTemplate {
        TODO("Not yet implemented")
    }
}