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
    // 1. FIX KHUSUS MENU / INVENTORY (Kursor UI)
    // ==========================================
    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void fixMenuCursor(long window, double x, double y, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Hanya jalan kalau mod aktif, tidak sedang looping, DAN SEDANG BUKA MENU
        if (com.mousefix.MouseFixClient.isEnabled && !isFixingPos && client.currentScreen != null) {
            isFixingPos = true;
            
            // Tukar X dan Y murni tanpa ditambah/kurang apa-apa.
            // Ini menjamin kursor visual dan titik klik Android berada di tempat yang sama!
            this.onCursorPos(window, y, x);
            
            ci.cancel(); // Hentikan input asli yang error
            isFixingPos = false;
        }
    }

    // ==========================================
    // 2. FIX KHUSUS GAMEPLAY (Kamera / Arah Pandang)
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
        
        // Hanya jalan kalau mod aktif dan SEDANG TIDAK BUKA MENU
        if (com.mousefix.MouseFixClient.isEnabled && client.currentScreen == null) {
            double x = args.get(0);
            double y = args.get(1);

            // Rumus yang udah terbukti bener di gameplay (tukar sumbu & balik arah Y)
            args.set(0, y);
            args.set(1, -x); 
        }
    }
}
