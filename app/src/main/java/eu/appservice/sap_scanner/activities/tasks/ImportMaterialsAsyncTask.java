package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by Lukasz on 11.10.13.
 * ﹕ SAP Scanner
 * @deprecated
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class ImportMaterialsAsyncTask extends AsyncTask<Void, Integer, Integer> {

    private MaterialsDbOpenHelper materialsDb;
    private int myProgress;
    private Workbook workbook;
    private Sheet sheet;
    private int amountRows;
    protected ProgressDialog progressDialog;
    private Context myContext;

    public ImportMaterialsAsyncTask(Context context){
        this.myContext=context;
    }

    @Override
    protected void onPreExecute() {

        materialsDb = new MaterialsDbOpenHelper(myContext);

        myProgress = 0;
        progressDialog = new ProgressDialog(myContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);


        progressDialog.setMessage(myContext.getString(R.string.loading_text));
                progressDialog.setMax(1);
        progressDialog.show();
        materialsDb.dropTableCpglStore();

        // progressBar.setProgress(0);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected Integer doInBackground(Void... params) {

        try {

            File file=new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/zrzut_stanu.xls");
            //FileInputStream fis=new FileInputStream(file);
            workbook = Workbook.getWorkbook(file);// "/sdcard/zrzut_stanu.xls"
            this.sheet = workbook.getSheet(0);
            amountRows = sheet.getRows();

            progressDialog.setMax(amountRows);
            Material material = new Material();

            SQLiteDatabase sqlDb = materialsDb.getWritableDatabase();
           sqlDb.beginTransaction();
            try {
                for (int row = 1; row < amountRows; row++) {

                    myProgress = row;

                    Cell b2 = sheet.getCell(0, row);
                    Cell c2 = sheet.getCell(1, row);
                    Cell d2 = sheet.getCell(2, row);
                    Cell e2 = sheet.getCell(3, row);
                    Cell f2 = sheet.getCell(4, row);


                    material.setIndex(b2.getContents());
                    material.setName(c2.getContents().replace(";", ",").replace("�", "ó"));
                    material.setUnit(d2.getContents());
                    if (e2.getType() == CellType.NUMBER) {
                        NumberCell nc = (NumberCell) e2;
                        //String amountString=e2.getContents().replace(",",".").replace(" ", "");
                        // Log.i("cell amount",nc.getValue());
                        material.setAmount(nc.getValue());
                    } else {
                        // showAlert();
                        return 1;


                    }


                    material.setStore(f2.getContents());
                    //material.setDescription(g2.getContents());
                    Log.i("material", material.toString());

                    materialsDb.addMaterialsFromExcel(material, sqlDb);
                    publishProgress(myProgress);
                }
                sqlDb.setTransactionSuccessful();
            } finally {
                sqlDb.endTransaction();
                sqlDb.close();
                materialsDb.close();
            }

            workbook.close();

        } catch (BiffException e) {
            Log.w("BiffException", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        progressDialog.setProgress(values[0]);
    }

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


    private void dialogCorrectUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                myContext);

        builder.setMessage("Baza zaktualizowana poprawnie. \n"
        );
        builder.setCancelable(false);
        builder.setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();


                    }
                });


        builder.show();

    }

    private void dialogIncorrectUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                myContext);

        builder.setMessage("Plik Excel niepoprawny! \n" +
                "4 kolumna powinna zawierać ilości.\n" +
                "Sprawdź plik excel i aktualizuj bazę ponownie!");
        builder.setCancelable(false);
        builder.setPositiveButton("OK",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();


                    }
                });


        builder.show();

    }

}
