package bluesteel42.copperpipes.entity;

import bluesteel42.copperpipes.CopperPipes;
import bluesteel42.copperpipes.block.ModBlocks;
import bluesteel42.copperpipes.block.CopperPipeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<CopperPipeBlockEntity> PIPE_BLOCK = register(
            "pipe_block",
            FabricBlockEntityTypeBuilder.create(
                    CopperPipeBlockEntity::new,
                    ModBlocks.COPPER_PIPE,
                    ModBlocks.EXPOSED_COPPER_PIPE,
                    ModBlocks.WEATHERED_COPPER_PIPE,
                    ModBlocks.OXIDIZED_COPPER_PIPE,
                    ModBlocks.WAXED_COPPER_PIPE,
                    ModBlocks.WAXED_EXPOSED_COPPER_PIPE,
                    ModBlocks.WAXED_WEATHERED_COPPER_PIPE,
                    ModBlocks.WAXED_OXIDIZED_COPPER_PIPE
            ).build()
    );

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(CopperPipes.MOD_ID, path), blockEntityType);
    }

    public static void initialize() {}
}
