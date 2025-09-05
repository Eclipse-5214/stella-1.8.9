package co.stellarskys.stella.hud

import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.utils.CompatHelpers.DrawContext
import co.stellarskys.stella.utils.TimeUtils
import co.stellarskys.stella.utils.TimeUtils.millis
import net.minecraft.client.gui.Gui.drawRect
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.pow

class HUDElement(
    val name: String,
    initialX: Float,
    initialY: Float,
    val width: Int,
    val height: Int,
    val exampleText: String,
    var scale: Float = 1f,
    var enabled: Boolean = true
) {
    private var currentX = initialX
    private var currentY = initialY
    var targetX = initialX
    var targetY = initialY
    private var lastUpdateTime = TimeUtils.now

    fun setPosition(x: Float, y: Float) {
        currentX = getRenderX()
        currentY = getRenderY()
        targetX = x
        targetY = y
        lastUpdateTime = TimeUtils.now
    }

    fun getRenderX(): Float {
        val timeDiff = lastUpdateTime.since.millis / 1000f
        val progress = (timeDiff * 8f).coerceIn(0f, 1f)
        return currentX + (targetX - currentX) * easeOutCubic(progress)
    }

    fun getRenderY(): Float {
        val timeDiff = lastUpdateTime.since.millis / 1000f
        val progress = (timeDiff * 8f).coerceIn(0f, 1f)
        return currentY + (targetY - currentY) * easeOutCubic(progress)
    }

    private fun easeOutCubic(t: Float) = 1f + (t - 1f).pow(3)

    fun render(mouseX: Float, mouseY: Float, partialTicks: Float, previewMode: Boolean) {
        if (!enabled && previewMode) return

        val renderX = getRenderX()
        val renderY = getRenderY()
        val isHovered = isMouseOver(mouseX, mouseY)

        GlStateManager.pushMatrix()
        GlStateManager.translate(renderX, renderY, 0f)
        GlStateManager.scale(scale, scale, 1f)

        val customRenderer = HUDManager.getCustomRenderer(name)
        if (customRenderer != null) {
            if (!previewMode) {
                val customDims = HUDManager.getCustomDimensions(name)
                if (customDims != null) {
                    renderCustomBackground(isHovered, customDims.first, customDims.second)
                } else {
                    renderBackground(isHovered)
                }
            }
            val customDims = HUDManager.getCustomDimensions(name)
            val renderWidth = customDims?.first ?: width
            val renderHeight = customDims?.second ?: height
            customRenderer.invoke(DrawContext() ,0f, 0f, renderWidth, renderHeight, scale, partialTicks, previewMode)
        } else {
            renderDefault(previewMode, isHovered)
        }

        GlStateManager.popMatrix()
    }

    private fun renderCustomBackground(isHovered: Boolean, customWidth: Int, customHeight: Int) {
        val alpha = if (!enabled) 40 else if (isHovered) 140 else 90
        val borderColor = when {
            !enabled -> Color(200, 60, 60).rgb
            isHovered -> Color(100, 180, 255).rgb
            else -> Color(100, 100, 120).rgb
        }

        drawRect(0, 0, customWidth, customHeight, Color(30, 35, 45, alpha).rgb)
        drawHollowRect(0, 0, customWidth, customHeight, borderColor)
    }

    private fun renderBackground(isHovered: Boolean) {
        val alpha = if (!enabled) 40 else if (isHovered) 140 else 90
        val borderColor = when {
            !enabled -> Color(200, 60, 60).rgb
            isHovered -> Color(100, 180, 255).rgb
            else -> Color(100, 100, 120).rgb
        }

        drawRect(0, 0, width, height, Color(30, 35, 45, alpha).rgb)
        drawHollowRect(0, 0, width, height, borderColor)
    }

    private fun renderDefault(previewMode: Boolean, isHovered: Boolean) {
        if (!previewMode) {
            renderBackground(isHovered)
        }

        val lines = exampleText.split("\n")
        val textAlpha = if (enabled) 255 else 128
        val textColor = Color(220, 240, 255, textAlpha).rgb

        lines.forEachIndexed { index, line ->
            val textY = 5f + (index * mc.fontRendererObj.FONT_HEIGHT)
            mc.fontRendererObj.drawStringWithShadow(line, 5f, textY, textColor)
        }
    }

    fun isMouseOver(mouseX: Float, mouseY: Float): Boolean {
        val renderX = getRenderX()
        val renderY = getRenderY()

        val customDims = HUDManager.getCustomDimensions(name)
        val actualWidth = customDims?.first ?: width
        val actualHeight = customDims?.second ?: height

        val scaledWidth = actualWidth * scale
        val scaledHeight = actualHeight * scale

        return mouseX >= renderX && mouseX <= renderX + scaledWidth && mouseY >= renderY && mouseY <= renderY + scaledHeight
    }

    private fun drawHollowRect(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        drawRect(x1, y1, x2, y1 + 1, color)
        drawRect(x1, y2 - 1, x2, y2, color)
        drawRect(x1, y1, x1 + 1, y2, color)
        drawRect(x2 - 1, y1, x2, y2, color)
    }
}