package bluesteel42.copperpipes.block;

import bluesteel42.copperpipes.CopperPipes;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static final Block COPPER_PIPE = register(
            "pipe",
            settings -> new OxidizableCopperPipeBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(3.0F, 4.8F)
                    .sounds(BlockSoundGroup.COPPER)
                    .nonOpaque(),
            true,
            true
    );
    public static final Block EXPOSED_COPPER_PIPE = register(
            "exposed_pipe",
            settings -> new OxidizableCopperPipeBlock(Oxidizable.OxidationLevel.EXPOSED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY),
            true,
            true
    );
    public static final Block WEATHERED_COPPER_PIPE = register(
            "weathered_pipe",
            settings -> new OxidizableCopperPipeBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.DARK_AQUA),
            true,
            true
    );
    public static final Block OXIDIZED_COPPER_PIPE = register(
            "oxidized_pipe",
            settings -> new OxidizableCopperPipeBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.TEAL),
            true,
            true
    );
    public static final Block WAXED_COPPER_PIPE = register(
            "waxed_pipe",
            settings -> new CopperPipeBlock(Oxidizable.OxidationLevel.UNAFFECTED, settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .requiresTool()
                    .strength(3.0F, 4.8F)
                    .sounds(BlockSoundGroup.COPPER)
                    .nonOpaque(),
            true,
            true
    );
    public static final Block WAXED_EXPOSED_COPPER_PIPE = register(
            "waxed_exposed_pipe",
            settings -> new CopperPipeBlock(Oxidizable.OxidationLevel.EXPOSED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY),
            true,
            true
    );
    public static final Block WAXED_WEATHERED_COPPER_PIPE = register(
            "waxed_weathered_pipe",
            settings -> new CopperPipeBlock(Oxidizable.OxidationLevel.WEATHERED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.DARK_AQUA),
            true,
            true
    );
    public static final Block WAXED_OXIDIZED_COPPER_PIPE = register(
            "waxed_oxidized_pipe",
            settings -> new CopperPipeBlock(Oxidizable.OxidationLevel.OXIDIZED, settings),
            AbstractBlock.Settings.copy(COPPER_PIPE).mapColor(MapColor.TEAL),
            true,
            true
    );

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, boolean registerItem, boolean nonOpaqueBlock) {
        final Identifier identifier = Identifier.of(CopperPipes.MOD_ID, path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);

        if (registerItem) {
            Items.register(block);
        }

        if (nonOpaqueBlock) {
            BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT);
        }

        return block;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE)
                .register((itemGroup) -> {
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.WAXED_OXIDIZED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.WAXED_WEATHERED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.WAXED_EXPOSED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.WAXED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.OXIDIZED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.WEATHERED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.EXPOSED_COPPER_PIPE);
                    itemGroup.addAfter(Items.HOPPER, ModBlocks.COPPER_PIPE);
                });

    }

}
