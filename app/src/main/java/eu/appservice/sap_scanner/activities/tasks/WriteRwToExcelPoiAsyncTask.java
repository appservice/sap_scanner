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
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.sap_scanner.databases.MaterialsDbOpenHelper;


/**
 * Created by Lukasz on 15.10.13.
 * ﹕ SAP Skanner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
@Deprecated
public class WriteRwToExcelPoiAsyncTask extends AsyncTask<Void, Integer, Void> {


    private Context myContext;
    private CollectedMaterialDbOpenHelper materialsDb;
    protected ProgressDialog progressDialog;
    protected String fileName = null;
    private List<File> filesForDelete;

    public WriteRwToExcelPoiAsyncTask(Context context) {
        this.myContext = context;

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
        filesForDelete = new ArrayList<>();

    }

    @Override
    protected Void doInBackground(Void... params) {
        if (isExternalStorageWritable()) {

            try {

                this.fileName = "rw_" + nowDate() + ".xls";

                HSSFWorkbook workbook = new HSSFWorkbook(); //.createWorkbook(new File(Environment.getExternalStorageDirectory().getPath()+ "/" + fileName))


                HSSFSheet sheet = workbook.createSheet("Wszystkie MPK");


                HSSFRow header = sheet.createRow(0);
                //header.set
                HSSFFont font = workbook.createFont();
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

                //---------cell format--------------------
                HSSFCellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(font);
                headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                headerStyle.setWrapText(true);
                headerStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
                headerStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
                headerStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
                headerStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
                headerStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);


                // header.setRowStyle(headerStyle);


                Cell c0 =header.createCell(0);
                c0.setCellValue("Index");
                c0.setCellStyle(headerStyle);

                Cell c1= header.createCell(1);
                c1.setCellValue("Ilość");
                c1.setCellStyle(headerStyle);

                Cell c2=header.createCell(2);
                c2.setCellValue("Jednostka");

                c2.setCellStyle(headerStyle);

                Cell c3=header.createCell(3);
                c3.setCellValue("Skład");
                c3.setCellStyle(headerStyle);

                Cell c4=header.createCell(4);
                c4.setCellValue("Mpk");
                c4.setCellStyle(headerStyle);

                Cell c5=header.createCell(5);
                c5.setCellValue("Budżet");
                c5.setCellStyle(headerStyle);

                Cell c6=header.createCell(6);
                c6.setCellValue("Czy na zero:");
                c6.setCellStyle(headerStyle);

                Cell c7=header.createCell(7);
                c7.setCellValue("Nazwa:");
                c7.setCellStyle(headerStyle);

                Cell c8=header.createCell(8);
                c8.setCellValue("Data:");
                c8.setCellStyle(headerStyle);

                Cell c9=header.createCell(9);
                c9.setCellValue("Pozostało:");
                c9.setCellStyle(headerStyle);

                Cell c10=header.createCell(10);
                c10.setCellValue("Adres podpisu");
                c10.setCellStyle(headerStyle);

                Cell c11=header.createCell(11);
                c11.setCellValue("w dniu rw pozostalo:");
                c11.setCellStyle(headerStyle);

/*
                //---------cell format--------------------
                WritableFont airal10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
                WritableCellFormat arial10format = new WritableCellFormat(airal10font);
                arial10format.setBackground(Colour.GRAY_25);
                arial10format.setAlignment(Alignment.CENTRE);
                arial10format.setBorder(Border.ALL, BorderLineStyle.MEDIUM);

                //arial10format.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
                arial10format.setLocked(true);
*/

