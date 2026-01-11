package bluesteel42.copperpipes.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record PipeBlockPointer(ServerWorld world, BlockPos pos, BlockState state) {
    public Vec3d centerPos() {
        return this.pos.toCenterPos();
    }
}
