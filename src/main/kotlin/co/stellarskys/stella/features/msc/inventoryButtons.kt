package co.stellarskys.stella.features.msc

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.GuiEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.features.msc.buttonUtils.ButtonManager
import co.stellarskys.stella.utils.TimeUtils
import net.minecraft.client.gui.inventory.GuiInventory
import org.lwjgl.input.Mouse
import kotlin.time.Duration.Companion.milliseconds

@Stella.Module
object inventoryButtons : Feature("buttonsEnabled") {
    private val slotSize = 20
    private val previewSlotsPerAnchor = 5

    var enabled = false

    var lastClick = TimeUtils.zero
    val clickCooldown = 500.milliseconds

    override fun initialize() {
        register<GuiEvent.BackgroundDraw> { event ->
            if (event.gui is GuiInventory) {
                val invX = (event.gui.width - 176) / 2
                val invY = (event.gui.height - 166) / 2
                ButtonManager.renderAll(invX, invY)
            }
        }

        register<GuiEvent.Click> { event ->
            if (lastClick.since < clickCooldown) return@register

            val gui = event.gui
            if (gui !is GuiInventory) return@register

            val mouseX = Mouse.getEventX() * gui.width / Stella.mc.displayWidth
            val mouseY = gui.height - (Mouse.getEventY() * gui.height / Stella.mc.displayHeight) - 1
            val mouseButton = Mouse.getEventButton()

            if (mouseButton != 0) return@register

            lastClick = TimeUtils.now
            ButtonManager.handleMouseClicked(gui, mouseX, mouseY)
        }
    }
}