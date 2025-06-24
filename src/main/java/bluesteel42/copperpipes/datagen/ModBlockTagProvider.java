package bluesteel42.copperpipes.datagen;

import bluesteel42.copperpipes.block.ModBlocks;
import bluesteel42.copperpipes.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Blocks.COPPER_PIPES)
                .add(ModBlocks.PIPE)
                .add(ModBlocks.EXPOSED_PIPE)
                .add(ModBlocks.WEATHERED_PIPE)
                .add(ModBlocks.OXIDIZED_PIPE)
                .add(ModBlocks.WAXED_PIPE)
                .add(ModBlocks.WAXED_EXPOSED_PIPE)
                .add(ModBlocks.WAXED_WEATHERED_PIPE)
                .add(ModBlocks.WAXED_OXIDIZED_PIPE);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).addTag(ModTags.Blocks.COPPER_PIPES);

    }
}
