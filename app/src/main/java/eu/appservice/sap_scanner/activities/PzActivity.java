package eu.appservice.sap_scanner.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map.Entry;
import java.util.TreeMap;

import eu.appservice.sap_scanner.BarcodeScanner;
import eu.appservice.sap_scanner.FlashLightSwitcher;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.ScannedMaterial;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.databases.PzMaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.FlashLightOnHtc;
import eu.appservice.sap_scanner.logfile.StoredMaterial;


public class PzActivity extends ActionBarActivity {
    private ImageButton imgBtnScanPzActiv;
    private EditText editTextAddedValue;
    private Button btnAddPzActiv;
    private static final int SCANNER_REQUEST = 0;
    private static final int SEARCH_REQUEST = 1;

    private Material material;
    private Material materialFromDb;
    private TextView tvScannedMaterialIndex;
    private TextView tvScannedMaterialName;
    private TextView tvScannedMaterialAmount;
    private ImageButton imgBtnSearchPzActiv;
    private TextView tvScannedMaterialUnit;
    private TextView tvScannedMaterialStock;
    private boolean isAutoFlashOffPreference;


    /* (non-Javadoc)
         * @see android.app.Activity#onCreate(android.os.Bundle)
         */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pz);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgBtnScanPzActiv = (ImageButton) findViewById(R.id.activity_pz_ibtn_scan);
        editTextAddedValue = (EditText) findViewById(R.id.activity_pz_et_value);
        btnAddPzActiv = (Button) findViewById(R.id.activity_pz_btn_save);
        tvScannedMaterialIndex = (TextView) findViewById(R.id.activity_pz_tv_scanned_index);
        tvScannedMaterialName = (TextView) findViewById(R.id.activity_pz_tv_scanned_name);
        imgBtnSearchPzActiv = (ImageButton) findViewById(R.id.activity_pz_ibtn_search);
        tvScannedMaterialAmount = (TextView) findViewById(R.id.activity_pz_et_scanned_amount);
        tvScannedMaterialUnit = (TextView) findViewById(R.id.activity_pz_tv_scanned_unit);
        tvScannedMaterialStock = (TextView) findViewById(R.id.activity_pz_tv_scanned_store);

        //isAutoFlashOffPreference=
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isAutoFlashOffPreference = sf.getBoolean("pref_is_auto_flash_off", false);
        initButtonsClick();
    }

    private void initButtonsClick() {
        OnClickListener buttonListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_pz_ibtn_scan:
                        showScanner();
                        break;
                    case R.id.activity_pz_ibtn_search:
                        showSearchActivity();
                        break;
                    case R.id.activity_pz_btn_save:
                        updateAmountInDatabase();
                        break;

                    default:
                        break;

                }

            }
        };
        imgBtnScanPzActiv.setOnClickListener(buttonListener);
        btnAddPzActiv.setOnClickListener(buttonListener);
        imgBtnSearchPzActiv.setOnClickListener(buttonListener);

    }



    protected void showSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(getString(R.string.call_search_activity), true);
        startActivityForResult(intent, SEARCH_REQUEST);

    }

    protected void updateAmountInDatabase() {
        // @Nullable
        String text = editTextAddedValue.getText().toString();
        if ((text.length() > 0) && (materialFromDb != null)) {
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(
                    getApplicationContext());
            Double addedValue = Double.valueOf(editTextAddedValue.getText()
                    .toString());

            boolean isAdded = db.updateAmount(materialFromDb, addedValue);

            if (isAdded) {
                Toast.makeText(getApplicationContext(), "Dodano " + addedValue + " " + this.materialFromDb.getUnit(), Toast.LENGTH_SHORT).show();
                materialFromDb.setAmount(materialFromDb.getAmount() + addedValue);
                addMaterialToPzDb(materialFromDb, addedValue);
                showMaterial();
            } else {
                materialNotInDb();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_text_set_amount_pz_activ, Toast.LENGTH_SHORT).show();
        }
    }

    private void addMaterialToPzDb(Material materialFromDb, Double addedValue) {


        Material material = materialFromDb.clone();
        material.setAmount(addedValue);
        PzMaterialsDbOpenHelper pzOpenHelper=new PzMaterialsDbOpenHelper(getApplicationContext());
        StoredMaterial storedMaterial=new StoredMaterial(material, Utils.nowDate());
        pzOpenHelper.addStoredMaterial(storedMaterial);

    }


    private void materialNotInDb() {

        //Toast.makeText(getApplicationContext(), material.toString()+" nie istnieje w bazie danych!\n Nie dodano.", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                PzActivity.this);

        builder.setMessage(material.toString() + "\n\nNIE ISTNIEJE W BAZIE DANYCH! Czy dodać materiał do bazy?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(
                                getApplicationContext());
                        boolean addingMaterialResult = db.addMaterial(material);
                        if (addingMaterialResult) {
                            Toast.makeText(getApplicationContext(), "Dodano materiał: " + material.getIndex() + " " + material.getName(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Błąd przy dodawaniu", Toast.LENGTH_LONG).show();

                        }

                    }
                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Nie dodano materiału do bazy", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

        builder.show();

    }

    private void showScanner() {
        BarcodeScanner barcodeScanner=new BarcodeScanner(this);
        barcodeScanner.showScannerForResult(SCANNER_REQUEST);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.material = new ScannedMaterial(data);
                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(
                            getApplicationContext());
                    materialFromDb = db.getMaterialByIndexAndStore(
                            material.getIndex(), material.getStore());
                    if (materialFromDb != null) {
                        showMaterial();
                    } else {
                        tvScannedMaterialIndex.setText("nie isnieje w bazie danych!");
                        materialNotInDb();
                    }
                }
                if (isAutoFlashOffPreference)
                    FlashLightOnHtc.getInstance().flashLightOff();
                break;

            case SEARCH_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.materialFromDb = (Material) data.getSerializableExtra("MATERIAL_FROM_SEARCH_ACTIVITY");
                    showMaterial();
                }
                break;

            default:
                break;

        }

    }


    private void showMaterial() {
        if (this.materialFromDb != null) {

            tvScannedMaterialIndex.setText(this.materialFromDb.getIndex());
            tvScannedMaterialName.setText(this.materialFromDb.getName());
            tvScannedMaterialUnit.setText(this.materialFromDb.getUnit());
            tvScannedMaterialStock.setText(this.materialFromDb.getStore());
            String unit = this.materialFromDb.getUnit();
            //String amount="";
            StringBuilder sb = new StringBuilder();
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
            TreeMap<String, Double> mapStockAmount = (TreeMap<String, Double>) db.getMapStockAmount(this.materialFromDb.getIndex());

            for (Entry<String, Double> pairs : mapStockAmount.entrySet()) {
                sb.append(Utils.parse(pairs.getValue()));
                sb.append(" ");
                sb.append(unit);
                sb.append("   skład: ");
                sb.append(pairs.getKey());
                sb.append("\n");
                //amount=amount+pairs.getValue()+" "+unit+"   "+pairs.getKey()+"\n";


            }

            tvScannedMaterialAmount.setText(sb.toString());
            editTextAddedValue.setText("");

        } else {
            tvScannedMaterialIndex.setText("");
            tvScannedMaterialName.setText("");
            tvScannedMaterialUnit.setText("");
            tvScannedMaterialStock.setText("");
            tvScannedMaterialAmount.setText("");
        }
    }


    public void onListButtonClick(View view){
        Intent intent=new Intent(getApplicationContext(),PzListActivity.class);
        startActivity(intent);

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_pz, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_pz_show_preferences:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_pz_add_new_material:
                startActivity(new Intent(getApplicationContext(), NewMaterialActivity.class));
                return true;
    /*        case R.id.menu_pz_lamp:
                FlashLightSwitcher.getInstance().flashLightToggle();

                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSearchRequested()
     */
    @Override
    public boolean onSearchRequested() {

        FlashLightOnHtc.getInstance().flashLightToggle();
        return true;
    }

}
