package mcp.mobius.waila.forge;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeCommonService implements ICommonService {

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return ModList.get()
            .getModContainerById(namespace)
            .map(ModContainer::getModInfo)
            .map(data -> new ModInfo(true, data.getModId(), data.getDisplayName(), data.getVersion().getQualifier()));
    }

    @Override
    public boolean isDev() {
        return !FMLLoader.isProduction();
    }

    @Override
    public IPluginInfo.Side getSide() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> IPluginInfo.Side.CLIENT;
            case DEDICATED_SERVER -> IPluginInfo.Side.SERVER;
        };
    }

}
