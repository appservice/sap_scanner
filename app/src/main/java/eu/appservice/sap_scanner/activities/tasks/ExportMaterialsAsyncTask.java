package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Lukasz on 11.10.13.
 * ﹕ SAP Skanner
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
@Deprecated
public class ExportMaterialsAsyncTask extends AsyncTask<Void, Integer, Void> {



        protected ProgressDialog progressDialog;
        protected String fileName;
        private MaterialsDbOpenHelper materialsDb;
    private Context myContext;
    public ExportMaterialsAsyncTask(Context context){
        this.myContext=context;

    }

        @Override
        protected void onPreExecute() {

            materialsDb = new MaterialsDbOpenHelper(myContext);


            progressDialog = new ProgressDialog(myContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMessage("Eksportuję...");
            progressDialog.setMax(1);
            progressDialog.show();
            fileName = "stan_z_komorki_" + nowDate() + ".xls";


            // progressBar.setProgress(0);
        }

        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Void doInBackground(Void... params) {
            try {

                WritableWorkbook workbook = Workbook.createWorkbook(new File(Environment
                        .getExternalStorageDirectory().getPath()
                        + "/" + fileName));
                WritableSheet sheet = workbook.createSheet("baza_z_komórki", 0);

                //SQLiteDatabase sqlDb = materialsDb.getReadableDatabase();
                List<Material> materiaList = materialsDb.getAllMaterials();
                progressDialog.setMax(materiaList.size());

                //---------cell format--------------------
                WritableFont airal10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
                WritableCellFormat arial10format = new WritableCellFormat(airal10font);
                arial10format.setBackground(jxl.format.Colour.GRAY_25);
                arial10format.setAlignment(jxl.format.Alignment.CENTRE);

                sheet.addCell(new Label(0, 0, "Index", arial10format));
                sheet.addCell(new Label(1, 0, "Nazwa", arial10format));
                sheet.addCell(new Label(2, 0, "Jednostka", arial10format));
                sheet.addCell(new Label(3, 0, "Ilość", arial10format));
                sheet.addCell(new Label(4, 0, "Skład", arial10format));
                //sheet.addCell(new Label(4, 0, "",arial10format));

                int excelRow = 1;
                for (Material m : materiaList) {
                    sheet.addCell(new Label(0, excelRow, m.getIndex()));
                    sheet.addCell(new Label(1, excelRow, m.getName()));
                    sheet.addCell(new Label(2, excelRow, m.getUnit()));
                    sheet.addCell(new jxl.write.Number(3, excelRow, m.getAmount()));
                    sheet.addCell(new Label(4, excelRow, m.getStore()));
                    //  sheet.addCell(new Label(4, excelRow, m.getDescription()));

                    publishProgress(excelRow);
                    excelRow++;

                }

                workbook.write();
                workbook.close();
                materialsDb.close();


            } catch (IOException e) {

                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {

            progressDialog.dismiss();

            Toast.makeText(myContext, "Plik " + fileName + " wyeksportowany.",
                    Toast.LENGTH_LONG).show();

        }

        private String nowDate() {
            java.util.Calendar now = java.util.GregorianCalendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd.MM.yy'_godz_'HH.mm.ss");
            return dateFormat.format(now.getTime());
        }
    }

