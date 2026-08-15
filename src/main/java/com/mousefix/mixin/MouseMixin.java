package com.mousefix.mixin;

import com.mousefix.MouseFixClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {

    /*
     * ============================================================
     * GAMEPLAY
     * ============================================================
     *
     * Tetap menggunakan sistem yang sudah berhasil sebelumnya.
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

        // Tukar sumbu X dan Y.
        args.set(0, y);

        // Balik arah vertikal sesuai konfigurasi gameplay.
        args.set(1, -x);
    }


    /*
     * ============================================================
     * MENU / INVENTORY
     * ============================================================
     *
     * Jangan mengubah posisi GLFW cursor secara langsung.
     *
     * Minecraft menerima posisi cursor melalui:
     *
     * Mouse.onCursorPos(...)
     *        ↓
     * Screen.mouseMoved(double, double)
     *
     * Jadi kita ubah ARGUMEN yang dikirim ke Screen.mouseMoved().
     *
     * Transformasi ini membuat koordinat menu mengikuti rotasi
     * yang sama dengan gameplay: X/Y ditukar dan arah Y dibalik.
     */
    @ModifyArgs(
        method = "method_1600(JDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/Screen;mouseMoved(DD)V"
        )
    )
    private void fixMenuCursor(Args args) {
        if (!MouseFixClient.isEnabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (client == null || client.currentScreen == null) {
            return;
        }

        double x = args.get(0);
        double y = args.get(1);

        /*
         * Ukuran area GUI Minecraft.
         */
        double width = client.getWindow().getScaledWidth();
        double height = client.getWindow().getScaledHeight();

        if (width <= 0.0 || height <= 0.0) {
            return;
        }

        /*
         * Rotasi 90 derajat:
         *
         * X baru berasal dari Y lama.
         * Y baru berasal dari X lama dengan arah dibalik.
         *
         * Scaling diperlukan karena lebar dan tinggi layar
         * belum tentu sama.
         */
        double newX = y * (width / height);
        double newY = height - (x * (height / width));

        /*
         * Jangan biarkan posisi keluar terlalu jauh dari area GUI.
         */
        newX = Math.max(0.0, Math.min(width, newX));
        newY = Math.max(0.0, Math.min(height, newY));

        args.set(0, newX);
        args.set(1, newY);
    }
}
