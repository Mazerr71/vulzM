package com.vulzm.vulzm.client.module;

import com.vulzm.vulzm.VulzM;
import com.vulzm.vulzm.client.module.hud.*;
import com.vulzm.vulzm.client.module.movement.SprintModule;
import com.vulzm.vulzm.client.module.visual.FullbrightModule;
import com.vulzm.vulzm.client.module.visual.ZoomModule;
import com.vulzm.vulzm.config.VulzMConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final Map<String, Module> BY_ID = new LinkedHashMap<>();
    private static final List<Module> ALL = new ArrayList<>();
    private static boolean initialized = false;

    private ModuleManager() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        register(new FpsModule());
        register(new CoordinatesModule());
        register(new CpsModule());
        register(new PingModule());
        register(new DirectionModule());
        register(new ArmorModule());

        register(new FullbrightModule());
        register(new ZoomModule());

        register(new SprintModule());

        loadState();

        HudRenderCallback.EVENT.register(ModuleManager::onHudRender);
        ClientTickEvents.END_CLIENT_TICK.register(ModuleManager::onEndTick);

        VulzM.LOGGER.info("[vulzM] {} modul terdaftar (HUD/Visual/Movement).", ALL.size());
    }

    private static void register(Module m) {
        BY_ID.put(m.id(), m);
        ALL.add(m);
    }

    public static Module get(String id) { return BY_ID.get(id); }
    public static List<Module> all() { return Collections.unmodifiableList(ALL); }

    public static List<Module> byCategory(ModuleCategory cat) {
        if (cat == ModuleCategory.ALL) return all();
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category() == cat) out.add(m);
        return out;
    }

    public static List<HudModule> hudModules() {
        List<HudModule> out = new ArrayList<>();
        for (Module m : ALL) if (m instanceof HudModule h) out.add(h);
        return out;
    }

    // ---------- render & tick ----------

    private static void onHudRender(net.minecraft.client.gui.DrawContext ctx,
                                    net.minecraft.client.render.RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        // Jangan gambar HUD di atas layar penuh seperti menu; HUD Editor menggambarnya sendiri.
        if (mc.options.hudHidden) return;
        for (Module m : ALL) {
            if (m instanceof HudModule h && h.isEnabled()) {
                try {
                    h.render(ctx, tick);
                } catch (Throwable t) {
                    h.markCrashed(t);
                }
            }
        }
    }

    private static void onEndTick(MinecraftClient client) {
        for (Module m : ALL) {
            if (!m.isEnabled()) continue;
            try {
                m.onClientTick(client);
            } catch (Throwable t) {
                m.markCrashed(t);
            }
        }
    }

    // ---------- persistensi ----------

    private static void loadState() {
        VulzMConfig c = VulzMConfig.instance;
        for (Module m : ALL) {
            Boolean en = c.moduleEnabled.get(m.id());
            m.setEnabledFromConfig(en != null ? en : m.defaultEnabled());
            m.loadSettings();
            if (m instanceof HudModule h) {
                float[] t = c.hudTransform.get(h.id());
                if (t != null && t.length >= 3) {
                    h.setPosition(t[0], t[1]);
                    h.setScale(t[2]);
                }
            }
        }
    }

    public static void saveState() {
        VulzMConfig c = VulzMConfig.instance;
        for (Module m : ALL) {
            c.moduleEnabled.put(m.id(), m.isToggledOn());
            m.saveSettings();
            if (m instanceof HudModule h) {
                c.hudTransform.put(h.id(), new float[]{h.x(), h.y(), h.scale()});
            }
        }
        VulzMConfig.save();
    }
}
