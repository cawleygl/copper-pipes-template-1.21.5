package bluesteel42.copperpipes.block;

import bluesteel42.copperpipes.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldEvents;
import net.minecraft.util.math.BlockPointer;
import org.jetbrains.annotations.Nullable;

public class CopperPipeBlockEntity extends DispenserBlockEntity implements SidedInventory {
    public static final int TRANSFER_COOLDOWN = 8;
    public static final int INVENTORY_SIZE = 5;
    private static final int[][] AVAILABLE_SLOTS_CACHE = new int[54][];
    private static final int DEFAULT_TRANSFER_COOLDOWN = -1;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int transferCooldown = -1;
    private long lastTickTime;
    private Direction facing;
    private static final DispenserBehavior BEHAVIOR = new ItemPipeDropBehavior();

    public CopperPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_BLOCK, pos, state);
        this.facing = state.get(CopperPipeBlock.FACING);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(view)) {
            Inventories.readData(view, this.inventory);
        }

        this.transferCooldown = view.getInt("TransferCooldown", -1);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData(view, this.inventory);
        }

        view.putInt("TransferCooldown", this.transferCooldown);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.generateLoot(null);
        return Inventories.splitStack(this.getHeldStacks(), slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.generateLoot(null);
        this.getHeldStacks().set(slot, stack);
        stack.capCount(this.getMaxCount(stack));
    }

    @Override
    public void setCachedState(BlockState state) {
        super.setCachedState(state);
        this.facing = state.get(CopperPipeBlock.FACING);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.pipe");
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, CopperPipeBlockEntity blockEntity) {
        blockEntity.transferCooldown--;
        blockEntity.lastTickTime = world.getTime();
        if (!blockEntity.needsCooldown()) {
            blockEntity.setTransferCooldown(0);
            insertOnly(world, pos, state, blockEntity);
        }
    }

    private static boolean insertOnly(World world, BlockPos pos, BlockState state, CopperPipeBlockEntity blockEntity) {
        if (world.isClient()) {
            return false;
        } else {
            if (!blockEntity.needsCooldown() && (Boolean)state.get(CopperPipeBlock.ENABLED)) {
                if (!blockEntity.isEmpty() && insert(world, pos, blockEntity)) {
                    blockEntity.setTransferCooldown(8);
                    markDirty(world, pos, state);
                    return true;
                }
            }

            return false;
        }
    }

    private static boolean insert(World world, BlockPos pos, CopperPipeBlockEntity blockEntity) {
        Inventory inventory = getOutputInventory(world, pos, blockEntity);
        BlockPos blockFacingPos = pos.offset(blockEntity.facing, 1);
        if (inventory != null) {
            Direction direction = blockEntity.facing.getOpposite();
            if (isInventoryFull(inventory, direction)) {
                return false;
            } else {
                for (int i = 0; i < blockEntity.size(); i++) {
                    ItemStack itemStack = blockEntity.getStack(i);
                    if (!itemStack.isEmpty()) {
                        int j = itemStack.getCount();
                        ItemStack itemStack2 = transfer(blockEntity, inventory, blockEntity.removeStack(i, 1), direction);
                        if (itemStack2.isEmpty()) {
                            inventory.markDirty();
                            return true;
                        }

                        itemStack.setCount(j);
                        if (j == 1) {
                            blockEntity.setStack(i, itemStack);
                        }
                    }
                }

                return false;
            }
        } else if (world.getBlockState(blockFacingPos).isSolidBlock(world, blockFacingPos)) {
            // Do nothing if facing solid block
            return false;
        } else {
            // Drop item if no inventory or solid block found
            return dropItem(world, pos, blockEntity);
        }
    }

    private static int[] getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return sidedInventory.getAvailableSlots(side);
        } else {
            int i = inventory.size();
            if (i < AVAILABLE_SLOTS_CACHE.length) {
                int[] is = AVAILABLE_SLOTS_CACHE[i];
                if (is != null) {
                    return is;
                } else {
                    int[] js = indexArray(i);
                    AVAILABLE_SLOTS_CACHE[i] = js;
                    return js;
                }
            } else {
                return indexArray(i);
            }
        }
    }

    private static int[] indexArray(int size) {
        int[] is = new int[size];
        int i = 0;

        while (i < is.length) {
            is[i] = i++;
        }

        return is;
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        int[] is = getAvailableSlots(inventory, direction);

        for (int i : is) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getCount() < itemStack.getMaxCount()) {
                return false;
            }
        }

        return true;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        if (to instanceof SidedInventory sidedInventory && side != null) {
            int[] is = sidedInventory.getAvailableSlots(side);

            for (int i = 0; i < is.length && !stack.isEmpty(); i++) {
                stack = transfer(from, to, stack, is[i], side);
            }
        } else {
            int j = to.size();

            for (int i = 0; i < j && !stack.isEmpty(); i++) {
                stack = transfer(from, to, stack, i, side);
            }
        }

        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        return !inventory.isValid(slot, stack) ? false : !(inventory instanceof SidedInventory sidedInventory && !sidedInventory.canInsert(slot, stack, side));
    }


    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, side)) {
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                bl = j > 0;
            }

            if (bl) {
                if (bl2 && to instanceof CopperPipeBlockEntity pipeBlockEntity && !pipeBlockEntity.isDisabled()) {
                    int j = 0;
                    if (from instanceof CopperPipeBlockEntity pipeBlockEntity2 && pipeBlockEntity.lastTickTime >= pipeBlockEntity2.lastTickTime) {
                        j = 1;
                    }

                    pipeBlockEntity.setTransferCooldown(8 - j);
                }

                to.markDirty();
            }
        }

        return stack;
    }

    @Nullable
    private static Inventory getOutputInventory(World world, BlockPos pos, CopperPipeBlockEntity blockEntity) {
        return getInventoryAt(world, pos.offset(blockEntity.facing));
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, pos, world.getBlockState(pos), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    private static Inventory getInventoryAt(World world, BlockPos pos, BlockState state, double x, double y, double z) {
        return getBlockInventoryAt(world, pos, state);
    }

    @Nullable
    private static Inventory getBlockInventoryAt(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof InventoryProvider) {
            return ((InventoryProvider)block).getInventory(state, world, pos);
        } else if (state.hasBlockEntity() && world.getBlockEntity(pos) instanceof Inventory inventory) {
            if (inventory instanceof ChestBlockEntity && block instanceof ChestBlock) {
                inventory = ChestBlock.getInventory((ChestBlock)block, state, world, pos, true);
            }

            return inventory;
        } else {
            return null;
        }
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.areItemsAndComponentsEqual(first, second);
    }

    private void setTransferCooldown(int transferCooldown) {
        this.transferCooldown = transferCooldown;
    }

    private boolean needsCooldown() {
        return this.transferCooldown > 0;
    }

    private boolean isDisabled() {
        return this.transferCooldown > 8;
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new PipeScreenHandler(syncId, playerInventory, this);
    }

    private static boolean dropItem(World world, BlockPos pos, CopperPipeBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return false;
        }

        int slot = blockEntity.chooseNonEmptySlot(serverWorld.random);
        if (slot < 0) {
            serverWorld.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
            return false;
        }

        ItemStack itemStack = blockEntity.getStack(slot);
        if (itemStack.isEmpty()) {
            return false;
        }

        BlockPointer blockPointer = new BlockPointer(
                serverWorld,
                pos,
                world.getBlockState(pos),
                blockEntity
        );

        // Dispense the item
        ItemStack dispensedStack = BEHAVIOR.dispense(blockPointer, itemStack.copyWithCount(1));

        // Update the slot - if dispense was successful, the stack should be empty or modified
        if (dispensedStack.isEmpty()) {
            ItemStack remainingStack = itemStack.copy();
            remainingStack.decrement(1);
            blockEntity.setStack(slot, remainingStack);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int chooseNonEmptySlot(Random random) {
        this.generateLoot(null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.inventory.size(); k++) {
            if (!this.inventory.get(k).isEmpty() && random.nextInt(j++) == 0) {
                i = k;
            }
        }

        return i;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return indexArray(this.size());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
