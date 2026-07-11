package com.thomass47.fastequip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class FastEquipHandler {

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

    //........ ||
    // AI slop || feel free to improve
    //........ \/

    private ItemStack previousCursorStack = null;

    @SubscribeEvent
    public void onInventoryMouseClick(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (!Config.isInventoryFastEquipEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        ItemStack currentCursor = mc.thePlayer.inventory.getItemStack();

        // If the cursor was empty last frame and now has an armor piece, the game just picked it up
        if (previousCursorStack == null && currentCursor != null && currentCursor.getItem() instanceof ItemArmor) {
            // If they are physically holding the right click button right now, it was a right-click pickup
            if (Mouse.isButtonDown(1)) {
                if (event.gui instanceof GuiContainer) {
                    GuiContainer container = (GuiContainer) event.gui;
                    try {
                        Slot hoveredSlot = ReflectionHelper
                            .getPrivateValue(GuiContainer.class, container, "theSlot", "field_147006_u");
                        if (hoveredSlot != null) {
                            ItemArmor armor = (ItemArmor) currentCursor.getItem();
                            int armorSlotIndex = 5 + armor.armorType;

                            // Check if they are already clicking the player's native armor slots in GuiInventory
                            boolean isClickingArmorSlot = (event.gui instanceof GuiInventory)
                                && (hoveredSlot.slotNumber == armorSlotIndex);

                            if (!isClickingArmorSlot) {
                                // Send packet to server to perform the swap
                                CommonProxy.network.sendToServer(
                                    new com.thomass47.fastequip.network.PacketFastEquip(hoveredSlot.slotNumber));

                                // Visually clear the cursor on the client immediately to avoid flicker
                                mc.thePlayer.inventory.setItemStack(null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        previousCursorStack = currentCursor;
    }
}
