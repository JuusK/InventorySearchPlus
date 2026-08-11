package com.poggers;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.PolygonMode;
import com.poggers.utils.ColorUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.poggers.config.ModConfig;
import com.poggers.mixin.HandledScreenAccessor;
import com.poggers.mixin.ScreenAccessor;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventorySearch implements ClientModInitializer, ModMenuApi {
	public static EditBox searchBox;
	private static ConfigHolder<ModConfig> configHolder;
	private ModConfig config;
	private static String savedSearchText;

	private static final RenderPipeline PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withColorTargetState(new ColorTargetState(
			Optional.of(BlendFunction.OVERLAY),
			ColorTargetState.WRITE_RED
					| ColorTargetState.WRITE_GREEN
					| ColorTargetState.WRITE_BLUE
					| ColorTargetState.WRITE_ALPHA
	)).withLocation("pipeline/gui_search").build());
	

	public static ModConfig getConfig() {
		return configHolder.getConfig();
	}

	public static void saveConfig(){
		configHolder.save();
	}

	@Override
	public void onInitializeClient() {
		configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = getConfig();
		
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
			if(screen instanceof ContainerScreen || screen instanceof InventoryScreen || screen instanceof ShulkerBoxScreen){
				searchBox = new EditBox(
						client.font,
						w - 120,
						h - 40,
						100,
						20,
						net.minecraft.network.chat.Component.literal("Search...")
				);

				searchBox.setHint(Component.literal("Search..."));
				if(savedSearchText != null){ 
					searchBox.setValue(savedSearchText);
				}


				((ScreenAccessor) screen).invokeAddRenderableWidget(searchBox);

				//((ScreenAccessor) screen).invokeAddDrawableChild(clearSearchButton);

				ScreenEvents.remove(screen).register((screenArg) -> {
					if(searchBox != null) {
						savedSearchText = searchBox.getValue();
					}
				});

				ScreenEvents.afterExtract(screen).register((screenArg, context, mouseX, mouseY, delta) -> {

					if(screenArg instanceof ContainerScreen || screenArg instanceof InventoryScreen || screenArg instanceof ShulkerBoxScreen){
						if(config.iSSettings.getEnabledState() || searchBox.isFocused())  {
							if (!searchBox.getValue().isEmpty()) {
								String searchText = searchBox.getValue().toLowerCase();
	
								Map<Slot, SlotViewWrapper> views = new HashMap<>();
								for (Slot slot : ((AbstractContainerScreen<?>) screenArg).getMenu().slots) {
									ItemStack stack = slot.getItem();
									if (stack.isEmpty()) continue;
	
									boolean matches = stack.getItemName().getString().toLowerCase().contains(searchText);
									for(TypedDataComponent<?> c : stack.getComponents()) {
										if(c.value() instanceof ItemLore lore) {
											for(Component t : lore.lines()) {
												System.out.println(t);
												if(t.getString().toLowerCase().contains(searchText)) {
													matches = true;
												}
											}
										}
									}
									views.put(slot, new SlotViewWrapper(matches));
								}
	
								drawSlotOverlay(screenArg, views, context);
							}
						}
					}
					
				});
			}
		});
	}

	private void drawSlotOverlay(Object gui, Map<Slot, SlotViewWrapper> views, GuiGraphicsExtractor context) {
		if(gui instanceof InventoryScreen || gui instanceof ContainerScreen || gui instanceof ShulkerBoxScreen){

			for (Map.Entry<Slot, SlotViewWrapper> entry : views.entrySet()) {
					Slot slot = entry.getKey();
					int x = slot.x + ((HandledScreenAccessor) gui).getX();
					int y = slot.y + ((HandledScreenAccessor) gui).getY();

					if(entry.getValue().isEnableOverlay()) {
						context.fill(PIPELINE, x, y, x + 16, y + 16, ColorUtils.parseHexColor(config.iSSettings.getHighlightColor()));
					} else {
						context.fill(x, y, x + 16, y + 16, new Color(0, 0, 0, 170).getRGB());
					}
			}
		}
	}

	public static class SlotViewWrapper {
		private final boolean enableOverlay;

		public SlotViewWrapper(boolean enableOverlay) {
			this.enableOverlay = enableOverlay;
		}

		public boolean isEnableOverlay() {
			return enableOverlay;
		}
	}

}