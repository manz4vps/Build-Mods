package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    // ==========================================
    // 1. BAGIAN FIX KAMERA (GAMEPLAY) - KODE LAMAMU
    // ==========================================
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

        double x = args.get(0);
        double y = args.get(1);

        // Gameplay: tukar X dan Y
        args.set(0, y);
        // Sumbu Y diberi tanda minus (-) supaya tidak terbalik saat melihat ke atas
        args.set(1, -x);
    }

    // ==========================================
    // 2. BAGIAN FIX KURSOR MENU/INVENTORY (BARU)
    // ==========================================

    // Kita "Shadow" method bawaan Minecraft biar bisa kita panggil ulang
    @Shadow
    private void onCursorPos(long window, double x, double y) {}

    // Variabel penanda biar kodenya nggak looping tanpa henti
    @Unique
    private boolean isFixingCursor = false;

    // Inject di awal (HEAD) method onCursorPos
    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void fixMenuCursor(long window, double x, double y, CallbackInfo ci) {
        
        // Cek apakah mod aktif dan kursor belum dalam proses perbaikan
        if (com.mousefix.MouseFixClient.isEnabled && !this.isFixingCursor) {
            this.isFixingCursor = true; // Tandai kalau kita lagi nge-fix kursornya

            // Tukar X dan Y buat kursor menu
            double fixedX = y;
            double fixedY = x; 

            // Panggil ulang method aslinya tapi pakai X dan Y yang udah kita balik
            this.onCursorPos(window, fixedX, fixedY);

            this.isFixingCursor = false; // Reset penanda
            ci.cancel(); // Batalkan eksekusi aslinya yang masih error
        }
    }
}
