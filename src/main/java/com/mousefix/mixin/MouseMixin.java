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

        // Catatan: Kode pengecekan "currentScreen" sudah dihapus di sini 
        // supaya mod tetap berfungsi saat membuka menu/inventory.

        double x = args.get(0);
        double y = args.get(1);

        // Gameplay: tukar X dan Y.
        args.set(0, y);
        // Sumbu Y diberi tanda minus (-) supaya tidak terbalik saat melihat ke atas/bawah
        args.set(1, -x); 
    }
}
