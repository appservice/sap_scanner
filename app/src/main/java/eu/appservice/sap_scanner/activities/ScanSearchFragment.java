package eu.appservice.sap_scanner.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

import eu.appservice.sap_scanner.BarcodeScanner;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.ScannedMaterial;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.activities.dialogs.NoExistInDbDialog;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.FlashLightOnHtc;


/**
 * Created by Lukasz on 18.02.14.
 * ﹕ SAP Skanner
 */
public class ScanSearchFragment extends Fragment implements View.OnClickListener{

    OnGetMaterialFromDb mCallback;


    private static final int SCANNER_REQUEST = 1;
    private static final int SEARCH_REQUEST = 2;


    private Material materialFromDb;
    private TextView tvScannedMaterialIndex;
    private TextView tvScannedMaterialName;
    private TextView tvScannedMaterialAmount;

    private boolean isSwitchedFlashLight;
    private boolean isAutoFlashOffPreference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_scan_search,container,false);
        ImageButton imgBtnSearch = (ImageButton) v.findViewById(R.id.fragment_scan_search_ibtn_search);
        imgBtnSearch.setOnClickListener(this);

        ImageButton imgBtnScan = (ImageButton) v.findViewById(R.id.fragment_scan_search_ibtn_scan);
        imgBtnScan.setOnClickListener(this);

        tvScannedMaterialIndex = (TextView) v.findViewById(R.id.fragment_scan_search_tv_index);
        tvScannedMaterialName = (TextView) v.findViewById(R.id.fragment_scan_search_tv_name);
        tvScannedMaterialAmount = (TextView) v.findViewById(R.id.fragment_scan_search_tv_amount);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_scan_search_ibtn_scan:
                showScanner();
                break;
            case R.id.fragment_scan_search_ibtn_search:
               showSearchActivity();
                break;

            default:
                break;
        }
    }



    private void showSearchActivity() {
        Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
        intent.putExtra("IS_FROM_OTHER_ACTIVITY", true);
        startActivityForResult(intent, SEARCH_REQUEST);
    }

    private void showScanner() {

        BarcodeScanner barcodeScanner=new BarcodeScanner(getActivity());
        barcodeScanner.showScannerForResult(SCANNER_REQUEST);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANNER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Material scannedMaterial = new ScannedMaterial(data);
                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getActivity().getApplicationContext());
                    this.materialFromDb = db.getMaterialByIndexAndStore(scannedMaterial.getIndex(), scannedMaterial.getStore());
                    showMaterial();
                    mCallback.onGetMaterialFromDb(this.materialFromDb);
                    if (materialFromDb == null) {
                        materialNotInDb(scannedMaterial);
                    }
                }
                if (isAutoFlashOffPreference) FlashLightOnHtc.getInstance().flashLightOff();
                break;
            case SEARCH_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    this.materialFromDb = (Material) data.getSerializableExtra("MATERIAL_FROM_SEARCH_ACTIVITY");
                    showMaterial();
                    mCallback.onGetMaterialFromDb(this.materialFromDb);
                }
                break;


            default:
                break;
        }
    }

    protected void showMaterial() {
        if (this.materialFromDb != null) {
            tvScannedMaterialIndex.setText(this.materialFromDb.getIndex());
            tvScannedMaterialName.setText(this.materialFromDb.getName());
            String unit = this.materialFromDb.getUnit();
            StringBuilder sb = new StringBuilder();
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getActivity().getApplicationContext());

            double amount = db.getMaterial(materialFromDb.getId()).getAmount();// uaktualnienie ilości
            this.materialFromDb.setAmount(amount);

            String store=this.getString(R.string.label_store);

            TreeMap<String, Double> mapStockAmount = (TreeMap<String, Double>) db.getMapStockAmount(this.materialFromDb.getIndex());
            for (Map.Entry<String, Double> pairs : mapStockAmount.entrySet()) {
                sb.append(Utils.parse(pairs.getValue()));
                sb.append(" ");
                sb.append(unit);
                sb.append("   "+store+": ");
                sb.append(pairs.getKey());
                sb.append("\n");

            }


            tvScannedMaterialAmount.setText(sb.toString());
            db.close();

        } else {
            tvScannedMaterialIndex.setText("");
            tvScannedMaterialName.setText("");
            tvScannedMaterialAmount.setText("");
        }
    }
    /**
     * show Dialog when the material wont be in Database
     */
    private void materialNotInDb(Material scannedMaterial) {
        //Toast.makeText(getApplicationContext(), material.toString()+" nie istnieje w bazie danych!\n Nie dodano.", Toast.LENGTH_SHORT).show();

        NoExistInDbDialog noExistInDbDialog=NoExistInDbDialog.getInstance(scannedMaterial);
        noExistInDbDialog.show(getActivity().getSupportFragmentManager(), "NO_EXIST_IN_DB");

    }

   public interface OnGetMaterialFromDb{
       void onGetMaterialFromDb(Material materialFromDb);
   }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGetMaterialFromDb) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGetMaterialFromDb");
        }
    }

    public void setMaterialFromDb(Material materialFromDb) {
        this.materialFromDb = materialFromDb;
    }
}
