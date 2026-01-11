package bluesteel42.copperpipes.registries;

import bluesteel42.copperpipes.block.ModBlocks;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;

public class ModRegistries {
    public static void registerOxidizables() {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.COPPER_PIPE, ModBlocks.EXPOSED_COPPER_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.EXPOSED_COPPER_PIPE, ModBlocks.WEATHERED_COPPER_PIPE);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(ModBlocks.WEATHERED_COPPER_PIPE, ModBlocks.OXIDIZED_COPPER_PIPE);

        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.COPPER_PIPE, ModBlocks.WAXED_COPPER_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.EXPOSED_COPPER_PIPE, ModBlocks.WAXED_EXPOSED_COPPER_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.WEATHERED_COPPER_PIPE, ModBlocks.WAXED_WEATHERED_COPPER_PIPE);
        OxidizableBlocksRegistry.registerWaxableBlockPair(ModBlocks.OXIDIZED_COPPER_PIPE, ModBlocks.WAXED_OXIDIZED_COPPER_PIPE);

    }
}
