package eu.appservice.sap_scanner.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.model.InventoredMaterial;


/**
 * Created by Lukasz on 03.12.13.
 * ﹕ SAP Skanner
 */
public class InventoryMaterialDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "inventory_material.db";
    private static final int DB_VERSION = 1;
    //private static final String DB_PATH="/data/data/eu.appservice/databases/";
    private static final String TABLE_NAME = "inventory_materials_db";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INDEX = "index_num";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_STORE = "store";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String[] ALL_COLUMNS_TABLE = new String[]{COLUMN_ID, COLUMN_INDEX, COLUMN_NAME, COLUMN_UNIT, COLUMN_AMOUNT, COLUMN_STORE, COLUMN_DESCRIPTION, COLUMN_DATE};

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INDEX + " text not null, "  //unique
            + COLUMN_NAME + " text not null, "
            + COLUMN_UNIT + " text, "
            + COLUMN_AMOUNT + " double,"
            + COLUMN_STORE + " text, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_DATE + " text "
            + ")";
    Context myContext;

    public InventoryMaterialDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MaterialsDbOpenHelper.class.getName(), "Upgreting database from old version: "
                + oldVersion + " to new version: " + newVersion + ", which will derstroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean addInvetoredMaterial(InventoredMaterial im) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INDEX, im.getMaterial().getIndex());
        cv.put(COLUMN_NAME, im.getMaterial().getName());
        cv.put(COLUMN_UNIT, im.getMaterial().getUnit());
        cv.put(COLUMN_AMOUNT, im.getMaterial().getAmount());
        cv.put(COLUMN_STORE, im.getMaterial().getStore());
        cv.put(COLUMN_DESCRIPTION, im.getMaterial().getDescription());
        Log.w("inventored date", im.getInventoredDate());
        cv.put(COLUMN_DATE, im.getInventoredDate());
        long result;
        result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result != -1;


    }


    //-----------------get one row ----------------------------------------

    /**
     * @param id id integer parameter -number of row in database
     * @return Return material from database founded by id

    public CollectedMaterial getPickedMaterial(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
    String columns[] = ALL_COLUMNS_TABLE;
    String where = COLUMN_ID + "=" + id;
    Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);

    if (cursor != null)

    cursor.moveToFirst();
    boolean isZero;
    isZero = cursor.getInt(10) == 1;
    Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6));
    CollectedMaterial collectedMaterial = new CollectedMaterial(material, cursor.getDouble(7), cursor.getString(9), cursor.getString(8), isZero, cursor.getString(11), cursor.getString(12));

    cursor.close();
    db.close();
    return collectedMaterial;

    }
     */
    /**
     * update picked material

     public int updatePickedMaterial(CollectedMaterial collectedMaterial){
     SQLiteDatabase db=this.getWritableDatabase();
     ContentValues cv=new ContentValues();
     cv.put(COLUMN_INDEX, collectedMaterial.getIndex());
     cv.put(COLUMN_NAME, collectedMaterial.getName());
     cv.put(COLUMN_UNIT, collectedMaterial.getUnit());
     cv.put(COLUMN_AMOUNT, collectedMaterial.getAmount());
     cv.put(COLUMN_STORE, collectedMaterial.getStore());
     cv.put(COLUMN_DESCRIPTION, collectedMaterial.getDescription());
     String where= collectedMaterial.getId()+"="+COLUMN_ID;
     int removedRow=db.update(TABLE_NAME,cv,where,null);
     db.close();
     return removedRow;

     } */
    /**
     * @return list of all picked materials
     */
    public List<InventoredMaterial> getAllInventoredMaterials() {
        List<InventoredMaterial> allInventoredMaterials = new ArrayList<InventoredMaterial>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_NAME + " like '%%'";
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {

                    Material material = new Material(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    InventoredMaterial collectedMaterial = new InventoredMaterial(material, cursor.getString(7));
                    allInventoredMaterials.add(collectedMaterial);
                }
                while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return allInventoredMaterials;

    }


    //--------drop table----------------------------------------------------

    /**
     * This function is dropping table with all data and create new clear one.
     */
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    /**
     * @param id number of material's id which should be delating.
     * @return return numer of row which is delated and 0 when nothing is delated
     */
    public int removePickedMaterialById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = COLUMN_ID + "=" + id;
        int removedRow = db.delete(TABLE_NAME, where, null);
        db.close();
        return removedRow;
    }

    /**
     * @param index if you don't want checking by this parameter set empty String ""
     * @param name  if you don't want checking by this parameter set empty String ""
     * @return Return activity_rw new ArrayList of founded Materials by above mentioned criteria
     */
    public List<CollectedMaterial> getPickedMaterialsByIndexAndName(String index, String name) {

        List<CollectedMaterial> allMaterialsByIndexAndName = new ArrayList<CollectedMaterial>();
        SQLiteDatabase db = this.getReadableDatabase();
        String ind = index.replace(" ", "%"); //zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql -
        String nam = name.replace(" ", "%");//zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql

// wyszukiwanie przez poszczeg�lne por�wniania w kolumnach
        String wher = "";
        if (ind.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";
            wher = wher + COLUMN_INDEX + " LIKE '" + ind + "'";
            //;
        }

        if (nam.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";

            wher = wher + COLUMN_NAME + " LIKE '%" + nam + "%'";
        }

        //           zapytanie do kursora
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, wher, null, null, null, null);
        Log.w("pytanie", wher);

        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                boolean isZero;
                do {
                    isZero = cursor.getInt(10) == 1;
                    Material material = new Material(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6));
                    CollectedMaterial collectedMaterial = new CollectedMaterial(material, cursor.getDouble(7), cursor.getString(8), cursor.getString(9), isZero, cursor.getString(11), cursor.getString(12));
                    allMaterialsByIndexAndName.add(collectedMaterial);
                }
                while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return allMaterialsByIndexAndName;

    }

    public void removeStoredMaterialById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = COLUMN_ID + "=" + id;
        int removedRow = db.delete(TABLE_NAME, where, null);
        db.close();
        // return removedRow;
    }

    public void deleteAllRows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

}
