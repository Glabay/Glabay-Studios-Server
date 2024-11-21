package com

import dev.openrune.cache.tools.Builder
import dev.openrune.cache.tools.tasks.TaskType
import io.xeros.AssetLoader
import org.jire.js5server.Js5Server

object RunJs5 {

    fun init() {
        val js5Server = Builder(
            type = TaskType.RUN_JS5,
            revision = REV,
            cacheLocation = AssetLoader.getFolder("cache"),
            js5Ports = listOf(443, 43594, 50000)
        )
        js5Server.build().initialize()
    }
}
