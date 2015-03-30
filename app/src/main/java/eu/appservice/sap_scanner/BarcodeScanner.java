package eu.appservice.sap_scanner;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Lukasz on 2014-04-12.
 * ﹕ SAP Skanner
 */
public class BarcodeScanner {

    private static final String OPERATION_MODE="SCAN_MODE", CODE_FORMAT_MODE="QR_CODE_MODE";

   private Activity parentActivity;


    public BarcodeScanner(Activity parentActivity){
        this.parentActivity=parentActivity;

    }

    public void showScannerForResult(int SCANNER_REQUEST){
        Intent barcodeScannerIntent = new Intent(parentActivity.getString(R.string.barcode_scan_intent_address));
        IntentAvailableChecker iac = new IntentAvailableChecker(parentActivity);

        if (iac.isIntentAvailable(barcodeScannerIntent)) {
            barcodeScannerIntent.putExtra(OPERATION_MODE, CODE_FORMAT_MODE);
            parentActivity.startActivityForResult(barcodeScannerIntent, SCANNER_REQUEST);

        } else {
            Toast.makeText(parentActivity, parentActivity.getString(R.string.barcode_scanner_not_installed_message), Toast.LENGTH_LONG).show();
        }
    }


//-----------------------------------------------------------------------------------------------------
    /**
     *
     * @param messageToEncode String of data which should be encode in BarcodeScanner;
     */
    public void encodeData(String messageToEncode) {

        Intent barcodeScannerIntent = new Intent(parentActivity.getString(R.string.barcode_encode_intent_address));
        IntentAvailableChecker iac = new IntentAvailableChecker(parentActivity);
        if (iac.isIntentAvailable(barcodeScannerIntent)) {
            String encodeData = messageToEncode;

            barcodeScannerIntent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
            barcodeScannerIntent.putExtra("ENCODE_DATA", encodeData);
            barcodeScannerIntent.putExtra("ENCODE_FORMAT", "QR_CODE");//CODE_128
            parentActivity.startActivity(barcodeScannerIntent);
        } else {
            Toast.makeText(parentActivity, "Zainstaluj aplikację BarcodeScaner", Toast.LENGTH_LONG).show();
        }
    }

}
