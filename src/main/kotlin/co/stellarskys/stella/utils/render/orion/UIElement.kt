package co.stellarskys.stella.utils.render.orion

abstract class UIElement {
    var width: Float = 100f
    var height: Float = 20f
    var parent: UIElement? = null
    val children = mutableListOf<UIElement>()

    private var xConstraint: UIConstraint = UIConstraint.Absolute(0f)
    private var yConstraint: UIConstraint = UIConstraint.Absolute(0f)

    var x: Any
        get() = resolvedX
        set(value) {
            xConstraint = when (value) {
                is Float -> UIConstraint.Absolute(value)
                is Int -> UIConstraint.Absolute(value.toFloat())
                is UIConstraint -> value
                else -> throw IllegalArgumentException("x must be Float, Int, or UIConstraint")
            }
        }

    var y: Any
        get() = resolvedY
        set(value) {
            yConstraint = when (value) {
                is Float -> UIConstraint.Absolute(value)
                is Int -> UIConstraint.Absolute(value.toFloat())
                is UIConstraint -> value
                else -> throw IllegalArgumentException("y must be Float, Int, or UIConstraint")
            }
        }

    private var resolvedX: Float = 0f
    private var resolvedY: Float = 0f

    val xPos: Float get() = resolvedX
    val yPos: Float get() = resolvedY

    open fun applyConstraints(previousSibling: UIElement?, screenWidth: Int, screenHeight: Int) {
        resolvedX = when (val c = xConstraint) {
            is UIConstraint.Relative -> {
                val baseX = parent?.resolvedX ?: 0f
                val baseW = parent?.width ?: screenWidth.toFloat()
                if (c.fromEnd) baseX + baseW - width - c.offset
                else baseX + c.offset
            }
            is UIConstraint.Absolute -> c.position
            is UIConstraint.Sibling -> previousSibling?.resolvedX?.plus(previousSibling.width)?.plus(c.offset) ?: 0f
            UIConstraint.Center -> {
                val baseX = parent?.resolvedX ?: 0f
                val baseW = parent?.width ?: screenWidth.toFloat()
                baseX + (baseW - width) / 2f
            }
        }

        resolvedY = when (val c = yConstraint) {
            is UIConstraint.Relative -> {
                val baseY = parent?.resolvedY ?: 0f
                val baseH = parent?.height ?: screenHeight.toFloat()
                if (c.fromEnd) baseY + baseH - height - c.offset
                else baseY + c.offset
            }
            is UIConstraint.Absolute -> c.position
            is UIConstraint.Sibling -> previousSibling?.resolvedY?.plus(previousSibling.height)?.plus(c.offset) ?: 0f
            UIConstraint.Center -> {
                val baseY = parent?.resolvedY ?: 0f
                val baseH = parent?.height ?: screenHeight.toFloat()
                baseY + (baseH - height) / 2f
            }
        }
    }

    fun add(child: UIElement) {
        child.parent = this
        children += child
    }

    open fun render() {
        var previous: UIElement? = null
        for (child in children) {
            child.applyConstraints(previous, screenWidth = parent?.width?.toInt() ?: 0, screenHeight = parent?.height?.toInt() ?: 0)
            child.render()
            previous = child
        }
    }

    open fun mouseClicked(mx: Int, my: Int, button: Int) {
        children.forEach { it.mouseClicked(mx, my, button) }
    }

    open fun keyTyped(char: Char, keyCode: Int) {
        children.forEach { it.keyTyped(char, keyCode) }
    }

    fun contains(mx: Int, my: Int): Boolean =
        mx in resolvedX.toInt()..(resolvedX + width).toInt() && my in resolvedY.toInt()..(resolvedY + height).toInt()
}

infix fun UIElement.childOf(parent: UIElement): UIElement {
    parent.add(this)
    return this
}
