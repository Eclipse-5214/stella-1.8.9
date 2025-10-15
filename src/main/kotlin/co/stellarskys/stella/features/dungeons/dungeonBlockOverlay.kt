package co.stellarskys.stella.features.dungeons

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.TickEvent
import co.stellarskys.stella.events.WorldEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.config
import co.stellarskys.stella.utils.config.RGBA
import co.stellarskys.stella.utils.render.Render3D
import co.stellarskys.stella.utils.skyblock.LocationUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30

@Stella.Module // Hevily Inspired by NEU. https://github.com/NotEnoughUpdates/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/dungeons/DungeonBlocks.java
object dungeonBlockOverlay : Feature("enableDungBlockOverlay") {

    private var fbBlocksTo: Framebuffer? = null
    private var fbBlocksFrom: Framebuffer? = null
    private val proj = BufferUtils.createFloatBuffer(16)
    private val model = BufferUtils.createFloatBuffer(16)

    private val dynTo = mutableMapOf<String, Framebuffer>()
    private val dynFrom = mutableMapOf<String, Framebuffer>()
    private val dynUpdated = mutableSetOf<String>()

    private val preload = mapOf(
        ResourceLocation("textures/entity/bat.png") to "dungBatColour",
        ResourceLocation("textures/entity/chest/normal.png") to "dungChestColour",
        ResourceLocation("textures/entity/chest/normal_double.png") to "dungChestColour",
        ResourceLocation("textures/entity/chest/trapped.png") to "dungTrappedChestColour",
        ResourceLocation("textures/entity/chest/trapped_double.png") to "dungTrappedChestColour"
    )

    // Mixin color getters (kept public)
    val batColor get() = (rgba("dungBatColour") ?: RGBA(0,255,0)).toColorInt()
    val chestColor get() = (rgba("dungChestColour") ?: RGBA(0,255,255)).toColorInt()
    val trappedChestColor get() = (rgba("dungTrappedChestColour") ?: RGBA(255,0,0)).toColorInt()

    override fun initialize() {
        register<TickEvent.Client> { tick() }
        register<WorldEvent.Unload> { reset() }
    }
    override fun onUnregister() { reset() }

    fun textureExists() = fbBlocksFrom != null && isOverriding()
    fun bindTextureIfExists() { fbBlocksFrom?.bindFramebufferTexture() }

    fun isOverriding(): Boolean {
        val enabled = config["enableDungBlockOverlay"] as? Boolean ?: false
        val everywhere = config["dungeonBlocksEverywhere"] as? Boolean ?: false
        val location = LocationUtils.area
        return enabled && (everywhere || location == "catacombs")
    }

    private fun tick() {
        if (!isOverriding() || Stella.mc.theWorld == null) return

        // Preload entity overlays (parity with NEU)
        dynUpdated.clear()
        preload.forEach { (loc, key) -> rgba(key)?.toColorInt()?.let { if (visible(it)) bindModifiedEntityTexture(loc, it) } }

        val tm = Stella.mc.textureManager
        tm.bindTexture(TextureMap.locationBlocksTexture)
        val w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
        val to = fb(fbBlocksTo, w, h)

        try {
            pushOrtho(w, h)
            to.bindFramebuffer(true)
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT)
            disableSceneState()

            tm.bindTexture(TextureMap.locationBlocksTexture)
            GlStateManager.color(1f, 1f, 1f, 1f)
            Render3D.drawTexturedRectNoBlend(0f, 0f, w.toFloat(), h.toFloat(), 0f, 1f, 1f, 0f, GL11.GL_LINEAR)

            listOf(
                "minecraft:blocks/stonebrick_cracked" to "dungCrackedColour",
                "minecraft:blocks/dispenser_front_horizontal" to "dungDispenserColour",
                "minecraft:blocks/lever" to "dungLeverColour",
                "minecraft:blocks/trip_wire" to "dungTripWireColour"
            ).forEach { (id, key) ->
                val c = rgba(key)?.toColorInt() ?: return@forEach
                if (!visible(c)) return@forEach
                val s = Stella.mc.textureMapBlocks.getAtlasSprite(id)
                Gui.drawRect((w * s.minU).toInt(), h - (h * s.maxV).toInt() - 1, (w * s.maxU).toInt() + 1, h - (h * s.minV).toInt(), c)
            }

            GL11.glPopMatrix()
            to.bindFramebufferTexture()
            genMipmaps()

            val from = fb(fbBlocksFrom, w, h)
            fbBlocksFrom = to; fbBlocksTo = from
        } catch (_: Exception) { /* keep quiet */ }

