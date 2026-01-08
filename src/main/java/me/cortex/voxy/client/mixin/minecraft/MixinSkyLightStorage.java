package me.cortex.voxy.client.mixin.minecraft;

import me.cortex.voxy.client.core.util.AbyssUtil;

import net.minecraft.core.BlockPos;

import net.minecraft.world.level.lighting.SkyLightSectionStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightSectionStorage.class)
public abstract class MixinSkyLightStorage {
    @Inject(method = "getLightValue", at = @At("RETURN"), cancellable = true)
        private void onGetLight(long blockPos, CallbackInfoReturnable<Integer> cir) {
            int x = BlockPos.getX(blockPos);
            int section = AbyssUtil.getSection(x);

            if (section > 3) {
                cir.setReturnValue(0);
        }
    }
}