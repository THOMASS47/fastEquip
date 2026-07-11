package com.thomass47.fastequip;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static boolean isHotbarFastEquipEnabled = true;
    public static boolean isInventoryFastEquipEnabled = true;
    public static Configuration configuration;

    public static void init(File configFile) {
        if (configuration == null) {
            configuration = new Configuration(configFile);
            syncConfig();
        }
    }

    public static void syncConfig() {
        isHotbarFastEquipEnabled = configuration.getBoolean(
            "isHotbarFastEquipEnabled",
            Configuration.CATEGORY_GENERAL,
            isHotbarFastEquipEnabled,
            "Set to false to disable fast armor equip when right clicking in hotbar");

        isInventoryFastEquipEnabled = configuration.getBoolean(
            "isInventoryFastEquipEnabled",
            Configuration.CATEGORY_GENERAL,
            isInventoryFastEquipEnabled,
            "Set to false to disable fast armor equip when right clicking in inventory");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    @cpw.mods.fml.common.eventhandler.SubscribeEvent
    public void onConfigChanged(cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(FastEquip.MODID)) {
            syncConfig();
        }
    }
}
