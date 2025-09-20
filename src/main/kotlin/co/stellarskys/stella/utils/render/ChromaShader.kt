package co.stellarskys.stella.utils.render

import co.stellarskys.stella.Stella
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

object ChromaShader {
    private var programId = -1

    fun init(vertexSource: String, fragmentSource: String) {
        val vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource)

        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexShader)
        GL20.glAttachShader(programId, fragmentShader)
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)

        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetProgramInfoLog(programId, 1024)
            throw RuntimeException("Shader link error: $log")
        }
    }

    fun bind() {
        GL20.glUseProgram(programId)
    }

    fun unbind() {
        GL20.glUseProgram(0)
    }

    fun setUniforms(
        chromaSize: Float,
        saturation: Float,
        brightness: Float,
        timeOffset: Float,
        alpha: Float,
        playerPos: Vec3
    ) {

        GL20.glUniform1f(getUniform("chromaSize"), chromaSize)
        GL20.glUniform1f(getUniform("timeOffset"), timeOffset)
        GL20.glUniform1f(getUniform("saturation"), saturation)
        GL20.glUniform1f(getUniform("brightness"), brightness)
        GL20.glUniform1f(getUniform("alpha"), alpha)

        GL20.glUniform3f(getUniform("playerWorldPosition"), playerPos.xCoord.toFloat(), playerPos.yCoord.toFloat(), playerPos.zCoord.toFloat())
    }

    fun setAlpha(alpha: Float) {
        GL20.glUniform1f(getUniform("alpha"), alpha)
    }

    private fun getUniform(name: String): Int {
        return GL20.glGetUniformLocation(programId, name)
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GL20.glCreateShader(type)
        GL20.glShaderSource(shader, source)
        GL20.glCompileShader(shader)

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetShaderInfoLog(shader, 1024)
            throw RuntimeException("Shader compile error: $log")
        }

        return shader
    }

    fun loadShaderSource(path: String): String {
        val location = ResourceLocation(Stella.NAMESPACE, path)
        return try {
            Stella.mc.resourceManager.getResource(location).inputStream.bufferedReader().use {
                it.readText()
            }
        } catch (e: Exception) {
            Stella.LOGGER.error("Failed to load shader from $location", e)
            throw e
        }
    }

}
