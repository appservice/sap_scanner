package eu.appservice.sap_scanner.databases;


//class which create database on android system

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.appservice.sap_scanner.PlantStrucMpk;


public class CompanyStructDbOpenHelper extends SQLiteOpenHelper {


    private static String DB_PATH = "/data/data/com.example.databasetest/databases/";
    private static final String DB_NAME = "company.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_FACTORY = "factory";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_VIEW_ID = "view_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_BUDGET = "budget";
    Context myContext;

    public CompanyStructDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    private static final String DATABASE_CREATE = "create table " + TABLE_FACTORY + "(" + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_PARENT_ID + " integer not null, " + COLUMN_VIEW_ID + " integer not null, " + COLUMN_NAME + " text not null, " + COLUMN_VALUE + " text, " + COLUMN_BUDGET + " text)";


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(CompanyStructDbOpenHelper.class.getName(), "Upgreting database from old version: "
                + oldVersion + " to new version: " + newVersion + ", which will derstroy all old data");
        db.execSQL("DROP TABLE IF EXIST " + TABLE_FACTORY);
        onCreate(db);
    }

    // all CRUD (CREATE, READ, UDPATE, DELETE)	operations

    //------------add row---------------------------------------
    public void addFactory(PlantStrucMpk plantStrucMpk) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PARENT_ID, plantStrucMpk.getParent_id());
        cv.put(COLUMN_VIEW_ID, plantStrucMpk.getView_id());
        cv.put(COLUMN_NAME, plantStrucMpk.getName());
        cv.put(COLUMN_VALUE, plantStrucMpk.getValue());
        cv.put(COLUMN_BUDGET, plantStrucMpk.getBudget());
        db.insert(TABLE_FACTORY, null, cv);
        db.close();

    }    //------------add row---------------------------------------
    public void addFactoryWithId(PlantStrucMpk plantStrucMpk) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID,plantStrucMpk.getId());
        cv.put(COLUMN_PARENT_ID, plantStrucMpk.getParent_id());
        cv.put(COLUMN_VIEW_ID, plantStrucMpk.getView_id());
        cv.put(COLUMN_NAME, plantStrucMpk.getName());
        cv.put(COLUMN_VALUE, plantStrucMpk.getValue());
        cv.put(COLUMN_BUDGET, plantStrucMpk.getBudget());
        db.insert(TABLE_FACTORY, null, cv);
        db.close();

    }

    public int getLastId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String MY_QUERY = "select MAX(" + COLUMN_ID + ") from " + TABLE_FACTORY;
        Cursor cur = db.rawQuery(MY_QUERY, null);
        cur.moveToFirst();
        int _id = cur.getInt(0);
        cur.close();
        db.close();
        return _id;

    }

    //-----------------get one row ----------------------------------------
    public PlantStrucMpk getFactory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = new String[]{COLUMN_ID, COLUMN_PARENT_ID, COLUMN_VIEW_ID, COLUMN_NAME, COLUMN_VALUE, COLUMN_BUDGET};
        String where = COLUMN_ID + "=" + id;
        Cursor cursor = db.query(TABLE_FACTORY, columns, where, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        PlantStrucMpk plantStrucMpk = new PlantStrucMpk(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1), Integer.parseInt(cursor.getString(2))), cursor.getString(3), cursor.getString(4), cursor.getString(5));

        cursor.close();
        db.close();
        return plantStrucMpk;

    }

    //---------------------get all records-----------------------------------
    public List<PlantStrucMpk> getAllFactorys() {
        List<PlantStrucMpk> allFactorys = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = new String[]{COLUMN_ID, COLUMN_PARENT_ID, COLUMN_VIEW_ID, COLUMN_NAME, COLUMN_VALUE, COLUMN_BUDGET};
        //String where=COLUMN_ID+"="+id;
        Cursor cursor = db.query(TABLE_FACTORY, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                PlantStrucMpk plantStrucMpk = new PlantStrucMpk(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                allFactorys.add(plantStrucMpk);
            }
            while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return allFactorys;

    }


    //-----take the list where parent id is the same---------------------
    public List<PlantStrucMpk> getFactorysByParentId(int parentId) {
        List<PlantStrucMpk> allFactorysByParentId = new ArrayList<PlantStrucMpk>();
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = new String[]{COLUMN_ID, COLUMN_PARENT_ID, COLUMN_VIEW_ID, COLUMN_NAME, COLUMN_VALUE, COLUMN_BUDGET};
        String where = COLUMN_PARENT_ID + "=" + parentId;
        Cursor cursor = db.query(TABLE_FACTORY, columns, where, null, null, null, COLUMN_VIEW_ID);
        if (cursor.moveToFirst()) {
            do {
                PlantStrucMpk plantStrucMpk = new PlantStrucMpk(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                allFactorysByParentId.add(plantStrucMpk);
            }
            while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return allFactorysByParentId;

    }

//------------------delete all rows -------------------------------------	

    public void deleteAllFactorys() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FACTORY, null, null);
        db.close();
    }

    //-----------------delete row where id is ---------------------------------
    public void deleteFactory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = COLUMN_ID + "=" + id;
        db.delete(TABLE_FACTORY, where, null);
        db.close();
    }

    //-------------------replace view_id ---------------

    public void replaceViewId(int id, int view_id, int id_prev, int view_id_prev) {
        SQLiteDatabase db = this.getWritableDatabase();
        //  String columns[] =new String[]{COLUMN_VIEW_ID};


        ContentValues cv1 = new ContentValues();
        cv1.put(COLUMN_VIEW_ID, view_id_prev);
        String where3 = COLUMN_ID + "=" + id;
        db.update(TABLE_FACTORY, cv1, where3, null);

        ContentValues cv2 = new ContentValues();
        cv2.put(COLUMN_VIEW_ID, view_id);
        String where4 = COLUMN_ID + "=" + id_prev;
        db.update(TABLE_FACTORY, cv2, where4, null);
        db.close();

    }

    /**
     * Creates activity_rw empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }
}





