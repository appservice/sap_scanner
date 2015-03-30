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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.Utils;

import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;
import eu.appservice.sap_scanner.logfile.InterfaceObservable;
import eu.appservice.sap_scanner.logfile.InterfaceObserver;
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
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Lukasz on 15.10.13.
 * ﹕ SAP Scanner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class WriteRwToExcelAsyncTask extends AsyncTask<Void, Integer, Void> implements InterfaceObservable {


    private Activity myContext;
    private CollectedMaterialDbOpenHelper materialsDb;
    protected ProgressDialog progressDialog;
    // protected String fileName = null;
    private File excelFile;
   // private List<File> filesForDelete;
    private List<InterfaceObserver> observersList;

    public WriteRwToExcelAsyncTask(Activity context) {
        this.myContext = context;
        this.observersList = new ArrayList<>();

    }

    @Override
    protected void onPreExecute() {

        materialsDb = new CollectedMaterialDbOpenHelper(myContext);

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
                sheet.addCell(new Label(1, 0, "Ilość pobrana:", arial10format));
                sheet.addCell(new Label(2, 0, "Jednostka:", arial10format));
                sheet.addCell(new Label(3, 0, "Skład:", arial10format));
                sheet.addCell(new Label(4, 0, "MPK:", arial10format));
                sheet.addCell(new Label(5, 0, "Budżet:", arial10format));
                sheet.addCell(new Label(6, 0, "Czy na zero:", arial10format));
                sheet.addCell(new Label(7, 0, "Nazwa:", arial10format));
                sheet.addCell(new Label(8, 0, "Data:", arial10format));
                sheet.addCell(new Label(9, 0, "W dniu wydania pozostało:", arial10format));
                sheet.addCell(new Label(10, 0, "w dniu rw pozostalo:", arial10format));
                sheet.addCell(new Label(11, 0, "Plik podpisu:", arial10format));
                sheet.addCell(new Label(12, 0, "Podpis:", arial10format));

                sheet.setRowView(0, 30 * 20);

                sheet.addColumnPageBreak(1);
                //-----write data to excel------------------------

                ArrayList<CollectedMaterial> collectedMaterialList;
                collectedMaterialList = (ArrayList<CollectedMaterial>) materialsDb.getAllCollectedMaterials();

                //-----------map (index,sum of amount on all stores)--------
                Map<String, Double> amountOnStoreMap = addAmountFromStore(collectedMaterialList);


                progressDialog.setMax(collectedMaterialList.size());

                int rowNumber = 1;

                //----------cell formatting----------------------
                WritableCellFormat wf2 = new WritableCellFormat();
                wf2.setBorder(Border.ALL, BorderLineStyle.THIN);

               //-----------date format-------------------------
                jxl.write.DateFormat dateForm = new jxl.write.DateFormat("dd-MMMM-yyyy hh:mm:ss");
                WritableCellFormat wfDate = new WritableCellFormat(dateForm);
                wfDate.setBorder(Border.ALL, BorderLineStyle.THIN);

                for (CollectedMaterial cm : collectedMaterialList) {
                    String czyNaZero;
                    if (cm.isToZero())
                        czyNaZero = "Na Zero";
                    else
                        czyNaZero = "";

                    sheet.addCell(new Label(0, rowNumber, cm.getIndex(), wf2));
                    sheet.addCell(new jxl.write.Number(1, rowNumber, cm.getCollectedQuantity(), wf2));
                    sheet.addCell(new Label(2, rowNumber, cm.getUnit(), wf2));
                    sheet.addCell(new Label(3, rowNumber, cm.getStore(), wf2));
                    sheet.addCell(new Label(4, rowNumber, cm.getMpk(), wf2));
                    sheet.addCell(new Label(5, rowNumber, cm.getBudget(), wf2));
                    sheet.addCell(new Label(6, rowNumber, czyNaZero, wf2));
                    sheet.addCell(new Label(7, rowNumber, cm.getName(), wf2));
                    sheet.addCell(new DateTime(8, rowNumber, Utils.encodeDate(cm.getDate()), wfDate));
                    sheet.addCell(new Number(9, rowNumber, cm.getAmount(), wf2));
                    sheet.addCell(new Number(10, rowNumber, amountOnStoreMap.get(cm.getIndex()), wf2));
                    sheet.addCell(new Label(11, rowNumber, cm.getSignAddress(), wf2));

                    //--------------write signature picture in cell-----------------------
                    if (cm.getSignAddress().length() > 0) {
                        File imageFile = readImageFile(cm.getSignAddress());

                        if (imageFile!=null) {
                            WritableImage wi = new WritableImage(12, rowNumber, //column, row
                                    1, 1, //width, height (according cels)
                                    imageFile);
                            wi.setImageAnchor(WritableImage.MOVE_WITH_CELLS);
                            sheet.addImage(wi);
                        }

                    }

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
                  /*  for (File f : filesForDelete) {
                        if (!f.delete())                //delete image file with sing which will be write in excel
                            Log.i("file", f.getName() + ": can't delete this file");
                    }*/

            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     * @param collectedMaterialList List of collected materials
     * @return map index of material: sum of amount in all stores this material
     */
    private Map<String, Double> addAmountFromStore(List<CollectedMaterial> collectedMaterialList) {
        Map<String, Double> map = new HashMap<>();
        MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(this.myContext);
        for (CollectedMaterial material : collectedMaterialList) {
            List<Material> listmat = db.getMaterialByIndex(material.getIndex());

            //----we add amount of all materials------
            Double materialAmounOnStore = 0.0;
            for (Material mat : listmat) {
                materialAmounOnStore += mat.getAmount();
            }
            map.put(material.getIndex(), materialAmounOnStore);
        }
        db.close();
        return map;
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

            materialsDb.close();
            notifyObservers();// notify all observers that was create excel file with the picked materials.
        }

    }






    private String getFileName() {

        return "rw_"+Utils.reformatDate(Utils.nowDate(),"dd.MM.yy'_godz_'HH.mm.ss")+".xls";

    }


    @Override
    public void addObserver(InterfaceObserver iObserver) {
        observersList.add(iObserver);
    }

    @Override
    public void deleteObserver(InterfaceObserver iObserver) {
        observersList.remove(iObserver);

    }

    @Override
    public void notifyObservers() {
        for (InterfaceObserver o : observersList) {
            o.update();
        }

    }

    @Override
    public void notifyObservers(Object arg) {

    }

//--------------------------------------------------------------------------------------------------
    /**
     *
     * @param fileName name of file with signature (from database)
     * @return image file with signature
     */
    private File readImageFile(String fileName) {

     if (fileName.length() > 0 && fileName != null) {
            File imageFile = new File(Environment
                    .getExternalStorageDirectory()
                    +File.separator
                    +myContext.getString(R.string.main_folder)
                    +File.separator
                    +myContext.getString(R.string.signatures_folder)
                     + fileName);

            if (imageFile.exists()) {
                return imageFile;
            }


        }
            return null;

    }
}

