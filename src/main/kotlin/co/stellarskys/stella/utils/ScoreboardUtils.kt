package co.stellarskys.stella.utils


import co.stellarskys.stella.Stella.Companion.mc
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam

object ScoreboardUtils {
    fun getSidebarLines(cleanColor: Boolean): List<String> {
        val scoreboard = mc.theWorld?.scoreboard ?: return emptyList()
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()

        return scoreboard.getSortedScores(objective)
            .mapNotNull { score: Score ->
                score.playerName?.let { playerName ->
                    stripAlienCharacters(
                        ScorePlayerTeam.formatPlayerName(
                            scoreboard.getPlayersTeam(playerName),
                            playerName
                        )
                    ).let {
                        if (cleanColor) it.clearCodes()
                        else it
                    }
                }
            }
            .reversed()
    }

    fun getScoreboardTitle(cleanColor: Boolean = true): String? {
        val scoreboard = mc.theWorld?.scoreboard ?: return null
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return null

        return objective.displayName?.let {
            if (cleanColor) it.clearCodes() else it
        }
    }

    /**
     * This code is modified
     * @Author: nea98
     * @Source: https://moddev.nea.moe
     **/
    private fun stripAlienCharacters(text: String): String {
        return text.filter {
            mc.fontRendererObj.getCharWidth(it) > 0 || it == '§'
        }
    }
}