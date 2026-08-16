package com.mousefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MouseFixClient implements ClientModInitializer {
    public static boolean isEnabled = true; 
    
    // ==========================================
    // VARIABEL BARU: Mode Orientasi (Kiri/Kanan)
    // ==========================================
    public static boolean isLeftOrientation = false; 

    private static KeyBinding toggleKey;
    private static KeyBinding orientationKey; // Shortcut baru buat F11

    @Override
    public void onInitializeClient() {
        // Tombol F12 untuk ON/OFF Mod (Kode aslimu)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Mouse Fix", 
                GLFW.GLFW_KEY_F12,      
                KeyBinding.Category.MISC
        ));

        // Tombol F11 untuk Ganti Orientasi Kiri/Kanan
        orientationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Phone Orientation", 
                GLFW.GLFW_KEY_F11,      
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Logika pas tombol F12 dipencet
            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled; 
                
                if (client.player != null) {
                    String statusMsg = isEnabled ? "§aON" : "§cOFF";
                    client.player.sendMessage(Text.literal("Mouse Fix: " + statusMsg), true);
                }
            }

            // Logika pas tombol F11 dipencet
            while (orientationKey.wasPressed()) {
                isLeftOrientation = !isLeftOrientation; 
                
                if (client.player != null) {
                    // Kasih teks berwarna biar gampang bedainnya
                    String orientMsg = isLeftOrientation ? "§bKIRI (Atas HP di Kiri)" : "§eKANAN (Atas HP di Kanan)";
                    client.player.sendMessage(Text.literal("Orientasi HP: " + orientMsg), true);
                }
            }
        });
    }
}
