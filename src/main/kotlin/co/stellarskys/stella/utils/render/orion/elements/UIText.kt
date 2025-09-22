package co.stellarskys.stella.utils.render.orion.elements

import co.stellarskys.stella.utils.render.nanoVG.Color
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import co.stellarskys.stella.utils.render.orion.UIElement

class UIText(val text: String, init: UIText.() -> Unit) : UIElement() {
    init { this.init() }

    override fun render() {
        NVGRenderer.text(text, xPos, yPos + height / 2f + 5f, 14f, Color(255, 255, 255).rgba, NVGRenderer.defaultFont)
        super.render()
    }
}
