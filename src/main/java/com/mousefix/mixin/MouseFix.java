package com.mousefix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MouseFix implements ClientModInitializer {

    public static boolean enabled = true;

    public static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {

        toggleKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.mousefix.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_F8,
                        "category.mousefix"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal(
                                    "MouseFix: " + (enabled ? "ON" : "OFF")
                            ),
                            true
                    );
                }
            }
        });
    }
}
