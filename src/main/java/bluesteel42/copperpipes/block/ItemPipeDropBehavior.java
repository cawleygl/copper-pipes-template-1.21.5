package bluesteel42.copperpipes.block;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ItemPipeDropBehavior implements DispenserBehavior {
    private static final int field_51916 = 6;

    @Override
    public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
        ItemStack itemStack2 = this.dispenseSilently(blockPointer, itemStack);
        this.playSound(blockPointer);
        this.spawnParticles(blockPointer, blockPointer.state().get(CopperPipeBlock.FACING));
        return itemStack2;
    }

    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(CopperPipeBlock.FACING);
        Position position = CopperPipeBlock.getOutputLocation(pointer);
        ItemStack itemStack = stack.split(1);
        spawnItem(pointer.world(), itemStack, 6, direction, position);
        return stack;
    }

    public static void spawnItem(World world, ItemStack stack, int speed, Direction side, Position pos) {
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        if (side.getAxis() == Direction.Axis.Y) {
            e -= 0.125;
        } else {
            e -= 0.15625;
        }

        ItemEntity itemEntity = new ItemEntity(world, d, e, f, stack);
        double g = world.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setVelocity(
                world.random.nextTriangular(side.getOffsetX() * g, 0.0172275 * speed),
                world.random.nextTriangular(0.2, 0.0172275 * speed),
                world.random.nextTriangular(side.getOffsetZ() * g, 0.0172275 * speed)
        );
        world.spawnEntity(itemEntity);
    }

    protected void playSound(BlockPointer pointer) {
        World world = pointer.world();
        if (!world.isClient()) {
            // Play a custom sound
            world.playSound(
                    null,
                    pointer.pos(),
                    SoundEvents.BLOCK_COPPER_BULB_TURN_OFF,
                    SoundCategory.BLOCKS,
                    1.0F,
                    1.0F
            );
        }
    }

    protected void spawnParticles(BlockPointer pointer, Direction side) {
        pointer.world().syncWorldEvent(WorldEvents.CRAFTER_SHOOTS, pointer.pos(), side.getIndex());
    }
}
