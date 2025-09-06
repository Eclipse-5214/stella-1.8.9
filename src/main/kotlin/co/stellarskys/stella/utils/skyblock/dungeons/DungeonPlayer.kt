package co.stellarskys.stella.utils.skyblock.dungeons

import net.minecraft.util.ResourceLocation
import net.minecraft.client.resources.DefaultPlayerSkin

class DungeonPlayer(val name: String) {
    var inRender: Boolean = false

    var iconX: Double? = null
    var iconZ: Double? = null
    var rotation: Float? = null
    var realX: Double? = null
    var realZ: Double? = null
    var currentRoom: Room? = null
    var skin: ResourceLocation = DefaultPlayerSkin.getDefaultSkinLegacy()

    var hat: Boolean = false

    val visitedRooms = mutableMapOf<Room, Long>() // replace with actual types if needed


    val clearedRooms = mutableMapOf(
        "WHITE" to mutableMapOf<String, DungeonScanner.RoomClearInfo>(), // using enum or some identifier instead of string
        "GREEN" to mutableMapOf<String, DungeonScanner.RoomClearInfo>()
    )

    var deaths: Int = 0
    var minRooms: Int = 0
    var maxRooms: Int = 0
    var secrets: Int = 0

    var lastRoomCheck: Long? = null
    var lastRoom: Room? = null


    fun getGreenChecks(): MutableMap<String, DungeonScanner.RoomClearInfo> =
        clearedRooms["GREEN"] ?: mutableMapOf()

    fun getWhiteChecks(): MutableMap<String, DungeonScanner.RoomClearInfo> =
        clearedRooms["WHITE"] ?: mutableMapOf()

    override fun toString(): String {
        return "DungeonPlayer[iconX: $iconX, iconZ: $iconZ, rotation: $rotation, realX: $realX, realZ: $realZ, currentRoom: $currentRoom]"
    }
}