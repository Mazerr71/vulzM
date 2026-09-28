package com.vulzm.vulzm.client.module.visual;

import com.vulzm.vulzm.client.module.Module;
import com.vulzm.vulzm.client.module.ModuleCategory;
import com.vulzm.vulzm.client.module.ModuleManager;
import com.vulzm.vulzm.client.module.setting.DoubleSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

/**
 * Zoom ala OptiFine: tahan tombol C untuk memperkecil FOV.
 * Diterapkan lewat opsi FOV agar tidak perlu mixin ke renderer.
 */
public class ZoomModule extends Module {
    public static final int KEY = org.lwjgl.glfw.GLFW.GLFW_KEY_C;

    private final DoubleSetting zoomFov =
            addSetting(new DoubleSetting("fov", "setting.vulzm.zoom.fov", 20.0, 5.0, 60.0, 1.0));

    private boolean zooming = false;
    private int savedFov = 70;

    public ZoomModule() {
        super("zoom", "module.vulzm.zoom", ModuleCategory.VISUAL, true);
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.options == null || client.currentScreen != null) {
            stopIfNeeded(client);
            return;
        }
        boolean held = org.lwjgl.glfw.GLFW.glfwGetKey(
                client.getWindow().getHandle(), KEY) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

        SimpleOption<Integer> fov = client.options.getFov();
        if (held && !zooming) {
            savedFov = fov.getValue();
            zooming = true;
        }
        if (held) {
            fov.setValue(zoomFov.get().intValue());
        } else {
            stopIfNeeded(client);
        }
    }

    private void stopIfNeeded(MinecraftClient client) {
        if (zooming && client.options != null) {
            client.options.getFov().setValue(savedFov);
            zooming = false;
        }
    }

    @Override
    protected void onToggle(boolean enabled) {
        if (!enabled) stopIfNeeded(MinecraftClient.getInstance());
    }
}
