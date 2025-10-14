package co.stellarskys.stella.features.msc

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.events.TablistEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.clearCodes

@Stella.Module
object petDisplay: Feature("petDisplay") {
    val petSummon = Regex("""You (summoned|despawned) your ([A-Za-z ]+)(?: ✦)?!""")
    val autoPet = Regex("""Autopet equipped your \[Lvl (\d+)] ([A-Za-z ]+)(?: ✦)?! VIEW RULE""")
    val tab = Regex("""\[Lvl (\d+)] ([A-Za-z ]+)(?: ✦)?""")

    var activePet: String? = null
    var activePetLvl = 0

    override fun initialize() {
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
                    }
                    "despawned" -> {
                        activePet = null
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

                // You can trigger logic here, e.g. update state or log
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

                }
            }
        }

    }
}