package co.stellarskys.stella.utils.render.orion

sealed class UIConstraint {
    data class Relative(
        val offset: Float,
        val fromEnd: Boolean = false
    ) : UIConstraint()

    data class Absolute(val position: Float) : UIConstraint()
    data class Sibling(val offset: Float) : UIConstraint()
    object Center : UIConstraint()
}
