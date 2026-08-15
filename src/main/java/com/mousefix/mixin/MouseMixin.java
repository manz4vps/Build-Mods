package com.mousefix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Unique
    private boolean isFixing = false;

    // Kita cegat koordinat mouse langsung dari sistem (GLFW) sebelum diproses game
    @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
    private void fixCursorPos(long window, double x, double y, CallbackInfo ci) {
        // Cek mod nyala atau mati, dan pastikan tidak terjadi infinite loop (isFixing)
        if (com.mousefix.MouseFixClient.isEnabled && !isFixing) {
            isFixing = true; 
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getWindow() != null) {
                // 1. Tukar X dan Y
                double fixedX = y;
                
                // 2. Karena atas-bawah kebalik, kita balik nilainya dari ukuran tinggi layar
                // Ini efeknya sama kayak -x di script lama, tapi berlaku juga buat menu!
                double fixedY = client.getWindow().getHeight() - x; 

                // Panggil ulang input mouse pakai koordinat yang udah dibenerin
                ((Mouse)(Object)this).onCursorPos(window, fixedX, fixedY);
                
                // Batalkan input aslinya yang nge-bug dari Pojav
                ci.cancel(); 
            }
            
            isFixing = false;
        }
    }
}
