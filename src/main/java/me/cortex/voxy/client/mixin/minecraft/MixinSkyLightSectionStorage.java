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

    /**
     * getLightValue es el método final que extrae el valor del almacenamiento.
     */
    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    private void onGetLightValue(long blockPosLong, CallbackInfoReturnable<Integer> cir) {
        // Convertimos el long de Minecraft a un BlockPos para usar tu Manager
        // En tu Mixin, mejor haz esto para no crear objetos:
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
            cir.setReturnValue(0);
        }
    }
}