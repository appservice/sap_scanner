package eu.appservice.sap_scanner.activities.fileChooser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;

public class FileChooserActivity extends ActionBarActivity {//


    private File currentDir;
    private FileItemArrayAdapter adapter;
    private ListView lv;
    private String fileFilter=".*"; //default filter for all files
    public static String FILE_FILTER="file_filter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_chooser);

        //get filter from intent
     Bundle extras=getIntent().getExtras();
        if(extras!=null){
            setFileFilter(extras.getString(FileChooserActivity.FILE_FILTER));
        }

      //-----------get main folder------------------
        String MAIN_FOLDER = "/" + this.getString(R.string.main_folder);


      lv =(ListView)findViewById(R.id.activity_file_chooser_lv);
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              FileItem o = adapter.getItem(position);

              if (o.getImage().equalsIgnoreCase("ic_folder2") || o.getImage().equalsIgnoreCase("ic_folder_up")) {
                  currentDir = new File(o.getPath());
                  fill(currentDir);
              } else {
                  onFileClick(o);
              }
          }
      });

        if(Utils.isExternalStorageWritable()){
            currentDir=  new File(Environment.getExternalStorageDirectory()+ MAIN_FOLDER);
            fill(currentDir);
            Log.i("path1",currentDir.getPath());
        }

       // currentDir = new File(MAIN_FOLDER);

    }

    private void onFileClick(FileItem o) {
     //   System.out.println(o.getPath());
        Intent intent = new Intent();
      //  intent.putExtra("GET_PATH",currentDir.toString());
       intent.putExtra("GetFileName",o.getName());

        setResult(RESULT_OK, intent);
        finish();
    }

    private void fill(File f) {
       // this.setTitle("Folder: "+f.getName());
        File[]dirs = f.listFiles();

        List<FileItem> dir = new ArrayList<FileItem>();
        List<FileItem>fls = new ArrayList<FileItem>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formatted = lastModDate.toString();
                    dir.add(new FileItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"ic_folder2"));
                }
                else
                {
                    if(ff.getName().matches(getFileFilter()))
                    fls.add(new FileItem(ff.getName(),Utils.readableFileSize(ff.length()), date_modify, ff.getAbsolutePath(),"ic_file"));
                }
            }
        }catch(Exception e)
        {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("/"))
            dir.add(0,new FileItem("..","Parent Directory","",f.getParent(),"ic_folder_up"));
        adapter = new FileItemArrayAdapter(FileChooserActivity.this,R.layout.row_list_file_item,dir);

        lv.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    public String getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(String fileFilter) {
        this.fileFilter=".*\\"+fileFilter.substring(1,fileFilter.length());
    }
}


