package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.content.tuner.ITunerOverlay;
import com.happysg.createbiomechanical.registry.BMItems;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CogolemEntity extends PathfinderMob implements GeoEntity, SmartBrainOwner<CogolemEntity>, ITunerOverlay, OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final float MAX_CHARGE_LEVEL = 100;

    private static final EntityDataAccessor<Float> CHARGE_LEVEL = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public CogolemEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE,32)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setChargeLevel(MAX_CHARGE_LEVEL);
        this.setCommand(GolemCommands.WANDER);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public float getChargeLevel() {
        return this.entityData.get(CHARGE_LEVEL);
    }

    public void setChargeLevel(float chargeLevel) {
        this.entityData.set(CHARGE_LEVEL, Math.min(chargeLevel, MAX_CHARGE_LEVEL));
    }

    public GolemCommands getCommand() {
        return GolemCommands.values()[this.entityData.get(COMMAND)];
    }

    public void setCommand(GolemCommands command) {
        this.entityData.set(COMMAND, command.ordinal());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public void setOwner(Player player) {
        if (player != null) {
            this.setOwnerUUID(player.getUUID());
        } else {
            this.setOwnerUUID(null);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE_LEVEL, 0f);
        builder.define(COMMAND, GolemCommands.STAY.ordinal());
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setChargeLevel(compound.getFloat("ChargeLevel"));
        this.setCommand(GolemCommands.values()[compound.getInt("Command")]);
        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(compound.getUUID("OwnerUUID"));
        } else {
            this.setOwnerUUID(null);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("ChargeLevel", this.getChargeLevel());
        compound.putInt("Command", this.getCommand().ordinal());
        if (this.getOwnerUUID() != null) {
            compound.putUUID("OwnerUUID", this.getOwnerUUID());
        }
    }


    @Override
    public List<? extends ExtendedSensor<? extends CogolemEntity>> getSensors() {
        return CogolemAI.getSensors();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getCoreTasks() {
        return CogolemAI.getCoreTasks();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getIdleTasks() {
        return CogolemAI.getIdleTasks();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getFightTasks() {
        return CogolemAI.getFightTasks();
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        CogolemAnimation.registerControllers(controllerRegistrar, this);
    }

    @Override
    protected void customServerAiStep() {
        this.tickBrain(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    Cogolem"));
        tooltip.add(Component.literal("Health  ")
                .append(barComponent((int) (getHealth() * 20 / getMaxHealth()))));
        tooltip.add(Component.literal("Charge ")
                .append(barComponent((int) (getChargeLevel() * 20/ MAX_CHARGE_LEVEL))));
        return true;
    }


    private MutableComponent barComponent(int level) {
        return Component.empty()
                .append(bars(Math.max(0, level), ChatFormatting.GREEN))
                .append(bars(Math.max(0, 20 - level), ChatFormatting.DARK_RED));

    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }

    public void cycleCommand(Player player) {
        if(level().isClientSide) {
            return;
        }
        if(getOwnerUUID() != null && !getOwnerUUID().equals(player.getUUID())) {
            return; // Only the owner can change the command
        }
        if(getOwnerUUID() == null) {
            setOwner(player);
        }
        setCommand(getCommand().cycle());
        if(getCommand() == GolemCommands.STAY) {
            getNavigation().stop();
        }
        player.sendSystemMessage(Component.literal("Cogolem command set to: " + getCommand().name())
                .withStyle(ChatFormatting.YELLOW));
    }
}
