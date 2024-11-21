package com.custom

import com.displee.cache.CacheLibrary
import dev.openrune.cache.tools.tasks.CacheTask
import dev.openrune.cache.util.getFiles
import io.xeros.util.OutputStream
import io.xeros.util.ProgressbarUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

data class SpriteCustom(
    var width: Int = -1,
    var height: Int = -1,
    var name: String = "null",
    var offsetX: Int = 0,
    var offsetY: Int = 0,
    var pixels: ByteArray? = null
) {

    fun load(image : BufferedImage) : SpriteCustom {
        width = image.width
        height = image.height

        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)

        pixels = baos.toByteArray();
        return this
    }

    fun encode() : ByteArray {
        val dos = OutputStream()

        if (width != -1) {
            dos.writeByte(1)
            dos.writeShort(width)
        }

        if (height != -1) {
            dos.writeByte(2)
            dos.writeShort(height)
        }

        if (name != "null") {
            dos.writeByte(3)
            dos.writeString(name)
        }

        if (offsetX != -1) {
            dos.writeByte(4)
            dos.writeShort(offsetX)
        }

        if (offsetY != -1) {
            dos.writeByte(5)
            dos.writeShort(offsetY)
        }

        if (pixels != null) {
            dos.writeByte(6)
            dos.writeInt(pixels!!.size)
            repeat(pixels!!.size) {
                dos.writeByte(pixels!![it].toInt())
            }
        }

        dos.writeByte(0)

        return dos.flip()
    }
}

class PackSpritesCustom(private val spritesDir : File) : CacheTask() {
    override fun init(library: CacheLibrary) {
        println(spritesDir)
        val spriteSize = getFiles(spritesDir,"png","PNG").size
        val progressSprites = ProgressbarUtils.progress("Packing Sprites", spriteSize)

        if (spriteSize != 0) {
            getFiles(spritesDir,"png","PNG").forEach {
                try {
                    val id = it.nameWithoutExtension.toInt()

                    val image = ImageIO.read(it.inputStream())

                    library.put(2, 40, id, SpriteCustom().load(image).encode())
                    progressSprites.step()
                }
                catch (e : Exception) {
                    e.printStackTrace()
                    println("Enable to pack ${it.name} is the name a int?")
                }
            }
            progressSprites.close()
        }
    }
}