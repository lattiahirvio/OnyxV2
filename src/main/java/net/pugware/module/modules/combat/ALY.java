//
// Decompiled by Procyon v0.5.36
//

package net.pugware.module.modules.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.keybind.Keybind;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.IntegerSetting;
import net.pugware.module.setting.KeybindSetting;
import net.pugware.util.AccessorUtils;
import net.pugware.util.InventoryUtils;

import static net.pugware.Pugware.MC;

public class ALY extends Module implements PlayerTickListener
{
    private IntegerSetting minTotems;
    private IntegerSetting minPearls;
    private IntegerSetting dropInterval;
    private Keybind activateKeybind;
    private KeybindSetting activateKey;
    private int dropClock;

    public ALY() {
        super("AutoLooterLegit", "yeet loots", false, Category.COMBAT);
        this.minTotems = new IntegerSetting.Builder().setName("Totems To Keep").setDescription("minimum totems to keep in your inventory").setModule(this).setValue(2).setMin(0).setMax(36).setAvailability(() -> true).build();
        this.minPearls = new IntegerSetting.Builder().setName("Pearls To Keep").setDescription("minimum pearls to keep in your inventory").setModule(this).setValue(16).setMin(0).setMax(576).setAvailability(() -> true).build();
        this.dropInterval = new IntegerSetting.Builder().setName("Dropping Speed").setDescription("the speed of dropping items").setModule(this).setValue(0).setMin(0).setMax(10).setAvailability(() -> true).build();
        this.activateKeybind = new Keybind("AutoLootYeeter_activateKeybind", 75, false, false, null);
        this.activateKey = new KeybindSetting.Builder().setName("Binding").setDescription("the key to activate it").setModule(this).setValue(this.activateKeybind).build();
        this.dropClock = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ALY.eventManager.add(PlayerTickListener.class, this);
        this.dropClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ALY.eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        final PlayerInventory inv = ALY.mc.player.getInventory();
        if (this.dropClock != 0) {
            --this.dropClock;
            return;
        }
        if (!(MC.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (!this.activateKeybind.isDown()) {
            return;
        }
        final Screen screen = MinecraftClient.getInstance().currentScreen;
        final HandledScreen<?> gui = (HandledScreen<?>)screen;
        final Slot slot = AccessorUtils.getSlotUnderMouse(gui);
        if (slot == null) {
            return;
        }
        final int SlotUnderMouse = AccessorUtils.getSlotUnderMouse(gui).getIndex();
        if (SlotUnderMouse > 35) {
            return;
        }
        if (SlotUnderMouse < 9) {
            return;
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.TOTEM_OF_UNDYING) {
            if (InventoryUtils.countItem(Items.TOTEM_OF_UNDYING) <= this.minTotems.get()) {
                return;
            }
            ALY.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)ALY.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity)ALY.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.ENDER_PEARL) {
            if (InventoryUtils.countItem(Items.ENDER_PEARL) <= this.minPearls.get()) {
                return;
            }
            ALY.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)ALY.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity)ALY.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.DIRT) {
            ALY.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)ALY.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity)ALY.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.COBBLESTONE) {
            ALY.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)ALY.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity)ALY.mc.player);
            this.dropClock = this.dropInterval.get();
        }
        if (((ItemStack)inv.main.get(SlotUnderMouse)).getItem() == Items.GRASS_BLOCK) {
            ALY.mc.interactionManager.clickSlot(((PlayerScreenHandler)((InventoryScreen)ALY.mc.currentScreen).getScreenHandler()).syncId, SlotUnderMouse, 1, SlotActionType.THROW, (PlayerEntity)ALY.mc.player);
            this.dropClock = this.dropInterval.get();
        }
    }
}