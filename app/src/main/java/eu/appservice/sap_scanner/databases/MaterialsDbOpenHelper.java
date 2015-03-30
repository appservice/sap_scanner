package eu.appservice.sap_scanner.databases;


import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.appservice.sap_scanner.Material;


/**
 * This class extends {@link android.database.sqlite.SQLiteOpenHelper}.</br></br>
 * <p/>
 * {@link #DB_NAME} -constant with the name of database</br>
 * {@link #DB_VERSION} -constant with version of database</br>
 * {@link #TABLE_NAME} -constant with the name of table</br>
 * {@link #DATABASE_CREATE} -constant with the SQL request for making table {@link #TABLE_NAME} -and creating database (Database is automatically created when doesn't exists)</br>
 * </br>
 * {@link #getMaterialsByIndexAndName(String, String, String, boolean)} -function which is using to find List of Materials according some parameters</br>
 * </br>
 *
 * @author mochelek
 * @
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class MaterialsDbOpenHelper extends SQLiteOpenHelper {
    /**
     * @param context
     * @
     */
    public MaterialsDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;

    }

    private static final String DB_NAME = "cpgl_store.db";
    private static final int DB_VERSION = 1;
    //private static final String DB_PATH="/data/data/eu.appservice/databases/";
    public static final String TABLE_NAME = "materials_db";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INDEX = "index_num";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_STORE = "store";
    public static final String COLUMN_DESCRIPTION = "description";
    private static final String[] ALL_COLUMNS_TABLE = new String[]{COLUMN_ID, COLUMN_INDEX, COLUMN_NAME, COLUMN_UNIT, COLUMN_AMOUNT, COLUMN_STORE, COLUMN_DESCRIPTION};
    Context myContext;

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INDEX + " text not null, "  //unique
            + COLUMN_NAME + " text not null, "
            + COLUMN_UNIT + " text, "
            + COLUMN_AMOUNT + " double,"
            + COLUMN_STORE + " text, "
            + COLUMN_DESCRIPTION + " text"
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


    //------------------CRUD--------------------------------------------------

    //-----------add row----------------------------------------

    /**
     * Add material to database
     *
     * @param material
     */
    public boolean addMaterial(Material material) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INDEX, material.getIndex());
        cv.put(COLUMN_NAME, material.getName());
        cv.put(COLUMN_UNIT, material.getUnit());
        cv.put(COLUMN_AMOUNT, material.getAmount());
        cv.put(COLUMN_STORE, material.getStore());
        cv.put(COLUMN_DESCRIPTION, material.getDescription());
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result != -1;

    }

    /**
     * This function add material and this operation could be transactional when we set that in db
     *
     * @param material
     * @param db
     */
    public long addMaterialsFromExcel(Material material, SQLiteDatabase db) {
        //SQLiteDatabase db=this.getWritableDatabase();
        //db.
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INDEX, material.getIndex());
        cv.put(COLUMN_NAME, material.getName());
        cv.put(COLUMN_UNIT, material.getUnit());
        cv.put(COLUMN_AMOUNT, material.getAmount());
        cv.put(COLUMN_STORE, material.getStore());
        cv.put(COLUMN_DESCRIPTION, material.getDescription());
        return db.insert(TABLE_NAME, null, cv);

        //db.close();

    }
    //-----------------get one row ----------------------------------------

    /**
     * @param id id integer parameter -number of row in database
     * @return Return material from database founded by id
     */
    public Material getMaterial(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = new String[]{COLUMN_ID, COLUMN_INDEX, COLUMN_NAME, COLUMN_UNIT, COLUMN_AMOUNT, COLUMN_STORE, COLUMN_DESCRIPTION};//
        String where = COLUMN_ID + "=" + id;
        Cursor cursor = db.query(TABLE_NAME, columns, where, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));

        cursor.close();
        db.close();
        return material;

    }

    //------------get all rows---------------
    public List<Material> getAllMaterials() {
        List<Material> allMaterials = new ArrayList<Material>();
        SQLiteDatabase db = this.getReadableDatabase();
        //String columns[] =new String[]{COLUMN_ID,COLUMN_PARENT_ID,COLUMN_VIEW_ID,COLUMN_NAME,COLUMN_VALUE,COLUMN_BUDGET};
        //String where=COLUMN_ID+"="+id;
        //db.
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, null, null, null, null, null); // last position limit for example "10000"
        if (cursor.moveToFirst()) {
            do {
                //COLUMN_ID,COLUMN_INDEX,COLUMN_NAME,COLUMN_UNIT,COLUMN_AMOUNT,COLUMN_STORE,COLUMN_DESCRIPTION
                // int id,String index, String name, String unit,double amount, String store, String description
                Material material = new Material(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6));
                allMaterials.add(material);
            }
            while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return allMaterials;
    }

