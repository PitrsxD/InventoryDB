package com.svobodapeter.inventorydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.svobodapeter.inventorydb.ToolsContract.ToolsEntry;


public class ToolsDbHelper extends SQLiteOpenHelper {
    //Name of Database!
    public static final String DB_NAME = "storedTools.db";
    public static final int DB_VERSION = 1;

    /*
    Constructor fot class ToolsDbHelper
     */
    public ToolsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /*
    OnCrate will create table through ToolsContract class
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ToolsEntry.CREATE_TABLE_TOOLS);

    }

    /*
    Table is dropped through ToolContract class and again created
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ToolsEntry.DELET_TABLE_ENTRIES_TOOLS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
