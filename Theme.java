package com.vulzm.vulzm.client.util;

/**
 * Tema VOLCANIC / MAGMA: obsidian hitam-kemerahan + lava oranye/merah.
 * Semua warna ARGB (0xAARRGGBB).
 */
public final class Theme {
    private Theme() {}

    // Latar obsidian
    public static final int OBSIDIAN       = 0xF0100A0A;
    public static final int OBSIDIAN_LIGHT = 0xF01C1210;
    public static final int PANEL          = 0xE0201512;
    public static final int PANEL_HOVER    = 0xE02E1D18;

    // Lava
    public static final int LAVA           = 0xFFFF5A1F; // aksen utama
    public static final int LAVA_BRIGHT    = 0xFFFF8A3D;
    public static final int MAGMA_RED      = 0xFFD9341A;
    public static final int EMBER          = 0xFFFFC46B;

    // Teks
    public static final int TEXT           = 0xFFF5E6D8;
    public static final int TEXT_DIM       = 0xFF9C8577;
    public static final int TEXT_ON        = 0xFF7DFF9A; // status ON
    public static final int TEXT_OFF       = 0xFFFF6B5A; // status OFF

    public static int withAlpha(int argb, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (argb & 0xFFFFFF);
    }

    /** Interpolasi linear dua warna ARGB. */
    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aa = (a >>> 24), ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int ba = (b >>> 24), br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int ra = (int) (aa + (ba - aa) * t);
        int rr = (int) (ar + (br - ar) * t);
        int rg = (int) (ag + (bg - ag) * t);
        int rb = (int) (ab + (bb - ab) * t);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    }

    /** Warna lava yang "berdenyut" pelan, untuk efek hidup pada UI. */
    public static int pulse(float timeSeconds) {
        float t = (float) (0.5 + 0.5 * Math.sin(timeSeconds * 2.2));
        return lerp(MAGMA_RED, LAVA_BRIGHT, t);
    }
}
