package co.stellarskys.stella.mixins;

import co.stellarskys.stella.features.dungeons.dungeonBlockOverlay;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TileEntitySpecialRenderer.class})
public abstract class MixinTileEntitySpecialRenderer {
    @Inject(method = "bindTexture", at = @At("HEAD"), cancellable = true)
    public void bindTexture(ResourceLocation location, CallbackInfo info) {
        if (dungeonBlockOverlay.INSTANCE.isOverriding()) {
            if (location.getResourcePath().equals("textures/entity/chest/normal.png") ||
                    location.getResourcePath().equals("textures/entity/chest/normal_double.png") ||
                    location.getResourcePath().equals("textures/entity/chest/trapped.png") ||
                    location.getResourcePath().equals("textures/entity/chest/trapped_double.png")) {
                 int color = location.getResourcePath().contains("trapped")
                        ? dungeonBlockOverlay.INSTANCE.getTrappedChestColor()
                        :
                        dungeonBlockOverlay.INSTANCE.getChestColor();
                if (dungeonBlockOverlay.INSTANCE.bindModifiedEntityTexture(
                        location,
                        color
                )) {
                    info.cancel();
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                }
            }
        }
    }
}