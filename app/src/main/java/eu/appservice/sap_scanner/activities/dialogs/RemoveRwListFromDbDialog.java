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
public class RemoveRwListFromDbDialog extends DialogFragment {
    private Communicator communicator;




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator=(Communicator) activity;

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Plik wyeksportowany.\nCzy usunąć listę RW?");
        builder.setIcon(R.drawable.ic_action_help);
        builder.setTitle("Lista Rw");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        communicator.removeRwListFromDb();

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
        Dialog dialog= builder.create();

        return dialog;
    }

    public interface Communicator{
        void removeRwListFromDb();
    }
}
