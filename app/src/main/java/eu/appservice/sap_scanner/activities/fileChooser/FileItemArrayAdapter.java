package eu.appservice.sap_scanner.activities.fileChooser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.appservice.sap_scanner.R;

/**
 * Created by luke on 12.06.14.
 */
public class FileItemArrayAdapter extends ArrayAdapter<FileItem>{
    private Context c;
    private int id;
    private List<FileItem> items;

    public FileItemArrayAdapter(Context context, int textViewResourceId,
                            List<FileItem> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    public FileItem getItem(int i)
    {
        return items.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
     //   ViewHolder holder;

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(id, null);
        }

               /* create a new view of my layout and inflate it in the row */
        //convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        final FileItem o = items.get(position);
        if (o != null) {
            TextView fileName = (TextView) rowView.findViewById(R.id.row_list_file_item_tv_filename);
            TextView fileSize = (TextView) rowView.findViewById(R.id.row_list_file_item_tv_filesize);
            TextView fileDate = (TextView) rowView.findViewById(R.id.row_list_file_item_tv_filedate);


                       /* Take the ImageView from layout and set the city's image */
            ImageView imageCity = (ImageView) rowView.findViewById(R.id.row_list_materials_tv_index);
            String uri = "drawable/" + o.getImage();
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
            Drawable image = c.getResources().getDrawable(imageResource);
            imageCity.setImageDrawable(image);

            if(fileName!=null)
                fileName.setText(o.getName());
            if(fileSize!=null)
                fileSize.setText(o.getData());
            if(fileDate!=null)
                fileDate.setText(o.getDate());
        }
        return rowView;
    }

/*    private static class ViewHolder{
        public TextView tvFileName,tvFileSize,tvFileDate;
        public ImageView imgFileIcon;

    }*/
}
