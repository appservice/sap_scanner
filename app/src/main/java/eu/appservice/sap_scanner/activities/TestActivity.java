package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.PlantStrucMpk;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.activities.fileChooser.FileChooserActivity;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.FlashLightOnHtc;
import eu.appservice.sap_scanner.logfile.MaterialToFileSaver;

/**
 * Created by Lukasz on 18.02.14.
 * ﹕ SAP Skanner
 */
public class TestActivity extends ActionBarActivity implements ScanSearchFragment.OnGetMaterialFromDb {

    private static final int MPK_REQUEST = 0;
    private static final int FILE_PATH_REQUEST=4;
    private Material materialFromDb;
    private TextView tvUnit, tvStock, tvMPK, tvBudget;
    private EditText editTextAmount;
    private CheckBox checkBoxToZero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvUnit = (TextView) findViewById(R.id.test_activity_tv_unit);
        tvStock = (TextView) findViewById(R.id.test_activity_tv_store);
        tvMPK = (TextView) findViewById(R.id.test_activity_tv_mpk);
        tvBudget = (TextView) findViewById(R.id.test_activity_tv_budget);
        editTextAmount = (EditText) findViewById(R.id.test_activity_et_value);
        checkBoxToZero = (CheckBox) findViewById(R.id.test_activity_chk_is_zero);
    }

    @Override
    public void onGetMaterialFromDb(Material materialFromDb) {
        this.materialFromDb = materialFromDb;
        tvUnit.setText(materialFromDb.getUnit());
        tvStock.setText(materialFromDb.getStore());
    }

    public void mpkButtonClicked(View view) {
        showMpkListActivity();

    }

    private void showMpkListActivity() {
        Intent intent = new Intent(getApplicationContext(), MpkListActivity.class);
        startActivityForResult(intent, MPK_REQUEST);
    }

    //----------------on click save button--------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void saveButtonClicked(View view) {
        String budget = tvBudget.getText().toString();
        String mpk = tvMPK.getText().toString();
        String amount = editTextAmount.getText().toString();

        boolean isToZero = checkBoxToZero.isChecked();

        if (materialFromDb != null) {
            if (!budget.isEmpty()&&!budget.equals(getString(R.string.label_budget))) {
                if(mpk.equals(getString(R.string.label_mpk))){mpk="";}
                if (!amount.isEmpty()) {
                    // Toast.makeText(getApplicationContext(), "zapiany:\n" + materialFromDb.toString(), Toast.LENGTH_LONG).show();

                    double collectedAmount = Double.parseDouble(amount);

                    //-----------update amount in Materials DB, add material in Collected Materials DB and in logFile
                    saveData(budget, mpk, isToZero, collectedAmount);


                    //-----------refresh fragment show-------------------
                    ScanSearchFragment scanSearchFragment = (ScanSearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    scanSearchFragment.showMaterial();

                    //------------clear data----------------------------
                    editTextAmount.setText("");
                    checkBoxToZero.setChecked(false);

                    //-------------Vibrate-------------------------------
                    Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibra.vibrate(60);

                    //-----------------show message about collected material----------------
                    Toast.makeText(getApplicationContext(), getString(R.string.message_collected)
                            + materialFromDb.toString(), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.warning_fill_amount)
                            , Toast.LENGTH_LONG).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.warning_chose_budget_mpk
                ), Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.warning_scan_material),
                    Toast.LENGTH_LONG).show();

        }
    }

//----------------------------------------------------------------------------------------------------

    /**
     * @param budget needed budget
     * @param mpk add MPK
     * @param isToZero if the finish amount should be zero
     * @param collectedAmount amount which is collected
     */
    private void saveData(String budget, String mpk, boolean isToZero, double collectedAmount) {
        //--------------remove collected amount from material----------------
        materialFromDb.setAmount(materialFromDb.getAmount() - collectedAmount);

        //-----------update in Materials database---------------------------------------
        MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
        db.updateMaterial(materialFromDb);
        db.close();

        //----------create Collected Material-----------------------------
        CollectedMaterial cm = new CollectedMaterial(materialFromDb, collectedAmount,
                budget, mpk, isToZero, Utils.nowDate(), "");

        //---------add Collected Material to Collected Materials DB------------
        CollectedMaterialDbOpenHelper cmDB = new CollectedMaterialDbOpenHelper(getApplicationContext());
        cmDB.addCollectedMaterial(cm);
        cmDB.close();

        //--------add to rw log file---------------------------------------------
        MaterialToFileSaver logFileSaver = new MaterialToFileSaver(getApplicationContext());
        logFileSaver.save(cm);
    }


    public void listButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), CollectedMaterialsListActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_PATH_REQUEST:
                if (resultCode == RESULT_OK) {
                  String fileName=data.getStringExtra("GetFileName");
                    Log.i("fila name", fileName);
                }

                break;
        }

        //---------this loop is checking function onActivityResult from all fragments assigned to this activity
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


        switch (requestCode) {
            //-------return MPK and Budget ----------------
            case MPK_REQUEST:
                if (resultCode == RESULT_OK) {

                    PlantStrucMpk myBudget=(PlantStrucMpk)data.getSerializableExtra("MPK_BUDGET_RESULT");
                    this.tvMPK.setText(myBudget.getValue());
                    this.tvBudget.setText(myBudget.getBudget());
                }
                break;



            default:
                break;
        }
    }


    /**
     * This function switch the flash light
     *
     */
    @Override
    public boolean onSearchRequested() {
        FlashLightOnHtc.getInstance().flashLightToggle();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_rw, menu);
        return super.onCreateOptionsMenu(menu);
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
                // exportListToExcel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //--------------show file lists-----test -------------------------------
    public void onFileListButtonClick(View v){
        Intent intent=new Intent(getApplicationContext(),FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.FILE_FILTER,"*.xls");

        startActivityForResult(intent, FILE_PATH_REQUEST);
    }

}
