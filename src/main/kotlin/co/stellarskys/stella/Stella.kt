package co.stellarskys.stella

import co.stellarskys.stella.events.*
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.features.FeatureLoader
import co.stellarskys.stella.utils.ChatUtils
import co.stellarskys.stella.utils.TickUtils
import co.stellarskys.stella.utils.config
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner
import co.stellarskys.stella.utils.skyblock.dungeons.RoomRegistry
import java.util.concurrent.ConcurrentHashMap
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.inventory.GuiInventory
import org.apache.logging.log4j.LogManager

@Mod(modid = "stella", version = "1.0.0", useMetadata = true, clientSideOnly = true)
class Stella {
    private var shown = false
    private var eventCall: EventBus.EventCall? = null

    @Target(AnnotationTarget.CLASS)
    annotation class Module

    @Target(AnnotationTarget.CLASS)
    annotation class Command

    @EventHandler
    fun onInitializeClient(event: FMLInitializationEvent) {
        EventBus.post(GameEvent.Load())

        init()
        FeatureLoader.init()
        RoomRegistry.loadFromRemote()
        DungeonScanner.init()

        eventCall = EventBus.register<EntityEvent.Join> ({ event ->
            if (event.entity == mc.thePlayer) {
                ChatUtils.addMessage(
                    "$PREFIX §bMod loaded.",
                    "§b${FeatureLoader.getFeatCount()} §dmodules §8- §b${FeatureLoader.getLoadtime()}§dms §8- §b${FeatureLoader.getCommandCount()} §dcommands"
                )
                eventCall?.unregister()
                eventCall = null
            }

            shown = true
        })

        config.registerListener{ name, value ->
            configListeners[name]?.forEach { it.update() }
            ConfigCallback[name]?.forEach { it() }
        }

        EventBus.register<GuiEvent.Open> ({ event ->
            if (event.screen is GuiInventory) isInInventory = true
            if (event.screen is GuiMainMenu && shown) EventBus.post(GameEvent.Unload())
        })

        EventBus.register<GuiEvent.Close> ({
            isInInventory = false
        })

        EventBus.register<AreaEvent.Main> ({
            TickUtils.scheduleServer(1) {
                areaFeatures.forEach { it.update() }
            }
        })

        EventBus.register<AreaEvent.Sub> ({
            TickUtils.scheduleServer(1) {
                subareaFeatures.forEach { it.update() }
            }
        })
    }


    companion object {
        private val pendingFeatures = mutableListOf<Feature>()
        private val features = mutableListOf<Feature>()
        private val configListeners = ConcurrentHashMap<String, MutableList<Feature>>()
        private val ConfigCallback = ConcurrentHashMap<String, MutableList<() -> Unit>>()
        private val areaFeatures = mutableListOf<Feature>()
        private val subareaFeatures = mutableListOf<Feature>()

        @JvmField val LOGGER = LogManager.getLogger("stella")
        val mc = Minecraft.getMinecraft()
        val NAMESPACE: String = "stella"
        val INSTANCE: Stella? = null
        val PREFIX: String = "§7[§dStella§7]"
        val SHORTPREFIX: String = "§d[SA]"

        var isInInventory = false

        fun addFeature(feature: Feature) = pendingFeatures.add(feature)

        fun initializeFeatures() {
            pendingFeatures.forEach { feature ->
                features.add(feature)
                if (feature.hasAreas()) areaFeatures.add(feature)
                if (feature.hasSubareas()) subareaFeatures.add(feature)
                feature.initialize()
                feature.configName?.let { registerListener(it, feature) }
                feature.update()
            }
            pendingFeatures.clear()
        }

        fun registerListener(configName: String, feature: Feature) {
            configListeners.getOrPut(configName) { mutableListOf() }.add(feature)
        }

        fun registerListener(configName: String, callback: () -> Unit) {
            ConfigCallback.getOrPut(configName) { mutableListOf() }.add(callback)
        }

        fun updateFeatures() {
            features.forEach { it.update() }
        }

        //fun getResource(path: String) = Identifier.of(NAMESPACE, path)

        fun init() {}

    }
}