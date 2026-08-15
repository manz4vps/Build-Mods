package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Unique
    private double capturedX;

    @Unique
    private double capturedY;

    // 1. Tangkap input koordinat asli dari PojavLauncher
    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void captureMousePos(long window, double x, double y, CallbackInfo ci) {
        this.capturedX = x;
        this.capturedY = y;
    }

    // 2. Ganti nilai parameter X yang masuk ke game menjadi Y (Tukar X ke Y)
    @ModifyVariable(method = "onCursorPos", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double fixX(double x) {
        if (com.mousefix.MouseFixClient.isEnabled) {
            return this.capturedY; 
        }
        return x;
    }

    // 3. Ganti nilai parameter Y yang masuk ke game menjadi (Tinggi Layar - X)
    // Supaya kamera saat melihat atas/bawah tidak terbalik, dan kursor menu akurat!
    @ModifyVariable(method = "onCursorPos", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double fixY(double y) {
        if (com.mousefix.MouseFixClient.isEnabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                return client.getWindow().getHeight() - this.capturedX;
            }
            // Fallback kalau window belum terbaca sempurna
            return -this.capturedX; 
        }
        return y;
    }
}
