package com.thomass47.fastequip;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonFastEquipHandler {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Config.isHotbarFastEquipEnabled) return;

        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;

        ItemStack heldItem = event.entityPlayer.getHeldItem();
        if (heldItem == null) return;
        if (heldItem.getItem() instanceof ItemArmor armor) {
            int equipmentSlot = 4 - armor.armorType;
            ItemStack wornArmor = event.entityPlayer.getEquipmentInSlot(equipmentSlot);

            if (event.world.isRemote) {
                event.entityPlayer.setCurrentItemOrArmor(equipmentSlot, heldItem);
            } else {
                event.entityPlayer.setCurrentItemOrArmor(equipmentSlot, heldItem);
                event.entityPlayer.setCurrentItemOrArmor(0, wornArmor);

                event.setCanceled(true);
            }

        }
    }

}
