package eu.appservice.sap_scanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.appservice.sap_scanner.PlantStrucMpk;
import eu.appservice.sap_scanner.R;

/**
 * Created by Lukasz on 2014-04-15.
 * ﹕ SAP Skanner
 */
public class MpkArrayAdapter extends ArrayAdapter<PlantStrucMpk> {

    Activity context;
    List<PlantStrucMpk> mpks;

    public MpkArrayAdapter(Activity context, int resource, List<PlantStrucMpk> mpks) {
        super(context, resource, mpks);
        this.context = context;
        this.mpks = mpks;
    }

    //------------------getView---------------------------------------------------------------------
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.row_list_mpk, null, true);
            holder = new ViewHolder();
            holder.tvName = (TextView) rowView.findViewById(R.id.row_list_mpk_tv_name);
            holder.tvMpk = (TextView) rowView.findViewById(R.id.row_list_mpk_tv_mpk);
            holder.tvBudget = (TextView) rowView.findViewById(R.id.row_list_mpk_tv_budget);
            holder.imgArrow=(ImageView)rowView.findViewById(R.id.row_list_mpk_iv_arrow);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        int pos=mpks.get(position).getView_id();
        String name = mpks.get(position).getName();
        String mpk = mpks.get(position).getValue();
        String budget = mpks.get(position).getBudget();
       // Log.w("name + budget + pos", name+" "+budget+" "+pos);


        //-------- set field name--------------------------------

        holder.tvName.setText(name);
        holder.tvBudget.setVisibility(View.VISIBLE);

        if(mpk.length()<1){
            holder.tvMpk.setVisibility(View.GONE);}
        else {
            holder.tvMpk.setText("MPK: "+mpk);
        }
        if(budget.length()<1) {
            holder.tvBudget.setVisibility(View.GONE);
            holder.imgArrow.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvBudget.setText("Zlecenie: "+budget);
            holder.imgArrow.setVisibility(View.INVISIBLE);
        }

     /*   //---------if is mpk  set to filed mpk------------------
        if ((!mpk.isEmpty()) && (mpk != null)) {
            holder.imgArrow.setVisibility(View.INVISIBLE);
            holder.tvMpk.setVisibility(View.VISIBLE);
            holder.tvMpk.setText(mpks.get(position).getValue());
        } else {
          //  holder.tvMpk.setText("");
          holder.tvMpk.setVisibility(View.GONE);

            holder.imgArrow.setVisibility(View.VISIBLE);

        }

        //---------if is budget  set to filed mpk------------------
        if ((budget != null)&&(budget.length()>0) ) {
            holder.imgArrow.setVisibility(View.INVISIBLE);
            holder.tvBudget.setVisibility(View.VISIBLE);
            holder.tvBudget.setText(mpks.get(position).getBudget());//TODO



        } else {
           // holder.tvBudget.setText("");
            holder.tvBudget.setVisibility(View.GONE);
            holder.imgArrow.setVisibility(View.VISIBLE);

        }
*/



        return rowView;
    }

    //--------------static class holder---------------------
    static class ViewHolder {
        TextView tvName, tvBudget, tvMpk;
        ImageView imgArrow;
    }
    //------------------------------------------------------
}
