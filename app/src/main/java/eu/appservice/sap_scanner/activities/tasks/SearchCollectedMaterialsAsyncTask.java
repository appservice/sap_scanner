package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.adapters.CollectedMaterialsArrayAdapter;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;

/**
 * Package eu.appservice.module1.app.activities.tasks
 * Created by luke on 09.08.14.
 */
public class SearchCollectedMaterialsAsyncTask extends AsyncTask<String,Void,List<CollectedMaterial>>{
   // private Context myContext;
    private Activity myActivity;
    private CollectedMaterialDbOpenHelper materialsDb;
    protected ProgressDialog progressDialog;

    public SearchCollectedMaterialsAsyncTask(Activity activity){
        this.myActivity=activity;
    }


    /*
            * @see android.os.AsyncTask#onPreExecute()
            */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPreExecute() {
        materialsDb = new CollectedMaterialDbOpenHelper(myActivity);
       // btnSearchPickedActiv.setClickable(false);
        progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("Ładuję...");
        progressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected List<CollectedMaterial> doInBackground(String... params) {
        String index=params[0];
        String name=params[1];
        String mpk=params[2];
        String budget=params[3];

      List  searchedMaterials =  materialsDb.getPickedMaterialsByParameters(index, name, mpk, budget);

        return searchedMaterials;


    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(List result) {

        progressDialog.dismiss();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(myActivity);
        dialogBuilder.setTitle("Odnaleziono " + result.size() + " :");

        ListView modeList = new ListView(myActivity);
        modeList.setFastScrollEnabled(true);
        Collections.reverse(result); // reverse result (will be showed from latest to first)
        CollectedMaterialsArrayAdapter arrayCollectedMaterialsAdapter = new CollectedMaterialsArrayAdapter(myActivity, R.layout.row_list_collected, result);

        modeList.setAdapter(arrayCollectedMaterialsAdapter);

        dialogBuilder.setView(modeList);
        Dialog dialog = dialogBuilder.create();
        dialog.show();

        //btnSearchPickedActiv.setClickable(true);
        super.onPostExecute(result);

    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onCancelled()
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCancelled() {

        progressDialog.dismiss();

        super.onCancelled();
       // btnSearchPickedActiv.setClickable(true);


    }

}
