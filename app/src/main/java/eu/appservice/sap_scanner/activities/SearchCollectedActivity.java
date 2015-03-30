package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.tasks.SearchCollectedMaterialsAsyncTask;


/**
 *Package ${PACKAGE_NAME}
 * Created by Lukasz on 01.10.13.
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class SearchCollectedActivity extends ActionBarActivity {
    private EditText editTextIndexSearchPickedActiv;
    private EditText editTextNameSearchPickedActiv;
    private EditText editTextMpkSearchPickedActiv;
    private EditText editTextBudgetSearchPickedActiv;


    private Button btnSearchPickedActiv;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_collected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editTextIndexSearchPickedActiv = (EditText) findViewById(R.id.activity_search_collected_et_index);
        editTextNameSearchPickedActiv = (EditText) findViewById(R.id.activity_search_collected_et_name);
        editTextMpkSearchPickedActiv = (EditText) findViewById(R.id.activity_search_collected_et_mpk);
        editTextBudgetSearchPickedActiv = (EditText) findViewById(R.id.activity_search_collected_et_budget);


        btnSearchPickedActiv = (Button) findViewById(R.id.activity_search_collected_btn_search);


        initButtonsClick();

    }

    private void initButtonsClick() {
        View.OnClickListener btnListener = new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.activity_search_collected_btn_search:


                        new SearchCollectedMaterialsAsyncTask(SearchCollectedActivity.this).execute(editTextIndexSearchPickedActiv.getText().toString(), editTextNameSearchPickedActiv.getText().toString(), editTextMpkSearchPickedActiv.getText().toString(), editTextBudgetSearchPickedActiv.getText().toString());
                        break;
                    default:
                        break;
                }

            }

        };
        btnSearchPickedActiv.setOnClickListener(btnListener);
    }


}