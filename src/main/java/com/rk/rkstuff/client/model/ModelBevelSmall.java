package com.rk.rkstuff.client.model;

import com.rk.rkstuff.util.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelBevelSmall {

    private IModelCustom model;

    public ModelBevelSmall() {
        model = AdvancedModelLoader.loadModel(Models.MODEL_BEVEL_SMALL);
    }

    public void render() {
        model.renderAll();
    }

}
