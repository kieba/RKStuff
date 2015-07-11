package com.rk.rkstuff.accelerator;

public enum AcceleratorControlCoreTypes {
    NORMAL("Normal", null),
    ENERGY("Energy", "Increases the energy usage of the LHC or Fusion Reactor"),
    EFFICIENCY("Efficiency", "Increases the efficiency of the LHC or Fusion Reactor");

    public String name;
    public String info;

    AcceleratorControlCoreTypes(String name, String info) {
        this.name = name;
        this.info = info;
    }

}
