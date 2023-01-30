package net.onyx.module.modules.combat;

import net.onyx.event.events.FrameBeginListener;
import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

import java.util.Random;

import static net.onyx.onyx.mc;

public class AutoHeadBob extends Module implements PlayerTickListener
{

    private final DecimalSetting amplitude = DecimalSetting.Builder.newInstance()
            .setName("HB amplitude")
            .setDescription("how much you want to bob your head")
            .setModule(this)
            .setValue(0.25)
            .setMin(0)
            .setMax(50)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting frequency = DecimalSetting.Builder.newInstance()
            .setName("How fast")
            .setDescription("how fast you want to bob your head")
            .setModule(this)
            .setValue(5.0)
            .setMin(0)
            .setMax(15)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting verticalRandomness = BooleanSetting.Builder.newInstance()
            .setName("verticalRandomness")
            .setDescription("whether or not to add random vertical offset")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final BooleanSetting horizontalRandomness = BooleanSetting.Builder.newInstance()
            .setName("horizontalRandomness")
            .setDescription("whether or not to add random horizontal offset")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();

    private final DecimalSetting randomness = DecimalSetting.Builder.newInstance()
            .setName("randomness")
            .setDescription("how random")
            .setModule(this)
            .setValue(0.2)
            .setMin(0)
            .setMax(5)
            .setStep(0.1)
            .setAvailability(() -> true)
            .build();

    private final Random rng = new Random();

    public AutoHeadBob() {
        super("AutoHeadBob", "bob your head like ceeew when crystalling, useful on high ping", false, Category.COMBAT);
    }

    private long startTime = 0;
    private long lastFrame = 0;

    @Override
    public void onEnable()
    {
        startTime = Util.getMeasuringTimeMs();
        lastFrame = startTime;
        eventManager.add(PlayerTickListener.class, this);
    }

    @Override
    public void onDisable()
    {
        eventManager.remove(PlayerTickListener.class, this);
    }

    @Override
    public void ItemUseListener(ItemUseListener.ItemUseEvent event) {

    }

    @Override
    public void onPlayerTick()
    {
        if (mc.player == null)
            return;
        if (!mc.player.getMainHandStack().isOf(Items.END_CRYSTAL))
            return;

        float tickDelta = mc.getTickDelta();
        long currentTime = Util.getMeasuringTimeMs();
        long delta = currentTime - lastFrame;
        lastFrame = currentTime;

        // I want the offset from the starting point of the head bob to be a sin wave,
        // so in the beginning of every frame I will calculate the derivative of the wave and add that to the pitch
        long timePast = currentTime - startTime;
        double f = timePast / 1000.0 * 2.0 * Math.PI * frequency.get();
        double g = -Math.cos(f) * amplitude.get(); // derivative of sin is cosine
        double h = 0.0;

        // random offsets
        double random = rng.nextDouble();
        // mapping [0, 1] to [-randomness, randomness]
        double randomnessV = randomness.get();
        random = random * 2 * randomnessV - randomnessV;

        if (verticalRandomness.get())
            g += random;
        if (horizontalRandomness.get())
            h += random;

        mc.player.changeLookDirection(h, g);

//		float pitch = mc.player.getPitch();
//		float yaw = mc.player.getYaw();
//		mc.player.setPitch(pitch + (float) g);
//		mc.player.setYaw(yaw + (float) h);
    }
}
