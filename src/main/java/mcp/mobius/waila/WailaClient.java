package mcp.mobius.waila;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import mcp.mobius.waila.access.DataAccessor;
import mcp.mobius.waila.api.IEventListener;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.TooltipHandler;
import mcp.mobius.waila.gui.screen.HomeScreen;
import mcp.mobius.waila.integration.IRecipeAction;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.IClientService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class WailaClient {

    public static KeyMapping keyOpenConfig;
    public static KeyMapping keyShowOverlay;
    public static KeyMapping keyToggleLiquid;
    public static KeyMapping keyShowRecipeInput;
    public static KeyMapping keyShowRecipeOutput;

    public static boolean showComponentBounds = false;

    @Nullable
    private static IRecipeAction recipeAction;

    public static void setRecipeAction(IRecipeAction action) {
        if (recipeAction == null) {
            Waila.LOGGER.info("Show recipe action set for " + action.getModName());
        } else if (!recipeAction.getModName().equals(action.getModName())) {
            Waila.LOGGER.warn("Show recipe action is already set for " + recipeAction.getModName());
            Waila.LOGGER.warn("Replaced it with one for " + action.getModName());
        }

        recipeAction = action;
    }

    protected static List<KeyMapping> registerKeyBinds() {
        return List.of(
            keyOpenConfig = createKeyBind(Tl.Key.CONFIG),
            keyShowOverlay = createKeyBind(Tl.Key.SHOW_OVERLAY),
            keyToggleLiquid = createKeyBind(Tl.Key.TOGGLE_LIQUID),
            keyShowRecipeInput = createKeyBind(Tl.Key.SHOW_RECIPE_INPUT),
            keyShowRecipeOutput = createKeyBind(Tl.Key.SHOW_RECIPE_OUTPUT)
        );
    }

    protected static void onClientTick() {
        Minecraft client = Minecraft.getInstance();
        WailaConfig config = Waila.CONFIG.get();

        TooltipHandler.tick();

        while (keyOpenConfig.consumeClick()) {
            client.setScreen(new HomeScreen(null));
        }

        while (keyShowOverlay.consumeClick()) {
            if (config.getGeneral().getDisplayMode() == IWailaConfig.General.DisplayMode.TOGGLE) {
                config.getGeneral().setDisplayTooltip(!config.getGeneral().isDisplayTooltip());
            }
        }

        while (keyToggleLiquid.consumeClick()) {
            PluginConfig.set(WailaConstants.CONFIG_SHOW_FLUID, !PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_FLUID));
        }

        if (recipeAction != null) {
            while (keyShowRecipeInput.consumeClick()) {
                recipeAction.showInput(DataAccessor.INSTANCE.getStack());
            }

            while (keyShowRecipeOutput.consumeClick()) {
                recipeAction.showOutput(DataAccessor.INSTANCE.getStack());
            }
        }
    }

    protected static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (PluginConfig.CLIENT.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            for (IEventListener listener : Registrar.INSTANCE.eventListeners.get(Object.class)) {
                String name = listener.getHoveredItemModName(stack, PluginConfig.CLIENT);
                if (name != null) {
                    tooltip.add(IWailaConfig.get().getFormatter().modName(name));
                    return;
                }
            }
        }
    }

    protected static void onServerLogIn(Connection connection) {
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

    protected static void onServerLogout(Connection connection) {
        Waila.BLACKLIST_CONFIG.invalidate();
        PluginConfig.getSyncableConfigs().forEach(config ->
            config.setServerValue(null));
    }

    private static KeyMapping createKeyBind(String id) {
        return IClientService.INSTANCE.createKeyBind(id, InputConstants.UNKNOWN.getValue());
    }

}
