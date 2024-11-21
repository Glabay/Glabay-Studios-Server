package com.custom

import com.displee.cache.CacheLibrary
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.util.getFiles
import dev.openrune.cache.util.progress
import io.xeros.util.ProgressbarUtils
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

val getIdxForFile = mapOf(
    "media.dat" to 0,
    "interface.dat" to 1,
    "title.dat" to 2,
)

class PackDats(private val datsDir : File) : CacheTask() {
    override fun init(library: CacheLibrary) {
        val datsSize = getFiles(datsDir, "dat", "idx").size
        val progressDats = ProgressbarUtils.progress("Packing Dats", datsSize)

        if (datsSize != 0) {
            getFiles(datsDir, "dat", "idx").forEach {
                try {
                    if (getIdxForFile.contains(it.name)) {
                        library.put(2, 41, getIdxForFile[it.name]!!, it.readBytes())
                        progressDats.extraMessage = it.name
                        progressDats.step()
                    } else
                        println("Unable to pack ${it.name} its missing an index")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Unable to pack ${it.name} is the name a int?")
                }
            }
            progressDats.close()
        }
    }
}
