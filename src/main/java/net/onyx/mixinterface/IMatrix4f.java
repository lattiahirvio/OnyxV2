package net.onyx.mixinterface;

import net.onyx.util.Vec4d;
import net.minecraft.util.math.Vec3d;

public interface IMatrix4f
{
    Vec4d multiply(Vec4d v);

    Vec3d multiply(Vec3d v);
}

