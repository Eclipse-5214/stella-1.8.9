package co.stellarskys.stella.features.stellanav.utils.render

import co.stellarskys.stella.Stella
import co.stellarskys.stella.features.stellanav.utils.*
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.skyblock.dungeons.Dungeon
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner
import co.stellarskys.stella.utils.CompatHelpers.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import javax.imageio.ImageIO

object boss {
    fun renderMap(context: UDrawContext) {
        val matrix = context.matrices

        val playerPos = Stella.mc.thePlayer ?: return
        val bossMap = BossMapRegistry.getBossMap(Dungeon.floorNumber!!, Vec3(playerPos.x, playerPos.y, playerPos.z)) ?: return

        val texture = ResourceLocation(Stella.NAMESPACE, "stellanav/boss/${bossMap.image}")
        val realTex = ResourceLocation(texture.resourceDomain, "textures/gui/sprites/${texture.resourcePath}.png")
        val stream = Stella.mc.resourceManager.getResource(realTex).inputStream
        val sprite = ImageIO.read(stream)
        val size = 128

        val sizeInWorld = minOf(
            bossMap.widthInWorld,
            bossMap.heightInWorld,
            bossMap.renderSize ?: Int.MAX_VALUE
        )

        val textureWidth = sprite.width.toDouble() // Replace with actual texture size if available
        val textureHeight = sprite.height.toDouble()

        val pixelWidth = (textureWidth / bossMap.widthInWorld) * (bossMap.renderSize ?: bossMap.widthInWorld)
        val pixelHeight = (textureHeight / bossMap.heightInWorld) * (bossMap.renderSize ?: bossMap.heightInWorld)
        val sizeInPixels = minOf(pixelWidth, pixelHeight)

        val textureScale = size / sizeInPixels

        var topLeftHudLocX = ((playerPos.x - bossMap.topLeftLocation[0]) / sizeInWorld) * size - size / 2
        var topLeftHudLocZ = ((playerPos.z - bossMap.topLeftLocation[1]) / sizeInWorld) * size - size / 2

        topLeftHudLocX = topLeftHudLocX.coerceIn(0.0, maxOf(0.0, textureWidth * textureScale - size))
        topLeftHudLocZ = topLeftHudLocZ.coerceIn(0.0, maxOf(0.0, textureHeight * textureScale - size))

        val w = (textureWidth * textureScale).toInt()
        val h = (textureHeight * textureScale).toInt()

        // Apply transforms
        matrix.pushMatrix()
        matrix.translate(5f,5f,0f)

        // Enable Scissor
        context.enableScissor(0, 0, size, size)

        Render2D.drawImage(
            context,
            texture,
            (-topLeftHudLocX).toInt(),
            (-topLeftHudLocZ).toInt(),
            w,
            h
        )

        context.disableScissor()
        matrix.popMatrix()

        // players

        // Apply transforms
        matrix.pushMatrix()
        matrix.translate(5f,5f,0f)

        // Enable Scissor
        context.enableScissor(0, 0, size, size)
        for ((k, v) in Dungeon.players) {
            val player = DungeonScanner.players.find { it.name == v.name } ?: continue
            val you = Stella.mc.thePlayer ?: continue
            if (v.isDead && v.name != you.name.string) continue

            val iconX = player.iconX ?: continue
            val iconY = player.iconZ ?: continue
            val rotation = player.rotation ?: continue

            val x = (iconX / 125.0 * 128.0)
            val y = (iconY / 125.0 * 128.0)

            val matrix = context.matrices

            matrix.pushMatrix()
            matrix.translate(x.toFloat(), y.toFloat(), 1f)
            matrix.rotate(rotation, 0f, 0f, 1f)
            matrix.scale(mapConfig.iconScale, mapConfig.iconScale, 1f)

            if (mapConfig.showPlayerHead) {
                val w = 12
                val h = 12

                val borderColor = if (mapConfig.iconClassColors) getClassColor(v.className) else mapConfig.iconBorderColor

                Render2D.drawRect(context, (-w.toDouble() / 2.0).toInt(), (-h.toDouble() / 2.0).toInt(), w, h, borderColor)

                val scale = 1f - mapConfig.iconBorderWidth

                matrix.scale(scale, scale, 1f)

                Render2D.drawTexture(
                    context,
                    player.skin,
                    (-w.toDouble() / 2.0).toInt(),
                    (-h.toDouble() / 2.0).toInt(),
                    8f,
                    8f,
                    w,
                    h,
                    8,
                    8,
                    64,
                    64,
                )

                if (player.hat) {
                    Render2D.drawTexture(
                        context,
                        player.skin,
                        (-w.toDouble() / 2.0).toInt(),
                        (-h.toDouble() / 2.0).toInt(),
                        40f,
                        8f,
                        w,
                        h,
                        8,
                        8,
                        64,
                        64,
                    )
                }
            } else {
                val w = 7
                val h = 10
                val head = if (v.name == you.name.string) GreenMarker else WhiteMarker

                Render2D.drawImage(
                    context,
                    head,
                    (-w.toDouble() / 2.0).toInt(),
                    (-h.toDouble() / 2.0).toInt(),
                    w,
                    h
                )
            }

            matrix.popMatrix()
        }
        context.disableScissor()
        matrix.popMatrix()
    }
}