package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public enum JukeboxProvider implements IBlockComponentProvider, IServerDataProvider<JukeboxBlockEntity> {

    INSTANCE;

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD) && accessor.getServerData().contains("record")) {
            tooltip.addLine(Component.Serializer.fromJson(accessor.getServerData().getString("record")));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor<JukeboxBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.JUKEBOX_RECORD)) {
            ItemStack stack = accessor.getTarget().getRecord();
            if (!stack.isEmpty()) {
                Component text = stack.getItem() instanceof RecordItem
                    ? Component.translatable(stack.getDescriptionId() + ".desc")
                    : stack.getDisplayName();
                data.putString("record", Component.Serializer.toJson(text));
            }
        }
    }

}
