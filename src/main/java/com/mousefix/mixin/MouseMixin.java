package com.mousefix.mixin;

import com.mousefix.MouseFixClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    /*
     * GAMEPLAY
     *
     * Mengubah delta mouse yang dikirim ke player.
     * Ini adalah bagian yang sebelumnya sudah berhasil
     * membuat arah gameplay sesuai yang kamu mau.
     */
    @ModifyArgs(
        method = "updateMouse(D)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
        )
    )
    private void fixGameplayMouse(Args args) {
        if (!MouseFixClient.isEnabled) {
            return;
        }

        double x = args.get(0);
        double y = args.get(1);

        // Tukar X dan Y.
        args.set(0, y);

        // Balik tanda supaya arah vertikal gameplay benar.
        args.set(1, -x);
    }
}
