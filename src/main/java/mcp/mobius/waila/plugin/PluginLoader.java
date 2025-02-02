package mcp.mobius.waila.plugin;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.registry.Registrar;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;

public abstract class PluginLoader {

    private static final boolean ENABLE_TEST_PLUGIN = Boolean.getBoolean("waila.enableTestPlugin");

    protected static final String PLUGIN_JSON_PATH = "waila_plugins.json";
    protected static final String KEY_INITIALIZER = "initializer";
    protected static final String KEY_SIDE = "side";
    protected static final String KEY_REQUIRED = "required";
    protected static final Map<String, IPluginInfo.Side> SIDES = Map.of(
        "client", IPluginInfo.Side.CLIENT,
        "server", IPluginInfo.Side.SERVER,
        "both", IPluginInfo.Side.BOTH,
        "*", IPluginInfo.Side.BOTH
    );

    protected abstract void gatherPlugins();

    protected void readPluginsJson(String modId, Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

            outer:
            for (String pluginId : object.keySet()) {
                JsonObject plugin = object.getAsJsonObject(pluginId);

                String initializer = plugin.getAsJsonPrimitive(KEY_INITIALIZER).getAsString();
                IPluginInfo.Side side = plugin.has(KEY_SIDE)
                    ? Objects.requireNonNull(SIDES.get(plugin.get(KEY_SIDE).getAsString()), () -> readError(path) + ", invalid side, available: " + SIDES.keySet().stream().collect(Collectors.joining(", ", "[", "]")))
                    : IPluginInfo.Side.BOTH;

                if (!side.matches(ICommonService.INSTANCE.getSide())) {
                    break;
                }

                List<String> required = new ArrayList<>();
                if (plugin.has(KEY_REQUIRED)) {
                    JsonArray array = plugin.getAsJsonArray(KEY_REQUIRED);
                    for (JsonElement element : array) {
                        String requiredModId = element.getAsString();
                        if (ModInfo.get(requiredModId).isPresent()) {
                            required.add(requiredModId);
                        } else {
                            break outer;
                        }
                    }
                }

                PluginInfo.register(modId, pluginId, side, initializer, required, false);
            }
        } catch (IOException e) {
            throw new RuntimeException(readError(path), e);
        }
    }

    public void loadPlugins() {
        gatherPlugins();

        if (ENABLE_TEST_PLUGIN) {
            PluginInfo.register(WailaConstants.MOD_ID, "waila:test", IPluginInfo.Side.BOTH, "mcp.mobius.waila.plugin.test.WailaTest", Collections.emptyList(), false);
        }

        List<String> legacyPlugins = new ArrayList<>();
        for (IPluginInfo info : PluginInfo.getAll()) {
            Waila.LOGGER.info("Registering plugin {} at {}", info.getPluginId(), info.getInitializer().getClass().getCanonicalName());
            info.getInitializer().register(Registrar.INSTANCE);

            if (((PluginInfo) info).isLegacy()) {
                legacyPlugins.add(info.getPluginId().toString());
            }
        }

        // TODO: print warning on prod on 1.20
        if (Waila.DEV && !legacyPlugins.isEmpty()) {
            Waila.LOGGER.warn("Found plugins registered via legacy platform-dependant method:");
            Waila.LOGGER.warn(legacyPlugins.stream().collect(Collectors.joining(", ", "[", "]")));
            Waila.LOGGER.warn("The method will be removed on Minecraft 1.21");
        }

        Registrar.INSTANCE.lock();
        PluginConfig.reload();
    }

    private static String readError(Path path) {
        return "Failed to read [" + path + "]";
    }

}
