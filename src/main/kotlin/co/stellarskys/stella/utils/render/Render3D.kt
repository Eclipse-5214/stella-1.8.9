package co.stellarskys.stella.utils.render

import co.stellarskys.stella.Stella
import net.minecraft.block.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.hypot


object Render3D {
    private val renderManager = Stella.mc.renderManager

    fun renderBox(
        x: Double,
        y: Double,
        z: Double,
        width: Double,
        height: Double,
        color: Color,
        phase: Boolean = false,
        lineWidth: Double = 1.0
    ) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        val renderX = x + 0.5 - renderManager.viewerPosX
        val renderY = y - renderManager.viewerPosY
        val renderZ = z + 0.5 - renderManager.viewerPosZ

        val boundingBox = AxisAlignedBB(
            renderX - width / 2, renderY, renderZ - width / 2,
            renderX + width / 2, renderY + height, renderZ + width / 2
        )

        GlStateManager.pushMatrix()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.depthMask(true)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)

        if (phase) GlStateManager.disableDepth() else GlStateManager.enableDepth()

        glLineWidth(lineWidth.toFloat())
        GlStateManager.color(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)

        worldRenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        tessellator.draw()
        worldRenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        tessellator.draw()
        worldRenderer.begin(GL_LINES, DefaultVertexFormats.POSITION)
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex()
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex()
        tessellator.draw()

        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun renderBlock(blockPosition: BlockPos, partialTicks: Float, fill: Boolean, color: Color, lineWidth: Float, phase: Boolean = true, fillColor: Color = color) {
        val world = Stella.mc.theWorld
        val blockState = world.getBlockState(blockPosition)
        val block = blockState.block
        val boundingBox = block.getSelectedBoundingBox(world, blockPosition).expand(0.005, 0.005, 0.005)
            ?: AxisAlignedBB(blockPosition.x.toDouble(), blockPosition.y.toDouble(), blockPosition.z.toDouble(), blockPosition.x + 1.0, blockPosition.y + 1.0, blockPosition.z + 1.0).expand(0.005, 0.005, 0.005)
        val player = Stella.mc.thePlayer
        val interpolatedX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val interpolatedY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val interpolatedZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

        val minX = boundingBox.minX - interpolatedX
        val minY = boundingBox.minY - interpolatedY
        val minZ = boundingBox.minZ - interpolatedZ
        val maxX = boundingBox.maxX - interpolatedX
        val maxY = boundingBox.maxY - interpolatedY
        val maxZ = boundingBox.maxZ - interpolatedZ

        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)

        if (phase) GlStateManager.disableDepth() else GlStateManager.enableDepth()

        GlStateManager.disableCull()

        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        if (fill) {
            GlStateManager.color(fillColor.red / 255.0f, fillColor.green / 255.0f, fillColor.blue / 255.0f, fillColor.alpha / 255.0f)
            worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
            worldRenderer.pos(minX, minY, minZ).endVertex()
            worldRenderer.pos(minX, maxY, minZ).endVertex()
            worldRenderer.pos(maxX, maxY, minZ).endVertex()
            worldRenderer.pos(maxX, minY, minZ).endVertex()
            worldRenderer.pos(maxX, minY, maxZ).endVertex()
            worldRenderer.pos(maxX, maxY, maxZ).endVertex()
            worldRenderer.pos(minX, maxY, maxZ).endVertex()
            worldRenderer.pos(minX, minY, maxZ).endVertex()
            worldRenderer.pos(minX, minY, minZ).endVertex()
            worldRenderer.pos(minX, minY, maxZ).endVertex()
            worldRenderer.pos(minX, maxY, maxZ).endVertex()
            worldRenderer.pos(minX, maxY, minZ).endVertex()
            worldRenderer.pos(maxX, maxY, minZ).endVertex()
            worldRenderer.pos(maxX, maxY, maxZ).endVertex()
            worldRenderer.pos(maxX, minY, maxZ).endVertex()
            worldRenderer.pos(maxX, minY, minZ).endVertex()
            worldRenderer.pos(minX, minY, minZ).endVertex()
            worldRenderer.pos(maxX, minY, minZ).endVertex()
            worldRenderer.pos(maxX, minY, maxZ).endVertex()
            worldRenderer.pos(minX, minY, maxZ).endVertex()
            worldRenderer.pos(minX, maxY, maxZ).endVertex()
            worldRenderer.pos(maxX, maxY, maxZ).endVertex()
            worldRenderer.pos(maxX, maxY, minZ).endVertex()
            worldRenderer.pos(minX, maxY, minZ).endVertex()
            tessellator.draw()
        }

        glLineWidth(lineWidth)
        GlStateManager.color(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
        worldRenderer.begin(GL_LINES, DefaultVertexFormats.POSITION)
        worldRenderer.pos(minX, minY, minZ).endVertex()
        worldRenderer.pos(maxX, minY, minZ).endVertex()
        worldRenderer.pos(maxX, minY, minZ).endVertex()
        worldRenderer.pos(maxX, minY, maxZ).endVertex()
        worldRenderer.pos(maxX, minY, maxZ).endVertex()
        worldRenderer.pos(minX, minY, maxZ).endVertex()
        worldRenderer.pos(minX, minY, maxZ).endVertex()
        worldRenderer.pos(minX, minY, minZ).endVertex()
        worldRenderer.pos(minX, maxY, minZ).endVertex()
        worldRenderer.pos(maxX, maxY, minZ).endVertex()
        worldRenderer.pos(maxX, maxY, minZ).endVertex()
        worldRenderer.pos(maxX, maxY, maxZ).endVertex()
        worldRenderer.pos(maxX, maxY, maxZ).endVertex()
        worldRenderer.pos(minX, maxY, maxZ).endVertex()
        worldRenderer.pos(minX, maxY, maxZ).endVertex()
        worldRenderer.pos(minX, maxY, minZ).endVertex()
        worldRenderer.pos(minX, minY, minZ).endVertex()
        worldRenderer.pos(minX, maxY, minZ).endVertex()
        worldRenderer.pos(maxX, minY, minZ).endVertex()
        worldRenderer.pos(maxX, maxY, minZ).endVertex()
        worldRenderer.pos(maxX, minY, maxZ).endVertex()
        worldRenderer.pos(maxX, maxY, maxZ).endVertex()
        worldRenderer.pos(minX, minY, maxZ).endVertex()
        worldRenderer.pos(minX, maxY, maxZ).endVertex()
        tessellator.draw()

        GlStateManager.enableCull()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    fun renderBlockShape(
        blockPos: BlockPos,
        partialTicks: Float,
        fill: Boolean,
        color: Color,
        lineWidth: Float,
        phase: Boolean = true,
        fillColor: Color = color,
        chroma: Boolean = false,
        chromaSpeed: Float = 0f
    ) {
        val world = Stella.mc.theWorld
        val state = world.getBlockState(blockPos)
        val block = state.block

        val shapeBoxes = mutableListOf<AxisAlignedBB>()

        if (shouldForceAabb(block)) {
            block.getSelectedBoundingBox(world, blockPos)?.let { shapeBoxes.add(it) }
        } else {
            block.addCollisionBoxesToList(
                world,
                blockPos,
                state,
                AxisAlignedBB(
                    blockPos.x.toDouble(),
                    blockPos.y.toDouble(),
                    blockPos.z.toDouble(),
                    blockPos.x + 1.0,
                    blockPos.y + 1.0,
                    blockPos.z + 1.0
                ),
                shapeBoxes,
                Stella.mc.thePlayer
            )
            if (shapeBoxes.isEmpty()) {
                block.getSelectedBoundingBox(world, blockPos)?.let { shapeBoxes.add(it) }
            }
        }

        val player = Stella.mc.thePlayer
        val interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

        if (chroma) {
            val vertexSource = ChromaShader.loadShaderSource("shaders/3d_chroma.vsh")
            val fragmentSource = ChromaShader.loadShaderSource("shaders/3d_chroma.fsh")

            ChromaShader.init(vertexSource, fragmentSource)
            ChromaShader.bind()

            val player = Stella.mc.thePlayer
            val position = player.positionVector
            val scaledRes = ScaledResolution(Stella.mc)
            val scaledWidth = scaledRes.scaledWidth
            val size =  30f * (scaledWidth / 100f)

            ChromaShader.setUniforms(
                chromaSize = size,
                saturation = 1f,
                brightness = 1f,
                timeOffset = chromaSpeed,
                alpha = 1f, // this will be overridden before each draw
                playerPos = position
            )
        }

        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)

        if (phase) GlStateManager.disableDepth() else GlStateManager.enableDepth()
        GlStateManager.disableCull()

        val tess = Tessellator.getInstance()
        val wr = tess.worldRenderer

        for (rawBox in shapeBoxes) {
            val aabb = rawBox.expand(0.005, 0.005, 0.005)

            val minX = aabb.minX - interpX
            val minY = aabb.minY - interpY
            val minZ = aabb.minZ - interpZ
            val maxX = aabb.maxX - interpX
            val maxY = aabb.maxY - interpY
            val maxZ = aabb.maxZ - interpZ

            if (fill) {
                if (chroma) ChromaShader.setAlpha(fillColor.alpha / 255f)

                GlStateManager.color(
                    fillColor.red / 255f,
                    fillColor.green / 255f,
                    fillColor.blue / 255f,
                    fillColor.alpha / 255f
                )
                wr.begin(GL_QUADS, DefaultVertexFormats.POSITION)
                // draw quads...
                wr.pos(minX, minY, minZ).endVertex()
                wr.pos(minX, maxY, minZ).endVertex()
                wr.pos(maxX, maxY, minZ).endVertex()
                wr.pos(maxX, minY, minZ).endVertex()

                wr.pos(maxX, minY, maxZ).endVertex()
                wr.pos(maxX, maxY, maxZ).endVertex()
                wr.pos(minX, maxY, maxZ).endVertex()
                wr.pos(minX, minY, maxZ).endVertex()

                wr.pos(minX, minY, minZ).endVertex()
                wr.pos(minX, minY, maxZ).endVertex()
                wr.pos(minX, maxY, maxZ).endVertex()
                wr.pos(minX, maxY, minZ).endVertex()

                wr.pos(maxX, maxY, minZ).endVertex()
                wr.pos(maxX, maxY, maxZ).endVertex()
                wr.pos(maxX, minY, maxZ).endVertex()
                wr.pos(maxX, minY, minZ).endVertex()

                wr.pos(minX, minY, minZ).endVertex()
                wr.pos(maxX, minY, minZ).endVertex()
                wr.pos(maxX, minY, maxZ).endVertex()
                wr.pos(minX, minY, maxZ).endVertex()

                wr.pos(minX, maxY, maxZ).endVertex()
                wr.pos(maxX, maxY, maxZ).endVertex()
                wr.pos(maxX, maxY, minZ).endVertex()
                wr.pos(minX, maxY, minZ).endVertex()
                tess.draw()
            }

            glLineWidth(lineWidth)
            if (chroma) ChromaShader.setAlpha(color.alpha / 255f)

            GlStateManager.color(
                color.red / 255f,
                color.green / 255f,
                color.blue / 255f,
                color.alpha / 255f
            )
            wr.begin(GL_LINES, DefaultVertexFormats.POSITION)
            // draw lines...
            wr.pos(minX, minY, minZ).endVertex()
            wr.pos(maxX, minY, minZ).endVertex()

            wr.pos(maxX, minY, minZ).endVertex()
            wr.pos(maxX, minY, maxZ).endVertex()

            wr.pos(maxX, minY, maxZ).endVertex()
            wr.pos(minX, minY, maxZ).endVertex()

            wr.pos(minX, minY, maxZ).endVertex()
            wr.pos(minX, minY, minZ).endVertex()

            wr.pos(minX, maxY, minZ).endVertex()
            wr.pos(maxX, maxY, minZ).endVertex()

            wr.pos(maxX, maxY, minZ).endVertex()
            wr.pos(maxX, maxY, maxZ).endVertex()

            wr.pos(maxX, maxY, maxZ).endVertex()
            wr.pos(minX, maxY, maxZ).endVertex()

            wr.pos(minX, maxY, maxZ).endVertex()
            wr.pos(minX, maxY, minZ).endVertex()

            wr.pos(minX, minY, minZ).endVertex()
            wr.pos(minX, maxY, minZ).endVertex()

            wr.pos(maxX, minY, minZ).endVertex()
            wr.pos(maxX, maxY, minZ).endVertex()

            wr.pos(maxX, minY, maxZ).endVertex()
            wr.pos(maxX, maxY, maxZ).endVertex()

            wr.pos(minX, minY, maxZ).endVertex()
            wr.pos(minX, maxY, maxZ).endVertex()
            tess.draw()
        }

        GlStateManager.enableCull()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()

        if (chroma) ChromaShader.unbind()
    }



    fun renderString(
        text: String,
        x: Double,
        y: Double,
        z: Double,
        bgColor: java.awt.Color = java.awt.Color(0, 0, 0, 180),
        renderBackground: Boolean = true,
        scale: Float = 1f,
        increase: Boolean = true,
        shadow: Boolean = true,
        partialTicks: Float,
        phase: Boolean = true
    ) {
        if (text.isEmpty()) return

        val mc = Stella.mc
        val fr = mc.fontRendererObj
        val rm = mc.renderManager

        // Interpolated camera position
        val player = mc.thePlayer
        val viewerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks
        val viewerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks
        val viewerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks

        // Position relative to camera
        val relX = x - viewerX
        val relY = y - viewerY
        val relZ = z - viewerZ

        // Split into lines if needed
        val lines = text.split("\n")
        val lineCount = lines.size
        val totalWidth = lines.maxOf { fr.getStringWidth(it.replace(Regex("§."), "")) }

        // Distance scaling
        var distScale = scale
        if (increase) {
            val dist = hypot(hypot(relX, relY), relZ)
            distScale = scale * 0.45f * (dist / 120.0).toFloat()
        }

        val mult = if (mc.gameSettings.thirdPersonView == 2) -1 else 1

        GlStateManager.pushMatrix()
        GlStateManager.translate(relX.toFloat(), relY.toFloat(), relZ.toFloat())
        GlStateManager.rotate(-rm.playerViewY, 0f, 1f, 0f)
        GlStateManager.rotate(rm.playerViewX * mult, 1f, 0f, 0f)
        GlStateManager.scale(-distScale, -distScale, distScale)

        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        Tessellator.getInstance().worldRenderer // just to ensure class is loaded

        GlStateManager.depthMask(false)
        if (phase) GlStateManager.disableDepth()

        // Background box
        if (renderBackground) {
            val r = bgColor.red / 255f
            val g = bgColor.green / 255f
            val b = bgColor.blue / 255f
            val a = bgColor.alpha / 255f
            val halfWidth = (totalWidth / 2f).toDouble()

            GlStateManager.disableTexture2D()
            val tess = Tessellator.getInstance()
            val wr = tess.worldRenderer
            wr.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            wr.pos(-halfWidth - 1, -1.0, 0.0).color(r, g, b, a).endVertex()
            wr.pos(-halfWidth - 1, 9.0 * lineCount, 0.0).color(r, g, b, a).endVertex()
            wr.pos(halfWidth + 1, 9.0 * lineCount, 0.0).color(r, g, b, a).endVertex()
            wr.pos(halfWidth + 1, -1.0, 0.0).color(r, g, b, a).endVertex()
            tess.draw()
            GlStateManager.enableTexture2D()
        }

        // Draw each line
        for ((i, line) in lines.withIndex()) {
            val cleanWidth = fr.getStringWidth(line.replace(Regex("§."), ""))
            fr.drawString(line, -cleanWidth / 2f, i * 9f, 0xFFFFFF, shadow)
        }

        if (renderBackground) GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
        GlStateManager.popMatrix()
    }

    fun shouldForceAabb(block: Block): Boolean {
        return block is BlockFence ||
                block is BlockFenceGate ||
                block is BlockWall ||
                block is BlockHopper ||
                block is BlockCauldron
    }

    fun drawTexturedRectNoBlend(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        uMin: Float,
        uMax: Float,
        vMin: Float,
        vMax: Float,
        filter: Int
    ) {
        GlStateManager.enableTexture2D()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter)

        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldrenderer
            .pos(x.toDouble(), (y + height).toDouble(), 0.0)
            .tex(uMin.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer
            .pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
            .tex(uMax.toDouble(), vMax.toDouble()).endVertex()
        worldrenderer
            .pos((x + width).toDouble(), y.toDouble(), 0.0)
            .tex(uMax.toDouble(), vMin.toDouble()).endVertex()
        worldrenderer
            .pos(x.toDouble(), y.toDouble(), 0.0)
            .tex(uMin.toDouble(), vMin.toDouble()).endVertex()
        tessellator.draw()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    }

    fun drawRectNoBlend(left: Int, top: Int, right: Int, bottom: Int, color: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        if (left < right) {
            val i = left
            left = right
            right = i
        }

        if (top < bottom) {
            val j = top
            top = bottom
            bottom = j
        }

        val f3 = (color shr 24 and 255).toFloat() / 255.0f
        val f = (color shr 16 and 255).toFloat() / 255.0f
        val f1 = (color shr 8 and 255).toFloat() / 255.0f
        val f2 = (color and 255).toFloat() / 255.0f
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.getWorldRenderer()
        GlStateManager.disableTexture2D()
        GlStateManager.color(f, f1, f2, f3)
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION)
        worldrenderer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        worldrenderer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        worldrenderer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
    }
}