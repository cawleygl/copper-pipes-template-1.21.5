package bluesteel42.copperpipes.registries;

import bluesteel42.copperpipes.block.ModBlocks;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;

public class ModRegistries {
    public static void registerOxidizables() {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.PIPE, ModBlocks.EXPOSED_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.EXPOSED_PIPE, ModBlocks.WEATHERED_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.WEATHERED_PIPE, ModBlocks.OXIDIZED_PIPE);

        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.PIPE, ModBlocks.WAXED_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.EXPOSED_PIPE, ModBlocks.WAXED_EXPOSED_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.WEATHERED_PIPE, ModBlocks.WAXED_WEATHERED_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.OXIDIZED_PIPE, ModBlocks.WAXED_OXIDIZED_PIPE);

    }
}
