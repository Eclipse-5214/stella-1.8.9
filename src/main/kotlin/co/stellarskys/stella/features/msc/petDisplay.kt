package co.stellarskys.stella.features.msc

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.events.RenderEvent
import co.stellarskys.stella.events.WorldEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.features.stellanav.utils.mapConfig
import co.stellarskys.stella.utils.render.Render3D
import co.stellarskys.stella.utils.skyblock.dungeons.DoorState
import co.stellarskys.stella.utils.skyblock.dungeons.DoorType
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner
import co.stellarskys.stella.utils.clearCodes

@Stella.Module
object petDisplay: Feature("petDisplay") {
    val petSummon = Regex("""You (summoned|despawned) your ([A-Za-z ]+)(?: ✦)?!""")
    val autoPet = Regex("""Autopet equipped your \[Lvl (\d+)] ([A-Za-z ]+)(?: ✦)?! VIEW RULE""")


    override fun initialize() {
        register<ChatEvent.Receive> { event ->
            val msg = event.message.string.clearCodes()

            // Pet summon/despawn matcher
            val summonMatch = petSummon.find(msg)
            if (summonMatch != null) {
                val action = summonMatch.groupValues[1] // "summoned" or "despawned"
                val petName = summonMatch.groupValues[2].trim()
                Stella.LOGGER.info("Pet $action: $petName")
                // You can trigger logic here, e.g. update state or log
                return@register
            }

            // Autopet matcher
            val autoMatch = autoPet.find(msg)
            if (autoMatch != null) {
                val level = autoMatch.groupValues[1].toInt()
                val petName = autoMatch.groupValues[2].trim()
                Stella.LOGGER.info("Autopet equipped: Lvl $level $petName")
                // You can trigger logic here, e.g. update state or log
                return@register
            }
        }

    }
}