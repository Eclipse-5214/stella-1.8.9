package co.stellarskys.stella.features.dungeons

import co.stellarskys.stella.Stella
import co.stellarskys.stella.Stella.Companion.SHORTPREFIX
import co.stellarskys.stella.events.ChatEvent
import co.stellarskys.stella.features.Feature
import co.stellarskys.stella.utils.ChatUtils
import co.stellarskys.stella.utils.clearCodes

@Stella.Module
object termTracker : Feature("termTracker", "catacombs") {
    private lateinit var completed: MutableMap<String, MutableMap<String, Int>>
    private val pattern = Regex("""^(\w{1,16}) (?:activated|completed) a (\w+)! \(\d/\d\)$""")

    override fun initialize() {
        completed = mutableMapOf()
        register<ChatEvent.Receive> { event ->
            if (event.event.type.toInt() == 2) return@register
            val msg = event.message.string.clearCodes()
            val matcher = pattern.find(msg)

            when {
                msg == "The Core entrance is opening!" -> {
                    completed.forEach { (user, data) ->
                        ChatUtils.addMessage("$SHORTPREFIX §b$user §7completed §f${data["terminal"] ?: 0} §7 terms, §f${data["device"] ?: 0} §7devices, and §f${data["lever"] ?: 0} §7levers!")
                    }
                }
                pattern.matches(msg) -> {
                    val match = pattern.matchEntire(msg)!!
                    val (user, type) = match.destructured
                    if (type in listOf("terminal", "lever", "device")) {
                        completed.getOrPut(user) { mutableMapOf() }[type] =
                            (completed[user]?.get(type) ?: 0) + 1
                    }
                }
            }
        }
    }

    override fun onRegister() {
        if (this::completed.isInitialized) completed.clear()
        super.onRegister()
    }

    override fun onUnregister() {
        if (this::completed.isInitialized) completed.clear()
        super.onUnregister()
    }
}