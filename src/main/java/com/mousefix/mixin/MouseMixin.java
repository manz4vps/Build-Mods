package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    // =========================
    // GAMEPLAY
    // =========================
    @ModifyArgs(
        method = "updateMouse(D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void fixGameplayMouse(Args args) {
        if (!com.mousefix.MouseFixClient.isEnabled) {
            return;
        }

        double x = args.get(0);
        double y = args.get(1);

        args.set(0, y);
        args.set(1, -x);
    }

    // =========================
    // MENU / INVENTORY CURSOR
    // =========================
    @ModifyArgs(
        method = "method_1600(JDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_437;method_16014(DD)V"
        )
    )
    private void fixMenuCursor(Args args) {
        if (!com.mousefix.MouseFixClient.isEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.currentScreen == null) {
            return;
        }

        double x = args.get(0);
        double y = args.get(1);

        // Balik sumbu cursor GUI.
        args.set(0, x);
        args.set(1, -y);
    }
}
