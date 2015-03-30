package eu.appservice.sap_scanner.activities.tasks;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.databases.CollectedMaterialDbOpenHelper;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Alignment;
import jxl.write.Border;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * Created by Lukasz on 03.12.13.
 * ﹕ SAP Skanner
 */

@TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class ExportInventoryAsyncTask extends AsyncTask<Void, Integer, Void> {


        private Context myContext;
        private CollectedMaterialDbOpenHelper materialsDb;
        protected ProgressDialog progressDialog;
        protected String fileName;
        private List<File> filesForDelete;
        public ExportInventoryAsyncTask(Context context){
            this.myContext=context;

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
            filesForDelete = new ArrayList<File>();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                this.fileName = "rw_" + nowDate() + ".xls";

                WritableWorkbook workbook = Workbook.createWorkbook(new File(Environment
                        .getExternalStorageDirectory().getPath()
                        + "/" + fileName));
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
                sheet.addCell(new Label(9, 0, "Pozostało:", arial10format));
                sheet.addCell(new Label(10, 0, "Podpis:", arial10format));
                sheet.addCell(new Label(11, 0, "Adres podpisu:", arial10format));

                sheet.setRowView(0, 30 * 20);

                sheet.addColumnPageBreak(1);
                //-----write data to excel------------------------

                ArrayList<CollectedMaterial> collectedMaterialList;
                collectedMaterialList = (ArrayList<CollectedMaterial>) materialsDb.getAllCollectedMaterials();
                progressDialog.setMax(collectedMaterialList.size());

                int rowNumber = 1;

                WritableCellFormat wf2 = new WritableCellFormat();
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
                    sheet.addCell(new jxl.write.Number(9, rowNumber, pm.getAmount(), wf2));
                    sheet.addCell(new Label(11, rowNumber, pm.getSignAddress(), wf2));
                    if (pm.getSignAddress().length() > 0) {
                        File imageFile = new File(Environment
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

                for (int columnNumber = 0; columnNumber < 11; columnNumber++) {
                    sheet.setColumnView(columnNumber, cv);
                }

                try {

                    workbook.write();
                    workbook.close();
                    for (File f : filesForDelete) {
                        if(!f.delete())                //delete image file with sing which will be write in excel
                            Log.i("file", f.getName() + ": can't delete this file");
                    }
                } catch (WriteException e) {
                    e.printStackTrace();

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
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

            Toast.makeText(myContext, "Plik " + fileName + " został utworzony.",
                    Toast.LENGTH_LONG).show();
            materialsDb.dropTable();
            materialsDb.close();

        }

        private String nowDate() {
            java.util.Calendar now = java.util.GregorianCalendar.getInstance();

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd.MM.yy'_godz_'HH.mm.ss");
            return dateFormat.format(now.getTime());
        }


    }




