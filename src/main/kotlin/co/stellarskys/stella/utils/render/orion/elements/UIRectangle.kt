package co.stellarskys.stella.utils.render.orion.elements

import co.stellarskys.stella.utils.render.orion.UIElement
import co.stellarskys.stella.utils.render.nanoVG.NVGRenderer
import java.awt.Color

class UIRectangle(val color: Color, init: UIRectangle.() -> Unit) : UIElement() {
    init {
        this.init()
    }

    override fun render() {
        NVGRenderer.rect(xPos, yPos, width, height, color.rgb, 0f)
        super.render()
    }
}
