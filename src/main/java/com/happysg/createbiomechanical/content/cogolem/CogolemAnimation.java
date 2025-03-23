package com.happysg.createbiomechanical.content.cogolem;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

public class CogolemAnimation {

    static PlayState predicate(CogolemEntity cogolem, AnimationState<CogolemEntity> animationState) {
        return PlayState.CONTINUE;
    }
}
