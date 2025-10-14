package co.stellarskys.stella.features.dungeons

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.RenderEvent
import co.stellarskys.stella.events.WorldEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.config
import co.stellarskys.stella.utils.config.RGBA
import co.stellarskys.stella.utils.render.Render3D
import co.stellarskys.stella.utils.skyblock.LocationUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30

@Stella.Module
object dungeonBlockOverlay : Feature("enableDungBlockOverlay") {
    private var framebufferBlocksTo: Framebuffer? = null
    private var framebufferBlocksFrom: Framebuffer? = null

    private val projectionMatrixOld = BufferUtils.createFloatBuffer(16)
    private val modelviewMatrixOld = BufferUtils.createFloatBuffer(16)

    private val framebuffersDynamicTo = mutableMapOf<String, Framebuffer>()
    private val framebuffersDynamicFrom = mutableMapOf<String, Framebuffer>()
    private val dynamicUpdated = mutableSetOf<String>()

    private val dynamicPreloadMap = mapOf(
        ResourceLocation("textures/entity/bat.png") to "dungBatColour",
        ResourceLocation("textures/entity/chest/normal.png") to "dungChestColour",
        ResourceLocation("textures/entity/chest/normal_double.png") to "dungChestColour",
        ResourceLocation("textures/entity/chest/trapped.png") to "dungTrappedChestColour",
        ResourceLocation("textures/entity/chest/trapped_double.png") to "dungTrappedChestColour"
    )

    val batColor get() = (config["dungBatColour"] as? RGBA ?: RGBA(0,255,0)).toColor().rgb
    val chestColor get() = (config["dungChestColour"] as? RGBA ?: RGBA(0,255,255)).toColor().rgb
    val trappedChestColor get() = (config["dungTrappedChestColour"] as? RGBA ?: RGBA( 255, 0, 0)).toColor().rgb


    override fun initialize() {
        register<RenderEvent.World> {
            tick()
        }
        register<WorldEvent.Unload> {
            framebufferBlocksTo = null
            framebufferBlocksFrom = null
        }
    }

    override fun onUnregister() {
        framebufferBlocksTo = null
        framebufferBlocksFrom = null
    }

    fun textureExists(): Boolean {
        return framebufferBlocksFrom != null && isOverriding()
    }

    fun bindTextureIfExists() {
        if (textureExists()) {
            framebufferBlocksFrom?.bindFramebufferTexture()
        }
    }

     fun isOverriding(): Boolean {
        val everywhere = config["dungeonBlocksEverywhere"] as? Boolean ?: false
        val location = LocationUtils.area
        return everywhere || location == "catacombs"
    }

    private fun tick() {
        if (!isOverriding() || Stella.mc.theWorld == null) return

        dynamicUpdated.clear()

        // Preload entity overlays
        dynamicPreloadMap.forEach { (loc, key) ->
            val rgba = (config[key] as? RGBA) ?: return@forEach
            bindModifiedEntityTexture(loc, rgba.toColor().rgb)
        }

        val texManager = Stella.mc.textureManager
        texManager.bindTexture(TextureMap.locationBlocksTexture)

        val w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)

        val to = checkFramebufferSizes(framebufferBlocksTo, w, h)

        try {
            GL11.glPushMatrix()

            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.loadIdentity()
            GlStateManager.ortho(0.0, w.toDouble(), h.toDouble(), 0.0, 1000.0, 3000.0)
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.loadIdentity()
            GlStateManager.translate(0.0f, 0.0f, -2000.0f)

            to.bindFramebuffer(true)
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT)

            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableFog()

            texManager.bindTexture(TextureMap.locationBlocksTexture)
            GlStateManager.color(1f, 1f, 1f, 1f)
            Render3D.drawTexturedRectNoBlend(0f, 0f, w.toFloat(), h.toFloat(), 0f, 1f, 1f, 0f, GL11.GL_LINEAR)

            // Build sprite map from config
            val crackedColor = ((config["dungCrackedColour"] as? RGBA) ?: RGBA(255, 0, 255, 255)).toColor().rgb
            val dispenserColor = ((config["dungDispenserColour"] as? RGBA) ?: RGBA(255, 255, 0, 255)).toColor().rgb
            val leverColor = ((config["dungLeverColour"] as? RGBA) ?: RGBA(0, 255, 0, 255)).toColor().rgb
            val tripwireColor = ((config["dungTripWireColour"] as? RGBA) ?: RGBA(0, 255, 255, 255)).toColor().rgb

