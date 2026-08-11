package com.poggers.mixin;

import com.poggers.InventorySearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onKeyPress(
            KeyEvent input,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (InventorySearch.searchBox == null) {
            return;
        }

        if (!InventorySearch.searchBox.isFocused()) {
            return;
        }

        if (InventorySearch.searchBox.keyPressed(input)) {
            cir.setReturnValue(true);
        }

        if (input.key() == Minecraft.getInstance()
                .options.keyInventory
                .getDefaultKey()
                .getValue()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD")
    )
    private void onMouseClick(
            MouseButtonEvent click,
            boolean doubled,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (InventorySearch.searchBox == null) {
            return;
        }

        InventorySearch.searchBox.setFocused(
                InventorySearch.searchBox.isMouseOver(click.x(), click.y())
        );
    }
}