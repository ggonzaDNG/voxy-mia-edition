package me.cortex.voxy.client.mixin.minecraft;

import me.cortex.voxy.client.core.util.AbyssLightZoneManager;
import me.cortex.voxy.client.core.util.AbyssUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightSectionStorage.class)
public abstract class MixinSkyLightSectionStorage {

    // if we dont inject here entities look weird for some reason

    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    private void onGetLightValue(long blockPosLong, CallbackInfoReturnable<Integer> cir) {

        int x = BlockPos.getX(blockPosLong);
        int y = BlockPos.getY(blockPosLong);
        int z = BlockPos.getZ(blockPosLong);
        int forcedSky = AbyssLightZoneManager.getZoneLightLevel(x, y, z);

        if (forcedSky != -1) {
            cir.setReturnValue(forcedSky);
            return;
        }

        // 2. Lógica del Abismo
        if (AbyssUtil.getSection(x) > 3) {
            cir.setReturnValue(1);
        }
    }
}