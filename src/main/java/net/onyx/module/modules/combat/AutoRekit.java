package net.onyx.module.modules.combat;

import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.InventoryUtils;

import static net.onyx.onyx.mc;

public class AutoRekit extends Module implements PlayerTickListener {

    private final IntegerSetting kitNum = IntegerSetting.Builder.newInstance()
            .setName("kitNum")
            .setDescription("the kit number you want to use")
            .setModule(this)
            .setValue(1)
            .setMin(1)
            .setMax(9)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting minTotemcount = IntegerSetting.Builder.newInstance()
            .setName("minTotemcount")
            .setDescription("when the amount of totems you have in your inventory is below this value, AutoRekit will be triggered")
            .setModule(this)
            .setValue(1)
            .setMin(1)
            .setMax(36)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting cooldown = IntegerSetting.Builder.newInstance()
            .setName("cooldown")
            .setDescription("the cooldown for AutoRekit (measured in ticks)")
            .setModule(this)
            .setValue(20)
            .setMin(0)
            .setMax(100)
            .setAvailability(() -> true)
            .build();

    private int cooldownClock = 0;

    public AutoRekit() {
        super("AutoRekit", "automatically rekit when you run out of totems", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
        cooldownClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        if (cooldownClock > 0) {
            cooldownClock--;
            return;
        }
        if (InventoryUtils.countItem(Items.TOTEM_OF_UNDYING) >= minTotemcount.get())
            return;
        mc.player.sendChatMessage("/k" + kitNum.get(), Text.literal(""));
        cooldownClock = cooldown.get();
    }
}
