package co.stellarskys.stella.features.stellanav.utils.render

import co.stellarskys.stella.Stella
import co.stellarskys.stella.utils.CompatHelpers.UDrawContext
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack

object score {
    fun getCurrentMap(): ItemStack? {
        val stack = Stella.mc.thePlayer?.inventory?.getStackInSlot(8) ?: return null
        if (stack.item !is ItemMap) return null
        return stack
    }


    fun render(context: UDrawContext){
        val matrix = context.matrices

        val map = getCurrentMap() ?: return
        val mapData = (map.item as ItemMap).getMapData(map, Stella.mc.theWorld)

        matrix.pushMatrix()
        matrix.translate(5f, 5f, 5f)
        GlStateManager.color(1f,1f,1f,1f)
        Stella.mc.entityRenderer.mapItemRenderer.renderMap(mapData, true)
        matrix.popMatrix()
    }
}