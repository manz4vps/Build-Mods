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

        // JANGAN DIUBAH — ini versi gameplay kamu yang sudah bekerja
        args.set(0, y);
        args.set(1, -x);
    }


    // =========================
    // MENU / INVENTORY CURSOR
    // =========================
    @ModifyArgs(
        method = "onCursorPos(JDD)V",
        at = @At("HEAD")
    )
    private void fixMenuCursor(Args args) {

        if (!com.mousefix.MouseFixClient.isEnabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        // Hanya perbaiki cursor ketika menu/inventory terbuka
        if (client.currentScreen == null) {
            return;
        }

        double x = args.get(1);
        double y = args.get(2);

        // Tukar X/Y seperti bug yang kamu alami
        args.set(1, y);
        args.set(2, x);
    }
}
