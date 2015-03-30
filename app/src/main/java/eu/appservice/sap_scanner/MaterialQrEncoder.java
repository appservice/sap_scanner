package eu.appservice.sap_scanner;

import android.content.Intent;

/**
 * Created by Lukasz on 2014-04-06.
 * ﹕ SAP Skanner
 */
public class MaterialQrEncoder {
    Material material;
    public MaterialQrEncoder(Material material){
        this.material=material;
    }

    public Intent encodeMaterialIntent(){
        //if (isIntentAvailable(getApplicationContext(), "com.google.zxing.client.android.ENCODE")) {
            String encodeData = material.getDataToEncodeQrCode();
            Intent intent = new Intent("com.google.zxing.client.android.ENCODE");
            intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
            intent.putExtra("ENCODE_DATA", encodeData);
            intent.putExtra("ENCODE_FORMAT", "QR_CODE");//CODE_128


        return intent;
    }


}
