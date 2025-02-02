package mcp.mobius.waila.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class HomeScreen extends Screen {

    private final Screen parent;

    public HomeScreen(Screen parent) {
        super(Component.literal(WailaConstants.MOD_NAME));

        this.parent = parent;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void init() {
        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 48, 200, 20, Component.translatable(Tl.Gui.WAILA_SETTINGS, WailaConstants.MOD_NAME), w ->
            minecraft.setScreen(new WailaConfigScreen(this))));
        addRenderableWidget(new Button(width / 2 - 100, height / 2 - 24, 200, 20, Component.translatable(Tl.Gui.PLUGIN_SETTINGS), w ->
            minecraft.setScreen(new PluginConfigScreen(this))));
        addRenderableWidget(new Button(width / 2 - 100, height / 2, 200, 20, Component.translatable(Tl.Gui.CREDITS), w ->
            minecraft.setScreen(new CreditsScreen(this))));
        addRenderableWidget(new Button(width / 2 - 50, height / 2 + 24, 100, 20, CommonComponents.GUI_DONE, w ->
            minecraft.setScreen(parent)));
    }

    @Override
    public void render(@NotNull PoseStack matrices, int x, int y, float partialTicks) {
        renderBackground(matrices);
        drawCenteredString(matrices, font, title.getString(), width / 2, height / 2 - 50 - font.lineHeight, 0xFFFFFF);
        super.render(matrices, x, y, partialTicks);
    }

}
