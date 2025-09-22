package co.stellarskys.stella.utils.render.orion.elements

import co.stellarskys.stella.utils.render.nanoVG.Color
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import co.stellarskys.stella.utils.render.orion.UIElement

class UIText(
    val text: String,
    val fontSize: Float = 14f,
    val color: Color = Color(255, 255, 255),
    val shadow: Boolean = false,
    init: UIText.() -> Unit
) : UIElement() {
    init { this.init() }

    override fun render() {
        val textX = xPos
        val textY = yPos + height / 2f

        if (shadow) {
            NVGRenderer.textShadow(
                text,
                textX,
                textY,
                fontSize,
                color.rgba,
                NVGRenderer.defaultFont
            )
        } else {
            NVGRenderer.text(
                text,
                textX,
                textY,
                fontSize,
                color.rgba,
                NVGRenderer.defaultFont
            )
        }

        super.render()
    }
}
