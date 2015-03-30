package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;

import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;


/**
 * Created by Lukasz on 2014-04-12.
 * ﹕ SAP Scanner
 */
public class SearchMaterialsAsyncTask extends AsyncTask<Object, Void, List<Material>> {
    private Activity parentActivity;
    private MaterialsDbOpenHelper materialsDb;
    protected ProgressDialog progressDialog;
    private volatile boolean running = true;
    private GetAsyncTaskListener getAsyncTaskListener;


    public SearchMaterialsAsyncTask(Activity parentActivity) {
        this.parentActivity = parentActivity;

        //it's necessary to call method in parent activity which is implements interface GetAsyncTaskListener
        getAsyncTaskListener = (GetAsyncTaskListener) parentActivity;
    }

//---------------------onPreExecute-----------------------------------------------------------------
    /*
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected void onPreExecute() {
        materialsDb = new MaterialsDbOpenHelper(parentActivity);
        progressDialog = new ProgressDialog(parentActivity);
        progressDialog.setMessage(parentActivity.getString(R.string.loading_text));
        progressDialog.show();

        super.onPreExecute();
    }

//-------------------doInBackground-----------------------------------------------------------------
    @Override
    protected List<Material> doInBackground(Object... params) {

        String index = (String) params[0];
        String name = (String) params[1];
        String stock = (String) params[2];
        Boolean isChecked = (Boolean) params[3];

        return  materialsDb.getMaterialsByIndexAndName(index, name, stock, isChecked, running);

    }


//--------------------onPostExecute-----------------------------------------------------------------
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(List<Material> result) {

        progressDialog.dismiss();
        materialsDb.close();
        running = false;
        getAsyncTaskListener.onAsyncTaskFinished(result);


        super.onPostExecute(result);

    }

//---------------------onCancelled------------------------------------------------------------------
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onCancelled()
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCancelled() {

        progressDialog.dismiss();
        super.onCancelled();
        materialsDb.close();
        running = false;

    }

//-----------interface to connecting with Activity-------------------------------------------------
    public interface GetAsyncTaskListener {
        public void onAsyncTaskFinished(List<Material> materials);
    }
}
