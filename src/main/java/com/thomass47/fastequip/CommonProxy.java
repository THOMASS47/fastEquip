package com.thomass47.fastequip;

import net.minecraftforge.common.MinecraftForge;

import com.thomass47.fastequip.network.PacketFastEquip;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public static SimpleNetworkWrapper network;

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("fastequip");
        network.registerMessage(PacketFastEquip.Handler.class, PacketFastEquip.class, 0, Side.SERVER);

        Config.init(event.getSuggestedConfigurationFile());
        cpw.mods.fml.common.FMLCommonHandler.instance()
            .bus()
            .register(new Config());

        int features = (Config.isHotbarFastEquipEnabled ? 1 : 0) + (Config.isInventoryFastEquipEnabled ? 1 : 0);
        FastEquip.LOG.info(
            "Time spent equipping armor reduced by "
                + (features == 0 ? 0 : (int) (Math.random() * 50) + (features - 1) * 50)
                + "%");
    }

    // load "Do your mod setup. Build whatever data structures you care about.
    // Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommonFastEquipHandler());
    }

    // postInit "Handle interaction with other mods, complete your setup based on
    // this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {}

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}
}
