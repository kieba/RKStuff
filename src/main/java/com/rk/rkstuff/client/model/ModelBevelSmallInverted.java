package com.rk.rkstuff.client.model;

import com.rk.rkstuff.util.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelBevelSmallInverted {

    private IModelCustom model;

    public ModelBevelSmallInverted() {
        model = AdvancedModelLoader.loadModel(Models.MODEL_BEVEL_SMALL_INVERTED);
    }

    public void render() {
        model.renderAll();
    }

}
