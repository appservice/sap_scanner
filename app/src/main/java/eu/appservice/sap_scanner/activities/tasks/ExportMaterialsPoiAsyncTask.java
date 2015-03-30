package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;


/**
 * Created by Lukasz on 11.10.13.
 * ﹕ SAP Skanner
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class ExportMaterialsPoiAsyncTask extends AsyncTask<Void, Integer, Void> {


    protected ProgressDialog progressDialog;
    protected String fileName;
    private MaterialsDbOpenHelper materialsDb;
    // private WritableSheet sheet;
    private Context myContext;
    String mainFolderName;

    public ExportMaterialsPoiAsyncTask(Context context) {
        this.myContext = context;

    }

    @Override
    protected void onPreExecute() {

        materialsDb = new MaterialsDbOpenHelper(myContext);

        mainFolderName= myContext.getString(R.string.main_folder);


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

 /*           workbook = Workbook.createWorkbook(new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/" + fileName));// "/sdcard/zrzut_stanu.xls"*/
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("baza_z_komórki");
            //this.sheet = workbook.createSheet("baza_z_komórki", 0);

            //SQLiteDatabase sqlDb = materialsDb.getReadableDatabase();
            List<Material> materiaList = materialsDb.getAllMaterials();
            progressDialog.setMax(materiaList.size());

            //---------cell format--------------------
/*            WritableFont airal10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
            WritableCellFormat arial10format = new WritableCellFormat(airal10font);
            arial10format.setBackground(jxl.format.Colour.GRAY_25);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);*/
            HSSFRow header = sheet.createRow(0);
            //header.set
            HSSFFont font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(font);
            headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            // header.setRowStyle(headerStyle);


            Cell c0 = header.createCell(0);
            c0.setCellValue("Index");
            c0.setCellStyle(headerStyle);

            Cell c1 = header.createCell(1);
            c1.setCellValue("Nazwa");
            c1.setCellStyle(headerStyle);

            Cell c2 = header.createCell(2);
            c2.setCellValue("Jednostka");
            c2.setCellStyle(headerStyle);

            Cell c3 = header.createCell(3);
            c3.setCellValue("Ilość");
            c3.setCellStyle(headerStyle);

            Cell c4 = header.createCell(4);
            c4.setCellValue("Skład");
            c4.setCellStyle(headerStyle);

            // header.get

          /*  for(int i=0;i<header.getLastCellNum();i++){

            }*/

            int excelRow = 1;
            for (Material m : materiaList) {
                HSSFRow row = sheet.createRow(excelRow);
                row.createCell(0).setCellValue(m.getIndex());
                row.createCell(1).setCellValue(m.getName());
                row.createCell(2).setCellValue(m.getUnit());
                row.createCell(3).setCellValue(m.getAmount());
                row.createCell(4).setCellValue(m.getStore());


                publishProgress(excelRow);
                excelRow++;

            }
            //sheet.autoSizeColumn(3);

            FileOutputStream fos = new FileOutputStream(new File(Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + mainFolderName
                    + File.separator
                    + fileName));

            workbook.write(fos);
            fos.close();
            materialsDb.close();


        } catch (IOException e) {

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

        Toast.makeText(myContext, "Plik " + fileName + " wyeksportowany do katalogu "+mainFolderName+".",
                Toast.LENGTH_LONG).show();

    }

    private String nowDate() {
        java.util.Calendar now = java.util.GregorianCalendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yy'_godz_'HH.mm.ss");
        return dateFormat.format(now.getTime());
    }
}

