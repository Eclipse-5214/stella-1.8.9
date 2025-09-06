package co.stellarskys.stella.features.stellanav

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.ChatUtils
import co.stellarskys.stella.utils.clearCodes
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner

@Stella.Module
class dungeonBreakdown(): Feature("dungeonBreakdown", "catacombs") {
    override fun initialize() {
        val completeRegex = Regex("""^\s*(Master Mode)?\s?(?:The)? Catacombs - (Entrance|Floor .{1,3})$""")

        register<ChatEvent.Receive> { event ->
            val msg = event.message.string.clearCodes()
            val match = completeRegex.find(msg) ?: return@register

            ChatUtils.addMessage(Stella.PREFIX + "Cleared room counts:")

            DungeonScanner.players.forEach { player ->
                ChatUtils.addMessage("§d| §b${player.name} §fcleared ")
            }
        }
    }
}