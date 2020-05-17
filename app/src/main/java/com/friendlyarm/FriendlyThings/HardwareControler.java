package com.friendlyarm.FriendlyThings;

import android.util.Log;

/**
 * Created by Fengchao.dai on 2020/05/05.
 */
public class HardwareControler {

    /* GPIO */
    static public native int exportGPIOPin(int pin);
    static public native int unexportGPIOPin(int pin);
    //GPIOEnum.LOW or GPIOEnum.HIGH
    static public native int setGPIOValue(int pin, int value);
    static public native int getGPIOValue(int pin);
    //GPIOEnum.IN or GPIOEnum.OUT
    static public native int setGPIODirection(int pin, int direction);
    static public native int getGPIODirection(int pin);

    static {
        try {
            System.loadLibrary("friendlyarm-things");
            Log.e("#DFC#","1111111111");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "libfriendlyarm-things library not found!");
        }
    }

}
