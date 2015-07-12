package com.rk.rkstuff.coolant;

import com.rk.rkstuff.util.RKConfig;
import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class CoolantStack {

    public static final float MIN_TEMPERATURE = 0.0f;
    public static final float MAX_TEMPERATURE = 3000.0f;

    private int amount;
    private float temperature;

    public CoolantStack(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
    }

    public CoolantStack() {
        this(0, 273.15f);
    }

    public void add(int amount, float temperature) {
        if (amount == 0) return;
        this.temperature = (this.amount * this.temperature + amount * temperature) / (this.amount + amount);
        this.amount += amount;
        checkTemperatureBounds();
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
        float oldTemp = this.temperature;
        this.amount -= a;
        return new CoolantStack(a, oldTemp);
    }

    public int getAmount() {
        return amount;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getEnergy() {
        return this.amount * this.temperature;
    }

    public void addEnergy(float energy) {
        if (amount > 0) {
            this.temperature = (getEnergy() + energy) / this.amount;
        }
        checkTemperatureBounds();
    }

    public void extractEnergy(float energy) {
        if (amount > 0) {
            this.temperature = (getEnergy() - energy) / this.amount;
        }
        checkTemperatureBounds();
    }

    public void set(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
        checkTemperatureBounds();
    }

    private void checkTemperatureBounds() {
        if (this.temperature < MIN_TEMPERATURE) this.temperature = MIN_TEMPERATURE;
        if (this.temperature > MAX_TEMPERATURE) this.temperature = MAX_TEMPERATURE;
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

    public boolean hasMinTemperature() {
        return temperature <= MIN_TEMPERATURE;
    }

    public boolean hasMaxTemperature() {
        return temperature >= MAX_TEMPERATURE;
    }

    public float getTempCelsius() {
        return toCelsius(getTemperature());
    }

    public float getTempFahrenheit() {
        return toFahrenheit(getTemperature());
    }

    public static float toCelsius(float kelvin) {
        return kelvin - 273.15f;
    }

    public static float celsiusToKelvin(float celsius) {
        return celsius + 273.15f;
    }

    public static float fahrenheitToCelsius(float fahrenheit) {
        return (fahrenheit + 459.67f) / 1.8f;
    }

    public static float toFahrenheit(float kelvin) {
        return kelvin * 1.8f - 459.67f;
    }

    public String getFormattedString() {
        return toFormattedString(temperature);
    }

    public static String toFormattedString(float kelvin) {
        if (RKConfig.useCelsius) {
            return String.format("%.2f °C", toCelsius(kelvin));
        } else {
            return String.format("%.2f °F", toFahrenheit(kelvin));
        }
    }

    @Override
    public String toString() {
        return "CoolantStack{" +
                "amount=" + amount +
                ", temperature=" + toFormattedString(temperature) +
                '}';
    }
}
