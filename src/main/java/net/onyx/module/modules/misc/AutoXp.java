package net.onyx.module.modules.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.onyx.Onyx;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;
import org.lwjgl.glfw.GLFW;

public class AutoXp extends Module
        implements PlayerTickListener {
    private int DropClock = 0;
    private final BooleanSetting ActivateOnRightClick = BooleanSetting.Builder.newInstance().setName("Activate On Right Click").setDescription("When deactivated, XP will also splash in Inventory Screen").setModule(this).setValue(true).setAvailability(() -> true).build();
    private final BooleanSetting OnlyMainScreen = BooleanSetting.Builder.newInstance().setName("MainList Screen Only").setDescription("When deactivated, XP will also splash in Inventory Screen").setModule(this).setValue(true).setAvailability(() -> true).build();
    private final DecimalSetting speed = DecimalSetting.Builder.newInstance().setName("Speed").setDescription("Dropping Speed").setModule(this).setValue(1.0).setMin(1.0).setMax(10.0).setStep(1.0).setAvailability(() -> true).build();

    public AutoXp() {
        super("Auto XP", "automatically splashes XP When you hold them", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        this.DropClock = 0;
        super.onEnable();
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        eventManager.remove(PlayerTickListener.class, this);
    }


    @Override
    public void onPlayerTick() {
        if (Onyx.mc.currentScreen != null && this.OnlyMainScreen.get()) {
            return;
        }
        if (this.ActivateOnRightClick.get() && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS) {
            return;
        }
        ++this.DropClock;
        if ((double)this.DropClock != this.speed.get()+1) {
            return;
        }
        this.DropClock = 0;
        ItemStack mainHandStack = Onyx.mc.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.EXPERIENCE_BOTTLE)) {
            return;
        }
        Onyx.mc.interactionManager.interactItem((PlayerEntity) Onyx.mc.player, Hand.MAIN_HAND);
        Onyx.mc.player.swingHand(Hand.MAIN_HAND);
    }
}