package com.vulzm.vulzm.client.module;

import com.vulzm.vulzm.VulzM;
import com.vulzm.vulzm.client.module.setting.ModuleSetting;
import com.vulzm.vulzm.config.VulzMConfig;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String id;
    private final String nameKey;
    private final ModuleCategory category;
    private final boolean defaultEnabled;
    private final List<ModuleSetting<?>> settings = new ArrayList<>();
    private boolean enabled;
    private boolean crashed;

    protected Module(String id, String nameKey, ModuleCategory category, boolean defaultEnabled) {
        this.id = id;
        this.nameKey = nameKey;
        this.category = category;
        this.defaultEnabled = defaultEnabled;
        this.enabled = defaultEnabled;
    }

    public String id() { return id; }
    public String nameKey() { return nameKey; }
    public Text displayName() { return Text.translatable(nameKey); }
    public ModuleCategory category() { return category; }
    public boolean isEnabled() { return enabled && !crashed; }
    public boolean isToggledOn() { return enabled; }
    public boolean isCrashed() { return crashed; }
    public boolean defaultEnabled() { return defaultEnabled; }

    public void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        try {
            onToggle(value);
        } catch (Throwable t) {
            markCrashed(t);
        }
        ModuleManager.saveState();
    }

    /** Dipakai saat memuat config, tanpa memicu save ulang. */
    public void setEnabledFromConfig(boolean value) {
        this.enabled = value;
        try {
            onToggle(value);
        } catch (Throwable t) {
            markCrashed(t);
        }
    }

    public void toggle() { setEnabled(!enabled); }

    /** Ditandai crash agar satu modul rusak tidak menjatuhkan seluruh client. */
    public void markCrashed(Throwable t) {
        this.crashed = true;
        VulzM.LOGGER.error("[vulzM] Modul '{}' dinonaktifkan karena error", id, t);
    }

    protected void onToggle(boolean enabled) {}
    public void onClientTick(net.minecraft.client.MinecraftClient client) {}

    protected <T extends ModuleSetting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    protected <T extends ModuleSetting<?>> T addSetting(T setting, java.util.function.BooleanSupplier visibleWhen) {
        setting.setVisibleWhen(visibleWhen);
        return addSetting(setting);
    }

    public List<ModuleSetting<?>> settings() { return settings; }
    public boolean hasSettings() { return !settings.isEmpty(); }

    // ---- persistensi setting ----
    public void loadSettings() {
        VulzMConfig c = VulzMConfig.instance;
        for (ModuleSetting<?> s : settings) {
            String key = id + "." + s.id();
            applyLoaded(s, key, c);
        }
    }

    private void applyLoaded(ModuleSetting<?> s, String key, VulzMConfig c) {
        if (s instanceof com.vulzm.vulzm.client.module.setting.BooleanSetting b && c.boolSettings.containsKey(key)) {
            b.set(c.boolSettings.get(key));
        } else if (s instanceof com.vulzm.vulzm.client.module.setting.DoubleSetting d && c.doubleSettings.containsKey(key)) {
            d.set(c.doubleSettings.get(key));
        } else if (s instanceof com.vulzm.vulzm.client.module.setting.ModeSetting m && c.modeSettings.containsKey(key)) {
            m.set(c.modeSettings.get(key));
        } else if (s instanceof com.vulzm.vulzm.client.module.setting.ColorSetting col && c.colorSettings.containsKey(key)) {
            col.set(c.colorSettings.get(key));
        } else if (s instanceof com.vulzm.vulzm.client.module.setting.KeybindSetting k && c.doubleSettings.containsKey(key)) {
            k.set((int) Math.round(c.doubleSettings.get(key)));
        }
    }

    public void saveSettings() {
        VulzMConfig c = VulzMConfig.instance;
        for (ModuleSetting<?> s : settings) {
            String key = id + "." + s.id();
            if (s instanceof com.vulzm.vulzm.client.module.setting.BooleanSetting b) c.boolSettings.put(key, b.get());
            else if (s instanceof com.vulzm.vulzm.client.module.setting.DoubleSetting d) c.doubleSettings.put(key, d.get());
            else if (s instanceof com.vulzm.vulzm.client.module.setting.ModeSetting m) c.modeSettings.put(key, m.get());
            else if (s instanceof com.vulzm.vulzm.client.module.setting.ColorSetting col) c.colorSettings.put(key, col.get());
            else if (s instanceof com.vulzm.vulzm.client.module.setting.KeybindSetting k) c.doubleSettings.put(key, (double) k.get());
        }
    }
}
