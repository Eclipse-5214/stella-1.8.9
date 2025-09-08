package co.stellarskys.stella.features

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.TimeUtils
import co.stellarskys.stella.utils.TimeUtils.millis
import org.reflections.Reflections
import net.minecraft.command.ICommand
import net.minecraftforge.client.ClientCommandHandler

object FeatureLoader {
    private var moduleCount = 0
    private var commandCount = 0
    private var loadtime: Long = 0

    fun init() {
        val reflections = Reflections("co.stellarskys.stella")

        val features = reflections.getTypesAnnotatedWith(Stella.Module::class.java)
        val starttime = TimeUtils.now
        val categoryOrder = listOf("dungeons", "stellanav", "msc")

        features.sortedWith(compareBy<Class<*>> { clazz ->
            val packageName = clazz.`package`.name
            val category = packageName.substringAfterLast(".")
            categoryOrder.indexOf(category).takeIf { it != -1 } ?: Int.MAX_VALUE
        }.thenBy { it.name }).forEach { clazz ->
            try {
                Class.forName(clazz.name)
                moduleCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val commands = reflections.getTypesAnnotatedWith(Stella.Command::class.java)

        commands.forEach { commandClass ->
            try {
                val commandInstance = commandClass.getDeclaredField("INSTANCE").get(null) as ICommand
                ClientCommandHandler.instance.registerCommand(commandInstance)
                commandCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Stella.initializeFeatures()
        loadtime = starttime.since.millis
    }

    fun getFeatCount(): Int = moduleCount
    fun getCommandCount(): Int = commandCount
    fun getLoadtime(): Long = loadtime
}