        Stella.mc.framebuffer.bindFramebuffer(true)
        GlStateManager.enableBlend()
    }

    fun bindModifiedEntityTexture(location: ResourceLocation, color: Int): Boolean {
        if (!isOverriding() || !visible(color)) return false
        val id = "${location.resourceDomain}:${location.resourcePath}"

        dynFrom[id]?.takeIf { dynUpdated.contains(id) }?.let {
            it.bindFramebufferTexture(); return true
        }

        val tm = Stella.mc.textureManager
        tm.bindTexture(location)
        val w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)
        val h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)
        val to = fb(dynTo[id], w, h); dynUpdated.add(id)

        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, proj)
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, model)
        pushOrtho(w, h)
        to.bindFramebuffer(true)
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT)
        disableSceneState()

        tm.bindTexture(location)
        GlStateManager.color(1f, 1f, 1f, 1f)
        Render3D.drawTexturedRectNoBlend(0f, 0f, w.toFloat(), h.toFloat(), 0f, 1f, 1f, 0f, GL11.GL_LINEAR)

        GlStateManager.enableBlend()
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA)
        Render3D.drawRectNoBlend(0, 0, w, h, color)

        GL11.glPopMatrix()
        restoreMatrices()
        to.bindFramebufferTexture()
        genMipmaps()

        val from = fb(dynFrom[id], w, h)
        dynFrom[id] = to; dynTo[id] = from

        Stella.mc.framebuffer.bindFramebuffer(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        return true
    }

    // ——— helpers ———

    private fun rgba(key: String) = config[key] as? RGBA
    private fun visible(color: Int) = ((color ushr 24) and 0xFF) >= 10

    private fun fb(fb: Framebuffer?, w: Int, h: Int): Framebuffer =
        fb?.apply {
            if (framebufferWidth != w || framebufferHeight != h) {
                createBindFramebuffer(w, h); setFramebufferFilter(GL11.GL_NEAREST)
            }
        }
            ?: Framebuffer(w, h, false).apply { setFramebufferFilter(GL11.GL_NEAREST) }

    private fun pushOrtho(w: Int, h: Int) {
        GL11.glPushMatrix()
        GlStateManager.matrixMode(GL11.GL_PROJECTION); GlStateManager.loadIdentity()
        GlStateManager.ortho(0.0, w.toDouble(), h.toDouble(), 0.0, 1000.0, 3000.0)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW); GlStateManager.loadIdentity()
        GlStateManager.translate(0.0f, 0.0f, -2000.0f)
    }

    private fun disableSceneState() {
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableFog()
    }

    private fun genMipmaps() {
        if (Stella.mc.gameSettings.mipmapLevels >= 0) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, Stella.mc.gameSettings.mipmapLevels)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0f)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, Stella.mc.gameSettings.mipmapLevels.toFloat())
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0f)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        }
    }

    private fun restoreMatrices() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION); GL11.glLoadMatrix(proj)
        GlStateManager.matrixMode(GL11.GL_MODELVIEW); GL11.glLoadMatrix(model)
    }

    private fun reset() {
        fbBlocksTo = null; fbBlocksFrom = null
        dynTo.clear(); dynFrom.clear(); dynUpdated.clear()
    }
}
