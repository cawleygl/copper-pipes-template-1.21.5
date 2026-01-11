package bluesteel42.copperpipes.datagen;

import bluesteel42.copperpipes.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.COPPER_PIPE);
        addDrop(ModBlocks.EXPOSED_COPPER_PIPE);
        addDrop(ModBlocks.WEATHERED_COPPER_PIPE);
        addDrop(ModBlocks.OXIDIZED_COPPER_PIPE);
        addDrop(ModBlocks.WAXED_COPPER_PIPE);
        addDrop(ModBlocks.WAXED_EXPOSED_COPPER_PIPE);
        addDrop(ModBlocks.WAXED_WEATHERED_COPPER_PIPE);
        addDrop(ModBlocks.WAXED_OXIDIZED_COPPER_PIPE);
    }
}
