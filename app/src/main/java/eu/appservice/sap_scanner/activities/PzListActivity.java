package eu.appservice.sap_scanner.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.dialogs.WarningDialogFragment;
import eu.appservice.sap_scanner.adapters.StoredMaterialsArrayAdapter;
import eu.appservice.sap_scanner.databases.PzMaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.StoredMaterial;


public class PzListActivity extends AppCompatActivity implements WarningDialogFragment.Communicator {

    private List<StoredMaterial> pzMaterials;
    private int longClickedPosition;
    private StoredMaterialsArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pz_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView lvPzListActivity = (ListView) findViewById(R.id.lvPzListActivity);


        pzMaterials =getStoredMaterials();


        Collections.reverse(pzMaterials);
        adapter=new StoredMaterialsArrayAdapter(this, R.layout.row_list_collected,pzMaterials);


        lvPzListActivity.setAdapter(adapter);
        lvPzListActivity.setFastScrollEnabled(true);
        lvPzListActivity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
        WarningDialogFragment dialog=WarningDialogFragment.getInstance("Czy usunąć pozycję "+(pzMaterials.size()-longClickedPosition)+"?");
        dialog.show(getSupportFragmentManager(),"DIALOG_WARNING");
        //if on Dialog clicked ok will execute method okClicked

    }

//--------------------------------------------------------------------------------------------------
    @Override
    public void okClicked() {

        //-------------delete from database--------------
        PzMaterialsDbOpenHelper pzDb = new PzMaterialsDbOpenHelper(getApplicationContext());
        pzDb.removeStoredMaterialById(pzMaterials.get(longClickedPosition).getMaterial().getId());
        pzDb.close();
        //-------------delete from list------------------
        pzMaterials.remove(longClickedPosition);
        adapter.notifyDataSetChanged();//refresh list
    }


//--------------------------------------------------------------------------------------------------
    private List<StoredMaterial> getStoredMaterials() {
        PzMaterialsDbOpenHelper pzDb=new PzMaterialsDbOpenHelper(getApplicationContext());
        return pzDb.getAllStoredMaterials();
    }



//--------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pz_list, menu);
        return true;
    }


//--------------------------------------------------------------------------------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;}

        return super.onOptionsItemSelected(item);
    }



}
