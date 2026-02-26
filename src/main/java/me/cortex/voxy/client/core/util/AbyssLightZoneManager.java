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
    private static final List<Zone> zones = new ArrayList<>();
    
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("voxy_mia_light_zones.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    interface Zone {
        boolean contains(int x, int y, int z);
        int getLightLevel();
    }

    record BoxZone(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int lightLevel) implements Zone {
        @Override
        public boolean contains(int x, int y, int z) {
            return x >= minX && x <= maxX &&
                   y >= minY && y <= maxY &&
                   z >= minZ && z <= maxZ;
        }

        @Override
        public int getLightLevel() { return lightLevel; }
    }

    record CircleZone(int centerX, int centerZ, int minY, int maxY, int radius, int lightLevel) implements Zone {
        @Override
        public boolean contains(int x, int y, int z) {

            if (y < minY || y > maxY) {
                return false;
            }

            double dx = x - centerX;
            double dz = z - centerZ;
            double distSq = dx * dx + dz * dz;

            double effectiveRadius = radius;

            return distSq <= (effectiveRadius * effectiveRadius);
        }

        @Override
        public int getLightLevel() { return lightLevel; }
    }

    private static class AbyssLightZoneEntry {
        String type;
        String name; // for better readability, we never used it in the code

        int x1, y1, z1;
        int x2, y2, z2;

        int x, z;
        int yMin, yMax;
        int radius;
        
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
                    addZoneFromEntry(entry);
                }
                System.out.println("[VoxyMIA_ABYSSLIGHTZONES] Loaded " + zones.size() + " custom light zones.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[VoxyMIA_ABYSSLIGHTZONES] Error while trying to load voxy_mia_light_zones.json");
        }
    }

    private static void addZoneFromEntry(AbyssLightZoneEntry entry) {
        if ("circle".equalsIgnoreCase(entry.type)) {
            zones.add(new CircleZone( // click the circles to the beat
                entry.x, entry.z,
                entry.yMin, entry.yMax,
                entry.radius,
                entry.lightLevel
            ));
        } else { // box
            int minX = Math.min(entry.x1, entry.x2);
            int maxX = Math.max(entry.x1, entry.x2);
            int minY = Math.min(entry.y1, entry.y2);
            int maxY = Math.max(entry.y1, entry.y2);
            int minZ = Math.min(entry.z1, entry.z2);
            int maxZ = Math.max(entry.z1, entry.z2);

            zones.add(new BoxZone(minX, minY, minZ, maxX, maxY, maxZ, entry.lightLevel));
        }
    }

    private static void createDefaultConfig() {
        try {
            String defaults = "hi";

            Files.createDirectories(CONFIG_PATH.getParent());
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(defaults, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getZoneLightLevel(BlockPos pos) {
        return getZoneLightLevel(pos.getX(), pos.getY(), pos.getZ());
    }

    public static int getZoneLightLevel(int x, int y, int z) {
        for (Zone zone : zones) {
            if (zone.contains(x, y, z)) {
                return zone.getLightLevel();
            }
        }
        return -1;
    }
}