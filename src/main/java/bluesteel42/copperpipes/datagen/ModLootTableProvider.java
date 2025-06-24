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
        addDrop(ModBlocks.PIPE);
        addDrop(ModBlocks.EXPOSED_PIPE);
        addDrop(ModBlocks.WEATHERED_PIPE);
        addDrop(ModBlocks.OXIDIZED_PIPE);
        addDrop(ModBlocks.WAXED_PIPE);
        addDrop(ModBlocks.WAXED_EXPOSED_PIPE);
        addDrop(ModBlocks.WAXED_WEATHERED_PIPE);
        addDrop(ModBlocks.WAXED_OXIDIZED_PIPE);
    }
}
