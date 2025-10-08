package co.stellarskys.stella.mixins;

import co.stellarskys.stella.features.msc.inventoryButtons;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({InventoryEffectRenderer.class})
public class MixinInventoryEffectRenderer {
    @ModifyVariable(method = "updateActivePotionEffects", at = @At(value = "STORE"))
    public boolean zen$showEffects(boolean hasVisibleEffect) {
        return !inventoryButtons.INSTANCE.isEnabled();
    }
}