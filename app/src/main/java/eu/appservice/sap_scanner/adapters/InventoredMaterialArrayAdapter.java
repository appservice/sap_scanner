package eu.appservice.sap_scanner.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;
import eu.appservice.sap_scanner.logfile.StoredMaterial;
import eu.appservice.sap_scanner.model.InventoredMaterial;

/**
 * Created by luke on 30.03.15.
 */
public class InventoredMaterialArrayAdapter extends ArrayAdapter<InventoredMaterial> {
    private final Activity context;
    private List<InventoredMaterial> inventoredMaterials;
    private SparseBooleanArray mSelectedItemIds;

    public InventoredMaterialArrayAdapter(Activity context, int resource,
                                       List<InventoredMaterial> inventoredMaterials) {
        super(context, resource, inventoredMaterials);

        this.context = context;
        this.inventoredMaterials = inventoredMaterials;
        mSelectedItemIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.row_list_collected, null, true);
            holder = new ViewHolder();
            holder.tvIndex = (TextView) rowView.findViewById(R.id.row_list_collected_tv_index);
            holder.tvName = (TextView) rowView.findViewById(R.id.row_list_collected_tv_name);
            holder.tvDate = (TextView) rowView.findViewById(R.id.row_list_collected_tv_date);
            holder.tvPickedAmount = (TextView) rowView.findViewById(R.id.row_list_collected_tv_amount_unit_isZero);
            holder.tvPos = (TextView) rowView.findViewById(R.id.row_list_collected_nr);
            holder.tvMpk = (TextView) rowView.findViewById(R.id.row_list_collected_tv_budget_mpk);
            holder.imSignification = (ImageView) rowView.findViewById(R.id.row_list_collected_iv);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        InventoredMaterial im = inventoredMaterials.get(position);


        String isToZero;
      /*  if (cm.isToZero()) isToZero = "na zero";
        else*/
        isToZero = "";

        int resultPosition = inventoredMaterials.size() - position;

        Log.w("data",im.getInventoredDate().toString());
        holder.tvIndex.setText(im.getMaterial().getSplittedIndexByCpglRegule());
        holder.tvName.setText(im.getMaterial().getName());
        holder.tvDate.setText(Utils.reformatDate(im.getInventoredDate().toString(), context.getString(R.string.date_format_out)));///reformatDate() testing reformat date -reformatDate()< ToDo
        holder.tvPickedAmount.setText("brakuje: " + Utils.parse(im.getMaterial().getAmount()) + " " + im.getMaterial().getUnit());//+ " " + cm.getUnit() + "       " + isToZero
        holder.tvMpk.setText(im.getMaterial().getDescription() );
       // holder.tvMpk.setVisibility(View.GONE);
        holder.tvPos.setText(String.valueOf(resultPosition));
/*
     if (!cm.getSignAddress().equals("") && cm.getSignAddress() != null)
            holder.imSignification.setVisibility(View.VISIBLE);
        else
            holder.imSignification.setVisibility(View.INVISIBLE);
*/

        rowView.setBackgroundColor(mSelectedItemIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
        return rowView;


    }

 /*   @Override
    public void add(StoredMaterial storedMaterial) {
        storedMaterial.add(storedMaterial);
        notifyDataSetChanged();
    }

    @Override
    public void remove(CollectedMaterial object) {
        collectedMaterials.remove(object);
        notifyDataSetChanged();
    }*/

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemIds.put(position, value);
        } else {
            mSelectedItemIds.delete(position);
        }
        notifyDataSetChanged();

    }

    public void removeSelection() {
        mSelectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemIds.get(position));
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemIds.size();
    }

    public SparseBooleanArray getSelectedItemIds() {
        return mSelectedItemIds;
    }

    static class ViewHolder {
        public TextView tvPos;
        public TextView tvIndex;
        public TextView tvName;
        public TextView tvDate;
        public TextView tvMpk;
        public ImageView imSignification;
        public TextView tvPickedAmount;

    }



}
