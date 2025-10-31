package co.stellarskys.stella.features.stellanav.utils.render

import co.stellarskys.stella.Stella
import co.stellarskys.stella.features.stellanav.utils.*
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.skyblock.dungeons.Dungeon
import co.stellarskys.stella.utils.CompatHelpers.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import java.util.UUID
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
        for (player in Dungeon.players) {
            if (player == null) continue
            val you = Stella.mc.thePlayer ?: continue
            if (!player.alive && player.name != you.name.string) continue

            val realX = player.realX ?: continue
            val realY = player.realZ ?: continue
            val rotation = player.yaw ?: continue

            val x = ((realX - bossMap.topLeftLocation[0]) / sizeInWorld) * size - topLeftHudLocX
            val y = ((realY - bossMap.topLeftLocation[1]) / sizeInWorld) * size - topLeftHudLocZ

            val matrix = context.matrices

            val ownName = mapConfig.dontShowOwn && player.name == you.name
            if (Dungeon.holdingLeaps && mapConfig.showNames && !ownName) {
                matrix.pushMatrix()
                matrix.translate(x.toFloat(), y.toFloat(), 1f)

                val scale = mapConfig.iconScale / 1.3f
                renderNametag(context, player.name, scale)
                matrix.popMatrix()
            }

            matrix.pushMatrix()
            matrix.translate(x.toFloat(), y.toFloat(), 1f)
            matrix.rotate(rotation, 0f, 0f, 1f)
            matrix.scale(mapConfig.iconScale, mapConfig.iconScale, 1f)

            if (mapConfig.showPlayerHead) {
                val w = 12
                val h = 12

                val borderColor = if (mapConfig.iconClassColors) getClassColor(player.dclass.displayName) else mapConfig.iconBorderColor
                Render2D.drawRect(context, (-w.toDouble() / 2.0).toInt(), (-h.toDouble() / 2.0).toInt(), w, h, borderColor)

                val scale = 1f - mapConfig.iconBorderWidth
                matrix.scale(scale, scale, 1f)
                Render2D.drawPlayerHead(-6,-6,12, player.uuid ?: UUID(0, 0))
            } else {
                val w = 7
                val h = 10
                val head = if (player.name == you.name.string) GreenMarker else WhiteMarker

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