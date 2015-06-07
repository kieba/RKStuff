package com.rk.rkstuff.client.model;

import com.rk.rkstuff.util.Models;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ModelBevelLarge {

    private IModelCustom model;

    public ModelBevelLarge() {
        model = AdvancedModelLoader.loadModel(Models.MODEL_BEVEL_LARGE);
    }

    public void render() {
        model.renderAll();
    }

}
