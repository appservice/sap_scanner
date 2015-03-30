package eu.appservice.sap_scanner.databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.appservice.sap_scanner.Material;
import eu.appservice.sap_scanner.logfile.StoredMaterial;


/**
 * This class extends {@link android.database.sqlite.SQLiteOpenHelper}.</br></br>
 * <p/>
 * {@link #DB_NAME} -constant with the name of database</br>
 * {@link #DB_VERSION} -constant with version of database</br>
 * {@link #TABLE_NAME} -constant with the name of table</br>
 * {@link #DATABASE_CREATE} -constant with the SQL request for making table {@link #TABLE_NAME} -and creating database (Database is automatically created when doesn't exists)</br>
 * </br>
 * {@link #getMaterialsByParameters(String, String, String, boolean)} -function which is using to find List of Materials according some parameters</br>
 * </br>
 *
 * @author mochelek
 * @ Created by Lukasz on 2014-04-09.
 * ﹕ SAP Skanner
 */
public class PzMaterialsDbOpenHelper extends SQLiteOpenHelper {  //TODO

    /**
     * @param context
     * @
     */
    public PzMaterialsDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

    }

    private static final String DB_NAME = "PZ.db";
    private static final int DB_VERSION = 1;
    //private static final String DB_PATH="/data/data/eu.appservice/databases/";
    public static final String TABLE_NAME = "pz_db";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDEX = "index_num";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_STORE = "store";
    public static final String COLUMN_DATE = "date";
    private static final String[] ALL_COLUMNS_TABLE = new String[]{COLUMN_ID, COLUMN_INDEX, COLUMN_NAME, COLUMN_UNIT, COLUMN_AMOUNT, COLUMN_STORE, COLUMN_DATE};
    Context myContext;

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INDEX + " text not null, "  //unique
            + COLUMN_NAME + " text not null, "
            + COLUMN_UNIT + " text, "
            + COLUMN_AMOUNT + " double,"
            + COLUMN_STORE + " text, "
            + COLUMN_DATE + " text"
            + ")";


    @Override
    public void onCreate(SQLiteDatabase database) {


        database.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(MaterialsDbOpenHelper.class.getName(), "Upgreting database from old version: "
                + oldVersion + " to new version: " + newVersion + ", which will derstroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    private ContentValues getStandardContentValues(StoredMaterial storedMaterial) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INDEX, storedMaterial.getMaterial().getIndex());
        cv.put(COLUMN_NAME, storedMaterial.getMaterial().getName());
        cv.put(COLUMN_UNIT, storedMaterial.getMaterial().getUnit());
        cv.put(COLUMN_AMOUNT, storedMaterial.getMaterial().getAmount());
        cv.put(COLUMN_STORE, storedMaterial.getMaterial().getStore());
        cv.put(COLUMN_DATE, storedMaterial.getDate());
        return cv;
    }


    private StoredMaterial getResultFromCursor(Cursor cursor) {
        Material material = new Material(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), null);
        return new StoredMaterial(material, cursor.getString(6));
    }

    //------------------CRUD--------------------------------------------------

    //-----------add row-----------------------------------------------------
    /**
     *
     * @param storedMaterial
     * @return If material is save in table return number of row, else return -1;
     */
    public long addStoredMaterial(StoredMaterial storedMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.
        ContentValues cv = getStandardContentValues(storedMaterial);
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }



    //-----------------get one row ----------------------------------------
    /**
     * @param id id integer parameter -number of row in database
     * @return Return material from database founded by id
     */
    public StoredMaterial getStoredMaterial(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_ID + "=" + id;
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        StoredMaterial storedMaterial = getResultFromCursor(cursor);
        cursor.close();
        db.close();
        return storedMaterial;

    }


    //------------get all rows-------------------------------------------------------
    /**
     *
     * @return list of all StoredMaterial from table
     */
    public List<StoredMaterial> getAllStoredMaterials() {
        List<StoredMaterial> allMaterials = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {

                StoredMaterial storedMaterial = getResultFromCursor(cursor);
                allMaterials.add(storedMaterial);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allMaterials;
    }


    //------------- get List of Stored Material by index-----------------------------------------
    /**
     *
     * @param index
     * @return List of StoredMaterials which were found by index
     */
    public List<StoredMaterial> getMaterialByIndex(String index) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<StoredMaterial> materialsList = new ArrayList<>();
        String where = COLUMN_INDEX + " like '" + index + "'";
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    StoredMaterial storedMaterial = getResultFromCursor(cursor);
                    materialsList.add(storedMaterial);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return materialsList;
        } else
            return null;

    }



    //------------- get List of Stored Material by index and store------------------------------
    /**
     *
     * @param index
     * @param store
     * @return StoredMaterial which was found by index and by store. It can be only one this kind of material.
     */
    public StoredMaterial getStoredMaterialByIndexAndStore(String index, String store) {

        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_INDEX + " like '" + index + "' and " + COLUMN_STORE + " like '" + store + "'";
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            StoredMaterial storedMaterial = getResultFromCursor(cursor);
            cursor.close();
            db.close();
            return storedMaterial;
        } else
            return null;

    }



    /**
     * @param index            if you don't want checking by this parameter set empty String ""
     * @param name             if you don't want checking by this parameter set empty String ""
     * @param store            if you don't want checking by this parameter set empty String ""
     * @param isBiggerThanZero if you don't want checking by this parameter set null
     * @return Return activity_rw new ArrayList of founded Materials by above mentioned criteria
     */
    public List<StoredMaterial> getMaterialsByParameters(String index, String name, String store, boolean isBiggerThanZero) {

        List<StoredMaterial> materialsByParameters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String ind = index.replace(" ", "%"); //zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql -
        String nam = name.replace(" ", "%");//zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql

// wyszukiwanie przez poszczególne porówniania w kolumnach
        String wher = "";
        if (ind.length() > 0) {
            wher = wher + COLUMN_INDEX + " LIKE '" + ind + "'";
            if (nam.length() > 0 || store.length() > 0 || isBiggerThanZero)
                wher = wher + " AND ";
        }
        if (nam.length() > 0) {
            wher = wher + COLUMN_NAME + " LIKE '%" + nam + "%'";
            if (store.length() > 0 || isBiggerThanZero)
                wher = wher + " AND ";
        }
        if (store.length() > 0) {
            wher = wher + COLUMN_STORE + " LIKE '" + store + "'";
            if (isBiggerThanZero)
                wher = wher + " AND ";
        }
        if (isBiggerThanZero) {
            wher = wher + COLUMN_AMOUNT + ">0";
        }
        //           zapytanie do kursora
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, wher, null, null, null, null);
        Log.w("pytanie", wher);

        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                   // Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                   StoredMaterial storedMaterial=getResultFromCursor(cursor);

                    materialsByParameters.add(storedMaterial);
                }
                while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return materialsByParameters;

    }

    //---------------------substract  material amount---------------------------------
    @Deprecated
    public boolean substractMaterialAmount(Material material, double substractedValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Double newAmount = material.getAmount() - substractedValue;
        //material.setAmount(newAmount);
        ContentValues cv = new ContentValues();
        cv.put("amount", newAmount);
        String[] whereArgs = {String.valueOf(material.getId())};
        db.update(TABLE_NAME, cv, "id=?", whereArgs);
        db.close();
        return true;
    }

