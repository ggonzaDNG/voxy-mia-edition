package me.cortex.voxy.client.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class AbyssLightZoneManager {
    
    private static final List<LightZone> zones = new ArrayList<>();
    
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("voxy_mia_light_zones.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public record LightZone(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int lightLevel) {
        public boolean contains(int x, int y, int z) {
            return x >= minX && x <= maxX &&
                   y >= minY && y <= maxY &&
                   z >= minZ && z <= maxZ;
        }
    }

    private static class AbyssLightZoneEntry {
        String name; // just for organization purposes
        int x1, y1, z1;
        int x2, y2, z2;
        int lightLevel;
        
        public AbyssLightZoneEntry() {}
    }

    public static void loadConfig() {
        zones.clear();
        File file = CONFIG_PATH.toFile();

        if (!file.exists()) {
            createDefaultConfig();
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<AbyssLightZoneEntry>>(){}.getType();
            List<AbyssLightZoneEntry> entries = GSON.fromJson(reader, listType);

            if (entries != null) {
                for (AbyssLightZoneEntry entry : entries) {
                    addZone(
                        new BlockPos(entry.x1, entry.y1, entry.z1),
                        new BlockPos(entry.x2, entry.y2, entry.z2),
                        entry.lightLevel
                    );
                }
                System.out.println("[VoxyMIA_ABYSSLIGHTZONES] Loaded " + zones.size() + " custom light zones.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[VoxyMIA_ABYSSLIGHTZONES] Error while trying to load voxy_mia_light_zones.json");
        }
    }

    private static void createDefaultConfig() {
        try {
            List<AbyssLightZoneEntry> defaults = new ArrayList<>();
            AbyssLightZoneEntry example = new AbyssLightZoneEntry();
            example.name = "Example Zone";
            example.x1 = 0; example.y1 = 60; example.z1 = 0;
            example.x2 = 10; example.y2 = 70; example.z2 = 10;
            example.lightLevel = 15;
            defaults.add(example);

            Files.createDirectories(CONFIG_PATH.getParent());
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(defaults, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addZone(BlockPos corner1, BlockPos corner2, int lightLevel) {
        if (lightLevel < 0) lightLevel = 0;
        if (lightLevel > 15) lightLevel = 15;

        int minX = Math.min(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());

        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        zones.add(new LightZone(minX, minY, minZ, maxX, maxY, maxZ, lightLevel));
    }

    public static int getZoneLightLevel(int x, int y, int z) {
        for (LightZone zone : zones) {
            if (zone.contains(x, y, z)) {
                return zone.lightLevel;
            }
        }
        return -1;
    }

    public static int getZoneLightLevel(BlockPos pos) {
        return getZoneLightLevel(pos.getX(), pos.getY(), pos.getZ());
    }
}