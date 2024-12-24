package com

import com.custom.PackSpritesCustom
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.tools.tasks.impl.PackMaps
import dev.openrune.cache.tools.tasks.impl.PackModels
import dev.openrune.cache.tools.tasks.impl.defs.PackConfig
import dev.openrune.cache.tools.tasks.impl.defs.PackMode
import io.xeros.AssetLoader

const val REV : Int = 227

val tasks : Array<CacheTask> = arrayOf(
    PackSpritesCustom(AssetLoader.getFolder("raw-cache/sprites/")),
    PackMaps(AssetLoader.getFolder("raw-cache/maps/")),
    PackModels(AssetLoader.getFolder("raw-cache/models/")),
//    PackConfig(PackMode.ITEMS, AssetLoader.getFolder("raw-cache/items/"))
)