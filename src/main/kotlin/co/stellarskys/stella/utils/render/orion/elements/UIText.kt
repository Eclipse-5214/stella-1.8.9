package co.stellarskys.stella.utils.render.orion.elements


import co.stellarskys.stella.utils.render.NVGRenderer
import co.stellarskys.stella.utils.render.orion.UIElement
import xyz.meowing.vexel.utils.style.Color

class UIText(
    val text: String,
    val fontSize: Float = 14f,
    val color: Color = Color(255, 255, 255),
    val shadow: Boolean = false,
    init: UIText.() -> Unit
) : UIElement() {
    init {
        width = NVGRenderer.textWidth(text, fontSize, NVGRenderer.defaultFont)
        height = fontSize
        this.init()
    }

    override fun render() {
        val textX = xPos
        val textY = yPos + fontSize

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