//-----------------add to material value
    @Deprecated
    public boolean updateAmount(Material material, double addValue) {

        if (material != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_AMOUNT, material.getAmount() + addValue);
            String where = COLUMN_INDEX + "='" + material.getIndex() + "' and " + COLUMN_STORE + "='" + material.getStore() + "'";
            db.update(TABLE_NAME, cv, where, null);
            db.close();

            return true;
        } else {

            return false;

        }

    }


    //-------------update material-------------------------------
    public int updateMaterial(StoredMaterial material) {
        if (material != null) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = getStandardContentValues(material);

            StringBuilder sbWhere = new StringBuilder();
            sbWhere.append(COLUMN_ID);
            sbWhere.append("=");
            sbWhere.append(material.getMaterial().getId());
            String where = sbWhere.toString();


            int result = db.update(TABLE_NAME, cv, where, null);
            db.close();
            return result;

        }

        return -1;
    }


    //--------------------------------------------------------------------------------------------------
    /**
     * @param index_num set material index
     * @return Is returned activity_rw map of stock and amounts
     */
    public Map<String, Double> getMapStockAmount(String index_num) {
        Map<String, Double> map = new TreeMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_INDEX + " like '" + index_num + "'";
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_STORE,
                COLUMN_AMOUNT}, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    map.put(cursor.getString(0),
                            cursor.getDouble(1));
                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return map;

    }


//----------------------------remove row------------------------------------------------------------
    /**
     * @param id number of material's id which should be delating.
     * @return return numer of row which is delated and 0 when nothing is delated
     */
    public int removeStoredMaterialById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = COLUMN_ID + "=" + id;
        int removedRow = db.delete(TABLE_NAME, where, null);
        db.close();
        return removedRow;
    }



    //--------drop table----------------------------------------------------
    /**
     * This function is dropping table with all data and create new clear one.
     */
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        db.close();

    }


}




