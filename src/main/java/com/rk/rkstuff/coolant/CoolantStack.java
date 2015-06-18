package com.rk.rkstuff.coolant;

import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class CoolantStack {

    private int amount;
    private float temperature;

    public CoolantStack(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
    }

    public CoolantStack() {
        this(0, 0);
    }

    public void add(int amount, float temperature) {
        this.temperature = (this.amount * this.temperature + amount * temperature) / (this.amount + amount);
        this.amount += amount;
    }

    public void add(CoolantStack stack) {
        this.add(stack.amount, stack.temperature);
        stack.amount = 0;
    }

    public void add(CoolantStack stack, int amount) {
        this.add(stack.remove(amount));
    }

    public CoolantStack remove(int amount) {
        int a = Math.min(amount, this.amount);
        this.amount -= a;
        return new CoolantStack(a, this.temperature);
    }

    public int getAmount() {
        return amount;
    }

    public float getTemperature() {
        return temperature;
    }

    public void set(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
    }

    public void addEnergy(int energy) {
        if (amount == 0.0) return;
        temperature += energy / (float) amount;
    }

    public void writeToNBT(String name, NBTTagCompound tag) {
        tag.setInteger(name + "Amount", amount);
        tag.setFloat(name + "Temp", temperature);
    }

    public void readFromNBT(String name, NBTTagCompound tag) {
        amount = tag.getInteger(name + "Amount");
        temperature = tag.getFloat(name + "Temp");
    }

    public void writeData(IOStream data) {
        data.writeLast(amount);
        data.writeLast(temperature);
    }

    public void readData(IOStream data) throws IOException {
        amount = data.readFirstInt();
        temperature = data.readFirstFloat();
    }
}
