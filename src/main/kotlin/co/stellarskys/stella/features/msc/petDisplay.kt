package co.stellarskys.stella.features.msc

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.events.RenderEvent
import co.stellarskys.stella.events.TablistEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.hud.HUDManager
import co.stellarskys.stella.utils.CompatHelpers.UDrawContext
import co.stellarskys.stella.utils.DataUtils
import co.stellarskys.stella.utils.clearCodes
import co.stellarskys.stella.utils.render.Render2D
import co.stellarskys.stella.utils.skyblock.NEUApi

@Stella.Module
object petDisplay: Feature("petDisplay") {
    const val name = "Pet Display"

    val petSummon = Regex("""You (summoned|despawned) your ([A-Za-z ]+)(?: ✦)?!""")
    val autoPet = Regex("""Autopet equipped your \[Lvl (\d+)] ([A-Za-z ]+)(?: ✦)?! VIEW RULE""")
    val tab = Regex("""\[Lvl (\d+)] ([A-Za-z ]+)(?: ✦)?""")

    var activePet: String? = null
    var activePetLvl = 0

    data class PetCache(
        val levels: MutableMap<String, Int> = mutableMapOf(),
        var lastActiveName: String? = null,
        var lastActiveLevel: Int = 0
    )

    val petCache = DataUtils("petCache", PetCache())

    fun cachePet(petName: String, level: Int) {
        petCache.update {
            val current = levels[petName]
            if (current == null || level != current) {
                levels[petName] = level
                Stella.LOGGER.info("Cached pet: $petName → Lvl $level")
            }
            // also update last active
            lastActiveName = petName
            lastActiveLevel = level
        }
    }

    fun getCachedLevel(petName: String): Int? = petCache().levels[petName]
    fun getAllCachedPets(): Map<String, Int> = petCache().levels
    fun getLastActivePet(): Pair<String, Int>? {
        val cache = petCache()
        return cache.lastActiveName?.let { it to cache.lastActiveLevel }
    }


    override fun initialize() {
        HUDManager.registerCustom(name, 120, 30,this::HUDEditorRender)

        getLastActivePet()?.let { (name, lvl) ->
            activePet = name
            activePetLvl = lvl
            Stella.LOGGER.info("Restored last active pet from cache: $name Lvl $lvl")
        }

        register<ChatEvent.Receive> { event ->
            val msg = event.message.string.clearCodes()

            // Pet summon/despawn matcher
            val summonMatch = petSummon.find(msg)
            if (summonMatch != null) {
                val action = summonMatch.groupValues[1] // "summoned" or "despawned"
                val petName = summonMatch.groupValues[2].trim()
                Stella.LOGGER.info("Pet $action: $petName")

                when (action) {
                    "summoned" -> {
                        activePet = petName
                        activePetLvl = getCachedLevel(petName) ?: 0
                    }
                    "despawned" -> {
                        activePet = null
                        activePetLvl = 0
                    }
                }


                return@register
            }

            // Autopet matcher
            val autoMatch = autoPet.find(msg)
            if (autoMatch != null) {
                val level = autoMatch.groupValues[1].toInt()
                val petName = autoMatch.groupValues[2].trim()
                Stella.LOGGER.info("Autopet equipped: Lvl $level $petName")
                activePet = petName
                activePetLvl = level
                cachePet(petName, level)

                return@register
            }
        }

        register<TablistEvent> { tabEvent ->
            tabEvent.packet.entries.forEach { entry ->
                val text = entry.displayName?.unformattedText?.clearCodes() ?: return@forEach

                val tabMatch = tab.find(text)
                if (tabMatch != null) {
                    val level = tabMatch.groupValues[1].toInt()
                    val petName = tabMatch.groupValues[2].trim()
                    Stella.LOGGER.info("tablist equipped: Lvl $level $petName")
                    activePet = petName
                    activePetLvl = level
                    cachePet(petName, level)

                }
            }
        }

        register<RenderEvent.Text> { event ->
            if (HUDManager.isEnabled(name)) renderHud(event.context)
        }
    }

    fun HUDEditorRender(
        context: UDrawContext,
        x: Float, y: Float,
        width: Int, height: Int, scale: Float,
        partialTicks: Float, previewMode: Boolean
    ){
        val matrix = context.matrices

        matrix.pushMatrix()
        matrix.translate(x, y, 0f)

        Render2D.drawString("§bEnder Dragon", 40, 7)
        Render2D.drawString("§7[Lvl 100]", 40, 17)

        val neuItem = NEUApi.getItemBySkyblockId("ender_dragon;4") ?: return
        val stack = NEUApi.createDummyStack(neuItem)

        Render2D.renderItem(stack, 0f, -5f, 2.5f)

        matrix.popMatrix()
    }

    fun renderHud(context: UDrawContext) {
        if (activePet == null) return
        val matrix = context.matrices

        val x = HUDManager.getX(name)
        val y = HUDManager.getY(name)
        val scale = HUDManager.getScale(name)

        matrix.pushMatrix()
        matrix.translate(x, y,0f)
        matrix.scale(scale, scale, 1f)

        Render2D.drawString("§b$activePet", 40, 7)
        Render2D.drawString("§7[Lvl $activePetLvl]", 40, 17)

        val neuItem = NEUApi.getItemBySkyblockId("${activePet?.replace(" ", "_")};4") ?: return
        val stack = NEUApi.createDummyStack(neuItem)

        Render2D.renderItem(stack, 0f, -5f, 2.5f)

        matrix.popMatrix()
    }

}