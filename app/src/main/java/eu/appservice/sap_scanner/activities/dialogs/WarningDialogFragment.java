package eu.appservice.sap_scanner.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Lukasz on 2014-04-12.
 * ﹕ SAP Skanner
 */
public class WarningDialogFragment extends DialogFragment{
    private String message;
    private Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try{
        communicator=(Communicator)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement interface WarningDialogFragment.Communicator");
        }
    }

    public WarningDialogFragment(String message){
        this.message=message;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        builder.setTitle("Uwaga");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        communicator.okClicked();

                    }
                }
        );
        builder.setNegativeButton("Anuluj",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

       return builder.create();

    }

    public interface Communicator{
        public void okClicked();
    }
}
