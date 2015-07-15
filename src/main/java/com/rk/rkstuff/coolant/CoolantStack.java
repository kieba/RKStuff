package com.rk.rkstuff.coolant;

import com.rk.rkstuff.util.RKConfig;
import net.minecraft.nbt.NBTTagCompound;
import rk.com.core.io.IOStream;

import java.io.IOException;

public class CoolantStack {

    public static final double MIN_TEMPERATURE = 0.0;
    public static final double MAX_TEMPERATURE = 3000.0;

    private int amount;
    private double temperature;

    public CoolantStack(int amount, double temperature) {
        this.amount = amount;
        this.temperature = temperature;
    }

    public CoolantStack() {
        this(0, 273.15);
    }

    public void add(int amount, double temperature) {
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
        double oldTemp = this.temperature;
        this.amount -= a;
        return new CoolantStack(a, oldTemp);
    }

    public int getAmount() {
        return amount;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getEnergy() {
        return this.amount * this.temperature;
    }

    public void addEnergy(double energy) {
        if (amount > 0) {
            this.temperature = (getEnergy() + energy) / this.amount;
        }
        checkTemperatureBounds();
    }

    public void extractEnergy(double energy) {
        if (amount > 0) {
            this.temperature = (getEnergy() - energy) / this.amount;
        }
        checkTemperatureBounds();
    }

    public void set(int amount, double temperature) {
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
        tag.setDouble(name + "Temp", temperature);
    }

    public void readFromNBT(String name, NBTTagCompound tag) {
        amount = tag.getInteger(name + "Amount");
        temperature = tag.getDouble(name + "Temp");
    }

    public void writeData(IOStream data) {
        data.writeLast(amount);
        data.writeLast(temperature);
    }

    public void readData(IOStream data) throws IOException {
        amount = data.readFirstInt();
        temperature = data.readFirstDouble();
    }

    public boolean hasMinTemperature() {
        return temperature <= MIN_TEMPERATURE;
    }

    public boolean hasMaxTemperature() {
        return temperature >= MAX_TEMPERATURE;
    }

    public double getTempCelsius() {
        return toCelsius(getTemperature());
    }

    public double getTempFahrenheit() {
        return toFahrenheit(getTemperature());
    }

    public static double toCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public static double celsiusToKelvin(double celsius) {
        return celsius + 273.15;
    }

    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit + 459.67) / 1.8;
    }

    public static double toFahrenheit(double kelvin) {
        return kelvin * 1.8 - 459.67;
    }

    public String getFormattedString() {
        return toFormattedString(temperature);
    }

    public static String toFormattedString(double kelvin) {
        if (RKConfig.useCelsius) {
            return String.format("%.5f °C", toCelsius(kelvin));
        } else {
            return String.format("%.5f °F", toFahrenheit(kelvin));
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
