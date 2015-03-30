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

import eu.appservice.sap_scanner.CollectedMaterial;
import eu.appservice.sap_scanner.Material;


/**
 *
 * Created by Lukasz on 09.09.13.
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class CollectedMaterialDbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "picked_material.db";
    private static final int DB_VERSION = 1;
    //private static final String DB_PATH="/data/data/eu.appservice/databases/";
    private static final String TABLE_NAME = "picked_materials_db";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INDEX = "index_num";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_STORE = "store";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COLLECTED_QUANTITY = "pickedQuantity";
    private static final String COLUMN_BUDGET = "budget";
    private static final String COLUMN_MPK = "mpk";
    private static final String COLUMN_IS_ZERO = "isZero";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_SIGN_ADDRESS = "signAddress";

    private static final String[] ALL_COLUMNS_IN_TABLE =
            new String[]{COLUMN_ID, COLUMN_INDEX, COLUMN_NAME, COLUMN_UNIT, COLUMN_AMOUNT
                    , COLUMN_STORE, COLUMN_DESCRIPTION, COLUMN_COLLECTED_QUANTITY, COLUMN_BUDGET
                    , COLUMN_MPK, COLUMN_IS_ZERO, COLUMN_DATE, COLUMN_SIGN_ADDRESS};

    private static final String DATABASE_CREATE =
            "create table " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INDEX + " text not null, "  //unique
            + COLUMN_NAME + " text not null, "
            + COLUMN_UNIT + " text, "
            + COLUMN_AMOUNT + " double,"
            + COLUMN_STORE + " text, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_COLLECTED_QUANTITY + " double, "
            + COLUMN_BUDGET + " text, "
            + COLUMN_MPK + " text,"
            + COLUMN_IS_ZERO + " integer, "
            + COLUMN_DATE + " text, "
            + COLUMN_SIGN_ADDRESS + " text "
            + ")";

    public CollectedMaterialDbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MaterialsDbOpenHelper.class.getName(), "Upgrading database from old version: "
                + oldVersion + " to new version: " + newVersion + ", which will derstroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

//---------------------------------------CRUD METHODS------------------------------------------

//---------------------ADD-----------------------
    public boolean addCollectedMaterial(CollectedMaterial material) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_INDEX, material.getIndex());
        cv.put(COLUMN_NAME, material.getName());
        cv.put(COLUMN_UNIT, material.getUnit());
        cv.put(COLUMN_AMOUNT, material.getAmount());
        cv.put(COLUMN_STORE, material.getStore());
        cv.put(COLUMN_DESCRIPTION, material.getDescription());
        cv.put(COLUMN_COLLECTED_QUANTITY, material.getCollectedQuantity());
        cv.put(COLUMN_BUDGET, material.getBudget());
        cv.put(COLUMN_MPK, material.getMpk());
        cv.put(COLUMN_SIGN_ADDRESS, material.getSignAddress());

        if (material.isToZero()) {
            cv.put(COLUMN_IS_ZERO, 1);
        } else {
            cv.put(COLUMN_IS_ZERO, 0);
        }

        // cv.put(COLUMN_MPK,material.getDate());
        cv.put(COLUMN_DATE, material.getDate());

        long result;
        result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result != -1;

    }

    //-----------------GET by id ------------------

    /**
     * @param id id integer parameter -number of row in database
     * @return Return material from database founded by id
     */
    @SuppressWarnings("UnusedDeclaration")
    public CollectedMaterial getPickedMaterial(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String where = COLUMN_ID + "=" + id;
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_IN_TABLE, where, null, null, null, null);

        if (cursor != null)

            cursor.moveToFirst();
        boolean isZero;
        assert cursor != null;
        isZero = cursor.getInt(10) == 1;
        Material material = new Material(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6));
        CollectedMaterial collectedMaterial = new CollectedMaterial(material, cursor.getDouble(7), cursor.getString(9), cursor.getString(8), isZero, cursor.getString(11), cursor.getString(12));

        cursor.close();
        db.close();
        return collectedMaterial;

    }


   //-------------------UPDATE-------------------------------------
    /**
     * update collected material
     */
    public int updateCollectedMaterial(CollectedMaterial collectedMaterial) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INDEX, collectedMaterial.getIndex());
        cv.put(COLUMN_NAME, collectedMaterial.getName());
        cv.put(COLUMN_UNIT, collectedMaterial.getUnit());
        cv.put(COLUMN_AMOUNT, collectedMaterial.getAmount());
        cv.put(COLUMN_STORE, collectedMaterial.getStore());
        cv.put(COLUMN_DESCRIPTION, collectedMaterial.getDescription());
        cv.put(COLUMN_COLLECTED_QUANTITY, collectedMaterial.getCollectedQuantity());
        cv.put(COLUMN_BUDGET, collectedMaterial.getBudget());
        cv.put(COLUMN_MPK, collectedMaterial.getMpk());
        cv.put(COLUMN_SIGN_ADDRESS, collectedMaterial.getSignAddress());
        String where = collectedMaterial.getId() + "=" + COLUMN_ID;
        int updatedRow = db.update(TABLE_NAME, cv, where, null);
        db.close();
        return updatedRow;

    }


    //------------------GET ALL--------------------------------------------
    /**
     * @return list of all picked materials
     */
    public List<CollectedMaterial> getAllCollectedMaterials() {
        List<CollectedMaterial> allCollectedMaterials = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_NAME + " like '%%'";
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_IN_TABLE, where, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                boolean isZero;
                do {
                    isZero = cursor.getInt(10) == 1;
                    Material material = new Material(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    CollectedMaterial collectedMaterial = new CollectedMaterial(material, cursor.getDouble(7), cursor.getString(8), cursor.getString(9), isZero, cursor.getString(11), cursor.getString(12));
                    allCollectedMaterials.add(collectedMaterial);
                }
                while (cursor.moveToNext());

            }

        }
        assert cursor != null;
        cursor.close();
        db.close();
        return allCollectedMaterials;

    }

