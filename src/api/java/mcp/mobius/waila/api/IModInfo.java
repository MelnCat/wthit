package mcp.mobius.waila.api;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IModInfo {

    static IModInfo get(String namespace) {
        return IApiService.INSTANCE.getModInfo(namespace);
    }

    static IModInfo get(ResourceLocation id) {
        return get(id.getNamespace());
    }

    static IModInfo get(Block block) {
        return get(Registry.BLOCK.getKey(block));
    }

    static IModInfo get(ItemStack stack) {
        return IApiService.INSTANCE.getModInfo(stack);
    }

    static IModInfo get(Item item) {
        return get(Registry.ITEM.getKey(item));
    }

    static IModInfo get(Entity entity) {
        return get(Registry.ENTITY_TYPE.getKey(entity.getType()));
    }

    /**
     * Returns whether the mod is actually present in the classpath.
     */
    boolean isPresent();

    /**
     * Returns the id of the mod.
     */
    String getId();

    /**
     * Returns the name of the mod.
     */
    String getName();

    /**
     * Returns the string representation of the version of the mod.
     */
    String getVersion();

}
