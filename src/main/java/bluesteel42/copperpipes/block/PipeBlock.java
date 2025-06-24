package bluesteel42.copperpipes.block;

import bluesteel42.copperpipes.entity.ModBlockEntities;
import bluesteel42.copperpipes.util.ModTags;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class PipeBlock extends BlockWithEntity {
    public static final MapCodec<PipeBlock> CODEC = createCodec(PipeBlock::new);
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final Map<Direction, BooleanProperty> INPUT_PROPERTIES = ImmutableMap.copyOf(
            Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP))
    );
    public static final EnumProperty<Direction> FACING = Properties.HOPPER_FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    private final Function<BlockState, VoxelShape> shapeFunction;

    @Override
    public MapCodec<PipeBlock> getCodec() {
        return CODEC;
    }

    public PipeBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN).with(ENABLED, true));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map<Direction, VoxelShape> outputBodyMap = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(6.0, 3.0, 9.0, 3.0, 11.0));
        Map<Direction, VoxelShape> outputTipMap = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(4.0, 4.0, 8.0, 0.0, 3.0));
        Map<Direction, VoxelShape> inputMap = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(6.0, 3.0, 9.0, 0.0, 5.0));
        return this.createShapeFunction(state -> {
            // Down-facing initial shape
            VoxelShape finalShape = VoxelShapes.union(
                    Block.createCuboidShape(5.0, 3.0, 5.0, 11.0, 9.0, 11.0),
                    Block.createCuboidShape(6.0, 0, 6.0, 10.0, 3.0, 10.0)
            );
            // Side-facing initial state
            if(!state.get(FACING).equals(Direction.DOWN)) {
                finalShape = Block.createCuboidShape(5.0, 3.0, 5.0, 11.0, 9.0, 11.0);
                for (Direction direction : Direction.Type.HORIZONTAL) {
                    if (state.get(FACING).equals(direction)) {
                        finalShape = VoxelShapes.union((VoxelShape)outputTipMap.get(direction), (VoxelShape)outputBodyMap.get(direction));
                    }
                }
            }
            // Input states
            for (Map.Entry<Direction, BooleanProperty> entry : INPUT_PROPERTIES.entrySet()) {
                VoxelShape inputAddition;
                if (entry.getKey().equals(Direction.UP)) {
                    inputAddition = Block.createCuboidShape(5.0, 7.0, 5.0, 11.0, 16.0, 11.0);
                } else {
                    inputAddition = (VoxelShape)inputMap.get(entry.getKey());
                }
                if ((Boolean)state.get((Property)entry.getValue())) {
                    finalShape = VoxelShapes.union(inputAddition, finalShape);
                }
            }
            return finalShape;
        });
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public static BlockState withInputProperties(BlockView world, BlockPos pos, BlockState state) {
        BlockState blockState2 = world.getBlockState(pos.up());
        BlockState blockState3 = world.getBlockState(pos.north());
        BlockState blockState4 = world.getBlockState(pos.east());
        BlockState blockState5 = world.getBlockState(pos.south());
        BlockState blockState6 = world.getBlockState(pos.west());
        if (blockState2.isIn(ModTags.Blocks.COPPER_PIPES)  || blockState2.isOf(Blocks.HOPPER)) {
            state = state.with(UP, blockState2.get(FACING).equals(Direction.DOWN));
        } else {
            state = state.with(UP, false);
        }
        if (blockState3.isIn(ModTags.Blocks.COPPER_PIPES)  || blockState3.isOf(Blocks.HOPPER)) {
            state = state.with(NORTH, blockState3.get(FACING).equals(Direction.SOUTH) && !state.get(FACING).equals(Direction.NORTH));
        } else {
            state = state.with(NORTH, false);
        }
        if (blockState4.isIn(ModTags.Blocks.COPPER_PIPES)  || blockState4.isOf(Blocks.HOPPER)) {
            state = state.with(EAST, blockState4.get(FACING).equals(Direction.WEST) && !state.get(FACING).equals(Direction.EAST));
        } else {
            state = state.with(EAST, false);
        }
        if (blockState5.isIn(ModTags.Blocks.COPPER_PIPES)  || blockState5.isOf(Blocks.HOPPER)) {
            state = state.with(SOUTH, blockState5.get(FACING).equals(Direction.NORTH) && !state.get(FACING).equals(Direction.SOUTH));
        } else {
            state = state.with(SOUTH, false);
        }
        if (blockState6.isIn(ModTags.Blocks.COPPER_PIPES)  || blockState6.isOf(Blocks.HOPPER)) {
            state = state.with(WEST, blockState6.get(FACING).equals(Direction.EAST) && !state.get(FACING).equals(Direction.WEST));
        } else {
            state = state.with(WEST, false);
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide().getOpposite();
        return withInputProperties(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState().with(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).with(ENABLED, true));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntities.PIPE_BLOCK, PipeBlockEntity::serverTick);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof PipeBlockEntity pipeBlockEntity) {
            player.openHandledScreen(pipeBlockEntity);
            player.incrementStat(Stats.INSPECT_HOPPER);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        if (!state.canPlaceAt(world, pos) || direction.equals(Direction.DOWN)) {
            tickView.scheduleBlockTick(pos, this, 1);
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        } else {
            boolean bl = false;
            if ((neighborState.isIn(ModTags.Blocks.COPPER_PIPES) || neighborState.isOf(Blocks.HOPPER)) && !state.get(FACING).equals(direction)) {
                bl = neighborState.get(FACING).equals(direction.getOpposite());
            }
            return state.with(INPUT_PROPERTIES.get(direction), bl);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean bl = !world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get(ENABLED)) {
            world.setBlockState(pos, state.with(ENABLED, bl), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) { return true; }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, FACING, ENABLED);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public static Position getOutputLocation(BlockPointer pointer) {
        return getOutputLocation(pointer, 0.7, Vec3d.ZERO);
    }

    public static Position getOutputLocation(BlockPointer pointer, double facingOffset, Vec3d constantOffset) {
        Direction direction = pointer.state().get(FACING);
        return pointer.centerPos()
                .add(
                        facingOffset * direction.getOffsetX() + constantOffset.getX(),
                        facingOffset * direction.getOffsetY() + constantOffset.getY(),
                        facingOffset * direction.getOffsetZ() + constantOffset.getZ()
                );
    }

}
