package net.onyx.module.modules.combat;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.onyx.Onyx;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.IntegerSetting;

public class HotbarRestock extends Module implements PlayerTickListener {

    public BooleanSetting cry;
    public BooleanSetting oby;
    public BooleanSetting pearl;
    public BooleanSetting gap;

    public IntegerSetting delay;
    public IntegerSetting crySlot;
    public IntegerSetting obySlot;
    public IntegerSetting pearlSlot;
    public IntegerSetting gapSlot;
    private int invClock;

    public HotbarRestock() {
        super("HotbarRestock", "Automatically restocks hotbar when inventory is open", false, Category.COMBAT);
        this.crySlot = new IntegerSetting.Builder().setName("Crystal Slot").setDescription("").setModule(this).setValue(3).setMin(0).setMax(8).setAvailability(() -> true).build();
        this.obySlot = new IntegerSetting.Builder().setName("Obsidian Slot").setDescription("").setModule(this).setValue(2).setMin(0).setMax(8).setAvailability(() -> true).build();
        this.pearlSlot = new IntegerSetting.Builder().setName("Pearls Slot").setDescription("").setModule(this).setValue(1).setMin(0).setMax(9).setAvailability(() -> true).build();
        this.gapSlot = new IntegerSetting.Builder().setName("Golden Apples Slot").setDescription("").setModule(this).setValue(4).setMin(0).setMax(8).setAvailability(() -> true).build();

        this.cry = BooleanSetting.Builder.newInstance().setName("Crystals").setDescription("").setModule(this).setValue(true).setAvailability(() -> true).build();
        this.oby = BooleanSetting.Builder.newInstance().setName("Obsidians").setDescription("").setModule(this).setValue(true).setAvailability(() -> true).build();
        this.pearl = BooleanSetting.Builder.newInstance().setName("Pearls").setDescription("").setModule(this).setValue(true).setAvailability(() -> true).build();
        this.gap = BooleanSetting.Builder.newInstance().setName("Golden Apples").setDescription("").setModule(this).setValue(true).setAvailability(() -> true).build();
        this.delay = new IntegerSetting.Builder().setName("Delay").setDescription("").setModule(this).setValue(0).setMin(0).setMax(8).setAvailability(() -> true).build();
//        this.crySlot = new DecimalSetting("Crystal Slot", 0.0, 8.0, 0.0, 1.0);
        this.invClock = -1;
    }

    @Override
    public void onEnable() {
        this.invClock = -1;
        eventManager.add(PlayerTickListener.class, this);
        super.onEnable();
    }

    @Override
    public void onPlayerTick() {
        if (!(Onyx.mc.currentScreen instanceof InventoryScreen)) {
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
        final PlayerInventory inv = Onyx.mc.player.getInventory();
        final ItemStack crystalSlot = (ItemStack)inv.main.get(this.crySlot.get());
        final ItemStack obsidianSlot = (ItemStack)inv.main.get(this.obySlot.get());
        final ItemStack enderpearlSlot = (ItemStack)inv.main.get(this.pearlSlot.get());
        final ItemStack gappleSlot = (ItemStack)inv.main.get(this.gapSlot.get());
        if (this.cry.get() && crystalSlot.getItem() != Items.END_CRYSTAL) {
            final int slot = this.findCrystalSlot();
            if (slot != -1) {
                assert Onyx.mc.interactionManager != null;
                Onyx.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)Onyx.mc.currentScreen).getScreenHandler()).syncId, slot, this.crySlot.get(), SlotActionType.SWAP, Onyx.mc.player);
            }
        }
        else if (this.oby.get() && obsidianSlot.getItem() != Items.OBSIDIAN) {
            final int slot = this.findObsidianSlot();
            if (slot != -1) {
                assert Onyx.mc.interactionManager != null;
                Onyx.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)Onyx.mc.currentScreen).getScreenHandler()).syncId, slot, this.obySlot.get(), SlotActionType.SWAP, Onyx.mc.player);
            }
        }
        else if (this.pearl.get() && enderpearlSlot.getItem() != Items.ENDER_PEARL) {
            final int slot = this.findPearlSlot();
            if (slot != -1) {
                assert Onyx.mc.interactionManager != null;
                Onyx.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)Onyx.mc.currentScreen).getScreenHandler()).syncId, slot, this.pearlSlot.get(), SlotActionType.SWAP, Onyx.mc.player);
            }
        }
        else if (this.gap.get() && gappleSlot.getItem() != Items.GOLDEN_APPLE) {
            final int slot = this.findGappleSlot();
            if (slot != -1) {
                assert Onyx.mc.interactionManager != null;
                Onyx.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)Onyx.mc.currentScreen).getScreenHandler()).syncId, slot, this.gapSlot.get(), SlotActionType.SWAP, Onyx.mc.player);
            }
        }
    }

    private int findCrystalSlot() {
        assert Onyx.mc.player != null;
        final PlayerInventory inv = Onyx.mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (((ItemStack)inv.main.get(i)).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private int findObsidianSlot() {
        assert Onyx.mc.player != null;
        final PlayerInventory inv = Onyx.mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (((ItemStack)inv.main.get(i)).getItem() == Items.OBSIDIAN) {
                return i;
            }
        }
        return -1;
    }

    private int findPearlSlot() {
        assert Onyx.mc.player != null;
        final PlayerInventory inv = Onyx.mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (((ItemStack)inv.main.get(i)).getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1;
    }

    private int findGappleSlot() {
        assert Onyx.mc.player != null;
        final PlayerInventory inv = Onyx.mc.player.getInventory();
        for (int i = 9; i < 36; ++i) {
            if (((ItemStack)inv.main.get(i)).getItem() == Items.GOLDEN_APPLE) {
                return i;
            }
        }
        return -1;
    }
}
