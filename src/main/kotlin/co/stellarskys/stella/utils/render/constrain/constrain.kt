package co.stellarskys.stella.utils.render.constrain

import xyz.meowing.vexel.components.base.Pos
import xyz.meowing.vexel.components.base.Size
import xyz.meowing.vexel.components.base.VexelElement

sealed class Constraint {
    data class SizeConstraint(val size: Size, val value: Float?) : Constraint()
    data class PosConstraint(val pos: Pos, val offset: Float?) : Constraint()
}

class ConstraintBuilder {
    var x: Constraint? = null
    var y: Constraint? = null
    var width: Constraint? = null
    var height: Constraint? = null

    fun applyTo(target: VexelElement<*>) {
        // Handle position
        val xConstraint = x as? Constraint.PosConstraint
        if (x != null && xConstraint == null) {
            error("x constraint must be a position constraint (e.g., 10.px, Pos.ScreenCenter)")
        }

        val yConstraint = y as? Constraint.PosConstraint
        if (y != null && yConstraint == null) {
            error("y constraint must be a position constraint (e.g., 10.px, Pos.ScreenCenter)")
        }

        if (xConstraint != null || yConstraint != null) {
            target.setPositioning(
                xConstraint?.offset ?: 0f,
                xConstraint?.pos ?: Pos.ParentPixels,
                yConstraint?.offset ?: 0f,
                yConstraint?.pos ?: Pos.ParentPixels
            )
        }

        // Handle size
        fun toSizeConstraint(constraint: Constraint?): Constraint.SizeConstraint? {
            return when (constraint) {
                null -> null
                is Constraint.SizeConstraint -> constraint
                is Constraint.PosConstraint -> {
                    val sizeType = when (constraint.pos) {
                        Pos.ParentPixels -> Size.Pixels
                        Pos.ParentPercent -> Size.ParentPerc
                        else -> error("Cannot use '${constraint.pos}' as a size constraint. Only 'px' and 'perc' are valid.")
                    }
                    Constraint.SizeConstraint(sizeType, constraint.offset)
                }
            }
        }

        val widthSize = toSizeConstraint(width)
        val heightSize = toSizeConstraint(height)

        if (widthSize != null || heightSize != null) {
            target.setSizing(
                widthSize?.value ?: 0f,
                widthSize?.size ?: Size.Auto,
                heightSize?.value ?: 0f,
                heightSize?.size ?: Size.Auto
            )
        }
    }

}


fun VexelElement<*>.constrain(block: ConstraintBuilder.() -> Unit): VexelElement<*> {
    val builder = ConstraintBuilder().apply(block)
    builder.applyTo(this)
    return this
}


val Float.px: Constraint get() = Constraint.PosConstraint(Pos.ParentPixels, this)
val Float.perc: Constraint get() = Constraint.PosConstraint(Pos.ParentPercent, this)

val Int.px: Constraint get() = toFloat().px
val Int.perc: Constraint get() = toFloat().perc

operator fun Pos.plus(offset: Float): Constraint = Constraint.PosConstraint(this, offset)
operator fun Pos.plus(offset: Int): Constraint = Constraint.PosConstraint(this, offset.toFloat())

val auto: Constraint get() = Constraint.SizeConstraint(Size.Auto, 0f)