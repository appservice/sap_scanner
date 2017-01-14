package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import eu.appservice.sap_scanner.BarcodeScanner;
import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.adapters.CollectedMaterialsArrayAdapter;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.MaterialToFileSaver;


/**
 * Created by Lukasz on 27.09.13.
 * ﹕ ${PROJECT_NAME}
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class CollectedMaterialsListActivity extends ActionBarActivity {


    //
    public static final int SIGNATURE_REQUEST = 0;
    // do testów
    final CharSequence[] alertMenu;
    //  private Set<Integer> checkedItems;
    private List<CollectedMaterial> list;
    private int signPos;
    private CollectedMaterialsArrayAdapter indexAdapter;


    public CollectedMaterialsListActivity() {
        alertMenu = new CharSequence[]{"Usuń", "Kopiuj indeks", "Do podpisu", "Twórz QRCode"}; //, "Zmień"
    }

    private void initialize() {

        CollectedMaterialDbOpenHelper mdb = new CollectedMaterialDbOpenHelper(this.getApplicationContext());
        list = mdb.getAllCollectedMaterials();
        Collections.reverse(list);

    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_collected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.activity_list_collected_lv);

        initialize();

        lv.setFastScrollEnabled(true);
        indexAdapter = new CollectedMaterialsArrayAdapter(CollectedMaterialsListActivity.this,
                R.layout.row_list_collected, list);
        lv.setAdapter(indexAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0,
                                           View arg1, int arg2, long arg3) {

                try {
                    onLongListItemClickAction(arg1, arg2, arg3);
                } catch (IOException e) {

                    e.printStackTrace();
                }

                return false;
            }

        });

    }

    //--------------------------------------------------------------------------------------------------
    private void onLongListItemClickAction(View arg1, int pos, long arg3)
            throws IOException {
        //	Context myContext=this.getApplicationContext();

        this.signPos = pos;
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Menu:");
        build.setItems(alertMenu, new DialogInterface.OnClickListener() {
            //  public int signPos;

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:

                        // boolean czyUsuwac = false;
                        AlertDialog.Builder builder = new AlertDialog.Builder(CollectedMaterialsListActivity.this);
                        final CollectedMaterial cmRemoved = list.get(signPos);
                        StringBuilder sbPmRemoved = new StringBuilder("Czy usunąć pozycję: ").append(list.size() - signPos).append("\n");
                        sbPmRemoved.append(cmRemoved.getIndex()).append("\n");
                        sbPmRemoved.append(cmRemoved.getName()).append("\n");
                        sbPmRemoved.append(cmRemoved.getCollectedQuantity()).append(" ");
                        sbPmRemoved.append(cmRemoved.getUnit()).append(" ");
                        sbPmRemoved.append(cmRemoved.getMpk()).append(" ");
                        sbPmRemoved.append(cmRemoved.getBudget()).append(" ?");

                        builder.setMessage(sbPmRemoved.toString())
                                .setCancelable(false).setPositiveButton("Tak",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        // czyUsuwac=true;
                                        //  removeFromLogFile1(signPos);
                                        removeFromLogFile(cmRemoved.getDate());
                                        updateDatabases();


                                    }
                                }
                        ).setNegativeButton("Nie",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );
                        AlertDialog alert = builder.create();
                        alert.setTitle("Usuwanie");
                        alert.show();


                        //    Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        //     vibra.vibrate(60);
                        break;

                    case 1:
                        Utils.copyTextToClipboard(getApplicationContext(),"index",list.get(signPos).getIndex());

                        break;
                    case 2:
                        onListItemSelected(signPos);

                        break;
                    case 3:
                        encodeMaterial();
                        break;

                    default:
                        break;


                }
            }
        });

        AlertDialog alertPos = build.create();
        alertPos.show();

    }

    //--------------------------------------------------------------------------------------------------
    private void encodeMaterial() {
        BarcodeScanner barcodeScanner = new BarcodeScanner(this);
        barcodeScanner.encodeData(list.get(signPos).getDataToEncodeQrCode());
    }


    //--------------------------------------------------------------------------------------------------
    private void removeFromLogFile(String removedMaterialDate) {
        MaterialToFileSaver mtf = new MaterialToFileSaver(getApplicationContext());
        if (mtf.removeFromLogFile(removedMaterialDate)) {
            Toast.makeText(getApplicationContext(), "Pozycja usunięta!", Toast.LENGTH_LONG).show();
        }

    }

    //--------------------------------------------------------------------------------------------------
    private void onListItemSelected(int signPos) {
        indexAdapter.toggleSelection(signPos);
    }

    private boolean updateDatabases() {
        CollectedMaterialDbOpenHelper dbm = new CollectedMaterialDbOpenHelper(getApplicationContext());

        dbm.removeCollectedMaterialById(list.get(signPos).getId()); //remove from database
        dbm.close();


        MaterialsDbOpenHelper dba = new MaterialsDbOpenHelper(
                getApplicationContext());
        Material materialInData = dba.getMaterialByIndexAndStore(list.get(signPos).getIndex(), list.get(signPos).getStore());
        dba.updateAmount(materialInData, list.get(signPos).getCollectedQuantity());
        indexAdapter.remove(list.get(signPos)); //remove from showed listView
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGNATURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    String signAddress = data.getStringExtra("SIGNATURE_RESULT");

                    CollectedMaterialDbOpenHelper pmd = new CollectedMaterialDbOpenHelper(getApplicationContext());
                    SparseBooleanArray selectedMaterials = indexAdapter.getSelectedItemIds();

                    for (int i = indexAdapter.getSelectedCount() - 1; i >= 0; i--) {

                        CollectedMaterial collectedMaterial = list.get(selectedMaterials.keyAt(i));
                        collectedMaterial.setSignAddress(signAddress);
                        pmd.updateCollectedMaterial(collectedMaterial);//>0){

                    }
                    pmd.close();
                    Toast.makeText(getApplicationContext(), signAddress, Toast.LENGTH_LONG).show();
                    this.indexAdapter.removeSelection();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.material_list_inflate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.schowPickedSearchActivity:
                Intent intent = new Intent(getApplicationContext(), SearchCollectedActivity.class);
                startActivity(intent);
                return true;
            case R.id.materialListInflateMenuSign:
                if (indexAdapter.getSelectedCount() > 0) {
                    Intent singIntent = new Intent(getApplicationContext(), FingerPaintActivity.class);
                    startActivityForResult(singIntent, SIGNATURE_REQUEST);
                } else {
                    Toast.makeText(getApplicationContext(), "Zaznacz pozycje do podpisu", Toast.LENGTH_LONG).show();
                }
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



}

