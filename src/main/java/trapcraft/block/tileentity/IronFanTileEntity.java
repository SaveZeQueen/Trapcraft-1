package trapcraft.block.tileentity;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import trapcraft.TrapcraftTileEntityTypes;
import trapcraft.block.IronFanBlock;
import trapcraft.config.ConfigHandler;

import javax.annotation.Nonnull;

public class IronFanTileEntity extends BlockEntity  {

    public float speed = 1.0F;
    public double extraRange = 3.0D;

    public IronFanTileEntity(BlockPos pos, BlockState blockState) {
        super(TrapcraftTileEntityTypes.IRONFAN.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos worldPosition, BlockState blockState, IronFanTileEntity blockEntity) {
        if (!blockState.getValue(IronFanBlock.POWERED)) {
            return;
        }

        final Direction facing = blockState.getValue(IronFanBlock.FACING);

        if (level.random.nextInt(10) == 0) {
            spawnParticles(level, blockState, worldPosition);
        }

        final List<Entity> list = level.getEntitiesOfClass(Entity.class, blockEntity.getDirection(facing));

        for (final Entity entity : list) {
            if (entity instanceof Player p && p.getAbilities().flying) {
                continue;
            }

            if (!blockEntity.isPathClear(entity, facing)) {
                continue;
            }

            double velocity = ConfigHandler.SERVER.FAN_ACCELERATION.get(); // Affects acceleration
            double threshholdVelocity = ConfigHandler.SERVER.FAN_MAX_SPEED.get(); // Affects max speed
            velocity *= blockEntity.speed;


            if (entity instanceof ItemEntity) {
                threshholdVelocity *= 2.8D;
                velocity *= 1.3D;
            }

            if (entity instanceof Minecart) {
                velocity *= 1.0D;
            }

            if ((entity instanceof FallingBlockEntity) && facing == Direction.UP) {
                velocity = 0.1D;
            }

            velocity += ((level.getBestNeighborSignal(worldPosition) >= 11) ? (0.005D * level.getBestNeighborSignal(worldPosition)/6f) * level.random.nextFloat() : 0D);

            if (facing == Direction.UP) {
                if (entity instanceof ItemEntity) {
                    threshholdVelocity /= 2D;
                } else {
                    threshholdVelocity *= 4D;
                }
            }

            if (Math.abs(entity.getDeltaMovement().get(facing.getAxis())) < threshholdVelocity) {
                
                entity.setDeltaMovement(entity.getDeltaMovement().add(facing.getStepX() * velocity, facing.getStepY() * velocity, facing.getStepZ() * velocity));
            }
        }
    }

    public boolean isPathClear(final Entity entity, final Direction facing) {
        final int x = facing.getStepX() * (Mth.floor(entity.getX()) - this.worldPosition.getX());
        final int y = facing.getStepY() * (Mth.floor(entity.getY()) - this.worldPosition.getY());
        final int z = facing.getStepZ() * (Mth.floor(entity.getZ()) - this.worldPosition.getZ());
        boolean flag = true;

        for (int l2 = 1; l2 < Math.abs(x + y + z); l2++) {

            if (Block.canSupportCenter(this.level, this.worldPosition.relative(facing, l2), facing.getOpposite())) {
                flag = false;
            }
        }

        return flag;
    }

    public String getSliderDisplay()
    {
        float f = speed;
        f *= 100F;
        f = Math.round(f) / 100F;
        return String.valueOf(f);
    }

    public AABB getDirection(final Direction facing) {
        BlockPos endPos = this.worldPosition.relative(facing, Mth.floor(ConfigHandler.SERVER.IRON_FAN_RANGE.get() + this.extraRange));
        if (facing == Direction.WEST)
            endPos = endPos.offset(0, 1, 1);
        else if (facing == Direction.NORTH)
            endPos = endPos.offset(1, 1, 0);

        if (facing == Direction.EAST)
            endPos = endPos.offset(1, 1, 1);
        else if (facing == Direction.SOUTH)
            endPos = endPos.offset(1, 1, 1);

        if (facing == Direction.UP)
            endPos = endPos.offset(1, 1, 1);
        else if (facing == Direction.DOWN)
            endPos = endPos.offset(1, 0, 1);

        return new AABB(this.worldPosition, endPos);
    }

    public static void spawnParticles(final Level world, final BlockState blockState, final BlockPos pos) {
        final double x = pos.getX() + world.random.nextFloat();
        final double y = pos.getY() + world.random.nextFloat();
        final double z = pos.getZ() + world.random.nextFloat();

        final Direction facing = blockState.getValue(IronFanBlock.FACING);
        final double velocity = 0.15F + world.random.nextFloat() * ((world.getBestNeighborSignal(pos) >= 10) ? (0.0266F * world.getBestNeighborSignal(pos) / 1.35f) : 0.4f);

        final double velX = facing.getStepX() * velocity;
        final double velY = facing.getStepY() * velocity;
        final double velZ = facing.getStepZ() * velocity;

        world.addParticle(ParticleTypes.END_ROD, x, y, z, velX, velY, velZ);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.speed = nbt.getFloat("speed");
        this.extraRange = nbt.getDouble("extraRange");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putFloat("speed", this.speed);
        compound.putDouble("extraRange", this.extraRange);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        final CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        if (!this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
        }
        return;
    }

}
