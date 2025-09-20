package co.stellarskys.stella.features.msc.buttonUtils

import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import net.minecraft.client.gui.ScaledResolution

object EditButtonPopup {
    var activeAnchor: AnchorType? = null
    var activeIndex: Int = 0
    var visible: Boolean = false

    fun open(anchor: AnchorType, index: Int) {
        activeAnchor = anchor
        activeIndex = index
        visible = true
    }

    fun close() {
        visible = false
        activeAnchor = null
        activeIndex = 0
    }

    fun render() {
        if (!visible || activeAnchor == null) return

        val res = ScaledResolution(mc)
        val screenWidth = res.scaledWidth
        val screenHeight = res.scaledHeight

        val popupWidth = 220f
        val popupHeight = 120f
        val x = (screenWidth - popupWidth) / 2f
        val y = (screenHeight - popupHeight) / 2f

        NVGRenderer.beginFrame(screenWidth.toFloat(), screenHeight.toFloat())
        NVGRenderer.push()

        // Background box
        NVGRenderer.rect(x, y, popupWidth, popupHeight, 0xFF202020.toInt(), 8f)

        // Title
        NVGRenderer.text(
            "Edit Button: ${activeAnchor!!.name} [$activeIndex]",
            x + 10f, y + 15f, 16f,
            0xFFFFFFFF.toInt(),
            NVGRenderer.defaultFont
        )

        // Placeholders for fields
        NVGRenderer.text("Item ID: [placeholder]", x + 10f, y + 40f, 14f, 0xFFAAAAAA.toInt(), NVGRenderer.defaultFont)
        NVGRenderer.text("Command: [placeholder]", x + 10f, y + 60f, 14f, 0xFFAAAAAA.toInt(), NVGRenderer.defaultFont)

        // Buttons
        NVGRenderer.text("Save", x + 10f, y + 90f, 14f, 0xFF88FF88.toInt(), NVGRenderer.defaultFont)
        NVGRenderer.text("Cancel", x + 70f, y + 90f, 14f, 0xFFFF8888.toInt(), NVGRenderer.defaultFont)

        NVGRenderer.pop()
        NVGRenderer.endFrame()
    }
}
