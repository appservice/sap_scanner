package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.HSSFUserException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.logfile.InterfaceObservable;
import eu.appservice.sap_scanner.logfile.InterfaceObserver;


/**
 * Created by Lukasz on 13.10.13.
 * ? SAP Skanner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ImportMaterialsPoiEventAsyncTask extends AsyncTask<Void, Integer, Integer> implements InterfaceObserver {


    protected ProgressDialog progressDialog;
    private String imported_excel_file_name;
    private String mainFolderName;
    private int numberOfRows = 0;
    private Context myContext;
    private String dialog2message = "";


    //private Activity activity;


    public ImportMaterialsPoiEventAsyncTask(Context context) {
        this.myContext = context;

    }

    //-----------------onPreExecute---------------------------------------------------------------------
    @Override
    protected void onPreExecute() {
        imported_excel_file_name = myContext.getString(R.string.imported_excel_file_name);
        mainFolderName = myContext.getString(R.string.main_folder);

        progressDialog = new ProgressDialog(myContext);
       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    //    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      //  progressDialog.setMax(13000);
        progressDialog.setMessage(myContext.getString(R.string.loading_text));
        progressDialog.show();



    }

    //-----------------doInBackground---------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected Integer doInBackground(Void... params) {

        File mainFolder = new File(Environment.getExternalStorageDirectory() + File.separator + mainFolderName);
        File file = new File(mainFolder, imported_excel_file_name);

        try {
            FileInputStream fis = new FileInputStream(file);
            POIFSFileSystem pois = new POIFSFileSystem(fis);
            InputStream dis = pois.createDocumentInputStream("Workbook");

            HSSFRequest request = new HSSFRequest();

            ImportMaterialsEventListener myListener = new ImportMaterialsEventListener(myContext);

            myListener.addObserver(this);//this observer show number of row which is add from excel file

            request.addListenerForAllRecords(myListener);

            HSSFEventFactory factory = new HSSFEventFactory();
            factory.abortableProcessEvents(request, dis);
           // publishProgress(numberOfRows);

            dis.close();
            fis.close();

            return 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            dialog2message = myContext.getString(R.string.warning_do_not_found_file, mainFolderName, imported_excel_file_name);//"W folderze "+mainFolderName+" nie znaleziono pliku: \n" + EXCEL_FILE_NAME;
            // return 1;
        } catch (IOException e) {
            e.printStackTrace();
            dialog2message = myContext.getString(R.string.warning_can_not_read_file, imported_excel_file_name);
            // return 1;
        } catch (HSSFUserException e) {
            e.printStackTrace();
            if (e.getMessage().length() > 0)
                dialog2message = e.getMessage();
            else

                dialog2message = myContext.getString(R.string.warning_check_data_in_file, imported_excel_file_name);
        }
        return 1;


    }

    //-----------------onProgressUpdate---------------------------------------------------------------

    @Override
    protected void onProgressUpdate(Integer... values) {
//Log.i("liczba", String.valueOf( values[0]));
      //  progressDialog.setProgress(values[0]);
        progressDialog.setMessage(myContext.getString(R.string.loading_text) + "   " + values[0]);
    }


    //-----------------onPostExecute---------------------------------------------------------------
    @Override
    protected void onPostExecute(Integer toDoTrace) {
        progressDialog.dismiss();
        switch (toDoTrace) {

            case 0:
                dialogCorrectUpdate();
                break;
            case 1:
                dialogIncorrectUpdate();
                break;

        }

    }


    //-----------------it show when process done correct--------------------------------------------
    private void dialogCorrectUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                myContext);
        builder.setTitle(myContext.getString(R.string.attention_message));
        builder.setIcon(R.drawable.ic_status_dialog_ok);
        builder.setMessage(myContext.getString(R.string.db_udated_message) + (numberOfRows) + "."

        );
        builder.setCancelable(false);

        builder.setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                }
        );


        builder.show();

    }

    //-----------------it show when process done incorrect------------------------------------------
    private void dialogIncorrectUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                myContext);
        builder.setTitle(myContext.getString(R.string.attention_message));
        builder.setIcon(R.drawable.ic_status_dialog_error);
        builder.setMessage(dialog2message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                }
        );


        builder.show();

    }


    //---------------functions of observer patterns-------------------------------------------------
    @Override
    public void update() {

    }

    @Override
    public void update(InterfaceObservable o, Object arg) {
        if (o instanceof ImportMaterialsEventListener) {
           publishProgress( numberOfRows++);


        }
    }


}

