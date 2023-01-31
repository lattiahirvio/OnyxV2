package net.onyx.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.onyx.Onyx;
import net.onyx.gui.ClickGui;
import net.onyx.gui.window.ModuleSettingWindow;
import net.onyx.gui.window.Window;
import net.onyx.mixinterface.ITextRenderer;
import net.onyx.module.Module;
import net.onyx.module.modules.hud.ClickGuiSettings;
import net.onyx.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import static net.onyx.Onyx.mc;

public class ModuleButtonComponent extends Component {

    private final Module module;
    private boolean settingWindowOpened = false;
    private ModuleSettingWindow moduleSettingWindow;

    public ModuleButtonComponent(Window parent, Module module, double x, double y) {
        super(parent, x, y, 10, module.getName());
        this.module = module;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double r = ClickGuiSettings.class.cast(Onyx.INSTANCE.getModuleManager().getModule(ClickGuiSettings.class)).getHudColorRed();
        double g = ClickGuiSettings.class.cast(Onyx.INSTANCE.getModuleManager().getModule(ClickGuiSettings.class)).getHudColorGreen();
        double b = ClickGuiSettings.class.cast(Onyx.INSTANCE.getModuleManager().getModule(ClickGuiSettings.class)).getHudColorBlue();
        double parentX = parent.getX();
        double parentY = parent.getY();
        double parentWidth = parent.getWidth();
        double parentLength = parent.getLength();
        double parentX2 = parent.getX() + parentWidth;
        double parentY2 = parent.getY() + parentLength;
        double x = getX() + parentX;
        double y = Math.max(getY() + parentY, parentY);
        double x2 = parentX2 - getX();
        double y2 = Math.min(getY() + parentY + 10, parentY2);
        if (getY() + 10 <= 0)
            return;
        if (parentY2 - (getY() + parentY) <= 0)
            return;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (parent == parent.parent.getTopWindow() && RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2)) {
            if (module.isEnabled())
                RenderSystem.setShaderColor((float) r + 0.2f, (float) g + 0.2f, (float) b + 0.2f, 1.0f);
            else
                RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
        } else {
            if (module.isEnabled())
                RenderSystem.setShaderColor((float) r, (float) g, (float) b, 1.0f);
            else
                RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.4f);
        }
        RenderUtils.drawQuad(x, y, x2, y2, matrices);
        double textX = x + 2;
        double textY = y + 1;
        ITextRenderer textRenderer = (ITextRenderer) mc.textRenderer;
        textRenderer.drawTrimmed(Text.literal(module.getName()), (float) textX, (float) textY, (int) (x2 - textX), 0x0, matrices.peek().getPositionMatrix());
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        double parentX = parent.getX();
        double parentY = parent.getY();
        double parentWidth = parent.getWidth();
        double parentLength = parent.getLength();
        double parentX2 = parent.getX() + parentWidth;
        double parentY2 = parent.getY() + parentLength;
        double x = getX() + parentX;
        double y = getY() + parentY;
        double x2 = parentX2 - getX();
        double y2 = Math.min(y + 20, parentY2);
        if (getY() + 10 <= 0)
            return;
        if (parentY2 - (getY() + parentY) <= 0)
            return;
        if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                module.toggle();
            } else {
                if (!settingWindowOpened && module.getSettings().size()!=0) {
                    ClickGui gui = parent.parent;
                    moduleSettingWindow = new ModuleSettingWindow(gui, mouseX, mouseY, module, this);
                    gui.add(moduleSettingWindow);
                    settingWindowOpened = true;
                } else if(module.getSettings().size()!=0) {
                    parent.parent.moveToTop(moduleSettingWindow);
                }
            }
        }
    }

    public void settingWindowClosed() {
        settingWindowOpened = false;
        moduleSettingWindow = null;
    }
}
