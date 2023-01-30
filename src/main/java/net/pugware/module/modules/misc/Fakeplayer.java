package net.pugware.module.modules.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.pugware.event.events.ItemUseListener;
import net.pugware.module.Category;
import net.pugware.module.Module;

import static net.pugware.Pugware.MC;

public class Fakeplayer extends Module
{

    public Fakeplayer() {
        super("Fakeplayer", "spawns a fake player", false, Category.MISC);
    }

    int id;

    @Override
    public void onEnable()
    {
        OtherClientPlayerEntity player = new OtherClientPlayerEntity(MC.world, new GameProfile(null, "FoxOnTop"), null);
        Vec3d pos = MC.player.getPos();
        player.updateTrackedPosition(pos.x,pos.y,pos.z);
        player.updatePositionAndAngles(pos.x, pos.y, pos.z, MC.player.getYaw(), MC.player.getPitch());
        player.resetPosition();
        MC.world.addPlayer(player.getId(), player);
        id = player.getId();
    }

    @Override
    public void onDisable()
    {
        MC.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }
}