//------------------------GET BY PARAMETERS----------------------------------------
    /**
     * @param index  if you don't want checking by this parameter set empty String ""
     * @param name   if you don't want checking by this parameter set empty String ""
     * @param mpk    if you don't want checking by this parameter set empty String ""
     * @param budget if you don't want checking by this parameter set empty String""
     * @return Return activity_rw new ArrayList of founded Materials by above mentioned criteria
     */
    public List<CollectedMaterial> getPickedMaterialsByParameters(String index, String name, String mpk, String budget){
        List<CollectedMaterial> allMaterialsByIndexAndName = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String ind = index.replace(" ", "%"); //zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql -
        String nam = name.replace(" ", "%");//zamienia wszyskie spacje na znaki %-zastępujące wszyskie znaki w sql

// wyszukiwanie przez poszczeg�lne por�wniania w kolumnach
        String wher = "";
        if (ind.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";
            wher = wher + COLUMN_INDEX + " LIKE '" + ind + "'";

        }

        if (nam.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";

            wher = wher + COLUMN_NAME + " LIKE '%" + nam + "%'";
        }
        if (mpk.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";
            wher = wher + COLUMN_MPK + " LIKE '" + mpk.replace(" ", "%") + "'";
        }
        if (budget.length() > 0) {
            if (wher.length() > 0)
                wher = wher + " AND ";
            wher = wher + COLUMN_BUDGET + " LIKE '" + budget.replace(" ", "%") + "'";
        }
        //           zapytanie do kursora
        Cursor cursor = db.query(TABLE_NAME, ALL_COLUMNS_IN_TABLE, wher, null, null, null, null);


        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                boolean isZero;
                do {
                    isZero = cursor.getInt(10) == 1;
                    Material material = new Material(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Double.parseDouble(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                    CollectedMaterial collectedMaterial = new CollectedMaterial(material, cursor.getDouble(7), cursor.getString(8), cursor.getString(9), isZero, cursor.getString(11), cursor.getString(12));
                    allMaterialsByIndexAndName.add(collectedMaterial);
                }
                while (cursor.moveToNext());

            }

        }
        assert cursor != null;
        cursor.close();
        db.close();
        return allMaterialsByIndexAndName;
    }



//---------------------------DELETE-----------------------------------------------------
    /**
     * @param id number of material's id which should be delating.
     * @return return numer of row which is delated and 0 when nothing is delated
     */
    public int removeCollectedMaterialById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = COLUMN_ID + "=" + id;
        int removedRow = db.delete(TABLE_NAME, where, null);
        db.close();
        return removedRow;
    }



    //-------------------DELETE ALL (drop table)----------------------------------

    /**
     * This function is dropping table with all data and create new clear one.
     */
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
