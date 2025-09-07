package co.stellarskys.stella.utils

import co.stellarskys.stella.Stella
import co.stellarskys.stella.Stella.Companion.mc
import co.stellarskys.stella.hud.HUDEditor
import co.stellarskys.stella.utils.skyblock.dungeons.DungeonScanner
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos

@Stella.Command
object MainCommand: CommandUtils(
    "assets/stella",
    "Opens the Config",
    listOf("sa", "stta")
) {
    override fun processCommand(sender: ICommandSender?, args: Array<out String?>?) {
        if (args != null && args.isNotEmpty()) {
            when (args[0]?.lowercase()) {
                "hud" -> {
                    TickUtils.schedule(1) {
                        mc.displayGuiScreen(HUDEditor())
                    }
                    return
                }
            }
        }

        TickUtils.schedule(1){
            config.open()
        }
    }

    override fun getTabCompletions(sender: ICommandSender, args: Array<String>, pos: BlockPos): List<String> {
        if (args.size == 1) return listOf("hud").filter { it.startsWith(args[0].lowercase()) }
        return emptyList()
    }
}