            val spriteMap = mapOf(
                Stella.mc.textureMapBlocks.getAtlasSprite("minecraft:blocks/stonebrick_cracked") to crackedColor,
                Stella.mc.textureMapBlocks.getAtlasSprite("minecraft:blocks/dispenser_front_horizontal") to dispenserColor,
                Stella.mc.textureMapBlocks.getAtlasSprite("minecraft:blocks/lever") to leverColor,
                Stella.mc.textureMapBlocks.getAtlasSprite("minecraft:blocks/trip_wire") to tripwireColor
            )

            spriteMap.forEach { (sprite, color) ->
                if (((color shr 24) and 0xFF) < 10) return@forEach
                Gui.drawRect(
                    (w * sprite.minU).toInt(),
                    h - (h * sprite.maxV).toInt() - 1,
                    (w * sprite.maxU).toInt() + 1,
                    h - (h * sprite.minV).toInt(),
                    color
                )
            }

            // Restore scaled resolution projection
            val scaled = ScaledResolution(Stella.mc)
            GlStateManager.matrixMode(GL11.GL_PROJECTION)
            GlStateManager.loadIdentity()
            GlStateManager.ortho(
                0.0, scaled.scaledWidth_double,
                scaled.scaledHeight_double, 0.0,
                1000.0, 3000.0
            )
            GlStateManager.matrixMode(GL11.GL_MODELVIEW)
            GlStateManager.loadIdentity()
            GlStateManager.translate(0.0f, 0.0f, -2000.0f)

            GL11.glPopMatrix()

            to.bindFramebufferTexture()
            if (Stella.mc.gameSettings.mipmapLevels >= 0) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, Stella.mc.gameSettings.mipmapLevels)
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0f)
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, Stella.mc.gameSettings.mipmapLevels.toFloat())
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0f)
                GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            }

            val from = checkFramebufferSizes(framebufferBlocksFrom, w, h)
            framebufferBlocksFrom = to
            framebufferBlocksTo = from

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Stella.mc.framebuffer.bindFramebuffer(true)
        GlStateManager.enableBlend()
    }

    fun bindModifiedEntityTexture(location: ResourceLocation, color: Int): Boolean {
        if (!isOverriding()) return false
        if (((color shr 24) and 0xFF) < 10) return false

        val id = "${location.resourceDomain}:${location.resourcePath}"
        if (dynamicUpdated.contains(id) && framebuffersDynamicFrom.containsKey(id)) {
            framebuffersDynamicFrom[id]?.bindFramebufferTexture()
            return true
        }

        val texManager = Stella.mc.textureManager
        texManager.bindTexture(location)

        val w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)

        val to = checkFramebufferSizes(framebuffersDynamicTo[id], w, h)
        dynamicUpdated.add(id)

        // Save matrices
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixOld)
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelviewMatrixOld)

        GL11.glPushMatrix()
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.loadIdentity()
        GlStateManager.ortho(0.0, w.toDouble(), h.toDouble(), 0.0, 1000.0, 3000.0)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.loadIdentity()
        GlStateManager.translate(0.0f, 0.0f, -2000.0f)

        to.bindFramebuffer(true)
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT)
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableFog()

        texManager.bindTexture(location)
        GlStateManager.color(1f, 1f, 1f, 1f)
        Render3D.drawTexturedRectNoBlend(0f, 0f, w.toFloat(), h.toFloat(), 0f, 1f, 1f, 0f, GL11.GL_LINEAR)

        GlStateManager.enableBlend()
        GL14.glBlendFuncSeparate(
            GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
            GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA
        )
        Render3D.drawRectNoBlend(0, 0, w, h, color)

        GL11.glPopMatrix()

        // Restore matrices
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GL11.glLoadMatrix(projectionMatrixOld)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GL11.glLoadMatrix(modelviewMatrixOld)

        to.bindFramebufferTexture()
        if (Stella.mc.gameSettings.mipmapLevels >= 0) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        }

        val from = checkFramebufferSizes(framebuffersDynamicFrom[id], w, h)
        framebuffersDynamicFrom[id] = to
        framebuffersDynamicTo[id] = from

        Stella.mc.framebuffer.bindFramebuffer(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        return true
    }

    private fun checkFramebufferSizes(fb: Framebuffer?, w: Int, h: Int): Framebuffer {
        return if (fb == null || fb.framebufferWidth != w || fb.framebufferHeight != h) {
            val newFb = Framebuffer(w, h, false)
            newFb.setFramebufferFilter(GL11.GL_NEAREST)
            newFb
        } else {
            fb
        }
    }
}