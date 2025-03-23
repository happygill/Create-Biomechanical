package com.happysg.createbiomechanical.content.station;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class StationBlockEntity extends KineticBlockEntity {

    BlockPos controllerPos = BlockPos.ZERO;

    public StationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isController())
            return;
    }


    //todo cache the speed of the left and right shaft potentially for performance
    public float getLeftShaftSpeed() {
        Direction front = getBlockState().getValue(HORIZONTAL_FACING);
        Direction left = getBlockState().getValue(HORIZONTAL_FACING).getClockWise();
        BlockPos leftShaftPos = getBlockPos().relative(front).above().relative(left);
        if (level.getBlockEntity(leftShaftPos) instanceof KineticBlockEntity kineticBlockEntity) {
            return kineticBlockEntity.getSpeed();
        }
        return 0;
    }

    public float getRightShaftSpeed() {
        Direction front = getBlockState().getValue(HORIZONTAL_FACING);
        Direction right = getBlockState().getValue(HORIZONTAL_FACING).getCounterClockWise();
        BlockPos rightShaftPos = getBlockPos().relative(front).above().relative(right);
        if (level.getBlockEntity(rightShaftPos) instanceof KineticBlockEntity kineticBlockEntity) {
            return kineticBlockEntity.getSpeed();
        }
        return 0;
    }

    public void setControllerPos(@Nonnull BlockPos pos) {
        controllerPos = pos;
    }


    public boolean isController() {
        return getBlockState().getValue(StationBlock.SHAPE) == StationShape.CENTER;
    }

    public Optional<StationBlockEntity> getController() {
        if (isController()) {
            return Optional.of(this);
        }
        if (controllerPos == null || !(level.getBlockEntity(controllerPos) instanceof StationBlockEntity stationBlockEntity)) {
            return Optional.empty();
        }
        return Optional.of(stationBlockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!isController()) {
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
        tooltip.add(Component.literal("    Debug Info"));
        tooltip.add(Component.literal("Left Shaft Speed: " + getLeftShaftSpeed()));
        tooltip.add(Component.literal("Right Shaft Speed: " + getRightShaftSpeed()));
        return true;
    }


    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putLong("controllerPos", controllerPos.asLong());
    }

    public void removeMultiBlock() {
        getController().ifPresent(controller -> {
            level.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
        });
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2, 1, 2);
    }
}

