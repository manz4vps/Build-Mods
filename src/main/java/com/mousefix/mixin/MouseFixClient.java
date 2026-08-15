package com.mousefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MouseFixClient implements ClientModInitializer {
    public static boolean isEnabled = true; 
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Mouse Fix", 
                GLFW.GLFW_KEY_F8,      
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled; 
                
                if (client.player != null) {
                    String statusMsg = isEnabled ? "§aON" : "§cOFF";
                    client.player.sendMessage(Text.literal("Mouse Fix: " + statusMsg), true);
                }
            }
        });
    }
}
