package com.example.internadmin.fooddiary;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "historyfooddiary.db";

    private static final String HISTORY_TABLE_NAME = "mealshistory";
    private static final String HISTORY_COLUMN_ID = "id";
    private static final String HISTORY_COLUMN_FOODNAME = "foodname";
    private static final String HISTORY_COLUMN_TIME = "time";
    private static final String HISTORY_COLUMN_SERVINGS = "servingamt";
    private static final String HISTORY_COLUMN_IMGPATH = "imgpath";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String DISHID_TABLE_NAME = "DishID";
    private static final String DISHID_COLUMN_FOODNAME = "foodname";
    private static final String DISHID_COLUMN_IMGPATH = "imgpath";
    private static final String DISHID_COLUMN_VERSION = "version";
    private static final String DISHID_COLUMN_NUTRITIONJSON = "nutritionjson";
    private static final String DISHID_COLUMN_INGREDIENTLIST = "ingredientlist";

    private Context ctx;

    public DBHandler(Context context) {

        super(context, DATABASE_NAME , null, 1);

        ctx = context;
    }

    /*
    OnCreate() -> Creates the database tables required for operation. The 2 tables are mealshistory and DishID.
    The format for the 2 tables are shown below:
                  ------------------------
    Table Name:   | mealshistory         |
                  ----------------------------------------------------------------------------------------------------
    Column Names: | id(INTEGER, PRI KEY) | foodname(TEXT) | time(DATETIME)        | servingamt(REAL) | imgpath(TEXT) |
                  ----------------------------------------------------------------------------------------------------
    Column Desc:  | Entry ID             | Name of Food   | Time Meal is Consumed | Serving Amount   | Path to Image |
                  ----------------------------------------------------------------------------------------------------
                                                                                    (Dependent on a
                                                                                    particular dish's
                                                                                    serving size)
                  ---------------------------
    Table Name:   | DishID                  |
                  ------------------------------------------------------------------------------------------------------------
    Column Names: | foodname(TEXT, PRI KEY) | version(INTEGER)  | nutritionjson(TEXT) | ingredientlist(TEXT) | imgpath(TEXT) |
                  ------------------------------------------------------------------------------------------------------------
    Column Desc:  | Dish Name as identifier | Dish Info Version | Nutrition Facts     | List of Ingredients  | Path to Image |
                  ------------------------------------------------------------------------------------------------------------
                                              (Trigger update if  (Stored as a JSON     (Stored as a GSON      (Image of Dish)
                                              Dish info version   string)               string)
                                              is older than
                                              server's)
    */

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_MEALSHISTORY = "create table " + HISTORY_TABLE_NAME +
                " (" + HISTORY_COLUMN_ID + " integer primary key autoincrement, " +
                HISTORY_COLUMN_FOODNAME + " text, " +
                HISTORY_COLUMN_TIME + " datetime default current_timestamp, " +
                HISTORY_COLUMN_IMGPATH + " text default null, " +
                HISTORY_COLUMN_SERVINGS + " real);";

        String CREATE_TABLE_DISHID = "create table " + DISHID_TABLE_NAME +
                " (" + DISHID_COLUMN_FOODNAME + " text primary key, " +
                DISHID_COLUMN_VERSION + " integer, " +
                DISHID_COLUMN_IMGPATH + " text default null, " +
                DISHID_COLUMN_NUTRITIONJSON + " text, " +
                DISHID_COLUMN_INGREDIENTLIST + " text); ";

        db.execSQL(CREATE_TABLE_MEALSHISTORY);
        db.execSQL(CREATE_TABLE_DISHID);
    }

    /* Deletes the Tables if an upgrade is called */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DISHID_TABLE_NAME);
        onCreate(db);
    }

    //---------------------------------------
    //| Methods for manipulating Meal Table |
    //---------------------------------------


    /*
    ------------------------------------------------------------------------
    insertMealEntry(): Adds a new entry into the mealhistory table.

    Expects data from the Meal class method saveToDatabase, and returns true if the insertion is successful.
    First creates an entry, then gets the ID. Updates the entry with the image path.

    Returns false if entry insertion into mealhistory table is unsuccessful.
    -------------------------------------------------------------------------
    */

    public boolean insertMealEntry (String FoodName, Date TimeConsumed, float ServingAmt, File FoodImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_COLUMN_FOODNAME, FoodName);
        contentValues.put(HISTORY_COLUMN_TIME, dateFormat.format(TimeConsumed));
        contentValues.put(HISTORY_COLUMN_SERVINGS, ServingAmt);

        long rowID = db.insert(HISTORY_TABLE_NAME, null, contentValues);
        if(rowID == -1){
            return false;
        }else{

            if(FoodImg != null){
                ContentValues cv = new ContentValues();
                try{
                    cv.put(HISTORY_COLUMN_IMGPATH, SaveMealImg(ctx, rowID, FoodName, FoodImg));
                }catch (IOException e){
                    Log.e("I/O Error", e.getMessage());
                }

                db.update(HISTORY_TABLE_NAME, cv, HISTORY_COLUMN_ID + " = ? ", new String[] { Long.toString(rowID)});
            }

            return true;
        }

    }

    /*
    ------------------------------------------------------------------------
    SaveMealImg(): Private Method used by insertMealEntry and updateMealEntry to save the food image to internal directory.

    During database insertion, the ID is not known until the entry created. Hence, the file, which requires the ID for naming,
    can only be saved to imageDir directory after the entry is created.

    SaveMealImg takes the original file (likely from cache), copies it to imageDir directory (using private method copyFile),
    and names it with the FoodName it represents, and the rowID.

    Returns the path of full path of the image, to be stored in the database for future retrieval.

    -------------------------------------------------------------------------
    */

    private String SaveMealImg(Context context, long rowID, String FoodName, File FoodImg) throws IOException{

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, FoodName + "_" + Long.toString(rowID) +".jpg");

        copyFile(FoodImg, mypath);

        return mypath.getAbsolutePath();
    }

    /*
    ------------------------------------------------------------------------

    numberOfMealRecords(): Returns the number of records the user has saved into mealhistory

    -------------------------------------------------------------------------
    */

    public Long numberOfMealRecords(){
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, HISTORY_TABLE_NAME);
    }

    /*
    ------------------------------------------------------------------------

    updateHistoryEntry(): Performs updates on an existing entry

    Expects data from the Meal class method saveToDatabase, and returns true if the update is successful.

    Returns false if entry not updated.

    -------------------------------------------------------------------------
    */

    public boolean updateHistoryEntry (String FoodName, Date TimeConsumed, float ServingAmt, File FoodImg, Long RowID)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(HISTORY_COLUMN_FOODNAME, FoodName);
        cv.put(HISTORY_COLUMN_TIME, dateFormat.format(TimeConsumed));
        cv.put(HISTORY_COLUMN_SERVINGS, ServingAmt);
        try{
            cv.put(HISTORY_COLUMN_IMGPATH, SaveMealImg(ctx, RowID, FoodName, FoodImg));
        }catch (IOException e){
            Log.e("I/O Error", e.getMessage());
        }

        int updated = db.update(HISTORY_TABLE_NAME, cv, HISTORY_COLUMN_ID + "=" + RowID, null);

        return (updated > 0);

    }

    /*
    ------------------------------------------------------------------------

    deleteHistoryEntry(): Deletes an existing entry

    Expects the ID of the entry. Returns true if deleted.

    Returns false if no entry is deleted.

    -------------------------------------------------------------------------
    */

    public boolean deleteHistoryEntry (long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(HISTORY_TABLE_NAME,
                "id = ? ",
                new String[] { Long.toString(id) }) > 0);
    }

    /*
    ------------------------------------------------------------------------

    getHistoryEntries(): Gives a list of IDs from mealhistory that are within given time range.

    Expects the starting time and ending time.

    Returns a List of Long.

    -------------------------------------------------------------------------
    */

    public List<Long> getHistoryEntries(Date startdate, Date enddate) {
        List<Long> list = new ArrayList<>();

        String sqlquery = "select " + HISTORY_COLUMN_ID +  " from " + HISTORY_TABLE_NAME;

        if(startdate == null){
            if(enddate != null){
                sqlquery += " where " + HISTORY_COLUMN_TIME + " <= '" + dateFormat.format(enddate) + "'";
            }
        }else{
            if (enddate == null){
                sqlquery += " where " + HISTORY_COLUMN_TIME + " >= '" + dateFormat.format(startdate) + "'";
            }else{
                sqlquery += " where " + HISTORY_COLUMN_TIME + " >= '" + dateFormat.format(startdate) + "' and " +
                HISTORY_COLUMN_TIME + " <= '" + dateFormat.format(enddate) + "'";
            }
        }

        sqlquery += " order by " + HISTORY_COLUMN_TIME + " desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(sqlquery, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            list.add(Long.valueOf(res.getInt(res.getColumnIndex(HISTORY_COLUMN_ID))));
            res.moveToNext();
        }
        res.close();
        return list;
    }

    /*
    ------------------------------------------------------------------------

    getAllServingsTimePeriod(): Sums the number of servings within a time period for each FoodName.

    Expects the starting time and ending time.

    Returns a Hashmap of the FoodName and the corresponding sum of servings.

    -------------------------------------------------------------------------
    */

    public HashMap<String, Float> getAllServingsTimePeriod(Date startdate, Date enddate) {
        HashMap<String, Float> hmap = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + HISTORY_COLUMN_FOODNAME + " , sum("+ HISTORY_COLUMN_SERVINGS +") from " +
                HISTORY_TABLE_NAME + " where " + HISTORY_COLUMN_TIME + " between '" +
                dateFormat.format(startdate) + "" + "' and '" + dateFormat.format(enddate) +
                "' group by " + HISTORY_COLUMN_FOODNAME, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            hmap.put(res.getString(0), res.getFloat(1));
            res.moveToNext();
        }
        res.close();

        return hmap;
    }

    /*
    ------------------------------------------------------------------------

    getMeal: Function used by Meal Class to populate class variables.

    Takes the ID of the Meal and returns a bundle with the class variables.

    Only can return 1 entry at a time.

    -------------------------------------------------------------------------
    */


    public Bundle getMeal(long mealID){
        SQLiteDatabase db = this.getReadableDatabase();
        Bundle b = new Bundle();

        String selection = HISTORY_COLUMN_ID + " = ? ";
        String[] columns = {HISTORY_COLUMN_FOODNAME, HISTORY_COLUMN_TIME, HISTORY_COLUMN_SERVINGS,
        HISTORY_COLUMN_IMGPATH};
        String[] selectionArgs = {  Long.toString(mealID)  };

        Cursor cursor = db.query(HISTORY_TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);

        if(cursor.moveToFirst()){
            b.putString("FoodName", cursor.getString(0));
            b.putFloat("ServingAmt", cursor.getFloat(2));
            try{
                b.putSerializable("TimeConsumed", dateFormat.parse(cursor.getString(1)));
                b.putSerializable("FoodImg", new File(cursor.getString(3)));
            }catch (ParseException e){
                Log.e("getMeal", "Parse Error: " + e.getMessage());
            }catch(NullPointerException e){
                e.getStackTrace();
            }

        }

        cursor.close();

        return b;
    }

    //-----------------------------------------
    //| Methods for manipulating DishID Table |
    //-----------------------------------------


    /*
    ------------------------------------------------------------------------
    insertNewDishID(): Adds a new entry into the DishID table.

    Expects data from the DishID class method saveToDatabase.
    First creates an entry, then gets the ID. Updates the entry with the image path.

    No return value.
    -------------------------------------------------------------------------
    */

    public void insertNewDishID(String FoodName, int ver, String NutritionJSONstr, String IngListstr, String ImgPath){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DISHID_COLUMN_FOODNAME, FoodName);
        cv.put(DISHID_COLUMN_VERSION, ver);
        cv.put(DISHID_COLUMN_NUTRITIONJSON, NutritionJSONstr);
        cv.put(DISHID_COLUMN_INGREDIENTLIST, IngListstr);
        cv.put(DISHID_COLUMN_IMGPATH, ImgPath);

        db.insertWithOnConflict(DISHID_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /*
    ------------------------------------------------------------------------

    getDishID: Function used by DishID Class to populate class variables.

    Takes the FoodName Primary Key of the DishID and returns a bundle with the class variables.

    Only can return 1 entry at a time.

    -------------------------------------------------------------------------
    */


    public Bundle getDishID(String FoodName){

        SQLiteDatabase db = this.getReadableDatabase();
        Bundle b = new Bundle();

        String selection = DISHID_COLUMN_FOODNAME + " = ? ";

        String[] selectionArgs = { FoodName };

        Cursor cursor = db.query(DISHID_TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()){
            b.putBoolean("Exists", true);
            b.putString("FoodName", cursor.getString(0));
            b.putInt("Version", cursor.getInt(1));
            b.putSerializable("FoodImg", new File(cursor.getString(2)));
            b.putString("Nutrition", cursor.getString(3));
            b.putString("Ingredients", cursor.getString(4));

        }else{
            b.putBoolean("Exists", false);
        }

        cursor.close();

        return b;

    }

    /*
    ------------------------------------------------------------------------

    deleteHistoryEntry(): Deletes an existing entry

    Expects the FoodName primary key of the entry. Returns true if deleted.

    Returns false if no entry is deleted.

    -------------------------------------------------------------------------
    */

    public Boolean deleteDishIDEntry (String FoodName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(HISTORY_TABLE_NAME,
                DISHID_COLUMN_FOODNAME + " = ? ",
                new String[] { "'" + FoodName + "'" }) > 0);
    }

    /*
    ------------------------------------------------------------------------

    copyFile(): Private Method used by saveFoodImg() to copy the Image

    Expects a source file and destination file to copy the images.

    Source file in FoodImage is likely from cache.

    -------------------------------------------------------------------------
    */

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

}