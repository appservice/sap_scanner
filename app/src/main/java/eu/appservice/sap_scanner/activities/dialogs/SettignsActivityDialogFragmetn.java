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
   private Context myContext;

    public SettignsActivityDialogFragmetn() {
        //   this.myContext = myContext;
    }

   public static  SettignsActivityDialogFragmetn newInstance(Context context){
       SettignsActivityDialogFragmetn s= new SettignsActivityDialogFragmetn();
       s.setContext(context);
       return s;
   }
    private void setContext(Context context){
        this.myContext=context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //  return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setMessage("Nie można wyświetlić");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

        }
        }
        );

        return builder.create();
    }


}
