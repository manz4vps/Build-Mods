package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    @ModifyArgs(
        method = "updateMouse(D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void fixMouseAxis(Args args) {
        // Cek status on/off dari mod
        if (!com.mousefix.MouseFixClient.isEnabled) {
            return;
        }

        // Ambil input mentah dari mouse/layar
        double x = args.get(0);
        double y = args.get(1);

        // Cek HP lagi miring kemana
        if (com.mousefix.MouseFixClient.isLeftOrientation) {
            // ==========================================
            // KONDISI 1: Atas HP di Kiri
            // ==========================================
            args.set(0, y);
            args.set(1, -x);
        } else {
            // ==========================================
            // KONDISI 2: Atas HP di Kanan (Script aslimu)
            // ==========================================
            args.set(0, -y);
            args.set(1, x);
        }
    }
}
