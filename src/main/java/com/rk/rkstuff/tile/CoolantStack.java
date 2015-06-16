package com.rk.rkstuff.tile;

public class CoolantStack {

    private int amount;
    private float temperature;

    public CoolantStack(int amount, float temperature) {
        this.amount = amount;
        this.temperature = temperature;
    }

    public void add(CoolantStack stack) {
        this.temperature = (this.amount * this.temperature + stack.amount * stack.temperature) / (this.amount + stack.amount);
        this.amount += stack.amount;
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
