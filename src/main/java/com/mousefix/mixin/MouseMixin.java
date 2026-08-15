package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    private void onCursorPos(long window, double x, double y) {}

    @Unique
    private boolean isFixingPos = false;

    // ==========================================
    // 1. FIX KURSOR MENU / INVENTORY (X & Y Ditukar & Dibalik)
    // ==========================================
    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void fixMenuCursor(long window, double x, double y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Jalan saat mod aktif dan sedang membuka menu/inventory
        if (com.mousefix.MouseFixClient.isEnabled && !isFixingPos && client.currentScreen != null) {
            isFixingPos = true;
            
            if (client.getWindow() != null) {
                // Tukar X dan Y, lalu balik arah Y menggunakan tinggi layar (sama persis dengan konsep gameplay)
                double fixedX = y;
                double fixedY = client.getWindow().getHeight() - x;

                this.onCursorPos(window, fixedX, fixedY);
            } else {
                // Fallback jika window belum siap
                this.onCursorPos(window, y, -x);
            }
            
            ci.cancel(); 
            isFixingPos = false;
        }
    }

    // ==========================================
    // 2. FIX KAMERA GAMEPLAY (Sudah Aman & Normal)
    // ==========================================
    @ModifyArgs(
        method = "updateMouse(D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void fixCameraAxis(Args args) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Jalan saat mod aktif dan sedang di dalam gameplay (tidak buka menu)
        if (com.mousefix.MouseFixClient.isEnabled && client.currentScreen == null) {
            double x = args.get(0);
            double y = args.get(1);

            args.set(0, y);
            args.set(1, -x); 
        }
    }
}
