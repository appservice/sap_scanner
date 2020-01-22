package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import eu.appservice.sap_scanner.PlantStrucMpk;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.adapters.MpkArrayAdapter;
import eu.appservice.sap_scanner.databases.CompanyStructDbOpenHelper;


/**
 * Created by Lukasz on 03.10.13.
 * ﹕ ${PROJECT_NAME}
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class MpkListActivity extends AppCompatActivity {


    CompanyStructDbOpenHelper db;
    private int parentId = 0;
    //private Bundle savedInstanceState;
    private ListView lv;
    private List<PlantStrucMpk> lista;
    private int longItemId;
    private String defaultBudget;
    private MpkArrayAdapter adapter;
   // private ArrayAdapter<String> adapter;


    private void fillData() {

        lista = db.getFactorysByParentId(parentId);
        adapter=new MpkArrayAdapter(MpkListActivity.this,R.layout.row_list_mpk,lista);
        lv.setAdapter(adapter);

    }

    //---------------onCreate--------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mpk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        defaultBudget = sf.getString("pref_default_budget", "");
        db = new CompanyStructDbOpenHelper(this);


        lv = (ListView) findViewById(R.id.activity_list_mpk_lv);


        fillData();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onListItemClick(i);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int pos, long id) {

                onLongListItemClickAction(pos);
                return true;
            }

        });


    }



    //---------- FUNCTION ON LONG LIST ITEM CLICK----------------------------------------------------
    private void onLongListItemClickAction(int pos) {
        longItemId = pos;
        dialogBuild();
        this.fillData();

    }


    //---------------onListItemClick----------------------------------------------------------------
    protected void onListItemClick(int position) {
        // lv.onListItemClick(l, v, position, id);
        parentId = lista.get(position).getId();

        if (lista.get(position).getValue().length() > 0 || lista.get(position).getBudget().length() > 0)//||(lista.get(position).getValue()!="Null")
        {
            Intent result = new Intent();

            result.putExtra("MPK_BUDGET_RESULT",lista.get(position));

            setResult(RESULT_OK, result);
            db.close();
            this.finish();

        }

        this.fillData();

    }


    //---------------onStop--------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) {
            db.close();
        }
    }

    //---------------onCreateOptionsMenu----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mpk_list, menu);

        return true;
    }

    //---------------onOptionsItemSelected----------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add:

                addMpkItem();

                //this.onCreate(savedInstanceState);

                adapter.notifyDataSetChanged();
                //this.fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//---------------addMpkItem----------------------------------------------------------
    /**
     * add the MPK Item to DB
     */
    private void addMpkItem() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_add_mpk, null);

        //  that's add default budget to AutoCompleteTextView.
        final EditText mpkValue = (EditText) textEntryView.findViewById(R.id.dialog_add_mpk_et_mpk);
        final AutoCompleteTextView mpkBudget = (AutoCompleteTextView) textEntryView.findViewById(R.id.dialog_add_mpk_et_budget);


        mpkValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mpkBudget.setText(defaultBudget);

            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogMpkTitle);
        alert.setMessage(R.string.dialogMpkMessage);
        alert.setIcon(R.drawable.ic_menu_add_mpk);
        alert.setView(textEntryView);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                EditText mpkName;


                mpkName = (EditText) textEntryView
                        .findViewById(R.id.dialog_add_mpk_et_name);


                int viewId = db.getLastId() + 1;
                db.addFactory(new PlantStrucMpk(parentId, viewId, mpkName.getText()
                        .toString(), mpkValue.getText().toString(), mpkBudget.getText().toString()));
                fillData();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
        );
        alert.show();

    }
//--------------dialog build-------------------------------------------------------------------
    private void dialogBuild() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        String[] aItems = new String[]{"Do góry", "Usuń"};
        ab.setItems(aItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        moveUp(longItemId);

                        break;
                    case 1:
                        deleteMpkItem();

                        break;
                    default:
                        break;
                }

            }

        });
        ab.show();
    }

    //----------------move item up-------------------------------------------------------------------
    private void moveUp(int pos) {

        if (pos > 0) {
            db.replaceViewId(lista.get(pos).getId(), lista.get(pos).getView_id(), lista.get(pos - 1).getId(), lista.get(pos - 1).getView_id());
           /* PlantStrucMpk pMPK=lista.get(pos);
            lista.set(pos,lista.get(pos-1));
            lista.set(pos-1,pMPK);*/
            PlantStrucMpk posName = lista.get(pos);
            lista.set(pos, lista.get(pos - 1));
            lista.set(pos - 1, posName);
            adapter.notifyDataSetChanged();
        }
    }

    //------------------function delete------------------------------------------------------------
    /**
     *
     */
    private void deleteMpkItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MpkListActivity.this);
        builder.setMessage("Czy usunąć pozycję: " + lista.get(longItemId).getName() + "?")
                .setCancelable(false)
                .setPositiveButton("Tak",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                db.deleteFactory(lista.get(longItemId).getId());
                                lista.remove(longItemId);
                                adapter.notifyDataSetChanged();

                            }
                        }
                )
                .setNegativeButton("Nie",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        builder.show();
        Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibra.vibrate(60);


    }


}

