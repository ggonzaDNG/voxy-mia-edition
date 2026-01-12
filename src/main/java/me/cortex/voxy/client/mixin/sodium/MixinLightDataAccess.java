package me.cortex.voxy.client.mixin.sodium;

import me.cortex.voxy.client.core.util.AbyssLightZoneManager;
import me.cortex.voxy.client.core.util.AbyssUtil;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class MixinLightDataAccess {

    @Shadow
    private BlockPos.MutableBlockPos pos;

    @Inject(method = "compute", at = @At("RETURN"), cancellable = true)
    private void onComputeReturn(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {

        int packed = cir.getReturnValue();

        int customSky = AbyssLightZoneManager.getZoneLightLevel(this.pos);

        if (customSky != -1) {
            int bl = LightDataAccess.unpackBL(packed);
            int lu = LightDataAccess.unpackLU(packed);
            float ao = LightDataAccess.unpackAO(packed);
            boolean em = LightDataAccess.unpackEM(packed);
            boolean op = LightDataAccess.unpackOP(packed);
            boolean fo = LightDataAccess.unpackFO(packed);
            boolean fc = LightDataAccess.unpackFC(packed);

            int newPacked = LightDataAccess.packFC(fc) | 
                            LightDataAccess.packFO(fo) | 
                            LightDataAccess.packOP(op) | 
                            LightDataAccess.packEM(em) | 
                            LightDataAccess.packAO(ao) | 
                            LightDataAccess.packLU(lu) | 
                            LightDataAccess.packSL(customSky) | 
                            LightDataAccess.packBL(bl);

            cir.setReturnValue(newPacked);
        } else if (AbyssUtil.getSection(this.pos.getX()) > 3) {
            int customValueForOtherLayers = 0;

            int bl = LightDataAccess.unpackBL(packed);
            int lu = LightDataAccess.unpackLU(packed);
            float ao = LightDataAccess.unpackAO(packed);
            boolean em = LightDataAccess.unpackEM(packed);
            boolean op = LightDataAccess.unpackOP(packed);
            boolean fo = LightDataAccess.unpackFO(packed);
            boolean fc = LightDataAccess.unpackFC(packed);

            int newPacked = LightDataAccess.packFC(fc) | 
                            LightDataAccess.packFO(fo) | 
                            LightDataAccess.packOP(op) | 
                            LightDataAccess.packEM(em) | 
                            LightDataAccess.packAO(ao) | 
                            LightDataAccess.packLU(lu) | 
                            LightDataAccess.packSL(customValueForOtherLayers) | 
                            LightDataAccess.packBL(bl);


            cir.setReturnValue(newPacked);
        }
    }
}