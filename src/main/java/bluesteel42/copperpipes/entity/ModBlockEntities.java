package bluesteel42.copperpipes.entity;

import bluesteel42.copperpipes.CopperPipes;
import bluesteel42.copperpipes.block.ModBlocks;
import bluesteel42.copperpipes.block.PipeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<PipeBlockEntity> PIPE_BLOCK = register(
            "pipe_block",
            FabricBlockEntityTypeBuilder.create(
                    PipeBlockEntity::new,
                    ModBlocks.PIPE,
                    ModBlocks.EXPOSED_PIPE,
                    ModBlocks.WEATHERED_PIPE,
                    ModBlocks.OXIDIZED_PIPE,
                    ModBlocks.WAXED_PIPE,
                    ModBlocks.WAXED_EXPOSED_PIPE,
                    ModBlocks.WAXED_WEATHERED_PIPE,
                    ModBlocks.WAXED_OXIDIZED_PIPE
            ).build()
    );

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(CopperPipes.MOD_ID, path), blockEntityType);
    }

    public static void initialize() {}
}
