package com.vulzm.vulzm.client.module.setting;

public class DoubleSetting extends ModuleSetting<Double> {
    private final double min, max, step;
    private double value;

    public DoubleSetting(String id, String nameKey, double def, double min, double max, double step) {
        super(id, nameKey);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(def);
    }

    private double clamp(double v) { return Math.max(min, Math.min(max, v)); }

    @Override public Double get() { return value; }

    @Override public void set(Double v) {
        double c = clamp(v);
        if (step > 0) c = Math.round(c / step) * step;
        this.value = clamp(c);
    }

    public double min() { return min; }
    public double max() { return max; }
    public double step() { return step; }
    public float asFloat() { return (float) value; }
    public double progress() { return (value - min) / (max - min); }
}
