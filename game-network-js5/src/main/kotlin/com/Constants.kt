package com

import com.custom.PackSpritesCustom
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.tools.tasks.impl.PackMaps
import dev.openrune.cache.tools.tasks.impl.PackModels
import io.xeros.AssetLoader

const val REV : Int = 226

val tasks : Array<CacheTask> = arrayOf(
    PackSpritesCustom(AssetLoader.getFolder("raw-cache/sprites/")),
    PackMaps(AssetLoader.getFolder("raw-cache/maps/")),
    PackModels(AssetLoader.getFolder("raw-cache/models/")),
)