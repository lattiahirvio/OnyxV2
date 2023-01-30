package net.pugware.module.modules.combat;

import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.pugware.Pugware;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PlayerTickListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.BlockUtils2;
import net.pugware.module.setting.BooleanSetting;
import net.pugware.module.setting.IntegerSetting;
import net.pugware.util.BlockUtils;
import net.pugware.util.InventoryUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.event.InputEvent;

import static net.pugware.Pugware.MC;

public class AnchorMacro extends Module implements PlayerTickListener {
    private boolean isClicking = false;

    private final IntegerSetting delay = IntegerSetting.Builder.newInstance()
            .setName("delay")
            .setDescription("delay between steps")
            .setModule(this)
            .setValue(1)
            .setMin(1)
            .setMax(8)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting disableOnCrouch = BooleanSetting.Builder.newInstance()
            .setName("disableOnCrouch")
            .setDescription("disable the macro while crouching")
            .setModule(this)
            .setValue(true)
            .setAvailability(() -> true)
            .build();

    private int GlowStonePlaceClock = 0;
    private boolean AnchorActivateQueued = false;
    private boolean GlowStonePlaceQueued;
    private int AnchorActivateClock = 0;
    private boolean isActivatingUnactivatedAnchor = false;

    public AnchorMacro() {
        super("AnchorMacro", "lalalalalalalalala lalalala", false, Category.COMBAT);
    }

    @Override
    public void onEnable() {
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable() {
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick() {
        //
        // MAIN ANCHOR MACRO CODE
        //
        if (disableOnCrouch.get() && MC.player.isSneaking()) {
            return;
        }
        if (MC.player.isHolding(Items.RESPAWN_ANCHOR) && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS && !isClicking) {
            isClicking = true;
            GlowStonePlaceQueued = true;
            GlowStonePlaceClock = delay.get();
        }
        if (GlowStonePlaceQueued && !isActivatingUnactivatedAnchor) {
            if (GlowStonePlaceClock == delay.get() / 2) {
                InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
            }
            if (GlowStonePlaceClock == 0) {
                    Pugware.INSTANCE.getRobot().mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    Pugware.INSTANCE.getRobot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                    AnchorActivateQueued = true;
                    AnchorActivateClock = delay.get();
                    GlowStonePlaceQueued = false;
            }
            GlowStonePlaceClock--;
        }
        if (AnchorActivateQueued && !isActivatingUnactivatedAnchor) {
            try {
                if (AnchorActivateClock == delay.get() / 2) {
                    InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                }
                if (AnchorActivateClock == 0) {
                    Robot robot = new Robot();
                    robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                    AnchorActivateQueued = false;
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
            AnchorActivateClock--;
        }
        //
        //ACTIVATE UNACTIVATED ANCHOR MACRO
        //
        if (MC.crosshairTarget instanceof BlockHitResult hit) {
            if (BlockUtils2.isBlock(Blocks.RESPAWN_ANCHOR, hit.getBlockPos())) {
                if (BlockUtils2.getBlockState(hit.getBlockPos()).get(RespawnAnchorBlock.CHARGES) == 0) {
                    if (MC.player.isHolding(Items.TOTEM_OF_UNDYING) && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS && !isClicking) {
                        isClicking = true;
                        GlowStonePlaceQueued = true;
                        GlowStonePlaceClock = delay.get();
                        isActivatingUnactivatedAnchor = true;
                    }
                }
            }
        }
        if (isActivatingUnactivatedAnchor) {
            if (GlowStonePlaceQueued) {
                if (GlowStonePlaceClock == delay.get() / 2) {
                    InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                }
                if (GlowStonePlaceClock == 0) {
                    try {
                        Robot robot = new Robot();
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        AnchorActivateQueued = true;
                        AnchorActivateClock = delay.get();
                        GlowStonePlaceQueued = false;
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
                GlowStonePlaceClock--;
            }
            if (AnchorActivateQueued) {
                try {
                    if (AnchorActivateClock == delay.get() / 2) {
                        InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                    }
                    if (AnchorActivateClock == 0) {
                        Robot robot = new Robot();
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        AnchorActivateQueued = false;
                        isActivatingUnactivatedAnchor = false;
                    }
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                AnchorActivateClock--;
            }
        }
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_RELEASE && isClicking) {
            isClicking = false;
        }
    }
}