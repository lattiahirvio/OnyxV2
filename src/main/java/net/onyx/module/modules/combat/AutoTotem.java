package net.onyx.module.modules.combat;

import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.mixinterface.IClientPlayerInteractionManager;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.InventoryUtils;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTotem extends Module implements PlayerTickListener
{
    private final IntegerSetting mode = IntegerSetting.Builder.newInstance()
            .setName("mode")
            .setDescription("mode 0: fast totem, mode 1: inv swap, mode 2: hotbar swap")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(2)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting delay = IntegerSetting.Builder.newInstance()
            .setName("delay")
            .setDescription("the delay for inv swap or hotbar swap")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(10)
            .setAvailability(() -> true)
            .build();

    private int nextTickSlot;
    private int totems;

    private boolean swapped;

    private int clock;

    public AutoTotem()
        {
            super("AutoTotem", "automatically put a totem on your offhand if there isn't already one", false, Category.COMBAT);
        }


    @Override
    public void onEnable()
    {
        nextTickSlot = -1;
        totems = 0;
        swapped = false;
        clock = delay.get();
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable()
    {
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void onPlayerTick()
    {
        switch (mode.get())
        {
            case 0:
                doFastTotem();
                return;
            case 1:
                doInvSwap();
                return;
            case 2:
                doHotbarSwap();
                return;
        }
    }

    private void doFastTotem()
    {
        finishMovingTotem();

        PlayerInventory inventory = mc.player.getInventory();
        int nextTotemSlot = searchForTotems(inventory);

        ItemStack offhandStack = inventory.getStack(40);
        if(isTotem(offhandStack))
        {
            totems++;
            return;
        }

        if(mc.currentScreen instanceof HandledScreen
                && !(mc.currentScreen instanceof AbstractInventoryScreen))
            return;

        if(nextTotemSlot != -1)
            moveTotem(nextTotemSlot, offhandStack);
    }

    private void doInvSwap()
    {
        PlayerInventory inventory = mc.player.getInventory();
        ItemStack offhandStack = inventory.getStack(40);
        if (isTotem(offhandStack))
        {
            swapped = false;
            clock = delay.get();
            return;
        }
        int totemSlot = searchForTotems(inventory);
        if (totemSlot == -1)
            return;
        if (clock > 0)
        {
            clock--;
            return;
        }
        if (swapped)
            return;
        mc.interactionManager.clickSlot(0, totemSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);
        swapped = true;
    }

    private void doHotbarSwap()
    {
        PlayerInventory inventory = mc.player.getInventory();
        ItemStack offhandStack = inventory.getStack(40);
        if (isTotem(offhandStack))
        {
            swapped = false;
            clock = delay.get();
            return;
        }
        if (clock > 0)
        {
            clock--;
            return;
        }
        if (swapped)
            return;
        if (mc.currentScreen != null)
            return;
        if (!InventoryUtils.selectItemFromHotbar(item -> item == Items.TOTEM_OF_UNDYING))
        {
            swapped = false;
            return;
        }
        ((IClientPlayerInteractionManager) mc.interactionManager).cwSyncSelectedSlot();
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
        swapped = true;
    }

    private void moveTotem(int nextTotemSlot, ItemStack offhandStack)
    {
        boolean offhandEmpty = offhandStack.isEmpty();

        mc.interactionManager.clickSlot(0, nextTotemSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);

        if(!offhandEmpty)
            nextTickSlot = nextTotemSlot;
    }

    private void finishMovingTotem()
    {
        if(nextTickSlot == -1)
            return;

        mc.interactionManager.clickSlot(0, nextTickSlot, 0, SlotActionType.PICKUP, mc.player);
        nextTickSlot = -1;
    }

    private int searchForTotems(PlayerInventory inventory)
    {
        totems = 0;
        int nextTotemSlot = -1;

        for(int slot = 0; slot <= 36; slot++)
        {
            if(!isTotem(inventory.getStack(slot)))
                continue;

            totems++;

            if(nextTotemSlot == -1)
                nextTotemSlot = slot < 9 ? slot + 36 : slot;
        }

        return nextTotemSlot;
    }

    private boolean isTotem(ItemStack stack)
    {
        return stack.getItem() == Items.TOTEM_OF_UNDYING;
    }
}
