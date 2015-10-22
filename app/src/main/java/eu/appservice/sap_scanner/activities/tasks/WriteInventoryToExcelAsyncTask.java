package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import eu.appservice.sap_scanner.R;

import eu.appservice.sap_scanner.Utils;

import eu.appservice.sap_scanner.databases.InventoryMaterialDbOpenHelper;
import eu.appservice.sap_scanner.model.InventoredMaterial;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Lukasz on 15.10.13.
 * ﹕ SAP Scanner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class WriteInventoryToExcelAsyncTask extends AsyncTask<Void, Integer, Void>  {


    private Activity myContext;
    private InventoryMaterialDbOpenHelper inventoryDb;
    protected ProgressDialog progressDialog;
    private File excelFile;

    public WriteInventoryToExcelAsyncTask(Activity context) {
        this.myContext = context;


    }

    @Override
    protected void onPreExecute() {
        inventoryDb = new InventoryMaterialDbOpenHelper(myContext);

        progressDialog = new ProgressDialog(myContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMessage("Eksportuję...");
        progressDialog.setMax(1);
        progressDialog.show();
        // filesForDelete = new ArrayList<File>();

    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utils.isExternalStorageWritable()) {

            try {

                // String fileName = "rw_" + nowDate() + ".xls";
                this.excelFile = new File(Environment.getExternalStorageDirectory()+File.separator+myContext.getString(R.string.main_folder)+File.separator+
                        getFileName());


                WritableWorkbook workbook = Workbook.createWorkbook(this.excelFile);
                WritableSheet sheet = workbook.createSheet("Wszystkie MPK", 0);

                //---------cell format--------------------
                WritableFont airal10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
                WritableCellFormat arial10format = new WritableCellFormat(airal10font);
                arial10format.setBackground(Colour.GRAY_25);
                arial10format.setAlignment(Alignment.CENTRE);
                arial10format.setBorder(Border.ALL, BorderLineStyle.MEDIUM);

                //arial10format.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
                arial10format.setLocked(true);


                //-----------write first row --------------------
                sheet.addCell(new Label(0, 0, "Index:", arial10format));
                sheet.addCell(new Label(1, 0, "Nazwa:", arial10format));

                sheet.addCell(new Label(2, 0, "Skład:", arial10format));
                sheet.addCell(new Label(3, 0, "Data:", arial10format));
                sheet.addCell(new Label(4, 0, "Miejsce:", arial10format));
                sheet.addCell(new Label(5, 0, "Brakuje:", arial10format));
                sheet.addCell(new Label(6, 0, "JM:", arial10format));

                sheet.setRowView(0, 30 * 20);

                sheet.addColumnPageBreak(1);
                //-----write data to excel------------------------

                ArrayList<InventoredMaterial> inventoredMaterialList;
                inventoredMaterialList = (ArrayList<InventoredMaterial>) inventoryDb.getAllInventoredMaterials();

                //-----------map (index,sum of amount on all stores)--------
               // Map<String, Double> amountOnStoreMap = addAmountFromStore(inventoredMaterialList);


                progressDialog.setMax(inventoredMaterialList.size());

                int rowNumber = 1;

                //----------cell formatting----------------------
                WritableCellFormat wf2 = new WritableCellFormat();
                wf2.setBorder(Border.ALL, BorderLineStyle.THIN);

                //-----------date format-------------------------
                jxl.write.DateFormat dateForm = new jxl.write.DateFormat("dd-MMMM-yyyy hh:mm:ss");
                WritableCellFormat wfDate = new WritableCellFormat(dateForm);
                wfDate.setBorder(Border.ALL, BorderLineStyle.THIN);

                for (InventoredMaterial im : inventoredMaterialList) {


                    sheet.addCell(new Label(0, rowNumber, im.getMaterial().getIndex(), wf2));
                 //   sheet.addCell(new jxl.write.Number(1, rowNumber, im.getCollectedQuantity(), wf2));
                    sheet.addCell(new Label(1, rowNumber, im.getMaterial().getName(), wf2));
                    sheet.addCell(new Label(2, rowNumber, im.getMaterial().getStore(), wf2));
                    sheet.addCell(new DateTime(3, rowNumber, Utils.encodeDate(im.getInventoredDate()), wfDate));
                    sheet.addCell(new Label(4, rowNumber, im.getMaterial().getDescription(), wf2));
                    sheet.addCell(new Number(5, rowNumber, im.getMaterial().getAmount(), wf2));
                    sheet.addCell(new Label(6, rowNumber, im.getMaterial().getUnit(), wf2));


                    //------set row height--------------------------
                    sheet.setRowView(rowNumber, 22 * 20);//heiht in points 20*20
                    //------set column height----------------------
                    sheet.setColumnView(11, 40 * 20);


                    publishProgress(rowNumber);
                    rowNumber++;
                }

                //----------add column autosize-----------------
                CellView cv = new CellView();
                cv.setAutosize(true);

                for (int columnNumber = 0; columnNumber < 13; columnNumber++) {
                    sheet.setColumnView(columnNumber, cv);
                }


                //---------write workbook------------------
                workbook.write();
                workbook.close();

            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }
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

        Toast.makeText(myContext, "Plik " + excelFile.getName() + " został utworzony.",
                Toast.LENGTH_LONG).show();
        if (excelFile.exists()) {

            inventoryDb.close();
        }

    }






    private String getFileName() {

        return "Inventaryzacja_"+Utils.reformatDate(Utils.nowDate(),"dd.MM.yy'_godz_'HH.mm.ss")+".xls";

    }



}