/*
                //-----------write first row --------------------
                sheet.addCell(new Label(0, 0, "Index:", arial10format));
                sheet.addCell(new Label(1, 0, "Ilość \n pobrana:", arial10format));
                sheet.addCell(new Label(2, 0, "Jednostka:", arial10format));
                sheet.addCell(new Label(3, 0, "Skład:", arial10format));
                sheet.addCell(new Label(4, 0, "MPK:", arial10format));
                sheet.addCell(new Label(5, 0, "Budżet:", arial10format));
                sheet.addCell(new Label(6, 0, "Czy na zero:", arial10format));
                sheet.addCell(new Label(7, 0, "Nazwa:", arial10format));
                sheet.addCell(new Label(8, 0, "Data:", arial10format));
                sheet.addCell(new Label(9, 0, "Pozostało:", arial10format));
                sheet.addCell(new Label(10, 0, "Podpis:", arial10format));
                sheet.addCell(new Label(11, 0, "Adres podpisu:", arial10format));
                sheet.addCell(new Label(12, 0, "w dniu rw pozostalo:", arial10format));
                sheet.setRowView(0, 30 * 20);

                sheet.addColumnPageBreak(1);*/
                //-----write data to excel------------------------

                ArrayList<CollectedMaterial> collectedMaterialList;
                collectedMaterialList = (ArrayList<CollectedMaterial>) materialsDb.getAllCollectedMaterials();

                //-----------map (index,sum of amount on all stores)--------
                Map<String, Double> amountOnStoremap = addAmountFromStore(collectedMaterialList);


                progressDialog.setMax(collectedMaterialList.size());

                int rowNumber = 1;

         /*       WritableCellFormat wf2 = new WritableCellFormat();
                wf2.setBorder(Border.ALL, BorderLineStyle.THIN);

                for (CollectedMaterial pm : collectedMaterialList) {
                    String czyNaZero;
                    if (pm.isToZero())
                        czyNaZero = "Na Zero";
                    else
                        czyNaZero = "";

                    sheet.addCell(new Label(0, rowNumber, pm.getIndex(), wf2));
                    sheet.addCell(new Label(7, rowNumber, pm.getName(), wf2));
                    sheet.addCell(new jxl.write.Number(1, rowNumber, pm.getCollectedQuantity(), wf2));
                    sheet.addCell(new Label(2, rowNumber, pm.getUnit(), wf2));
                    sheet.addCell(new Label(3, rowNumber, pm.getStore(), wf2));
                    sheet.addCell(new Label(4, rowNumber, pm.getMpk(), wf2));
                    sheet.addCell(new Label(5, rowNumber, pm.getBudget(), wf2));
                    sheet.addCell(new Label(6, rowNumber, czyNaZero, wf2));
                    sheet.addCell(new Label(8, rowNumber, pm.getDate(), wf2));
                    sheet.addCell(new Number(9, rowNumber, pm.getAmount(), wf2));
                    sheet.addCell(new Label(11, rowNumber, pm.getSignAddress(), wf2));
                    sheet.addCell(new Number(12, rowNumber, amountOnStoremap.get(pm.getIndex()), wf2));
                    if (pm.getSignAddress().length() > 0) {
                        java.io.File imageFile = new java.io.File(Environment
                                .getExternalStorageDirectory().getPath()
                                + "/Podpisy/" + pm.getSignAddress());
                        //  sheet.addCell(new Cell(12,rowNumber),wf2);
                        WritableImage wi = new WritableImage(11, rowNumber, //column, row
                                1, 1, //width, height (according cels)
                                imageFile);
                        wi.setImageAnchor(WritableImage.MOVE_WITH_CELLS);
                        sheet.addImage(wi);


                        filesForDelete.add(imageFile);


                        //imageFile.delete();
                    }
                    sheet.setRowView(rowNumber, 22 * 20);//heiht in points 20*20
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

                try {

                    workbook.write();
                    workbook.close();
                    for (File f : filesForDelete) {
                        if (!f.delete())                //delete image file with sing which will be write in excel
                            Log.i("file", f.getName() + ": can't delete this file");
                    }
                } catch (WriteException e) {
                    e.printStackTrace();

                }*/


/*                HSSFRow row = workbook.getSheetAt(0).getRow(0);
                for(int colNum = 0; colNum<row.getLastCellNum();colNum++)
                    workbook.getSheetAt(0).autoSizeColumn(colNum);*/
for(int column=0;column<12;column++)
            sheet.autoSizeColumn(1);

                FileOutputStream fos = new FileOutputStream(new File(Environment
                        .getExternalStorageDirectory().getPath()
                        + "/" + fileName));

                workbook.write(fos);
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }/* catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }*/
        }
        return null;
    }

    private Map<String, Double> addAmountFromStore(List<CollectedMaterial> collectedMaterialList) {
        Map<String, Double> map = new HashMap();
        MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(this.myContext);
        for (CollectedMaterial material : collectedMaterialList) {
            List<Material> listmat = db.getMaterialByIndex(material.getIndex());
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

        Toast.makeText(myContext, "Plik " + fileName + " został utworzony.",
                Toast.LENGTH_LONG).show();
        if (fileName != null) {
            materialsDb.dropTable();
            materialsDb.close();
        }

    }

    private String nowDate() {
        java.util.Calendar now = java.util.GregorianCalendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd.MM.yy'_godz_'HH.mm.ss");
        return dateFormat.format(now.getTime());
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}


