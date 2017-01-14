package eu.appservice.sap_scanner.logfile;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.activities.tasks.ImportMaterialsEventListener;


/**
 * Created by Lukasz on 23.02.14.
 * ﹕ SAP Skaner
 */
public class MaterialToFileSaver implements MaterialSaver, InterfaceObserver {
    private static final String LOG_TXT_FILE = "magazyn_log.txt";
 private   File file;
    private Context myContext;

    public MaterialToFileSaver(Context context) {
        this.myContext=context;
        try {
            this.file = new File(Environment.getExternalStorageDirectory(), LOG_TXT_FILE);
            {
                if (!this.file.exists()) {
                    if (!this.file.createNewFile())
                        Log.e("creating file" + file.getName(), "Can't create file");

                }


            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public int save(CollectedMaterial collectedMaterial) {

        //  if (isExternalStorageWritable()) {


        try {
            FileWriter fw = new FileWriter(this.file, true);
            BufferedWriter bf = new BufferedWriter(fw, 8 * 1024);
            bf.write(collectedMaterialTextFormat(collectedMaterial));
            bf.close();
            fw.close();
            MediaScannerConnection.scanFile(myContext, new String[] { file.getAbsolutePath() }, null, null);


            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Checks if external storage is available for read and write
     */
/*    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }*/
    private String collectedMaterialTextFormat(CollectedMaterial collectedMaterial) {

        String textWhenIsZero;
        if (collectedMaterial.isToZero()) {
            textWhenIsZero = "na zero";
        } else
            textWhenIsZero = "";

        StringBuilder sb = new StringBuilder();
        sb.append(collectedMaterial.getIndex()).append(";");
        sb.append(collectedMaterial.getCollectedQuantity().toString()).append(";");
        sb.append(collectedMaterial.getUnit()).append(";");
        sb.append(collectedMaterial.getStore()).append(";");
        sb.append(collectedMaterial.getMpk()).append(";");
        sb.append(collectedMaterial.getBudget()).append(";");
        sb.append(textWhenIsZero).append(";");
        sb.append(collectedMaterial.getName()).append(";");
        sb.append(collectedMaterial.getDate()).append(";");
        sb.append("\n");


        return sb.toString();
    }

    //---------------when excel file is created its add this information to log file-------------
    private void saveMarkerTxtFile(String textToLog) {

        try {

            //FileOutputStream fos = new FileOutputStream(file, true);
            FileWriter fw = new FileWriter(this.file, true);
            BufferedWriter bf = new BufferedWriter(fw, 8 * 1024);
            bf.write("\nW dniu " + saveDate() + " wykonano RW. \n\n");
            bf.close();
            fw.close();
            MediaScannerConnection.scanFile(myContext, new String[] { file.getAbsolutePath() }, null, null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update() {
        saveMarkerTxtFile("");
    }

    @Override
    public void update(InterfaceObservable o, Object arg) {
    }
/*    if (o instanceof ImportMaterialsEventListener) {
        publishProgress( numberOfRows++);


    }*/

    private String saveDate() {
        return Utils.nowDate();
    }

    public boolean removeFromLogFile(String date) {
        File inputFile = new File(Environment.getExternalStorageDirectory(), LOG_TXT_FILE);
        File tempFile = new File(Environment.getExternalStorageDirectory(), "tempFile.txt");
        if (!tempFile.exists())
            try {
                tempFile.createNewFile();

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                FileOutputStream fos = new FileOutputStream(tempFile, true);
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    if (!currentLine.contains(date))

                        osw.write(currentLine + "\n");
                }
                boolean successful = tempFile.renameTo(inputFile);

                osw.close();
                fos.close();
                reader.close();
                if (successful) {
                    MediaScannerConnection.scanFile(myContext, new String[] { file.getAbsolutePath() }, null, null);

                    return true;


                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        return false;

    }

}
