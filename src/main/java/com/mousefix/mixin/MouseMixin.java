package com.mousefix.mixin;

import com.mousefix.MouseFix;
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

        // MouseFix OFF = input mouse asli
        if (!MouseFix.enabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        // Menu/inventory = input mouse asli
        if (client.currentScreen != null) {
            return;
        }

        double x = args.get(0);
        double y = args.get(1);

        // MouseFix ON = perbaiki sumbu X/Y saat gameplay
        args.set(0, y);
        args.set(1, x);
    }
}
