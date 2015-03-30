package eu.appservice.sap_scanner;

import java.io.IOException;


/**
 * That is singleton instance. </br>
 * This object is using to switch activity_rw flash light in activity_rw HTC Desire mobile phone.
 *
 * @author mochelek
 * @deprecated
 */
public enum HtcFlashLight {
    ;
    private final static String[] LIGHT_ON = {"/system/bin/sh", "-c",
            "echo 125 > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"};
    private final static String[] LIGHT_OFF = {"/system/bin/sh", "-c",
            "echo 0 > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"};

    /**
     * @return This function return true when flash light on activity_rw HTC Desire mobile is switch off.
     */
    public static boolean flashLightOn() {
        try {
            Runtime.getRuntime().exec(LIGHT_ON);
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
    public static boolean flashLightOn(int value) {
        try {
            Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c",
                    "echo " + value + " > /sys/devices/platform/flashlight.0/leds/flashlight/brightness"});
            return true;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return This function return true when flash light on activity_rw HTC Desire mobile is switch off.
     */
    public static boolean flashLightOff() {
        try {
            Runtime.getRuntime().exec(LIGHT_OFF);
            return true;
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

    }

}
