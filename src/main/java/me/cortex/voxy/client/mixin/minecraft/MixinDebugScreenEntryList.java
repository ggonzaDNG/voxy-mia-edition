package me.cortex.voxy.client.mixin.minecraft;

import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenEntryList.class)
public abstract class MixinDebugScreenEntryList {
    @Shadow @Final private List<Identifier> currentlyEnabled;
    @Shadow public abstract boolean isOverlayVisible();

    @Inject(method = "rebuildCurrentList", at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"))
    private void voxy$injectVersionDisplay(CallbackInfo cir) {
        if (this.isOverlayVisible()) {
            var id0 = Identifier.fromNamespaceAndPath("voxy", "version");
            var id1 = Identifier.fromNamespaceAndPath("voxy", "abyss_coords");
            if (!this.currentlyEnabled.contains(id0)) {
                this.currentlyEnabled.add(id0);
            }
            if (!this.currentlyEnabled.contains(id1)) {
                this.currentlyEnabled.add(id1);
            }
        }
    }
}
