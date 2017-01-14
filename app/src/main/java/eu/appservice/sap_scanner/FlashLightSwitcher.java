package eu.appservice.sap_scanner;


import android.hardware.Camera;
import android.util.Log;

/**
 * Created by lukasz.mochel on 14.01.2017.
 */
public class FlashLightSwitcher {

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    android.hardware.Camera.Parameters params;


    private static FlashLightSwitcher ourInstance = new FlashLightSwitcher();

    public static FlashLightSwitcher getInstance() {
        return ourInstance;
    }

    private FlashLightSwitcher() {
        getCamera();
    }


    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. : ", e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound


            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
          //  toggleButtonImage();
        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound

            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            // changing button/switch image
          //  toggleButtonImage();
        }
    }

    public void toggleButtonImage() {
       if(isFlashOn){
           turnOffFlash();

       }else{
           turnOnFlash();
       }
    }

    public void onStop(){
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
