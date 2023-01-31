package net.onyx.module.modules.combat;

import net.onyx.event.events.AttackEntityListener;
import net.onyx.event.events.PostActionListener;
import net.onyx.event.events.PreActionListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.EnumSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class AutoWTap extends Module implements AttackEntityListener, PreActionListener, PostActionListener
{


    private final EnumSetting<Mode> mode = new EnumSetting<>("mode", "the mode it uses", Mode.values(), Mode.NORMAL, this);

    public AutoWTap() {
        super("AutoWTap", "Automatically sprint reset after each hit", false, Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
        eventManager.add(AttackEntityListener.class, this);
        eventManager.add(PreActionListener.class, this);
        eventManager.add(PostActionListener.class, this);
    }

    @Override
    public void onDisable()
    {
        eventManager.remove(AttackEntityListener.class, this);
        eventManager.remove(PreActionListener.class, this);
        eventManager.remove(PostActionListener.class, this);
    }

    private boolean attacked;
    private boolean sprinting;

    @Override
    public void onAttackEntity(AttackEntityEvent event)
    {
        if (mode.getValue() == Mode.PACKET)
        {
            if (mc.player.isSprinting())
            {
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
            return;
        }
        attacked = true;
        sprinting = mc.player.isSprinting();
    }

    @Override
    public void onPreAction()
    {
        if (mode.getValue() == Mode.PACKET)
            return;
        if (attacked && sprinting)
        {
            mc.player.setSprinting(false);
        }
    }

    @Override
    public void onPostAction()
    {
        if (mode.getValue() == Mode.PACKET)
            return;
        if (attacked && sprinting)
        {
            mc.player.setSprinting(true);
        }
        attacked = false;
    }

    private enum Mode
    {
        NORMAL,
        PACKET
    }
}
