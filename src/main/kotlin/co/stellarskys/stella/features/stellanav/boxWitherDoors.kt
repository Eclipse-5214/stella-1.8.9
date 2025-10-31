package co.stellarskys.stella.features.stellanav

import co.stellarskys.stella.Stella
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.events.RenderEvent
import co.stellarskys.stella.events.WorldEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.features.stellanav.utils.mapConfig
import co.stellarskys.stella.utils.render.Render3D
import co.stellarskys.stella.utils.clearCodes
import co.stellarskys.stella.utils.skyblock.dungeons.Dungeon
import co.stellarskys.stella.utils.skyblock.dungeons.utils.DoorState
import co.stellarskys.stella.utils.skyblock.dungeons.utils.DoorType

@Stella.Module
object boxWitherDoors: Feature("boxWitherDoors", "catacombs") {
    var keyObtained = false
    var bloodOpen = false

    val obtainKey = Regex("""^(?:\[[^]]+]\s)?(\w+) has obtained (Wither|Blood) Key!$""")
    val openedDoor = Regex("""^(\w+) opened a WITHER door!$""")
    val obtainKeyAlt = Regex("""A (Blood|Wither) Key was picked up!""")
    val bloodOpened = Regex("""^The BLOOD DOOR has been opened!$""")

    override fun initialize() {
        register<ChatEvent.Receive> { event ->
            val msg = event.message.string.clearCodes()

            val keyMatch = obtainKey.find(msg)
            if (keyMatch != null){
                keyObtained = true
                return@register
            }

            val keyMatchAlt = obtainKeyAlt.find(msg)
            if (keyMatchAlt != null){
                keyObtained = true
                return@register
            }

            val doorMatch = openedDoor.find(msg)
            if (doorMatch != null){
                keyObtained = false
                return@register
            }

            val bloodMatch = bloodOpened.find(msg)
            if (bloodMatch != null){
                keyObtained = false
                bloodOpen = true
                return@register
            }
        }

        register<RenderEvent.World> { event ->
            if(bloodOpen) return@register

            val color = if (keyObtained) mapConfig.key else mapConfig.noKey

            Dungeon.doors.forEach { door ->
                if (door == null) return@forEach
                if (door.state != DoorState.DISCOVERED) return@forEach
                if (door.type !in setOf(DoorType.WITHER, DoorType.BLOOD)) return@forEach

                val (x, y, z) = door.getPos()

                Render3D.renderBox(
                    x.toDouble(), y.toDouble(), z.toDouble(),
                    3.0, 4.0,
                    color, true, mapConfig.doorLW
                )
            }
        }

        register<WorldEvent.Unload> {
            bloodOpen = false
            keyObtained = false
        }
    }

    override fun onUnregister() {
        bloodOpen = false
        keyObtained = false
    }
}