package com

import dev.openrune.cache.tools.Builder
import dev.openrune.cache.tools.tasks.TaskType
import java.io.File

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-27
 */
fun main() {
    init()
}

private fun init() {
    val js5Server = Builder(
        type = TaskType.RUN_JS5,
        revision = REV,
        cacheLocation = File("./Data/cache"),
        js5Ports = listOf(443, 43594, 50000)
    )
    js5Server.build().initialize()
}