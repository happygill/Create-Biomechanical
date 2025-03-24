package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.content.cogolem.behavior.FollowOwner;
import com.happysg.createbiomechanical.registry.BMBlocks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.BowAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.example.SBLSkeleton;

import java.util.List;

public class CogolemAI {

    public static List<? extends ExtendedSensor<CogolemEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<CogolemEntity>()
        );
    }

    public static BrainActivityGroup<CogolemEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new FollowOwner<>().stopFollowingWithin(2).startCondition(cogolemEntity -> cogolemEntity.getCommand() == GolemCommands.FOLLOW),
                new MoveToWalkTarget<CogolemEntity>().startCondition(cogolemEntity -> cogolemEntity.getCommand() != GolemCommands.STAY && cogolemEntity.getChargeLevel() > 0),
                new LookAtTarget<>().runFor(entity -> entity.getRandom().nextIntBetweenInclusive(40, 300))
        );

    }

    public static BrainActivityGroup<CogolemEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<CogolemEntity>(
                        new TargetOrRetaliate<>().attackablePredicate(target -> target instanceof Enemy && !(target instanceof Creeper)),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<CogolemEntity>().startCondition(cogolemEntity -> cogolemEntity.getCommand() == GolemCommands.WANDER),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))
        );
    }

    public static BrainActivityGroup<CogolemEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<CogolemEntity>(2)
                        .attackInterval(e->20)
                        .startCondition(cogolemEntity -> cogolemEntity.getChargeLevel() > 0)
                        .whenStarting(cogolemEntity -> cogolemEntity.takeCharge(5))
        );
    }

}
