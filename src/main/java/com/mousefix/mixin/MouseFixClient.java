package com.mousefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MouseFixClient implements ClientModInitializer {
    // Status default mod saat game baru dibuka (true = nyala)
    public static boolean isEnabled = true; 
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // Mendaftarkan tombol F8 ke dalam menu setting control Minecraft
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Mouse Fix", // Nama yang muncul di menu
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,      // Default tombolnya F8
                "Mouse Fix Mod"        // Nama kategorinya
        ));

        // Event yang mengecek setiap saat apakah tombol ditekan
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled; // Ubah status ON jadi OFF, atau OFF jadi ON
                
                // Kasih notifikasi tulisan di atas hotbar biar ketahuan statusnya
                if (client.player != null) {
                    String statusMsg = isEnabled ? "§aON" : "§cOFF";
                    client.player.sendMessage(Text.literal("Mouse Fix: " + statusMsg), true);
                }
            }
        });
    }
}
