package net.pugware.module.modules.misc;

import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PacketInputListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

import static net.pugware.Pugware.MC;

public class AntiLookUpdateFeature extends Module implements PacketInputListener
{

	public AntiLookUpdateFeature() {
		super("AntiLookUpdate", "removes lookupdate to look more like marlow!", false, Category.MISC);
	}

	@Override
	public void onEnable()
	{
		eventManager.add(PacketInputListener.class, this);
	}

	@Override
	public void onDisable()
	{
		eventManager.remove(PacketInputListener.class, this);
	}

	@Override
	public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

	}

	@Override
	public void onReceivePacket(PacketInputEvent event)
	{
		if (!(event.getPacket() instanceof PlayerPositionLookS2CPacket && MC.currentScreen == null))
			return;
		event.cancel();
		PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
		MC.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(packet.getTeleportId()));
		MC.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
	}
}
