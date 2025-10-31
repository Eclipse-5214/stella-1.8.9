package co.stellarskys.stella.utils.skyblock.dungeons.utils

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.EventBus
import co.stellarskys.stella.events.PacketEvent
import co.stellarskys.stella.events.TickEvent
import co.stellarskys.stella.utils.skyblock.dungeons.Dungeon
import co.stellarskys.stella.utils.skyblock.dungeons.Dungeon.inBoss
import co.stellarskys.stella.utils.skyblock.dungeons.map.MapScanner
import co.stellarskys.stella.utils.skyblock.dungeons.utils.ScanUtils.roomDoorCombinedSize
import net.minecraft.item.ItemMap
import net.minecraft.network.play.server.S34PacketMaps
import net.minecraft.util.Vec4b
import net.minecraft.world.storage.MapData

object MapUtils {
    val Vec4b.mapX get() = (this.func_176112_b() + 128) shr 1
    val Vec4b.mapZ get() = (this.func_176113_c() + 128) shr 1
    val Vec4b.yaw get() = this.func_176111_d() * 22.5f

    var mapCorners = Pair(5, 5)
    var mapRoomSize = 16
    var mapGapSize = 0
    var coordMultiplier = 0.625
    var calibrated = false

    var mapData: MapData? = null
    var guessMapData: MapData? = null

    fun init() {
        EventBus.register<PacketEvent.Received> { event ->
            if (!Dungeon.inDungeon) return@register

            if (event.packet is S34PacketMaps && mapData == null) {
                val world = Stella.mc.theWorld ?: return@register
                val id = event.packet.mapId
                if (id and 1000 == 0) {
                    val guess =
                        world.mapStorage.loadData(MapData::class.java, "map_$id") as MapData? ?: return@register
                    if (guess.mapDecorations.any { it.value.func_176110_a() == 1.toByte() }) {
                        guessMapData = guess
                    }
                }
            }
        }

        EventBus.register<TickEvent.Client> {
            if (!Dungeon.inDungeon) return@register


            if (!calibrated) {
                if (mapData == null) {
                    mapData = getCurrentMapState()
                }

                calibrated = calibrateDungeonMap()
            } else if (!inBoss) {
                (mapData ?: guessMapData)?.let {
                    MapScanner.updatePlayers(it)
                    MapScanner.scan(it)
                    checkBloodDone(it)
                }
            }
        }
    }

    fun getCurrentMapState(): MapData? {
        val map = Stella.mc.thePlayer?.inventory?.getStackInSlot(8) ?: return null
        if (map.item !is ItemMap || !map.displayName.contains("Magical Map")) return null
        return (map.item as ItemMap).getMapData(map, Stella.mc.theWorld)
    }

    fun calibrateDungeonMap(): Boolean {
        val mapState = getCurrentMapState() ?: return false
        val entranceInfo = findEntranceCorner(mapState.colors) ?: return false

        val (startIndex, size) = entranceInfo
        mapRoomSize = size
        mapGapSize = mapRoomSize + 4 // compute gap size from room width

        var x = (startIndex % 128) % mapGapSize
        var z = (startIndex / 128) % mapGapSize

        val floor = Dungeon.floorNumber?: return false
        if (floor in listOf(0, 1)) x += mapGapSize
        if (floor == 0) z += mapGapSize

        mapCorners = x to z
        coordMultiplier = mapGapSize / roomDoorCombinedSize.toDouble()

        return true
    }

    fun findEntranceCorner(colors: ByteArray): Pair<Int, Int>? {
        for (i in colors.indices) {
            if (colors[i] != 30.toByte()) continue

            // Check horizontal 15-block chain
            if (i + 15 < colors.size && colors[i + 15] == 30.toByte()) {
                // Check vertical 15-block chain
                if (i + 128 * 15 < colors.size && colors[i + 128 * 15] == 30.toByte()) {
                    var length = 0
                    while (i + length < colors.size && colors[i + length] == 30.toByte()) {
                        length++
                    }
                    return Pair(i, length)
                }
            }
        }
        return null
    }

    fun checkBloodDone(state: MapData) {
        if (Dungeon.bloodClear) return

        val startX = mapCorners.first + (mapRoomSize / 2)
        val startY = mapCorners.second + (mapRoomSize / 2) + 1

        for (x in startX until 118 step (mapGapSize / 2)) {
            for (y in startY until 118 step (mapGapSize / 2)) {
                val i = x + y * 128
                if (state.colors.getOrNull(i) == null) continue

                val center = state.colors[i - 1]
                val roomColor = state.colors.getOrNull(i + 5 + 128 * 4) ?: continue

                if (roomColor != 18.toByte()) continue
                if (center != 30.toByte()) continue
                Dungeon.bloodClear = true
            }
        }
    }

    fun reset() {
        mapCorners = Pair(5, 5)
        mapRoomSize = 16
        mapGapSize = 0
        coordMultiplier = 0.625
        calibrated = false
        mapData = null
        guessMapData = null
    }
}