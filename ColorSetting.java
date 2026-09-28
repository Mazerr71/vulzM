package com.vulzm.vulzm.client.module.setting;

/** Warna ARGB. */
public class ColorSetting extends ModuleSetting<Integer> {
    private int argb;

    public ColorSetting(String id, String nameKey, int argb) {
        super(id, nameKey);
        this.argb = argb;
    }

    @Override public Integer get() { return argb; }
    @Override public void set(Integer v) { this.argb = v; }
    public int rgb() { return argb & 0xFFFFFF; }
}
