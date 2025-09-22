package co.stellarskys.stella.utils.render.orion.elements

import co.stellarskys.stella.utils.render.orion.UIElement
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import java.awt.Color

class UIRectangle(
    val color: Color,
    val radius: Float = 0f,
    val borderColor: Color? = null,
    val borderWidth: Float = 0f,
    init: UIRectangle.() -> Unit
) : UIElement() {
    init { this.init() }

    override fun render() {
        // Fill
        NVGRenderer.rect(xPos, yPos, width, height, color.rgb, radius)

        // Outline if requested
        if (borderColor != null && borderWidth > 0f) {
            NVGRenderer.hollowRect(xPos, yPos, width, height, borderWidth, borderColor.rgb, radius)
        }

        super.render()
    }
}
