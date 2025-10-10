package co.stellarskys.stella.features.msc.buttonUtils

import co.stellarskys.stella.utils.render.NVGRenderer
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.skyblock.NEUApi
import net.minecraft.util.ResourceLocation
import xyz.meowing.vexel.core.VexelScreen

class ButtonLayoutEditor : VexelScreen() {
    private val dummyInventoryTexture = ResourceLocation("textures/gui/container/inventory.png")
    private val slotSize = 20
    private val popup = EditButtonPopup(window)

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()

        // Draw dummy inventory
        mc.textureManager.bindTexture(dummyInventoryTexture)
        val invX = (width - 176) / 2
        val invY = (height - 166) / 2
        drawTexturedModalRect(invX, invY, 0, 0, 176, 166)

        NVGRenderer.beginFrame(width.toFloat(), height.toFloat())

        for (anchor in AnchorType.entries) {
            for (index in 0 until anchor.slots) {
                val (x, y) = ButtonManager.resolveAnchorPosition(anchor, index, invX, invY)

                NVGRenderer.hollowRect(
                    x.toFloat(),
                    y.toFloat(),
                    slotSize.toFloat(),
                    slotSize.toFloat(),
                    1f,
                    0xFFAAAAAA.toInt(),
                    4f
                )

                ButtonManager.getAll().find { it.anchor == anchor && it.index == index }?.let { button ->
                    val item = NEUApi.getItemBySkyblockId(button.iconId) ?: return@let
                    val stack = NEUApi.createDummyStack(item)

                    val offsetX = (20f - 16f) / 2f
                    val offsetY = (20f - 16f) / 2f

                    Render2D.renderItem(stack, x.toFloat() + offsetX, y.toFloat() + offsetY, 1f)
                }
            }
        }

        NVGRenderer.endFrame()

        super.drawScreen(mouseX, mouseY, partialTicks)
        popup.renderPreviewItem()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val invX = (width - 176) / 2
        val invY = (height - 166) / 2

        if (!popup.shown) {
            for (anchor in AnchorType.entries) {
                for (index in 0 until anchor.slots) {
                    val (x, y) = ButtonManager.resolveAnchorPosition(anchor, index, invX, invY)
                    if (mouseX in x..(x + slotSize) && mouseY in y..(y + slotSize)) {
                        popup.open(anchor, index)
                        return
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }
}

