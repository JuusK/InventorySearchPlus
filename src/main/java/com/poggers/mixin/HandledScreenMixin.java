package com.poggers.mixin;

import com.poggers.InventorySearch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        if (InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()) {
            //System.out.println("Key Code: " + keyCode + ", Modifiers: " + modifiers);
            if(InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()){
                if(InventorySearch.searchBox.keyPressed(input)){
                    cir.cancel();
                }

                if(input.getKeycode() == MinecraftClient.getInstance().options.inventoryKey.getDefaultKey().getCode()){
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClick(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir){
        if (InventorySearch.searchBox != null) {
            InventorySearch.searchBox.setFocused(InventorySearch.searchBox.isMouseOver(click.x(), click.y()));
        }
    }
}
