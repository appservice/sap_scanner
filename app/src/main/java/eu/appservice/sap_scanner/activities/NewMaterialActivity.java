package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;


/**
 * Created by Lukasz on 10.01.14.
 * ﹕ SAP Skanner
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class NewMaterialActivity extends AppCompatActivity {



        private EditText editTextIndexAddActiv;
        private EditText editTextNameAddActiv;
        private EditText editTextStoreAddActiv;
        private Spinner spinnerUnitAddStore;
        private Button btnAddNewMaterialActiv;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_material);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            editTextIndexAddActiv = (EditText) findViewById(R.id.activity_new_material_et_index);
            editTextNameAddActiv = (EditText) findViewById(R.id.activity_new_material_et_name);
            editTextStoreAddActiv = (EditText) findViewById(R.id.activity_new_material_et_store);
            spinnerUnitAddStore = (Spinner) findViewById(R.id.activity_new_material_sp_unit);
            btnAddNewMaterialActiv = (Button) findViewById(R.id.activity_new_material_btn_save);
            initButtonsClick();
        }

        private void initButtonsClick() {
            View.OnClickListener buttonListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.activity_new_material_btn_save:
                            addMaterial();
                            break;
                        default:
                            break;
                    }
                }
            };
            btnAddNewMaterialActiv.setOnClickListener(buttonListener);

        }

        protected void addMaterial() {
            if((editTextIndexAddActiv.getText().toString().length()>0) &&( editTextNameAddActiv.getText().toString().length()>0)
                    &&editTextStoreAddActiv.getText().toString().length()>0){


            Material material = new Material();
            material.setIndex(editTextIndexAddActiv.getText().toString());
            material.setName(editTextNameAddActiv.getText().toString());
            material.setStore(editTextStoreAddActiv.getText().toString());
            material.setAmount(0);
            material.setDescription("");
            material.setUnit(spinnerUnitAddStore.getSelectedItem().toString());
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
            db.addMaterial(material);
            Toast.makeText(getApplicationContext(), "Dodano materiał: " + editTextNameAddActiv.getText().toString(), Toast.LENGTH_SHORT).show();
            clearText();
            }else { Toast.makeText(getApplicationContext(), "Uzupełnij dane", Toast.LENGTH_SHORT).show();}



        }

    private void clearText(){
        editTextIndexAddActiv.setText("");
        editTextNameAddActiv.setText("");
        editTextNameAddActiv.setText("");
        editTextStoreAddActiv.setText("");
        spinnerUnitAddStore.setSelection(0);
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.activity_new_material, menu);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected( MenuItem item) {

            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;

            }
            return super.onOptionsItemSelected(item);
        }
        /* (non-Javadoc)
         * @see android.app.Activity#onResume()
         */
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onResume() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean isOnlyDigitsInIndex = sp.getBoolean("pref_is_only_digits", false);
            if (isOnlyDigitsInIndex) {
                editTextIndexAddActiv.setInputType(InputType.TYPE_CLASS_PHONE);
            } else {
                editTextIndexAddActiv.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            super.onResume();
        }


    }

