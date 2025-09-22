package co.stellarskys.stella.features.msc.buttonUtils

import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

object EditButtonPopup {
    var activeAnchor: AnchorType? = null
    var activeIndex: Int = 0
    var visible: Boolean = false

    private var itemId: String = ""
    private var command: String = ""
    private var focusedField: Int = -1 // 0 = itemId, 1 = command

    fun open(anchor: AnchorType, index: Int) {
        activeAnchor = anchor
        activeIndex = index
        visible = true
        // TODO: preload existing button data if available
        itemId = ""
        command = ""
    }

    fun close() {
        visible = false
        activeAnchor = null
        activeIndex = 0
        focusedField = -1
    }

    fun render() {
        if (!visible || activeAnchor == null) return

        val res = ScaledResolution(mc)
        val screenWidth = res.scaledWidth
        val screenHeight = res.scaledHeight

        val popupWidth = 300f
        val popupHeight = 180f
        val x = (screenWidth - popupWidth) / 2f
        val y = (screenHeight - popupHeight) / 2f

        NVGRenderer.beginFrame(screenWidth.toFloat(), screenHeight.toFloat())
        NVGRenderer.push()

        // --- Background ---
        NVGRenderer.rect(x, y, popupWidth, popupHeight, Color(32, 32, 32).rgb, 8f)

        // --- Title ---
        NVGRenderer.text(
            "Edit Inventory Button",
            x + 10f, y + 20f, 16f,
            Color(255, 255, 255).rgb,
            NVGRenderer.defaultFont
        )

        // Close button [X]
        NVGRenderer.text("X", x + popupWidth - 20f, y + 20f, 16f, Color(200, 80, 80).rgb, NVGRenderer.defaultFont)

        // --- Item Preview Slot (square) ---
        NVGRenderer.rect(x + 10f, y + 55f, 50f, 50f, Color(48, 48, 48).rgb, 4f)
        // TODO: Render actual item preview here with Render2D.renderItem(...)

        // --- Display Item Id ---
        NVGRenderer.text("Display Item Id", x + 70f, y + 50f, 14f, Color(170, 170, 170).rgb, NVGRenderer.defaultFont)
        NVGRenderer.rect(x + 70f, y + 65f, 200f, 22f, Color(48, 48, 48).rgb, 4f)
        NVGRenderer.text(itemId.ifEmpty { "DUNGEON_CHEST_KEY" }, x + 75f, y + 80f, 14f, Color(255, 255, 255).rgb, NVGRenderer.defaultFont)

        // --- Command When Clicked ---
        NVGRenderer.text("Command When Clicked", x + 70f, y + 100f, 14f, Color(170, 170, 170).rgb, NVGRenderer.defaultFont)
        NVGRenderer.rect(x + 70f, y + 115f, 200f, 22f, Color(48, 48, 48).rgb, 4f)
        NVGRenderer.text(command.ifEmpty { "/warp dungeon_hub" }, x + 75f, y + 130f, 14f, Color(255, 255, 255).rgb, NVGRenderer.defaultFont)

        // --- Buttons (styled like Stella GUI) ---
        // Save
        NVGRenderer.rect(x + 60f, y + popupHeight - 35f, 80f, 22f, Color(60, 120, 60).rgb, 4f)
        NVGRenderer.text("Save", x + 90f, y + popupHeight - 20f, 14f, Color(255, 255, 255).rgb, NVGRenderer.defaultFont)

        // Cancel
        NVGRenderer.rect(x + 160f, y + popupHeight - 35f, 80f, 22f, Color(120, 60, 60).rgb, 4f)
        NVGRenderer.text("Cancel", x + 180f, y + popupHeight - 20f, 14f, Color(255, 255, 255).rgb, NVGRenderer.defaultFont)

        NVGRenderer.pop()
        NVGRenderer.endFrame()
    }
}
