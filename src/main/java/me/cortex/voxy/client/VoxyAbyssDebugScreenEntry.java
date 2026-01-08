package main.java.me.cortex.voxy.client;

import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.client.core.VoxyRenderSystem;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VoxyAbyssDebugScreenEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        if (!VoxyCommon.isAvailable()) {
            return;
        }

        var instance = VoxyCommon.getInstance();
        if (instance == null) {
            return;
        }

        VoxyRenderSystem vrs = null;
        var wr = Minecraft.getInstance().levelRenderer;
        if (wr != null) vrs = ((IGetVoxyRenderSystem) wr).getVoxyRenderSystem();

        if (vrs != null) {
            List<String> AbyssCoords = new ArrayList<>();
            vrs.addDebugInfo_Abyss(AbyssCoords);
            lines.addToGroup(Identifier.fromNamespaceAndPath("voxy", "abyss_coords"), AbyssCoords);
        }
    }


}
