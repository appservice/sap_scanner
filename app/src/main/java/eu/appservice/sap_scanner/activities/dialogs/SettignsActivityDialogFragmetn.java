package eu.appservice.sap_scanner.activities.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Lukasz on 10.10.13.
 * ﹕ SAP Skanner
 */
public class SettignsActivityDialogFragmetn extends DialogFragment {
    Context myContext;

    public SettignsActivityDialogFragmetn(Context myContext) {
        this.myContext = myContext;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //  return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage("Nie można wyświetlić");
        builder.setPositiveButton(1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

        }
        }
        );

        return builder.create();
    }


}
