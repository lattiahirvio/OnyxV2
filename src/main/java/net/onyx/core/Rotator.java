package net.onyx.core;

import net.minecraft.util.math.Vec3d;
import net.onyx.Onyx;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.util.RotationUtils;

import java.util.ArrayList;

import static net.onyx.Onyx.mc;

public class Rotator implements PlayerTickListener {

    public Rotator() {
        Onyx.INSTANCE.getEventManager().add(PlayerTickListener.class, this);
    }

    private final ArrayList<Rotation> rotations = new ArrayList<>();
    private Runnable callback;

    @Override
    public void onPlayerTick() {
        if (rotations.size() != 0) {
            RotationUtils.setRotation(rotations.get(rotations.size() - 1));
            rotations.remove(rotations.size() - 1);
            if (rotations.size() == 0)
                callback.run();
        }
    }

    public void stepToward(Vec3d pos, int steps, Runnable callback) {
        stepToward(RotationUtils.getNeededRotations(pos), steps, callback);
    }

    public void stepToward(Rotation rotation, int steps, Runnable callback) {
        rotations.clear();
        float yaw = rotation.getYaw();
        float pitch = rotation.getPitch();
        float stepYaw = (yaw - mc.player.getYaw()) / (float) steps;
        float stepPitch = (pitch - mc.player.getPitch()) / (float) steps;
        for (int i = 0; i < steps; i++) {
            rotations.add(new Rotation(yaw, rotation.isIgnoreYaw(), pitch, rotation.isIgnorePitch()));
            yaw -= stepYaw;
            pitch -= stepPitch;
        }
        this.callback = callback;
    }
}
