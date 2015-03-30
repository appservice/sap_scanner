package eu.appservice.sap_scanner.logfile;

import android.util.Log;

import java.io.IOException;

/**
 * Created by Lukasz on 25.02.14.
 * ﹕ SAP Skanner
 */
public class FlashLightOnHtc {

    private final static String[] LIGHT_ON = {"/system/bin/sh", "-c",
            "echo 125 > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"};
    private final static String[] LIGHT_OFF = {"/system/bin/sh", "-c",
            "echo 0 > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"};

    private boolean isFlashLight;
    // private int brightness=125;

    //----------singleton-----------------------------------------
    private static FlashLightOnHtc instance;

    private FlashLightOnHtc() {

    }

    public static FlashLightOnHtc getInstance() {
        if (instance == null) {
            synchronized (FlashLightOnHtc.class) {  //is synchronized during the first invoke
                instance = new FlashLightOnHtc();
            }
        }
        return instance;
    }
    //-------------------------------------------------------


    /**
     * @return This function return true when flash light on activity_rw HTC Desire mobile is switch off.
     */
    public boolean flashLightOn() {
        try {
            Runtime.getRuntime().exec(LIGHT_ON);
            isFlashLight = true;
            Log.w("flashLightOn", "flash switched On, isFlashLIght:" + isFlashLight);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param value -how strong flash should light, max 128;
     * @return This function return true when flash light on activity_rw HTC Desire mobile is switch off.
     */
    public boolean flashLightOn(int value) {
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c",
                    "echo " + value + " > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"});
            isFlashLight = true;
            Log.w("flashLightOn", "flash switched On, isFlashLIght:" + isFlashLight);
            return true;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return This function return true when flash light on activity_rw HTC Desire mobile is switch off.
     */
    public boolean flashLightOff() {
        try {
            Runtime.getRuntime().exec(LIGHT_OFF);
            isFlashLight = false;
            Log.w("flashLightOff", "flash switched Off isFlashLIght:" + isFlashLight);
            return true;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

    }

    public boolean flashLightToggle() {
        if (!isFlashLight) {
            return flashLightOn();
        } else {
            return flashLightOff();
        }
    }

}
