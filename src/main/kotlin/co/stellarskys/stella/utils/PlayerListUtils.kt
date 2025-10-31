package co.stellarskys.stella.utils

import co.stellarskys.stella.Stella
import com.google.common.collect.ComparisonChain
import com.google.common.collect.Ordering
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.world.WorldSettings
import java.util.regex.Matcher
import java.util.regex.Pattern


object PlayerListUtils {
    val NetworkPlayerInfo.text: String
        get() = displayName?.formattedText ?: ScorePlayerTeam.formatPlayerName(playerTeam, gameProfile.name)

    val S38PacketPlayerListItem.AddPlayerData.text: String
        get() = displayName?.formattedText ?: ScorePlayerTeam.formatPlayerName(team, profile.name)

    val S38PacketPlayerListItem.AddPlayerData.team
        get() = Stella.mc.theWorld?.scoreboard?.getPlayersTeam(profile.name)

    private val playerInfoOrdering = object : Ordering<NetworkPlayerInfo>() {
        override fun compare(p1: NetworkPlayerInfo?, p2: NetworkPlayerInfo?): Int {
            return when {
                p1 != null && p2 != null -> ComparisonChain.start()
                    .compareTrueFirst(
                        p1.gameType != WorldSettings.GameType.SPECTATOR,
                        p2.gameType != WorldSettings.GameType.SPECTATOR
                    )
                    .compare(p1.playerTeam?.registeredName ?: "", p2.playerTeam?.registeredName ?: "")
                    .compare(p1.gameProfile.name, p2.gameProfile.name)
                    .result()

                p1 == null -> -1
                else -> 0
            }
        }
    }

    var playerList: List<NetworkPlayerInfo> = emptyList()

    init {
        TickUtils.loop(20) { update() }
    }

    fun update() {
        val player = Stella.mc.thePlayer ?: return
        playerList = playerInfoOrdering.immutableSortedCopy(player.sendQueue.playerInfoMap)
    }

    fun strAt(idx: Int): String? {
        if (playerList.isEmpty() || idx !in playerList.indices) return null
        val str = playerList[idx].text.clearCodes().trim()
        return str.ifEmpty { null }
    }

    fun regexAt(idx: Int, pattern: Pattern): Matcher? {
        val str = strAt(idx) ?: return null
        val matcher = pattern.matcher(str)
        return if (matcher.matches()) matcher else null
    }
}
