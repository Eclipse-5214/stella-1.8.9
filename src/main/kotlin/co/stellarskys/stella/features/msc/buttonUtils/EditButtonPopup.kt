package co.stellarskys.stella.features.msc.buttonUtils

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.config.ui.Palette
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.skyblock.NEUApi
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import xyz.meowing.vexel.components.base.*
import xyz.meowing.vexel.components.core.*
import xyz.meowing.vexel.core.VexelWindow
import xyz.meowing.vexel.elements.Button
import xyz.meowing.vexel.elements.TextInput
import java.awt.Color
import kotlin.io.path.Path

class EditButtonPopup(window: VexelWindow) {
    var activeAnchor: AnchorType? = null
    var activeIndex: Int = 0
    var shown = false

    private var itemId: String = ""
    private var command: String = ""

    //gui
    val window = VexelWindow()

    val rect = Rectangle(
        Color.black.rgb,
        Palette.Purple.rgb,
        10f, 1f
    )
        .constrain {
            x = Pos.ScreenCenter + 0f
            y = Pos.ScreenCenter + 0f
            width = percentParent(50f)
            height = percentParent(50f)
        }
        .childOf(window)
        .hide()

    val popupTitle = Text("Edit Inventory Button", fontSize = 40f)
        .constrain {
            x = 20.px
            y = 10.px
        }
        .childOf(rect)

    val closeX = Button(
        "X",
        backgroundColor = Color.black.rgb,
        borderColor = Palette.Purple.rgb,
        fontSize = 24f
    )
        .setPositioning(90f, Pos.ParentPercent, 5f, Pos.ParentPercent)
        .constrain {
            width = 40.px
            height = 40.px
        }
        .onClick { _, _, _ ->
            close()
            true
        }
        .childOf(rect)


    val itemPreview = Rectangle(
        Color.black.rgb,
        Palette.Purple.rgb,
        10f, 1f
    )
        .constrain {
            x = 20.px
            y = Pos.AfterSibling + 20f
            width = 130.px
            height = 130.px
        }
        .childOf(rect)

    val itemIdInput = TextInput(
        placeholder = "EX: ENDER_PEARL",
        fontSize = 24f,
        backgroundColor = Color.black.rgb,
        borderColor = Palette.Purple.rgb,
        borderRadius = 10f,
        borderThickness = 1f
    )
        .constrain {
            x = Pos.ParentCenter + 0f
            y = Pos.AfterSibling + 30f
            width = percentParent(95f)
        }
        .childOf(rect)

    val commandInput = TextInput(
        placeholder = "EX: /hub",
        fontSize = 24f,
        backgroundColor = Color.black.rgb,
        borderColor = Palette.Purple.rgb,
        borderRadius = 10f,
        borderThickness = 1f
    )
        .constrain {
            x = Pos.ParentCenter + 0f
            y = Pos.AfterSibling + 30f
            width = percentParent(95f)
        }
        .childOf(rect)

    val buttonWrapper = Rectangle()
        .constrain {
            width = 280.px
            height = 40f.px
            x = Pos.ParentCenter + 0f
            y = Pos.AfterSibling + 30f
        }
        .childOf(rect)

    val saveButton = Button(
        "save",
        backgroundColor = Palette.Green.rgb,
        fontSize = 24f
    )
        .constrain {
            x = 0.px
            y = Pos.ParentCenter + 0f
            width = 130.px
        }
        .onClick { _, _, _ ->
            save()
            true
        }
        .childOf(buttonWrapper)

    val deleteButton = Button(
        "delete",
        backgroundColor = Palette.Red.rgb,
        fontSize = 24f
    )
        .constrain {
            x = 150.px
            y = Pos.ParentCenter + 0f
            width = 130.px
        }
        .onClick { _, _, _ ->
            delete()
            true
        }
        .childOf(buttonWrapper)

    fun renderPreviewItem() {
        if (!shown) return

        val item = NEUApi.getItemBySkyblockId(itemIdInput.value)

        val barrior = Item.getItemFromBlock(Blocks.barrier)
        var stack = ItemStack(barrior, 1)

        if (item != null) {
            stack = NEUApi.createDummyStack(item)
        }

        val res = ScaledResolution(Stella.mc)
        val scale = res.scaleFactor.toFloat()

        val x = itemPreview.x / scale
        val y = itemPreview.y / scale

        Render2D.renderItem(stack, x + 5, y + 5, 7f / scale)
    }

    fun open(anchor: AnchorType, index: Int) {
        activeAnchor = anchor
        activeIndex = index

        val existing = ButtonManager.getButtonAt(anchor, index)

        itemId = existing?.iconId.orEmpty()
        command = existing?.command.orEmpty()

        itemIdInput.value = itemId.takeIf { it.isNotBlank() } ?: itemIdInput.value
        commandInput.value = command.takeIf { it.isNotBlank() } ?: commandInput.value

        rect.show()
        shown = true
    }

    fun close() {
        activeAnchor = null
        activeIndex = 0
        rect.hide()
        shown = false
    }

    fun save() {
        val anchor = activeAnchor ?: return
        val index = activeIndex

        if (itemIdInput.value.isEmpty() || commandInput.value.isEmpty()) {
            ButtonManager.remove(anchor, index)
            close()
            return
        }

        val existing = ButtonManager.getButtonAt(anchor, index)

        if (existing != null) {
            existing.iconId = itemIdInput.value
            existing.command = commandInput.value
        } else {
            val newButton = StellaButton(
                anchor = anchor,
                index = index,
                iconId = itemIdInput.value,
                command = commandInput.value,
                background = true
            )
            ButtonManager.add(newButton)
        }

        close()
    }

    fun delete() {
        val anchor = activeAnchor ?: return
        val index = activeIndex

        ButtonManager.remove(anchor, index)

        close()
    }
}
