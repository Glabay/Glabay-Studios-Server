package com

import dev.openrune.cache.tools.Builder
import dev.openrune.cache.tools.tasks.TaskType
import dev.openrune.cache.tools.tasks.impl.RemoveXteas
import io.xeros.AssetLoader

object UpdateCache {
    fun init() {
        val builder = Builder(type = TaskType.FRESH_INSTALL, revision = REV, AssetLoader.getFolder("cache"))
        builder.extraTasks(*tasks,RemoveXteas(AssetLoader.getFile("cache","xteas.json"))).build().initialize()
    }
}

fun main() {
    AssetLoader.initCache()
    UpdateCache.init()
}
