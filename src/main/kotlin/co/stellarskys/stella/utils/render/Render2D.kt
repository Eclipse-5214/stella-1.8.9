package co.stellarskys.stella.utils.render

import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.utils.CompatHelpers.DrawContext
import co.stellarskys.stella.utils.clearCodes
import co.stellarskys.stella.utils.skyblock.dungeons.players.DungeonPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.DefaultPlayerSkin
import net.minecraft.entity.player.EnumPlayerModelParts
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.UUID

object Render2D {
    private val formattingRegex = "(?<!\\\\\\\\)&(?=[0-9a-fk-or])".toRegex()

    private fun preDraw() {
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
    }

    private fun postDraw() {
        GlStateManager.disableBlend()
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
    }

    fun drawTexture(
        ctx: DrawContext,
        image: ResourceLocation,
        x: Int,
        y: Int,
        u: Float,
        v: Float,
        width: Int,
        height: Int,
        regionWidth: Int,
        regionHeight: Int,
        textureWidth: Int = 256,
        textureHeight: Int = 256
    ) {
        preDraw()
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(image)
        Gui.drawScaledCustomSizeModalRect(
            x, y,
            u, v,
            regionWidth, regionHeight,
            width, height,
            textureWidth.toFloat(), textureHeight.toFloat()
        )
        postDraw()
    }


    fun drawImage(ctx: DrawContext, image: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        val newImage = ResourceLocation(image.resourceDomain, "textures/gui/sprites/${image.resourcePath}.png")

        preDraw()
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)

        mc.textureManager.bindTexture(newImage)
        Gui.drawScaledCustomSizeModalRect(
            x, y,
            0f, 0f,
            256, 256,
            width, height,
            256f, 256f
        )

        postDraw()
    }



    fun drawRect(ctx: DrawContext, x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        drawRect(x, y, width, height, color)
    }

    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Color = Color.WHITE) {
        Gui.drawRect(x, y, x + width, y + height, color.rgb)
    }

    fun drawString(context: DrawContext, str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        drawString(str, x, y, scale, shadow)
    }

    fun drawString(str: String, x: Int, y: Int, scale: Float = 1f, shadow: Boolean = true) {
        if (scale != 1f) {
            GlStateManager.pushMatrix()
            GlStateManager.scale(scale, scale, 1f)
        }

        mc.fontRendererObj.drawString(
            str.replace(formattingRegex, "§"),
            x.toFloat(),
            y.toFloat(),
            -1,
            shadow
        )

        if (scale != 1f) GlStateManager.popMatrix()
    }

    fun renderItem(item: ItemStack, x: Float, y: Float, scale: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, 0f)
        GlStateManager.scale(scale, scale, 1f)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(item, 0, 0)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.popMatrix()
    }

    fun drawPlayerHead(x: Int, y: Int, size: Int, uuid: UUID) {
        val info = mc.thePlayer.sendQueue.getPlayerInfo(uuid)
        val skin = info?.locationSkin ?: DefaultPlayerSkin.getDefaultSkinLegacy()

        preDraw()
        GlStateManager.enableTexture2D()
        GlStateManager.color(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(x, y, 8f, 8f, 8, 8, size, size, 64f, 64f)
        Gui.drawScaledCustomSizeModalRect(x, y, 40f, 8f, 8, 8, 12, 12, 64f, 64f)
        postDraw()
    }


    fun String.width(): Int {
        val lines = split('\n')
        return lines.maxOf { mc.fontRendererObj.getStringWidth(it.clearCodes()) }
    }

    fun String.height(): Int {
        val lineCount = count { it == '\n' } + 1
        return mc.fontRendererObj.FONT_HEIGHT * lineCount
    }
}