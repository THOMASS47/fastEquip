package com.thomass47.fastequip.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketFastEquip implements IMessage {

    public int slotId; // The slot index in the open container that was clicked

    public PacketFastEquip() {}

    public PacketFastEquip(int slotId) {
        this.slotId = slotId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.slotId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.slotId);
    }

    public static class Handler implements IMessageHandler<PacketFastEquip, IMessage> {

        @Override
        public IMessage onMessage(PacketFastEquip message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // The item they picked up should be in their cursor since the vanilla window click packet arrives first
            ItemStack cursorStack = player.inventory.getItemStack();

            if (cursorStack != null && cursorStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) cursorStack.getItem();
                int equipmentSlot = 4 - armor.armorType;

                ItemStack wornArmor = player.getEquipmentInSlot(equipmentSlot);

                // Equip the new armor
                player.setCurrentItemOrArmor(equipmentSlot, cursorStack.copy());

                // Clear the cursor
                player.inventory.setItemStack(null);

                // Put the old armor back in the clicked slot
                if (player.openContainer != null && message.slotId >= 0
                    && message.slotId < player.openContainer.inventorySlots.size()) {
                    net.minecraft.inventory.Slot slot = (net.minecraft.inventory.Slot) player.openContainer.inventorySlots
                        .get(message.slotId);
                    slot.putStack(wornArmor);
                } else if (wornArmor != null) {
                    // Fallback: drop it if slot is invalid
                    player.dropPlayerItemWithRandomChoice(wornArmor, false);
                }

                // Update client
                player.openContainer.detectAndSendChanges();
                // We also need to send the player's updated inventory/equipment because detectAndSendChanges only
                // updates the open container (like the chest) and main inventory, it might not update the equipment
                // slots visually right away
                // Wait, equipment slots are synced by EntityTracker natively!
            }

            return null;
        }
    }
}
