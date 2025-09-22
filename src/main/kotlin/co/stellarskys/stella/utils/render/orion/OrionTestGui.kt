package co.stellarskys.stella.utils.render.orion

import co.stellarskys.stella.utils.config.ui.Palette
import co.stellarskys.stella.utils.render.orion.elements.*
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class OrionTestGui : GuiScreen() {
    private lateinit var window: UIWindow

    override fun initGui() {
        window = UIWindow {
            val panel = UIRectangle(Color(0,0,0,0), 5f, Palette.Purple, 2f) {
                width = 200f
                height = 100f
                x = UIConstraint.Center
                y = UIConstraint.Center
            } childOf this

            val textbox = UIRectangle(Color(0,0,0)) {
                width = 180f
                height = 20f
                x = UIConstraint.Relative(10f)
                y = UIConstraint.Relative(10f)
            } childOf panel

            val label = UIText("Hello, Orion!") {
                width = 180f
                height = 20f
                x = UIConstraint.Relative(10f)
                y = UIConstraint.Relative(10f)
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
