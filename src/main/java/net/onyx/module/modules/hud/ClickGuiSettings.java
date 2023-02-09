package net.onyx.module.modules.hud;

import net.onyx.event.events.ItemUseListener;
import net.onyx.event.events.PlayerTickListener;
import net.onyx.keybind.Keybind;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.IntegerSetting;
import net.onyx.module.setting.KeybindSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class ClickGuiSettings extends Module implements PlayerTickListener {
    public static final Logger LOGGER = LoggerFactory.getLogger("ClickGui");
    private final IntegerSetting hudColorRed = IntegerSetting.Builder.newInstance()
            .setName("red")
            .setDescription("hud color red")
            .setModule(this)
            .setValue(171)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    private final IntegerSetting hudColorGreen = IntegerSetting.Builder.newInstance()
            .setName("green")
            .setDescription("hud color green")
            .setModule(this)
            .setValue(2)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    private final IntegerSetting hudColorBlue = IntegerSetting.Builder.newInstance()
            .setName("blue")
            .setDescription("hud color blue")
            .setModule(this)
            .setValue(2)
            .setMin(0)
            .setMax(255)
            .setAvailability(() -> true)
            .build();
    private final BooleanSetting rgbEffect = BooleanSetting.Builder.newInstance()
            .setName("Breathing")
            .setDescription("Setting to make funny gaming rgb")
            .setModule(this)
            .setValue(false)
            .setAvailability(() -> true)
            .build();
    public final KeybindSetting activateKey = new KeybindSetting.Builder()
            .setName("activateKey")
            .setDescription("the key to activate it")
            .setModule(this)
            .setValue(new Keybind("",0,false,false,null))
            .build();

    public double h = 360;
    public double s = 1;
    public double v = 1;

    public ClickGuiSettings(){
        super("ClickGui", "modify the gui", true, Category.HUD);
        eventManager.add(PlayerTickListener.class,this);
    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    @Override
    public void onDisable() {
        this.setEnabled(true);
    }


    public double getHudColorBlue() {
        if(rgbEffect.get()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return rgb.getBlue()/255.0;
        }
        return hudColorBlue.get()/255.0;
    }

    public double getHudColorGreen() {
        if(rgbEffect.get()) {
            int rgb = (Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return new Color(rgb).getGreen()/255.0;
        }
        return hudColorGreen.get()/255.0;
    }

    public double getHudColorRed() {
        if(rgbEffect.get()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return rgb.getRed()/255.0;
        }
        return hudColorRed.get()/255.0;
    }

    @Override
    public void onPlayerTick() {
        if(h<360){
            h++;
        } else {
            h=0;
        }
    }
}
