package com.happysg.createbiomechanical.content.cogolem;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CogolemRenderer extends GeoEntityRenderer<CogolemEntity> {

    public CogolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CogolemModel());
    }

    @Override
    protected float getDeathMaxRotation(CogolemEntity animatable) {
        return 0.0F;
    }
}
