package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.registry.BMBlocks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;

import java.util.List;

public class CogolemAI {

    public static List<? extends ExtendedSensor<CogolemEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<CogolemEntity>()
                        .setPredicate((target, entity) ->
                                target instanceof Player ||
                                        target instanceof Monster),
                new NearbyBlocksSensor<CogolemEntity>()
                        .setRadius(32, 5)
                        .setPredicate((state, pos) -> state.is(BMBlocks.STATION.get()))
                        .setScanRate(CogolemEntity::getScanRate)
        );
    }

    public static BrainActivityGroup<CogolemEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new MoveToWalkTarget<>()
        );

    }

    public static BrainActivityGroup<CogolemEntity> getIdleTasks() {
        return BrainActivityGroup.empty();
    }

    public static BrainActivityGroup<CogolemEntity> getFightTasks() {
        return BrainActivityGroup.empty();
    }

}
