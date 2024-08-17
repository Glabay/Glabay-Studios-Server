package com.varlamore

import com.varlamore.custom.PackSpritesCustom
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.tools.tasks.impl.PackMaps
import dev.openrune.cache.tools.tasks.impl.PackModels
import io.xeros.AssetLoader

const val REV : Int = 224

val tasks : Array<CacheTask> = arrayOf(
    PackSpritesCustom(AssetLoader.getFolder("raw-cache/sprites/")),
    PackMaps(AssetLoader.getFolder("raw-cache/maps/")),
    PackModels(AssetLoader.getFolder("raw-cache/models/")),
)