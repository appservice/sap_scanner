package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.tasks.SearchMaterialsAsyncTask;
import eu.appservice.sap_scanner.adapters.MaterialsArrayAdapter;

@TargetApi(Build.VERSION_CODES.BASE)
public class SearchActivity extends AppCompatActivity implements SearchMaterialsAsyncTask.GetAsyncTaskListener {
    private EditText editTextIndexSearchActiv;
    private EditText editTextNameSearchActiv;
    private TextView textViewUnitSearchActiv;
    private EditText editTextStockSearchActiv;
    private TextView textViewAmountSearchActiv;
    private CheckBox checkBoxSearchActiv;
    private Button btnSearchActiv;
    private TextView textViewPositionSearchActiv;
    // private  String CALL_SEARCH_ACTIVITY;


    boolean isRequestDataFromOtherActivity;  //True if it is showed by other activity to find requested material

    private Dialog dialog;
    List<Material> searchedMaterials;
// ArrayList<Material> searchedMaterials = new ArrayList<Material>();


    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextIndexSearchActiv = (EditText) findViewById(R.id.activity_search_et_index);
        editTextNameSearchActiv = (EditText) findViewById(R.id.activity_search_et_name);
        editTextStockSearchActiv = (EditText) findViewById(R.id.activity_search_et_store);
        textViewUnitSearchActiv = (TextView) findViewById(R.id.activity_search_tv_unit);
        textViewAmountSearchActiv = (TextView) findViewById(R.id.activity_search_tv_amount);
        checkBoxSearchActiv = (CheckBox) findViewById(R.id.activity_search_chk_more_zero);
        btnSearchActiv = (Button) findViewById(R.id.activity_search_btn_search);
        textViewPositionSearchActiv = (TextView) findViewById(R.id.activity_search_tv_position);

        isRequestDataFromOtherActivity = this.getIntent().getBooleanExtra(getString(R.string.call_search_activity), false);


        initButtonsClick();

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
            editTextIndexSearchActiv.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            editTextIndexSearchActiv.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        super.onResume();
    }

    private void initButtonsClick() {
        OnClickListener btnListener = new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_search_btn_search:
                        //searchMaterials(editTextIndexSearchActiv.getText().toString(),editTextNameSearchActiv.getText().toString(),editTextStockSearchActiv.getText().toString(),checkBoxSearchActiv.isChecked());
                        new SearchMaterialsAsyncTask(SearchActivity.this)
                                .execute(editTextIndexSearchActiv.getText().toString(),
                                        editTextNameSearchActiv.getText().toString(),
                                        editTextStockSearchActiv.getText().toString(),
                                        checkBoxSearchActiv.isChecked());

                        // new GatDataTask().execute();
                        break;
                    default:
                        break;
                }

            }

        };
        btnSearchActiv.setOnClickListener(btnListener);
    }






    //------------this is called when SearchMaterialsAsyncTask task is finished---------------------
    @Override
    public void onAsyncTaskFinished(List<Material> materials) {
        searchedMaterials = materials;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchActivity.this);
        dialogBuilder.setTitle(SearchActivity.this.getString(R.string.warning_founded,searchedMaterials.size()));

        ListView modeList = new ListView(getApplicationContext());


        MaterialsArrayAdapter arrayMaterialAdapter = new MaterialsArrayAdapter(SearchActivity.this, R.layout.row_list_materials, materials);
        modeList.setFastScrollEnabled(true);
        modeList.setAdapter(arrayMaterialAdapter);

        modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos,
                                    long id) {
                Material foundedMaterial=searchedMaterials.get(pos);

                if (isRequestDataFromOtherActivity) {
                    Intent returnData = new Intent();
                    returnData.putExtra("MATERIAL_FROM_SEARCH_ACTIVITY", foundedMaterial);
                    setResult(RESULT_OK, returnData);
                    dialog.cancel();
                    SearchActivity.this.finish();

                } else {
                    //Toast.makeText(view.getContext(), pos, Toast.LENGTH_LONG).show();
                    editTextIndexSearchActiv.setText(foundedMaterial.getIndex()); //stringArray[pos]
                    editTextNameSearchActiv.setText(foundedMaterial.getName());
                    editTextStockSearchActiv.setText(foundedMaterial.getStore());
                    textViewAmountSearchActiv.setText(String.valueOf(foundedMaterial.getAmount()));
                    textViewAmountSearchActiv.setVisibility(View.VISIBLE);
                    textViewUnitSearchActiv.setText(foundedMaterial.getUnit());
                    textViewUnitSearchActiv.setVisibility(View.VISIBLE);
                    String tabPosition[] = foundedMaterial.getPosition();
                    if (tabPosition != null) {
                        String position = "pozycja: " + tabPosition[1] + "/" + tabPosition[2] + "  gałąź: " + tabPosition[0];
                        textViewPositionSearchActiv.setText(position);
                    }

                }
                dialog.cancel();


            }

        });
        dialogBuilder.setView(modeList);
        dialog = dialogBuilder.create();
        dialog.show();

    }

//-----------------onCreateOptionsMenu--------------------------------------------------------------
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search_show_preferences:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
