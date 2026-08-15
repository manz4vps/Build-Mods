package com.mousefix.mixin;

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
    private void swapMouseAxis(Args args) {
        double x = args.get(0);
        double y = args.get(1);

        // Tukar X dan Y
        args.set(0, y);
        args.set(1, x);
    }
}
