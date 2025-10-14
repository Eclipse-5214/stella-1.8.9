package co.stellarskys.stella.mixins;

import co.stellarskys.stella.features.dungeons.dungeonBlockOverlay;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderList.class})
public abstract class MixinRenderList {
    @Inject(method = "renderChunkLayer", at = @At("HEAD"))
    public void renderChunkLayer(EnumWorldBlockLayer layer, CallbackInfo ci) {
        if (dungeonBlockOverlay.INSTANCE.textureExists()) {
            dungeonBlockOverlay.INSTANCE.bindTextureIfExists();

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }
}