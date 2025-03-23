package com.happysg.createbiomechanical.content.cogolem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CogolemEntity extends PathfinderMob implements GeoEntity, SmartBrainOwner<CogolemEntity> {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
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
        AnimationController<CogolemEntity> controller =
                new AnimationController<>(this, "controller", 0,
                        state -> CogolemAnimation.predicate(this, state));
        controllerRegistrar.add(controller);
    }

    @Override
    protected void customServerAiStep() {
        this.tickBrain(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    public int getScanRate() {
        return 10; // Adjust this value to change the scan rate of the Cogolem
    }
}
