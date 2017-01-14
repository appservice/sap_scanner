package eu.appservice.sap_scanner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map.Entry;
import java.util.TreeMap;

import eu.appservice.sap_scanner.BarcodeScanner;
import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.FlashLightSwitcher;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.PlantStrucMpk;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.ScannedMaterial;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.activities.dialogs.ExportDataToExcelDialog;
import eu.appservice.sap_scanner.activities.dialogs.NoExistInDbDialog;
import eu.appservice.sap_scanner.activities.dialogs.NoSdCardDetectedDialog;
import eu.appservice.sap_scanner.activities.dialogs.RemoveRwListFromDbDialog;
import eu.appservice.sap_scanner.activities.tasks.WriteRwToExcelAsyncTask;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.FlashLightOnHtc;
import eu.appservice.sap_scanner.logfile.MaterialSaver;
import eu.appservice.sap_scanner.logfile.MaterialToFileSaver;


public class RwActivity extends ActionBarActivity implements ExportDataToExcelDialog.Communicator, RemoveRwListFromDbDialog.Communicator {

    private Button showMpkBtn;

    private static final int MPK_REQUEST = 0;
    private static final int SCANNER_REQUEST = 1;
    private static final int SEARCH_REQUEST = 2;
    private static final int SIGNATURE_REQUEST = 3;

    private Material material;
    private Material materialFromDb;


    private EditText tvScannedMaterialIndex;
    private TextView tvScannedMaterialName;
    private TextView tvScannedMaterialAmount;



    private TextView tvScannedMaterialUnit;
    private TextView tvScannedMaterialStock;

    private boolean isAutoFlashOffPreference;

    private ImageButton imgBtnScanRwActiv;
    private Button btnSaveRwActiv;

    private EditText editTextRemovedValue;
    private ImageButton imgBtnSearchRwActiv;

    private TextView tvMpkRwActiv;
    private Button btnPickedMaterialList;
    private ImageButton btnFingerPaintActiv;

    //private String budgetValue;

    private TextView tvBudgetValue;

    private CheckBox checkBoxZeroRwActiv;
    private String sign = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showMpkBtn = (Button) findViewById(R.id.activity_rw_btn_mpk);
        imgBtnScanRwActiv = (ImageButton) findViewById(R.id.activity_rw_ibtn_scan);
        editTextRemovedValue = (EditText) findViewById(R.id.activity_rw_et_value);
        btnSaveRwActiv = (Button) findViewById(R.id.activity_rw_btn_save);
        tvScannedMaterialIndex = (EditText) findViewById(R.id.activity_rw_tv_scanned_index);


