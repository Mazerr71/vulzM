# vulzM

Client mod **Fabric 1.21.11** bertema **Volcanic / Magma** (obsidian hitam + lava oranye-merah).
Struktur dan gaya mengikuti Xoldium Client: modul HUD/Visual/Movement, Mod Menu (Right Shift),
HUD Editor drag-and-drop, config JSON, bahasa EN + ID.

## Fitur

| Kategori | Modul |
|---|---|
| HUD | FPS, Coordinates, CPS Counter, Ping, Direction, Armor Status |
| Visual | Fullbright, Zoom (tahan **C**) |
| Movement | Auto Sprint |

- **Right Shift** membuka menu (Mod Menu / HUD Editor).
- Klik kartu = toggle modul. Tombol `...` = pengaturan modul.
- HUD Editor: seret elemen, pilih lalu `-` / `+` untuk ubah ukuran.
- Config tersimpan di `.minecraft/config/vulzm.json`.
- **Tanpa mixin** sama sekali, jadi aman terhadap perubahan internal Minecraft dan kompatibel
  dengan Sodium/VulkanMod.

## Cara build

Butuh **JDK 21** dan koneksi internet (untuk mengunduh Minecraft + Fabric).

```bash
# 1. Buat wrapper Gradle (sekali saja, butuh Gradle terpasang)
gradle wrapper --gradle-version 9.1.0

# 2. Build
./gradlew build          # Windows: gradlew.bat build
```

Hasil: `build/libs/vulzM-1.0.0.jar` -> taruh di folder `mods/` bersama **Fabric API**.

Uji langsung tanpa membuat jar: `./gradlew runClient`

## Sesuaikan versi dependensi

Buka `gradle.properties` dan cocokkan dengan versi terbaru di https://fabricmc.net/develop :

```
yarn_mappings=1.21.11+build.X
fabric_version=X.Y.Z+1.21.11
```
