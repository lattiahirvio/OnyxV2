package net.onyx.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.onyx.event.events.ItemUseListener;
import net.onyx.module.Category;
import net.onyx.module.Module;

public class Fakeplayer extends Module
{

    public Fakeplayer() {
        super("Fakeplayer", "spawns a fake player", false, Category.MISC);
    }

    int id;

    @Override
    public void onEnable()
    {
        OtherClientPlayerEntity player = new OtherClientPlayerEntity(mc.world, new GameProfile(null, "FoxOnTop"), null);
        Vec3d pos = mc.player.getPos();
        player.updateTrackedPosition(pos.x,pos.y,pos.z);
        player.updatePositionAndAngles(pos.x, pos.y, pos.z, mc.player.getYaw(), mc.player.getPitch());
        player.resetPosition();
        mc.world.addPlayer(player.getId(), player);
        id = player.getId();
    }

    @Override
    public void onDisable()
    {
        mc.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
    }

}
