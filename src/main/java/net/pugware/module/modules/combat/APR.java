// 
// Decompiled by Procyon v0.5.36
// 

package net.pugware.module.modules.combat;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BooleanSetting;
import net.pugware.module.setting.IntegerSetting;

public class APR extends Module implements PlayerTickListener
{
    private final IntegerSetting delay = IntegerSetting.Builder.newInstance().setName("Delay").setDescription("the delay for auto switch after opening inventory").setModule(this).setValue(1).setMin(0).setMax(20).setAvailability(() -> true).build();
    private final IntegerSetting pearlSlot;
    private final BooleanSetting forcePearl = BooleanSetting.Builder.newInstance().setName("Replace Junk Items").setDescription("replace your main hand item (if there is one)").setModule(this).setValue(true).setAvailability(() -> true).build();
    private int invClock;
    
    public APR() {
        super("AutoPearlRestock", "Automatically puts Pearls in your hotbar when you need some", false, Category.COMBAT);
        this.pearlSlot = new IntegerSetting.Builder().setName("Pearl Slot").setDescription("the pearl slot to restock").setModule(this).setValue(5).setMin(0).setMax(8).setAvailability(() -> true).build();
        this.invClock = -1;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.invClock = -1;
        APR.eventManager.add(PlayerTickListener.class, this);
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        APR.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        if (!(APR.mc.currentScreen instanceof InventoryScreen)) {
            this.invClock = -1;
            return;
        }
        if (this.invClock == -1) {
            this.invClock = this.delay.get();
        }
        if (this.invClock > 0) {
            --this.invClock;
            return;
        }
        assert APR.mc.player != null;
        final PlayerInventory inv = APR.mc.player.getInventory();
        final ItemStack pearlHand = (ItemStack)inv.main.get((int)this.pearlSlot.get());
        if (pearlHand.isEmpty() || (this.forcePearl.get() && pearlHand.getItem() != Items.ENDER_PEARL)) {
            final int slot = this.findPearlSlot();
            if (slot != -1) {
                assert APR.mc.interactionManager != null;
                APR.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)APR.mc.currentScreen).getScreenHandler()).syncId, slot, (int)this.pearlSlot.get(), SlotActionType.SWAP, (PlayerEntity)APR.mc.player);
            }
        }
    }
    
    private int findPearlSlot() {
        assert APR.mc.player != null;
        final PlayerInventory inv = APR.mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (((ItemStack)inv.main.get(i)).getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }
}
