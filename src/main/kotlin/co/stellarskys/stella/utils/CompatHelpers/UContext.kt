package co.stellarskys.stella.utils.CompatHelpers

import co.stellarskys.stella.Stella
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

data class TransformState(
    var x: Float = 0f,
    var y: Float = 0f,
    var scaleX: Float = 1f,
    var scaleY: Float = 1f
)

class TransformTracker {
    private val stack = ArrayDeque<TransformState>().apply { addFirst(TransformState()) }

    fun push() = stack.addFirst(stack.first().copy())
    fun pop() = stack.removeFirst()
    fun translate(dx: Float, dy: Float) {
        val top = stack.first()
        top.x += dx
        top.y += dy
    }
    fun scale(sx: Float, sy: Float) {
        val top = stack.first()
        top.x *= sx
        top.y *= sy
        top.scaleX *= sx
        top.scaleY *= sy
    }
    fun current(): TransformState = stack.first()
}

object UMatrix {
    private val tracker = TransformTracker()

    fun pushMatrix() {
        GlStateManager.pushMatrix()
        tracker.push()
    }

    fun popMatrix() {
        GlStateManager.popMatrix()
        tracker.pop()
    }

    fun translate(x: Float, y: Float, z: Float) {
        GlStateManager.translate(x, y, z)
        tracker.translate(x, y)
    }

    fun scale(x: Float, y: Float, z: Float) {
        GlStateManager.scale(x, y, z)
        tracker.scale(x, y)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        GlStateManager.rotate(angle, x, y, z)
        // Rotation not tracked yet
    }

    fun currentTransform(): TransformState = tracker.current()
}

class DrawContext {
    val matrices = UMatrix

    fun enableScissor(x: Int, y: Int, width: Int, height: Int) {
        val mc = Stella.mc
        val scaled = ScaledResolution(mc)
        val scaleFactor = scaled.scaleFactor

        val transform = matrices.currentTransform()

        // Apply matrix transform
        val tx = ((x + transform.x) * transform.scaleX).toInt()
        val ty = ((y + transform.y) * transform.scaleY).toInt()
        val tw = (width * transform.scaleX).toInt()
        val th = (height * transform.scaleY).toInt()

        // Flip Y for OpenGL's bottom-left origin
        val glY = (scaled.scaledHeight - ty - th) * scaleFactor
        val glX = tx * scaleFactor
        val glW = tw * scaleFactor
        val glH = th * scaleFactor

        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        GL11.glScissor(glX, glY, glW, glH)
    }

    fun disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
}

typealias UDrawContext = DrawContext