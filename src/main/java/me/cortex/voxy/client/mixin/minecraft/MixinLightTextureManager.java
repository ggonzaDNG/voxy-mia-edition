package me.cortex.voxy.client.mixin.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.cortex.voxy.client.core.util.AbyssUtil;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.MinecraftClient;

@Mixin(LightmapTextureManager.class)
public class MixinLightTextureManager {
    @Inject(method = "getSkyLightCoordinates", at = @At("HEAD"), cancellable = true)
    private static void onGetSkyLightCoordinates(int light, CallbackInfoReturnable<Integer> cir) {
        if (AbyssUtil.getSection(MinecraftClient.getInstance().player.getBlockX()) > 3) {
            cir.setReturnValue(0);
        }
    }
}