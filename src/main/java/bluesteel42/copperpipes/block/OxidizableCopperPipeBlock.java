package bluesteel42.copperpipes.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableCopperPipeBlock extends CopperPipeBlock implements Oxidizable {
    public static final MapCodec<OxidizableCopperPipeBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(CopperPipeBlock::getOxidationLevel),
                            createSettingsCodec()
                    )
                    .apply(instance, OxidizableCopperPipeBlock::new)
    );

    @Override
    public MapCodec<OxidizableCopperPipeBlock> getCodec() {
        return CODEC;
    }

    public OxidizableCopperPipeBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(oxidationLevel, settings);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.getOxidationLevel();
    }

    @Override
    public boolean isWaxed() {
        return false;
    }
}

