package co.stellarskys.stella.utils.render.orion.elements

import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.utils.render.NVGRenderer
import co.stellarskys.stella.utils.render.orion.UIElement
import net.minecraft.client.gui.ScaledResolution

class UIWindow(init: UIWindow.() -> Unit) : UIElement() {
    init {
        val res = ScaledResolution(mc)
        width = res.scaledWidth.toFloat()
        height = res.scaledHeight.toFloat()
        parent = null
        this.init()
    }

    override fun render() {
        NVGRenderer.beginFrame(width, height)

        var previous: UIElement? = null
        for (child in children) {
            child.applyConstraints(previous, width.toInt(), height.toInt())
            child.render()
            previous = child
        }

        NVGRenderer.endFrame()
    }
}