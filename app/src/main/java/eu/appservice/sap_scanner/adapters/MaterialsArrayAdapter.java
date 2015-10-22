package eu.appservice.sap_scanner.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;

/**
 * Created by Lukasz on 30.09.13.
 *﹕ ${PROJECT_NAME}
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class MaterialsArrayAdapter extends ArrayAdapter<Material> {

    private final Activity context;
    private List<Material> materialList;

    public MaterialsArrayAdapter(Activity context, int resource, List<Material> lm) {
        super(context, resource, lm);
        this.context = context;
        this.materialList = lm;
    }

    static class ViewHolder {
        public TextView tvPos;
        public TextView tvIndex;
        public TextView tvName;
        public TextView tvAmountUnit;
        public TextView tvStock;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater li = context.getLayoutInflater();
            rowView = li.inflate(R.layout.row_list_materials, null, true);
            holder = new ViewHolder();
            holder.tvPos = (TextView) rowView.findViewById(R.id.row_list_materials_tv_nr);
            holder.tvIndex = (TextView) rowView.findViewById(R.id.row_list_materials_tv_index);
            holder.tvName = (TextView) rowView.findViewById(R.id.row_list_materials_tv_name);
            holder.tvStock = (TextView) rowView.findViewById(R.id.row_list_materials_tv_stock);
            holder.tvAmountUnit = (TextView) rowView.findViewById(R.id.row_list_materials_tv_unit);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();

        }
        holder.tvPos.setText(String.valueOf(position + 1));
        holder.tvIndex.setText((materialList.get(position)).getSplittedIndexByCpglRegule());
        holder.tvName.setText(materialList.get(position).getName());
        holder.tvStock.setText("skład: " + materialList.get(position).getStore());
        holder.tvAmountUnit.setText("ilość: " + Utils.parse(materialList.get(position).getAmount()) + " " + materialList.get(position).getUnit());

        return rowView;
    }
}
