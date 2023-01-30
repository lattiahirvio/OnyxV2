package net.pugware.module.modules.combat;

import net.pugware.event.events.AttackEntityListener;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PostActionListener;
import net.pugware.event.events.PreActionListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.EnumSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.pugware.module.Module;

import static net.pugware.Pugware.MC;

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

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    private boolean attacked;
    private boolean sprinting;

    @Override
    public void onAttackEntity(AttackEntityEvent event)
    {
        if (mode.getValue() == Mode.PACKET)
        {
            if (MC.player.isSprinting())
            {
                MC.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                MC.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(MC.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
            return;
        }
        attacked = true;
        sprinting = MC.player.isSprinting();
    }

    @Override
    public void onPreAction()
    {
        if (mode.getValue() == Mode.PACKET)
            return;
        if (attacked && sprinting)
        {
            MC.player.setSprinting(false);
        }
    }

    @Override
    public void onPostAction()
    {
        if (mode.getValue() == Mode.PACKET)
            return;
        if (attacked && sprinting)
        {
            MC.player.setSprinting(true);
        }
        attacked = false;
    }

    private enum Mode
    {
        NORMAL,
        PACKET
    }
}
