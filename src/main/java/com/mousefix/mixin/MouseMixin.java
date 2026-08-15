package com.mousefix.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {
    
    @ModifyArgs(
        method = "updateMouse()V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V")
    )
    private void swapMouseAxis(Args args) {
        double originalDeltaX = args.get(0);
        double originalDeltaY = args.get(1);
        
        // TUKAR SUMBU X DAN Y
        args.set(0, originalDeltaY);
        args.set(1, originalDeltaX);
    }
}
