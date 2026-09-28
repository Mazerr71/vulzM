package com.vulzm.vulzm.client.module.setting;

import java.util.function.BooleanSupplier;

/** Dasar semua setting modul. */
public abstract class ModuleSetting<T> {
    private final String id;
    private final String nameKey;
    private BooleanSupplier visibleWhen = () -> true;

    protected ModuleSetting(String id, String nameKey) {
        this.id = id;
        this.nameKey = nameKey;
    }

    public String id() { return id; }
    public String nameKey() { return nameKey; }

    public boolean isVisible() { return visibleWhen.getAsBoolean(); }
    public void setVisibleWhen(BooleanSupplier s) { this.visibleWhen = s; }

    public abstract T get();
    public abstract void set(T value);
}
