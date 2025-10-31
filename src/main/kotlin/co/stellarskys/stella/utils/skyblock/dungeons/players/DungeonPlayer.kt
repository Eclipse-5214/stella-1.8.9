package co.stellarskys.stella.utils.skyblock.dungeons.players

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.skyblock.HypixelApi
import co.stellarskys.stella.utils.skyblock.dungeons.map.MapScanner.RoomClearInfo
import co.stellarskys.stella.utils.skyblock.dungeons.map.Room
import co.stellarskys.stella.utils.skyblock.dungeons.utils.DungeonClass
import net.minecraft.entity.player.EntityPlayer
import java.util.*

class DungeonPlayer(val name: String) {
    // position
    var iconX: Double? = null
    var iconZ: Double? = null
    var realX: Double? = null
    var realZ: Double? = null
    var yaw: Float? = null

    // score stuff
    var deaths = 0
    var minRooms = 0
    var maxRooms = 0
    var dclass = DungeonClass.UNKNOWN
    val alive get() = dclass != DungeonClass.DEAD;

    private var initSecrets: Int? = null
    private var currSecrets: Int? = null
    val secrets get() = currSecrets!! - initSecrets!!

    // api
    var uuid: UUID? = null
    var entity: EntityPlayer? = null
    var inRender = false

    var currRoom: Room? = null
    var lastRoom: Room? = null

    val clearedRooms = mutableMapOf(
        "WHITE" to mutableMapOf<String, RoomClearInfo>(),
        "GREEN" to mutableMapOf<String, RoomClearInfo>()
    )

    init {
        findPlayerAndUUID(name)?.let { (id, p) ->
            uuid = id
            entity = p
        }

        HypixelApi.fetchSecrets(uuid.toString(), 120_000) { secrets ->
            initSecrets = secrets
            currSecrets = secrets
        }
    }

    private fun findPlayerAndUUID(name: String): Pair<UUID, EntityPlayer>? {
        val world = Stella.mc.theWorld ?: return null
        val player = world.playerEntities
            .filterIsInstance<EntityPlayer>()
            .find { it.name == name } ?: return null
        return Pair(player.uniqueID, player)
    }

    fun updateSecrets() {
        if (uuid == null) return

        HypixelApi.fetchSecrets(uuid.toString(), cacheMs = 0) { secrets ->
            currSecrets = secrets
        }
    }

    fun getGreenChecks(): MutableMap<String, RoomClearInfo> = clearedRooms["GREEN"] ?: mutableMapOf()
    fun getWhiteChecks(): MutableMap<String, RoomClearInfo> = clearedRooms["WHITE"] ?: mutableMapOf()
}