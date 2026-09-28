package com.vulzm.vulzm.client.module.setting;

public class BooleanSetting extends ModuleSetting<Boolean> {
    private boolean value;

    public BooleanSetting(String id, String nameKey, boolean def) {
        super(id, nameKey);
        this.value = def;
    }

    @Override public Boolean get() { return value; }
    @Override public void set(Boolean v) { this.value = v; }
    public boolean isOn() { return value; }
    public void toggle() { value = !value; }
}
