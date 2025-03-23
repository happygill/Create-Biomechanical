package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.Biomechanical;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CogolemModel extends DefaultedEntityGeoModel<CogolemEntity> {

    public CogolemModel() {
        super(Biomechanical.rl("cogolem"), true);
    }
}
