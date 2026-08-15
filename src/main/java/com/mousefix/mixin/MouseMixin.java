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
        // Cek status on/off dari mod (menggunakan tombol F12)
        if (!com.mousefix.MouseFixClient.isEnabled) {
            return;
        }

        // Ambil input mentah dari mouse/layar
        double x = args.get(0);
        double y = args.get(1);

        // ==========================================
        // IDE BARU: Balik semua arahnya (Inverted)
        // ==========================================
        
        // Kiri jadi Kanan, Kanan jadi Kiri (sumbu X diisi dengan minus Y)
        args.set(0, -y);
        
        // Atas jadi Bawah, Bawah jadi Atas (sumbu Y diisi dengan X positif)
        args.set(1, x); 
    }
}
