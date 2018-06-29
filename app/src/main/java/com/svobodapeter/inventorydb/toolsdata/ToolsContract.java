package com.svobodapeter.inventorydb.toolsdata;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ToolsContract {

    //String path for app and variable for name of database
    public static final String CONTENT_AUTHORITY = "com.svobodapeter.inventorydb";
    public static final String PATH_TOOLS = "storedTools";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private ToolsContract() {
    }

    public static class ToolsEntry implements BaseColumns {
        //Name of table in database
        public static final String TABLE_NAME = "tools";

        //Constants for table columns
        public static final String _ID = "_id";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        public static final String INTEGER = " INTEGER";
        public static final String TEXT = " TEXT";
        public static final String PRIMARY_KEY = " PRIMARY KEY";
        public static final String NOT_NULL = " NOT NULL";
        public static final String AUTOINCREMENT = " AUTOINCREMENT";
        public static final String DEFAULT = " DEFAULT 0";
        public static final String COMMA = ", ";

        //String which will create a table
        public static final String CREATE_TABLE_TOOLS = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMMA +
                COLUMN_PRODUCT_NAME + TEXT + NOT_NULL + COMMA +
                COLUMN_PRICE + INTEGER + NOT_NULL + DEFAULT + COMMA +
                COLUMN_QUANTITY + INTEGER + NOT_NULL + DEFAULT + COMMA +
                COLUMN_SUPPLIER_NAME + TEXT + COMMA +
                COLUMN_SUPPLIER_PHONE + INTEGER + DEFAULT +
                ")";

        //String which will destroy a table
        public static final String DELET_TABLE_ENTRIES_TOOLS = "DROP TABLE IF EXISTS" + TABLE_NAME;

        //Complete URI address for storedTools.db
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TOOLS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + PATH_TOOLS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + PATH_TOOLS;


    }
}
