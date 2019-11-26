package com.example.test.myapplicationdemo;

/**
 * @author
 * @data 2019/11/26
 */
public class CancleEventBus {
    private boolean isDisaddpear;

    public CancleEventBus(boolean isDisaddpear) {
        this.isDisaddpear = isDisaddpear;
    }

    public boolean isDisaddpear() {
        return isDisaddpear;
    }

    public void setDisaddpear(boolean disaddpear) {
        isDisaddpear = disaddpear;
    }
}
