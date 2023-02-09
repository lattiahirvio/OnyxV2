package net.onyx.module.modules.misc;

import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PacketInputListener;
import net.onyx.event.events.PacketOutputListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.IntegerSetting;
import net.minecraft.network.Packet;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        super("PingDelayer", "delay all of your outgoing", false, Category.MISC);
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
    public void onSendPacket(PacketOutputEvent event)
    {
        event.cancel();
        //new Thread(() -> sendPacket(event.getPacket())).start();
        scheduler.schedule(() -> mc.getNetworkHandler().getConnection().send(event.getPacket()), ping.get(), TimeUnit.MILLISECONDS);
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

        //mc.getNetworkHandler().sendPacket(packet); // this will cause an infinite recursion
        mc.getNetworkHandler().getConnection().send(packet);
    }

    @Override
    public void onReceivePacket(PacketInputEvent event)
    {

    }
}
