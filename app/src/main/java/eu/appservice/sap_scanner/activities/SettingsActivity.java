package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import eu.appservice.sap_scanner.PlantStrucMpk;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.tasks.ExportMaterialsPoiAsyncTask;
import eu.appservice.sap_scanner.activities.tasks.ImportMaterialsPoiEventAsyncTask;
import eu.appservice.sap_scanner.databases.CompanyStructDbOpenHelper;


//import eu.appservice.activities.tasks.ImportMaterialsAsyncTask;

@TargetApi(Build.VERSION_CODES.BASE)
public class SettingsActivity extends PreferenceActivity {
    private Preference prefRenewMatDat;
    private Preference prefExportMatDat;
    private Preference prefAboutProgram;
    private Preference prefExportMpk;
    private Preference prefImportMpk;

    /*
     * (non-Javadoc)
     *
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.my_preferences);
        prefRenewMatDat = findPreference("pref_renew_mat_dat");
        prefExportMatDat = findPreference("pref_export_mat_dat");
        prefAboutProgram = findPreference("pref_about_program");
        prefExportMpk = findPreference("pref_export_mpk");
        prefImportMpk = findPreference("pref_import_mpk");
        initPreferenceClick();
    }

    private void initPreferenceClick() {

        prefRenewMatDat.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                                         @Override
                                                         public boolean onPreferenceClick(Preference preference) {
                                                             renewMaterialsDatabase();
                                                             // new ImportMaterialsPoiEventAsyncTask(SettingsActivity.this).execute();
                                                             return true;
                                                         }
                                                     }

        );

        prefExportMatDat.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                exportMaterialsDatabase();
                return true;
            }
        });

        prefAboutProgram.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showInfoActivity();
                return true;
            }
        });
        prefExportMpk.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                exportMPKsDialog();
                return true;
            }
        });
        prefImportMpk.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importMPKsDialog();
                return true;
            }
        });

    }

    private void showInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }


    private void renewMaterialsDatabase() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

        builder.setMessage("Czy uaktualnić bazę materiałów?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new ImportMaterialsPoiEventAsyncTask(SettingsActivity.this).execute();
                        //new ImportMaterialsPoiAsyncTask(SettingsActivity.this).execute();
                        // new Import2Test(SettingsActivity.this).execute();
                        //new ImportMaterialsPoiEventAsyncTask(SettingsActivity.this).execute();
                    }
                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();


    }


    private void exportMaterialsDatabase() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SettingsActivity.this);

        builder.setMessage("Czy eksportować do pliku  bazę materiałów?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // new ExportMaterialsAsyncTask(SettingsActivity.this).execute();
                        new ExportMaterialsPoiAsyncTask(SettingsActivity.this).execute();


                    }
                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();


    }

    private void exportMPKsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SettingsActivity.this);

        builder.setMessage("Czy eksportować bazę MPK?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // new ExportMaterialsAsyncTask(SettingsActivity.this).execute();
                        //    new ExportMaterialsPoiAsyncTask(SettingsActivity.this).execute();
                        exportMPKs();


                    }


                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    private void importMPKsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SettingsActivity.this);

        builder.setMessage("Czy IMPORTOWAĆ bazę MPK?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // new ExportMaterialsAsyncTask(SettingsActivity.this).execute();
                        //    new ExportMaterialsPoiAsyncTask(SettingsActivity.this).execute();
                        importMPKs();


                    }


                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    private void exportMPKs() {
        CompanyStructDbOpenHelper cd = new CompanyStructDbOpenHelper(getApplicationContext());
        List<PlantStrucMpk> mpks = cd.getAllFactorys();

        try {
            File file =
            new File(Environment.getExternalStorageDirectory() + File.separator +
                    getApplicationContext().getString(R.string.main_folder) + File.separator +
                    "mpk_db.csv");
            FileOutputStream fis = new FileOutputStream(file);

            BufferedOutputStream bus = new BufferedOutputStream(fis);
            BufferedWriter pw = new BufferedWriter(new PrintWriter(bus));

            StringBuilder sb = new StringBuilder();
            sb.append("id;parent_id;view_id;name;value;budget\n");
            for (PlantStrucMpk p : mpks) {

                sb.append(p.getId()).append(";")
                        .append(p.getParent_id()).append(";")
                        .append(p.getView_id()).append(";")
                        .append(p.getName()).append(";")
                        .append(p.getValue()).append(";")
                        .append(p.getBudget()).append("\n");

            }
            pw.write(sb.toString());

            pw.flush();
            pw.close();
            bus.close();
            fis.close();
            cd.close();
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, null);
            Toast.makeText(getApplicationContext(), "Utworzono plik mpk_db.csv", Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void importMPKs() {
        CompanyStructDbOpenHelper cd = new CompanyStructDbOpenHelper(getApplicationContext());

        try {
            File file =
                    new File(Environment.getExternalStorageDirectory() + File.separator +
                            getApplicationContext().getString(R.string.main_folder) + File.separator +
                            "mpk_db.csv");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);

            BufferedReader br=new BufferedReader(isr);

            String line;
            cd.deleteAllFactorys();
            int i=0;
            while((line=br.readLine())!=null){
                i++;
                if(i==1)continue;

                String [] data=line.split(";",-1);
                Log.d("number of row",String.valueOf(i));
                Log.d("table of import size",String.valueOf(data.length));

                try {
                    PlantStrucMpk mpk = new PlantStrucMpk();
                    mpk.setId(Integer.parseInt(data[0]));
                    mpk.setParent_id(Integer.parseInt(data[1]));
                    mpk.setView_id(Integer.parseInt(data[2]));
                    mpk.setName(data[3]);
                    mpk.setValue(data[4]);
                    mpk.setBudget(data[5]);

                    cd.addFactoryWithId(mpk);
                }catch(NumberFormatException ex){
                    Toast.makeText(getApplicationContext(), "Zły format danych: "+ex.getMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }

            br.close();
            fis.close();
            cd.close();
            Toast.makeText(getApplicationContext(), "Baza MPK zaimportowana", Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }


}
