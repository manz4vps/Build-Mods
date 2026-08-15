package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    // Fitur Shadow untuk membypass status "private" pada method bawaan Minecraft
    @Shadow
    private void onCursorPos(long window, double x, double y) {}

    @Unique
    private boolean isFixing = false;

    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void fixCursorPos(long window, double x, double y, CallbackInfo ci) {
        // Cek mod nyala atau mati, dan pastikan tidak terjadi infinite loop
        if (com.mousefix.MouseFixClient.isEnabled && !isFixing) {
            isFixing = true; 
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                // 1. Tukar X dan Y
                double fixedX = y;
                
                // 2. Balik nilai Y dari ukuran tinggi layar
                double fixedY = client.getWindow().getHeight() - x; 

                // Panggil ulang input mouse pakai method Shadow yang sudah di-bypass
                this.onCursorPos(window, fixedX, fixedY);
                
                // Batalkan input aslinya yang nge-bug dari Pojav
                ci.cancel(); 
            }
            
            isFixing = false;
        }
    }
}
