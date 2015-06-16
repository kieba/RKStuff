package com.rk.rkstuff.coolant;

public class CoolantStack {

    private int amount;
    private float temperature;

    public CoolantStack(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
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
}
