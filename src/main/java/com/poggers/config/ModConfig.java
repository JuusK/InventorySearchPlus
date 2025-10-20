package com.poggers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;

@Config(name = "inventory-search")
public class ModConfig implements ConfigData {
    @Category("inventorySearchSettings")
    @TransitiveObject
    public InventorySearchSettings iSSettings = new InventorySearchSettings();

    public static class InventorySearchSettings{
        @ColorPicker
        private String HIGHLIGHT_COLOR = "#D9884580";

        private boolean STAY_ENABLED = true;

        public String getHighlightColor() {
            return HIGHLIGHT_COLOR;
        }

        public void setHighlightColor(String hc) {
            this.HIGHLIGHT_COLOR = hc;
        }

        public boolean getEnabledState() {
            return STAY_ENABLED;
        }

        public void setEnabledState(boolean es) {
            this.STAY_ENABLED = es;
        }

        
    }
    
}
