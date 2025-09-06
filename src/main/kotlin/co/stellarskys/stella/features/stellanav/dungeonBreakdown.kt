package co.stellarskys.stella.features.stellanav

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.ChatUtils
import co.stellarskys.stella.utils.TickUtils
import co.stellarskys.stella.utils.clearCodes
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner
import kotlin.math.min

@Stella.Module
class dungeonBreakdown(): Feature("dungeonBreakdown", "catacombs") {
    override fun initialize() {
        val completeRegex = Regex("""^\s*(Master Mode)?\s?(?:The)? Catacombs - (Entrance|Floor .{1,3})$""")

        register<ChatEvent.Receive> { event ->
            val msg = event.message.string.trim().clearCodes()
            val match = completeRegex.find(msg) ?: return@register

            ChatUtils.addMessage(Stella.PREFIX + "Cleared room counts:")

            TickUtils.schedule(2 * 20 ) {
                DungeonScanner.players.forEach { player ->
                    val name = player.name
                    val secrets = player.secrets
                    val minmax = "${player.minRooms}-${player.maxRooms}"
                    val deaths = player.deaths

                    ChatUtils.addMessage("§d| §b$name §fcleared §b$minmax &frooms | §b$secrets §fsecrets | §b$deaths §f deaths")
                }
            }
        }
    }
}