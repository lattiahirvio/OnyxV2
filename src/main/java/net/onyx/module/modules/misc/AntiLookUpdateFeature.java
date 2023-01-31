package net.onyx.module.modules.misc;

import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PacketInputListener;
import net.onyx.module.Category;
import net.onyx.module.Module;

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
	public void onReceivePacket(PacketInputEvent event)
	{
		if (!(event.getPacket() instanceof PlayerPositionLookS2CPacket && mc.currentScreen == null))
			return;
		event.cancel();
		PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
		mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(packet.getTeleportId()));
		mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());
	}
}
