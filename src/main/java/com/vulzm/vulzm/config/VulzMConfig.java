package com.vulzm.vulzm.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vulzm.vulzm.VulzM;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Config JSON di .minecraft/config/vulzm.json */
public class VulzMConfig {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("vulzm.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static VulzMConfig instance = new VulzMConfig();

    public int configVersion = 1;
    public Map<String, Boolean> moduleEnabled = new HashMap<>();
    public Map<String, float[]> hudTransform = new HashMap<>();
    public Map<String, Boolean> boolSettings = new HashMap<>();
    public Map<String, Double> doubleSettings = new HashMap<>();
    public Map<String, String> modeSettings = new HashMap<>();
    public Map<String, Integer> colorSettings = new HashMap<>();

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try (Reader r = Files.newBufferedReader(PATH)) {
            VulzMConfig loaded = GSON.fromJson(r, VulzMConfig.class);
            if (loaded != null) instance = loaded;
        } catch (Exception e) {
            VulzM.LOGGER.warn("[vulzM] Config rusak, memakai default: {}", e.toString());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer w = Files.newBufferedWriter(PATH)) {
                GSON.toJson(instance, w);
            }
        } catch (IOException e) {
            VulzM.LOGGER.error("[vulzM] Gagal menyimpan config", e);
        }
    }
}
