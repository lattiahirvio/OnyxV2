package net.pugware.module.modules.misc;

import net.pugware.event.events.ItemUseListener;
import net.pugware.event.events.PacketInputListener;
import net.pugware.event.events.PacketOutputListener;
import net.pugware.module.Category;
import net.pugware.module.Module;
import net.pugware.module.setting.IntegerSetting;
import net.minecraft.network.Packet;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.pugware.Pugware.MC;

public class PingSpoof extends Module implements PacketOutputListener, PacketInputListener
{

    private final IntegerSetting ping = IntegerSetting.Builder.newInstance()
            .setName("ping")
            .setDescription("the ping that will be added onto your current ping")
            .setModule(this)
            .setValue(0)
            .setMin(0)
            .setMax(1500)
            .setAvailability(() -> true)
            .build();
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1000);

    public PingSpoof() {
        super("PingSpoof", "delay all of your outgoing", false, Category.MISC);
    }

    @Override
    public void onEnable()
    {
        eventManager.add(PacketOutputListener.class, this);
        eventManager.add(PacketInputListener.class, this);
    }

    @Override
    public void onDisable()
    {
        eventManager.remove(PacketOutputListener.class, this);
        eventManager.remove(PacketInputListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onSendPacket(PacketOutputEvent event)
    {
        event.cancel();
        //new Thread(() -> sendPacket(event.getPacket())).start();
        scheduler.schedule(() -> MC.getNetworkHandler().getConnection().send(event.getPacket()), ping.get(), TimeUnit.MILLISECONDS);
    }

    private void sendPacket(Packet<?> packet)
    {
        try
        {
            Thread.sleep(ping.get());
        } catch (InterruptedException e)
        {
            throw new RuntimeException("");
        }

        //MC.getNetworkHandler().sendPacket(packet); // this will cause an infinite recursion
        MC.getNetworkHandler().getConnection().send(packet);
    }

    @Override
    public void onReceivePacket(PacketInputEvent event)
    {

    }
}
