package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.activities.tasks.ExportMaterialsPoiAsyncTask;
import eu.appservice.sap_scanner.activities.tasks.ImportMaterialsPoiEventAsyncTask;


//import eu.appservice.activities.tasks.ImportMaterialsAsyncTask;

@TargetApi(Build.VERSION_CODES.BASE)
public class SettingsActivity extends PreferenceActivity {
    private Preference prefRenewMatDat;
    private Preference prefExportMatDat;
    private Preference prefAboutProgram;

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
        prefAboutProgram=findPreference("pref_about_program");
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

    }

    private void showInfoActivity() {
        Intent intent=new Intent(this,InfoActivity.class);
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



}
