/* Decompiler 5ms, total 51ms, lines 39 */
package net.onyx.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.onyx.module.Category;
import net.onyx.module.Module;
import net.onyx.module.setting.BooleanSetting;
import net.onyx.module.setting.DecimalSetting;

public class Hitboxes extends Module {
    private DecimalSetting hitboxSize = DecimalSetting.Builder.newInstance().setName("Expand").setDescription("size of hitbox").setModule(this).setValue(0.5D).setMin(0.1D).setMax(5.0D).setAvailability(() -> {
        return true;
    }).build();
    private BooleanSetting renderHitboxes = BooleanSetting.Builder.newInstance().setName("Render hitboxes").setDescription("render expanded hitboxes").setModule(this).setValue(true).setAvailability(() -> {
        return true;
    }).build();

    public Hitboxes() {
        super("Hitboxes", "expand players hitbox", false, Category.COMBAT);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }



    public float getHitboxSize(Entity entity) {
        return this.isEnabled() && entity.getType() == EntityType.PLAYER ? this.hitboxSize.get().floatValue() : 0.0F;
    }

    public boolean shouldHitboxRender() {
        return this.renderHitboxes.get();
    }
}