//-------------read data from table-----------------------------------------
    public List<Material> getMaterialByIndex(String index) {

        SQLiteDatabase db = this.getReadableDatabase();
        //String columns[] =new String[]{COLUMN_ID,COLUMN_INDEX,COLUMN_NAME,COLUMN_UNIT,COLUMN_AMOUNT,COLUMN_STORE,COLUMN_DESCRIPTION};
      List<Material> materialsList=new ArrayList<Material>();
        String where = COLUMN_INDEX + " like '" + index + "'";
        Log.w("zapytanie", where);
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if(cursor.moveToFirst()){
                do{
            Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    materialsList.add(material);
                }while(cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return materialsList;
        } else
            return null;

    }

    public Material getMaterialByIndexAndStore(String index, String store) {

        SQLiteDatabase db = this.getReadableDatabase();
        //String columns[] =new String[]{COLUMN_ID,COLUMN_INDEX,COLUMN_NAME,COLUMN_UNIT,COLUMN_AMOUNT,COLUMN_STORE,COLUMN_DESCRIPTION};
        String where = COLUMN_INDEX + " like '" + index + "' and " + COLUMN_STORE + " like '" + store + "'";
        Log.w("zapytanie", where);
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));

            cursor.close();
            db.close();
            return material;
        } else
            return null;

    }

    /**
     * @param index_num set material index
     * @return Is returned activity_rw map of stock and amounts
     */
    public Map<String, Double> getMapStockAmount(String index_num) {
        Map<String, Double> map = new TreeMap<String, Double>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_INDEX + " like '" + index_num + "'";
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_STORE,
                COLUMN_AMOUNT}, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    map.put(cursor.getString(0),
                            Double.parseDouble(cursor.getString(1)));
                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return map;

    }


    public List<Material> getMaterialsByName(String name) {
        List<Material> allMaterialsByParentId = new ArrayList<Material>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_NAME + " like '%" + name + "%'";
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    allMaterialsByParentId.add(material);
                }
                while (cursor.moveToNext());

            }

        }
        cursor.close();
        db.close();
        return allMaterialsByParentId;

    }

    /**
     * @param index            if you don't want checking by this parameter set empty String ""
     * @param name             if you don't want checking by this parameter set empty String ""
     * @param store            if you don't want checking by this parameter set empty String ""
     * @param isBiggerThanZero if you don't want checking by this parameter set null
     * @return Return activity_rw new ArrayList of founded Materials by above mentioned criteria
     */
    public List<Material> getMaterialsByIndexAndName(String index, String name, String store, boolean isBiggerThanZero, boolean isRunning) {

        List<Material> allMaterialsByIndexAndName = new ArrayList<Material>();
        SQLiteDatabase db = this.getReadableDatabase();
        String ind = index.replace(" ", "%"); //zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql -
        String nam = name.replace(" ", "%");//zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql

// wyszukiwanie przez poszczeg�lne por�wniania w kolumnach		
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
                    Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    allMaterialsByIndexAndName.add(material);
                }
                while (cursor.moveToNext()&&isRunning);

            }

        }
        cursor.close();
        db.close();
        return allMaterialsByIndexAndName;

    }
    public List<Material> getMaterialsByIndexAndName(String index, String name, String store, boolean isBiggerThanZero) {
     return getMaterialsByIndexAndName(index,name,store,isBiggerThanZero,true);
    }

    //---------------------substract  material amount---------------------------------
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
    public int updateMaterial(Material material) {
        if (material != null) {
//            if(material.getId()!=null){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_INDEX, material.getIndex());
            cv.put(COLUMN_NAME, material.getName());
            cv.put(COLUMN_UNIT, material.getUnit());
            cv.put(COLUMN_AMOUNT, material.getAmount());
            cv.put(COLUMN_STORE, material.getStore());
            cv.put(COLUMN_DESCRIPTION, material.getDescription());

            StringBuilder sbWhere = new StringBuilder();
            sbWhere.append(COLUMN_ID);
            sbWhere.append("=");
            sbWhere.append(material.getId());
            String where = sbWhere.toString();


            int result=db.update(TABLE_NAME, cv, where, null);
            db.close();
            return result;

        }

        return -1;
    }
    //--------drop table----------------------------------------------------

    /**
     * This function is dropping table with all data and create new clear one.
     */
    public void dropTableCpglStore() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        db.close();

    }


}
