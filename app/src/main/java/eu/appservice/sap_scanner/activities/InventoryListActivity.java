package eu.appservice.sap_scanner.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.dialogs.WarningDialogFragment;
import eu.appservice.sap_scanner.adapters.InventoredMaterialArrayAdapter;
import eu.appservice.sap_scanner.adapters.StoredMaterialsArrayAdapter;
import eu.appservice.sap_scanner.databases.InventoryMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.PzMaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.StoredMaterial;
import eu.appservice.sap_scanner.model.InventoredMaterial;


public class InventoryListActivity extends ActionBarActivity implements WarningDialogFragment.Communicator {

    private List<InventoredMaterial> inventoredMaterials;
    private int longClickedPosition;
    private InventoredMaterialArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lvInventoryActivity = (ListView) findViewById(R.id.lv_inventory_activity);

        inventoredMaterials =getInventoredMaterials();


        Collections.reverse(inventoredMaterials);
        adapter=new InventoredMaterialArrayAdapter(this, R.layout.row_list_collected,inventoredMaterials);
        lvInventoryActivity.setAdapter(adapter);
        lvInventoryActivity.setFastScrollEnabled(true);
        lvInventoryActivity .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                longClickedPosition = pos;
                deleteStoredMaterial();

                return true;
            }
        });

    }


    //--------------------------------delate from list and db-------------------------------------------
    private void deleteStoredMaterial() {
        //-------------dialogFragment warning ------------------
        WarningDialogFragment dialog=new WarningDialogFragment("Czy usunąć pozycję "+(inventoredMaterials.size()-longClickedPosition)+"?");
        dialog.show(getSupportFragmentManager(),"DIALOG_WARNING");
        //if on Dialog clicked ok will execute method okClicked

    }

    //--------------------------------------------------------------------------------------------------
    @Override
    public void okClicked() {

        //-------------delete from database--------------
        InventoryMaterialDbOpenHelper invDb = new InventoryMaterialDbOpenHelper(getApplicationContext());
        invDb.removeStoredMaterialById(inventoredMaterials.get(longClickedPosition).getMaterial().getId());
        invDb.close();
        //-------------delete from list------------------
        inventoredMaterials.remove(longClickedPosition);
        adapter.notifyDataSetChanged();//refresh list
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //--------------------------------------------------------------------------------------------------
    private List<InventoredMaterial> getInventoredMaterials() {
        InventoryMaterialDbOpenHelper inDb=new InventoryMaterialDbOpenHelper(getApplicationContext());
        return inDb.getAllInventoredMaterials();
    }

}
