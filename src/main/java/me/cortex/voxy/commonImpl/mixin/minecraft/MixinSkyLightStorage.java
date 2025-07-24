package me.cortex.voxy.commonImpl.mixin.minecraft;

import me.cortex.voxy.client.core.util.AbyssUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.SkyLightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightStorage.class)
public abstract class MixinSkyLightStorage {

    @Inject(method = "getLight(JZ)I", at = @At("RETURN"), cancellable = true)
        private void onGetLight(long blockPos, boolean cached, CallbackInfoReturnable<Integer> cir) {
            int x = BlockPos.unpackLongX(blockPos);
            int section = AbyssUtil.getSection(x);

            if (section > 3) {
                cir.setReturnValue(0);
        }
}
}   