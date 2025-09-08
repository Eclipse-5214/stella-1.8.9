package co.stellarskys.stella.features.msc

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.RenderEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.render.Render3D
import co.stellarskys.stella.utils.config
import co.stellarskys.stella.utils.config.RGBA
import net.minecraft.init.Blocks

@Stella.Module
object blockOverlay : Feature("overlayEnabled") {
    private val excludedBlocks = setOf(Blocks.air, Blocks.flowing_lava, Blocks.lava, Blocks.flowing_water, Blocks.water)

    override fun initialize() {
        register<RenderEvent.BlockHighlight> { event ->
            val block = event.blockPos.let { world?.getBlockState(it)?.block }
            if (block !in excludedBlocks) {
                event.cancel()

                val chroma = config["chromaHighlight"] as Boolean
                val outlineColor = if (chroma) RGBA(255,255,255,255) else config["blockHighlightColor"] as RGBA
                val outlineWidth = (config["overlayLineWidth"] as Int).toFloat()
                val fillColor = if (chroma) RGBA(255,255,255,255) else config["blockFillColor"] as RGBA

                Render3D.renderBlock(
                    event.blockPos,
                    event.partialTicks,
                    config["fillBlockOverlay"] as Boolean,
                    outlineColor.toColor(),
                    outlineWidth,
                    false
                )
            }
        }
    }
}