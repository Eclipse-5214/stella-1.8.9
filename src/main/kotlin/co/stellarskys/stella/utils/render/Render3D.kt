package co.stellarskys.stella.utils.render

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.CompatHelpers.DrawContext
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

    fun renderBox(ctx: DrawContext, x: Double, y: Double, z: Double, width: Double, height: Double, color: Color, phaze: Boolean = false, lineWidth: Double = 1.0) {
        renderBox(x, y, z, width, height, color, phaze, lineWidth)
    }

    fun renderBox(
        x: Double,
        y: Double,
        z: Double,
        width: Double,
        height: Double,
        color: Color,
        phaze: Boolean = false,
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
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)

        glLineWidth(lineWidth.toFloat())
        GlStateManager.color(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)

        if (phaze) GlStateManager.disableDepth()

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

        if (phaze) GlStateManager.enableDepth()

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

        if (phase) GlStateManager.enableDepth()
        if (renderBackground) GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.popMatrix()
    }
}