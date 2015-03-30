package eu.appservice.sap_scanner.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import eu.appservice.sap_scanner.R;

/**
 * Created by Lukasz on 2014-04-03.
 * ﹕ SAP Skanner
 */
public class ExportDataToExcelDialog extends DialogFragment {

    private Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        communicator=(Communicator)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Czy eksportować pobrania do pliku excel?");
        builder.setIcon(R.drawable.ic_action_help);
        builder.setTitle("Zapisywanie do Excel");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        communicator.okClicked();

                           }
                }
        );

        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );


        return builder.create();

    }

    public interface Communicator{
        public void okClicked();
    }
}
