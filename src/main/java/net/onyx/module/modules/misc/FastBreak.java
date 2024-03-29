package net.onyx.module.modules.misc;

import net.onyx.event.events.PacketOutputListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.mixinterface.IClientPlayerInteractionManager;
import net.onyx.util.BlockUtils2;
import net.onyx.module.setting.DecimalSetting;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.util.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastBreak extends Module implements PacketOutputListener, PlayerTickListener
{

    private final DecimalSetting range = DecimalSetting.Builder.newInstance()
            .setName("range")
            .setDescription("how far can you reach the block")
            .setModule(this)
            .setValue(4.5)
            .setMin(0)
            .setMax(10)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    private final IntegerSetting delay = IntegerSetting.Builder.newInstance()
            .setName("delay")
            .setDescription("the delay for it to send stop digging packet")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(20)
            .setAvailability(() -> true)
            .build();

    private int delayClock = 0;
    private boolean letgo = false;

    private boolean mining = false;
    private BlockPos miningBlock = null;
    private Direction miningDirection = null;

    public FastBreak()
    {
        super("FastBreak", "break fast", false, Category.MISC);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        delayClock = 0;
        eventManager.add(PacketOutputListener.class, this);
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        eventManager.remove(PacketOutputListener.class, this);
        eventManager.remove(PlayerTickListener.class, this);
        cancelMining();
    }

    @Override
    public void onSendPacket(PacketOutputEvent event)
    {
        if (letgo)
            return;

        if (!(event.getPacket() instanceof PlayerActionC2SPacket packet))
            return;

        if (BlockUtils2.getBlock(packet.getPos()) == Blocks.BEDROCK)
            return;

        if (packet.getAction() == Action.START_DESTROY_BLOCK)
        {
            event.cancel();

            int oldSlot = mc.player.getInventory().selectedSlot;
            InventoryUtils.selectItemFromHotbar(item -> item == Items.NETHERITE_PICKAXE);
            ((IClientPlayerInteractionManager) mc.interactionManager).cwSyncSelectedSlot();

            letgo = true;
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, packet.getPos(), packet.getDirection()));
            letgo = false;
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, packet.getPos(), packet.getDirection()));

            mc.player.getInventory().selectedSlot = oldSlot;
            ((IClientPlayerInteractionManager) mc.interactionManager).cwSyncSelectedSlot();

            mining = true;
            miningBlock = packet.getPos();
            miningDirection = packet.getDirection();
        }
    }

    public void cancelMining()
    {
        if (!mining)
            return;
        mining = false;
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, miningBlock, miningDirection));
        ((IClientPlayerInteractionManager) mc.interactionManager).setCurrentBreakingPos(new BlockPos(-1, -1, -1));
        ((IClientPlayerInteractionManager) mc.interactionManager).setBreakingBlock(false);
        delayClock = 0;
    }

    public boolean isMining()
    {
        return mining && isEnabled();
    }

    @Override
    public void onPlayerTick()
    {
        if (!mining)
            return;

        if (!BlockUtils2.hasBlock(miningBlock))
        {
            cancelMining();
            return;
        }

        if (!BlockUtils2.isBlockReachable(miningBlock, range.get()))
        {
            cancelMining();
            return;
        }

        if (mc.player.isUsingItem())
            return;

        if (delayClock > 0)
        {
            delayClock--;
            return;
        }
        delayClock = delay.get();

        int oldSlot = mc.player.getInventory().selectedSlot;
        InventoryUtils.selectItemFromHotbar(item -> item == Items.NETHERITE_PICKAXE);
        ((IClientPlayerInteractionManager) mc.interactionManager).cwSyncSelectedSlot();

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, miningBlock, miningDirection));

        mc.player.getInventory().selectedSlot = oldSlot;
    }
}
