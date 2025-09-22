package co.stellarskys.stella.features.msc.buttonUtils

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import co.stellarskys.stella.utils.skyblock.NEUApi
import net.minecraft.client.gui.ScaledResolution

object ButtonManager {
    private val buttons = mutableListOf<StellaButton>()

    fun getAll(): List<StellaButton> = buttons

    fun getButtonAt(anchor: AnchorType, index: Int): StellaButton? {
        return buttons.find { it.anchor == anchor && it.index == index }
    }

    fun add(button: StellaButton) {
        buttons.removeIf { it.anchor == button.anchor && it.index == button.index } // replace if exists
        buttons += button
    }

    fun remove(anchor: AnchorType, index: Int) {
        buttons.removeIf { it.anchor == anchor && it.index == index }
    }

    fun clear() {
        buttons.clear()
    }

    fun renderAll(invX: Int = 0, invY: Int = 0) {
        buttons.forEach { renderButton(it, invX, invY) }
    }

    private fun renderButton(button: StellaButton, invX: Int, invY: Int) {
        val item = NEUApi.getItemBySkyblockId(button.iconId) ?: return
        val stack = NEUApi.createDummyStack(item)

        val (x, y) = resolveAnchorPosition(button.anchor, button.index, invX, invY)

        if (button.background) {
            NVGRenderer.hollowRect(
                x.toFloat() - 2f,
                y.toFloat() - 2f,
                20f,
                20f,
                1.5f,
                0xFFAAAAAA.toInt(),
                4f
            )
        }

        Render2D.renderItem(stack, x.toFloat() + button.offsetX, y.toFloat() + button.offsetY, button.scale)
    }

    fun resolveAnchorPosition(anchor: AnchorType, index: Int, invX: Int, invY: Int): Pair<Int, Int> {
        val res = ScaledResolution(Stella.mc)
        val screenWidth = res.scaledWidth
        val screenHeight = res.scaledHeight
        val spacing = 24
        val slotSize = 20

        return when (anchor) {
            // Screen corners
            AnchorType.SCREEN_TOP_LEFT ->
                10 + (index * spacing) to 10

            AnchorType.SCREEN_TOP_RIGHT ->
                screenWidth - slotSize - 10 - (index * spacing) to 10

            AnchorType.SCREEN_BOTTOM_LEFT ->
                10 + (index * spacing) to screenHeight - slotSize - 10

            AnchorType.SCREEN_BOTTOM_RIGHT ->
                screenWidth - slotSize - 10 - (index * spacing) to screenHeight - slotSize - 10

            // Screen edges
            AnchorType.SCREEN_TOP ->
                (screenWidth / 2 - (2 * spacing)) + (index * spacing) - 10 to 5

            AnchorType.SCREEN_BOTTOM ->
                (screenWidth / 2 - (2 * spacing)) + (index * spacing) - 10 to screenHeight - slotSize - 5

            AnchorType.SCREEN_LEFT ->
                5 to (screenHeight / 2 - (2 * spacing)) + (index * spacing)

            AnchorType.SCREEN_RIGHT ->
                screenWidth - slotSize - 5 to (screenHeight / 2 - (2 * spacing)) + (index * spacing)

            // Inventory frame
            AnchorType.INVENTORY_TOP ->
                invX + 6 + (index * spacing) to invY - slotSize - 5

            AnchorType.INVENTORY_BOTTOM ->
                invX + 6 + (index * spacing) to invY + 170

            AnchorType.INVENTORY_LEFT ->
                invX - slotSize - 5 to invY + 12 + (index * spacing)

            AnchorType.INVENTORY_RIGHT ->
                invX + 176 + 5 to invY + 12 + (index * spacing)

            // Player model corners (explicit anchors are cleaner)
            AnchorType.PLAYER_MODEL_TOP_LEFT ->
                invX + 25 to invY + 8

            AnchorType.PLAYER_MODEL_TOP_RIGHT ->
                invX + 58 to invY + 8

            AnchorType.PLAYER_MODEL_BOTTOM_LEFT ->
                invX + 25 to invY + 58

            AnchorType.PLAYER_MODEL_BOTTOM_RIGHT ->
                invX + 58 to invY + 58

        }
    }
}