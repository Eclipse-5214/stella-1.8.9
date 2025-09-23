package co.stellarskys.stella.utils.render.orion

import co.stellarskys.stella.utils.config.ui.Palette
import co.stellarskys.stella.utils.render.orion.elements.*
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class OrionTestGui : GuiScreen() {
    private lateinit var window: UIWindow

    override fun initGui() {
        window = UIWindow {
            val panel = UIRectangle(Color(0,0,0), 5f, Palette.Purple, 1f) {
                width = 300f
                height = 200f
                x = UIConstraint.Center
                y = UIConstraint.Center
            } childOf this

            val xThing = UIRectangle(Color(0,0,0), 5f, Palette.Purple, 1f) {
                width = 20f
                height = 20f
                x = UIConstraint.Relative(10f, true)
                y = UIConstraint.Relative(10f)
            } childOf panel

            val xButton = UIText("X") {
                x = UIConstraint.Center
                y = UIConstraint.Relative(1f)
            } childOf xThing

            val label = UIText("Edit Button") {
                x = UIConstraint.Relative(10f)
                y = UIConstraint.Relative(10f)
            } childOf panel

            val itemPreview = UIRectangle(Color(0,0,0), 5f, Palette.Purple, 1f) {
                width = 50f
                height = 50f
                x = UIConstraint.Relative(10f)
                y = UIConstraint.Relative(40f)
            } childOf panel

            val itemIdBox = UIRectangle(Color(0,0,0), 5f, Palette.Purple, 1f) {
                width = 250f
                height = 20f
                x = UIConstraint.Center
                y = UIConstraint.Relative(70f)
            } childOf panel

            val commandBox = UIRectangle(Color(0,0,0), 5f, Palette.Purple, 1f) {
                width = 250f
                height = 20f
                x = UIConstraint.Center
                y = UIConstraint.Relative(110f)
            } childOf panel

        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        window.render()
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        window.mouseClicked(mouseX, mouseY, button)
        super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        window.keyTyped(typedChar, keyCode)
        super.keyTyped(typedChar, keyCode)
    }
}