        tvScannedMaterialName = (TextView) findViewById(R.id.activity_rw_tv_scanned_name);
        imgBtnSearchRwActiv = (ImageButton) findViewById(R.id.activity_rw_ibtn_search);
        tvScannedMaterialAmount = (TextView) findViewById(R.id.activity_rw_tv_scanned_amount);
        tvScannedMaterialUnit = (TextView) findViewById(R.id.activity_rw_tv_scanned_unit);
        tvScannedMaterialStock = (TextView) findViewById(R.id.activity_rw_tv_scanned_store);
        btnPickedMaterialList = (Button) findViewById(R.id.activity_rw_btn_list_materials);
        checkBoxZeroRwActiv = (CheckBox) findViewById(R.id.activity_rw_chk_is_zero);
        btnFingerPaintActiv = (ImageButton) findViewById(R.id.activity_rw_btn_signature);
        tvBudgetValue = (TextView) findViewById(R.id.activity_rw_tv_budget);
        tvMpkRwActiv = (TextView) findViewById(R.id.activity_rw_tv_mpk);

        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isAutoFlashOffPreference = sf.getBoolean("pref_is_auto_flash_off", false);
        //tvScannedMaterialAmount.setText("test \n test2 \n test3 \b test4\n");
        initButtonsClick();


    }

    private void initButtonsClick() {
        //  Context context=getApplicationContext();
        //  SharedPreferences sf = context.getSharedPreferences(getString(R.string.pref_flash),context.MODE_PRIVATE);
        //   boolean defaultValue=getResources().getBoolean(R.string.is_auto_off_default);
        //   isAutoFlashOffPreference=sf.getBoolean(getString(R.string.my_pref_flash),defaultValue);

        OnClickListener btnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_rw_ibtn_scan:
                        showScanner();
                        break;
                    case R.id.activity_rw_ibtn_search:
                        showSearchActivity();
                        break;
                    case R.id.activity_rw_btn_mpk:
                        showMpkListActivity();
                        break;
                    case R.id.activity_rw_btn_save:
                        addMaterialToRw();
                        break;
                    case R.id.activity_rw_btn_list_materials:
                        showPickedMaterialList();
                        break;
                    case R.id.activity_rw_btn_signature:
                        showFingerDrawActivity();
                    default:
                        break;
                }
            }
        };
        showMpkBtn.setOnClickListener(btnListener);
        imgBtnScanRwActiv.setOnClickListener(btnListener);
        imgBtnSearchRwActiv.setOnClickListener(btnListener);
        btnSaveRwActiv.setOnClickListener(btnListener);
        btnPickedMaterialList.setOnClickListener(btnListener);
        btnFingerPaintActiv.setOnClickListener(btnListener);



   /*   tvScannedMaterialIndex.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.copyTextToClipboard(RwActivity.this,"index",tvScannedMaterialIndex.getText().toString());
                Toast.makeText(RwActivity.this,"dodano do schowka",Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/
    }

    private void showFingerDrawActivity() {
        Intent intent = new Intent(getApplicationContext(), FingerPaintActivity.class);
        startActivityForResult(intent, SIGNATURE_REQUEST);
    }

    private void showPickedMaterialList() {
        Intent intent = new Intent(getApplicationContext(), CollectedMaterialsListActivity.class);//MainList.class
        startActivity(intent);

    }

    private void showSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(getString(R.string.call_search_activity), true);
        startActivityForResult(intent, SEARCH_REQUEST);
    }

    private void showScanner() {

        BarcodeScanner barcodeScanner=new BarcodeScanner(this);
        barcodeScanner.showScannerForResult(SCANNER_REQUEST);
    }

    private void showMpkListActivity() {
        Intent intent = new Intent(getApplicationContext(), MpkListActivity.class);
        startActivityForResult(intent, MPK_REQUEST);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.material = new ScannedMaterial(data);

                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
                    this.materialFromDb = db.getMaterialByIndexAndStore(material.getIndex(), material.getStore());
                    db.close();
                    showMaterial();
                    if (materialFromDb == null) {
                        materialNotInDb();

                    }
                }
                //if (isAutoFlashOffPreference) HtcFlashLight.flashLightOff();
                if (isAutoFlashOffPreference) FlashLightOnHtc.getInstance().flashLightOff();
                break;
            case SEARCH_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.materialFromDb = (Material) data.getSerializableExtra("MATERIAL_FROM_SEARCH_ACTIVITY");
                    showMaterial();
                }
                break;
            case MPK_REQUEST:
                if (resultCode == RESULT_OK) {
           /*         String text = data.getStringExtra("MPK_BUDGET_RESULT");
                    assert text != null;

                    if (text.contains(";")) {
                        String[] budget = text.split(";");
                        this.tvMpkRwActiv.setText(budget[0]);
                        this.tvBudgetValue.setText(budget[1]);

                    }*/
                    PlantStrucMpk myBudget=(PlantStrucMpk)data.getSerializableExtra("MPK_BUDGET_RESULT");
                    this.tvMpkRwActiv.setText(myBudget.getValue());
                    this.tvBudgetValue.setText(myBudget.getBudget());
                }
                break;
            case SIGNATURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.sign = data.getStringExtra("SIGNATURE_RESULT");
                    Log.i("adres podpisu", this.sign);


                }

                break;

            default:
                break;
        }
    }


    /*
     * @author mochelek 11-03-2013
     */
    @Override
    protected void onResume() {

        super.onResume();
        showMaterial();

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

            double amount = db.getMaterial(materialFromDb.getId()).getAmount();// uaktualnienie ilości
            this.materialFromDb.setAmount(amount);

            TreeMap<String, Double> mapStockAmount = (TreeMap<String, Double>) db.getMapStockAmount(this.materialFromDb.getIndex());
            for (Entry<String, Double> pairs : mapStockAmount.entrySet()) {
                sb.append(Utils.parse(pairs.getValue()));
                sb.append(" ");
                sb.append(unit);
                sb.append("   skład: ");
                sb.append(pairs.getKey());
                sb.append("\n");

            }


            tvScannedMaterialAmount.setText(sb.toString());
            db.close();

        } else {
            tvScannedMaterialIndex.setText("");
            tvScannedMaterialName.setText("");
            tvScannedMaterialUnit.setText("");
            tvScannedMaterialStock.setText("");
            tvScannedMaterialAmount.setText("");
        }
    }

    /**
     * show Dialog when the material wont be in Database
     */
    private void materialNotInDb() {

        NoExistInDbDialog noExistInDbDialog=NoExistInDbDialog.getInstance(material);
        noExistInDbDialog.show(getSupportFragmentManager(),"NO_EXIST_IN_DB");

    }

    private void addMaterialToRw() {
        String removedValueString = editTextRemovedValue.getText().toString();
        String mpk = tvMpkRwActiv.getText().toString();
        String budget = tvBudgetValue.getText().toString();
        Boolean toZero = this.checkBoxZeroRwActiv.isChecked();


        if (materialFromDb != null) {
            if ((removedValueString.length() > 0)) {
                if (mpk.length() > 0 || budget.length() > 0) {

                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
                    Double removedValue = Double.valueOf(removedValueString);
                    this.materialFromDb.removeValue(removedValue); //uaktualnienie materiału


                    int removedRow = db.updateMaterial(this.materialFromDb); //uaktualnienie ilości w bazie danych
                    db.close();
                    if (removedRow > -1) {

                        CollectedMaterial collectedMaterial = new CollectedMaterial(materialFromDb, removedValue, budget, mpk, toZero, Utils.nowDate(), this.sign);

                        addToCollectedMaterialDbOpenHelper(collectedMaterial);// zapis do bazy danych pobranych materiałów
                        writeToFile(collectedMaterial);//zapis do pliku txt pobranych materiałów

                        Toast.makeText(getApplicationContext(), materialFromDb.toString() + "Pobrano " + removedValue + " " + this.materialFromDb.getUnit(), Toast.LENGTH_SHORT).show();
                        Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        vibra.vibrate(60);

                        showMaterial();

                        editTextRemovedValue.setText("");
                        checkBoxZeroRwActiv.setChecked(false);
                        this.sign = "";
                    } else {
                        materialNotInDb();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Wybierz MPK / Zlecenie", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Podaj pobieraną ilość", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Wybirz / zeskanuj pobierany materiał!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This function is saving picking material to file "magazyn.txt"
     */
    private void writeToFile(CollectedMaterial collectedMaterial) {  //Material materialFromDatabase, Double pickingQuantity, String mpk, String budget, boolean isZero
        if (Utils.isExternalStorageWritable()) {
            MaterialSaver materialSaver = new MaterialToFileSaver(getApplicationContext());
            if (materialSaver.save(collectedMaterial) == -1) {
                Log.w("saveToLog", "can't save data to log file");
            }

        } else {
            cantWriteToExternalStorageMessage();
            Log.w("external storage", "card not instlled");
        }


    }





    /**
     * This function add to database of picked materials  the new picedMaterial
     *
     * @param collectedMaterial material picked
     */
    private void addToCollectedMaterialDbOpenHelper(CollectedMaterial collectedMaterial) {
        CollectedMaterialDbOpenHelper collectedMaterialDbOpenHelper = new CollectedMaterialDbOpenHelper(getApplicationContext());
        collectedMaterialDbOpenHelper.addCollectedMaterial(collectedMaterial);
        collectedMaterialDbOpenHelper.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_rw, menu);
        return true;
    }


    /* (non-Javadoc)
         * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
         */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_pz_show_preferences:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_rw_export_to_xls:
                exportListToExcel();
                return true;
            case R.id.menu_rw_lamp:
                //FlashLightOnHtc.getInstance().flashLightToggle();
                FlashLightSwitcher.getInstance().toggleButtonImage();
                Log.i("FlashLight", "Flash LIght on/off ");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Exporting data to excel file
     */
    private void exportListToExcel() {

        if (Utils.isExternalStorageWritable()) {
            ExportDataToExcelDialog toExcelDialog = new ExportDataToExcelDialog();
            toExcelDialog.show(getSupportFragmentManager(), "EXPORTING_TO_SD");

        } else {
            cantWriteToExternalStorageMessage();

        }

    }


    /**
     *This function is called when on ExportDataToExcelDialog is clicked ok button.
     * */
    @Override
    public void okClicked() {
        WriteRwToExcelAsyncTask at = new WriteRwToExcelAsyncTask(RwActivity.this);
        at.addObserver(new MaterialToFileSaver(getApplicationContext()));
        at.execute();

        RemoveRwListFromDbDialog removeRwListFromDbDialog=new RemoveRwListFromDbDialog();
        removeRwListFromDbDialog.show(getSupportFragmentManager(),"REMOVE_RW_LIST");
    }

    public void removeRwListFromDb() {
        CollectedMaterialDbOpenHelper collectedMaterialList = new CollectedMaterialDbOpenHelper(getApplicationContext());
        collectedMaterialList.dropTable();
        collectedMaterialList.close();

    }

    /**
     * This function show alert when the SD card is not mounted
     */
    private void cantWriteToExternalStorageMessage() {

        NoSdCardDetectedDialog noSdCardDetectedDialog = new NoSdCardDetectedDialog();
        noSdCardDetectedDialog.show(getSupportFragmentManager(), "NO_SD_CARD");
    }


    @Override
    public boolean onSearchRequested() {

        FlashLightOnHtc.getInstance().flashLightToggle();

        return true;
    }

    @Override
    protected void onStop() {
        FlashLightSwitcher.getInstance().onStop();
        super.onStop();
    }
}
