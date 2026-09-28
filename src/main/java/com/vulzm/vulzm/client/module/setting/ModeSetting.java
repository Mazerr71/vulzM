package com.vulzm.vulzm.client.module.setting;

import java.util.List;

public class ModeSetting extends ModuleSetting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String id, String nameKey, String def, String... modes) {
        super(id, nameKey);
        this.modes = List.of(modes);
        this.index = Math.max(0, this.modes.indexOf(def));
    }

    @Override public String get() { return modes.get(index); }

    @Override public void set(String v) {
        int i = modes.indexOf(v);
        if (i >= 0) index = i;
    }

    public void cycle() { index = (index + 1) % modes.size(); }
    public List<String> modes() { return modes; }
}
