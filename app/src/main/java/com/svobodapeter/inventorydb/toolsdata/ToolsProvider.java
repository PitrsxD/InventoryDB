package com.svobodapeter.inventorydb.toolsdata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.svobodapeter.inventorydb.toolsdata.ToolsContract.ToolsEntry;

public class ToolsProvider extends ContentProvider {

    //Global variable for created table
    ToolsDbHelper mToolsDbHelper;

    //URI matcher helping recognise passing URI address
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * URI matcher will recognize address and give it ID with number 100 or 101, which is helpful
     * later, in methods, where we are deciding which parameters to use with "if" method according to path
     */
    private static final int TOOLS = 100;

    static {
        sUriMatcher.addURI(ToolsContract.CONTENT_AUTHORITY, ToolsContract.PATH_TOOLS, TOOLS);
    }

    private static final int TOOLS_ID = 101;

    static {
        sUriMatcher.addURI(ToolsContract.CONTENT_AUTHORITY, ToolsContract.PATH_TOOLS + "/#",
                TOOLS_ID);
    }


    /**
     * Initialize the provider and the database helper object
     */
    @Override
    public boolean onCreate() {
        //Variable is taking name of database and version to recognise database to work with
        mToolsDbHelper = new ToolsDbHelper(getContext(), ToolsDbHelper.DB_NAME, null, ToolsDbHelper.DB_VERSION);
        return true;
    }

    /**
     * Query will help us to update data from table into our views for our users
     *
     * @param uri           - Address which is obtained - PETS or PETS_ID
     * @param projection    - parameters that should be send back as result
     * @param selection     - parameters to filter in table
     * @param selectionArgs - parameters to filter in table - detail
     * @param sortOrder     - will sort result acc. demand
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //Will attach readable table to our SQL variable
        SQLiteDatabase db = mToolsDbHelper.getReadableDatabase();

        //Cursor reading in table
        Cursor cursor;

        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case TOOLS:
                //Will query whole table
                cursor = db.query(ToolsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TOOLS_ID:
                // TOOLS_ID query single item acc. ID in URI
                selection = ToolsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Will query item acc. to ID above (in URI/selection, selectionArgs)
                cursor = db.query(ToolsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                //Throws exception about unknown URI
                throw new IllegalArgumentException("Cannot query unknown uri" + " " + uri);
        }

        //Setting listener for cases when data is updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        // Getting matcher to select right path
        int matcher = sUriMatcher.match(uri);
        //Checking if URI is correct and return type of MIME
        switch (matcher) {
            case TOOLS:
                return ToolsEntry.CONTENT_LIST_TYPE;
            case TOOLS_ID:
                return ToolsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + " " + uri +
                        "with match" + " " + matcher);
        }
    }

    /**
     * Method will insert a new item into database acc. to selected table
     *
     * @param uri    - matched address
     * @param values - inserted values of item
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Getting matcher to select right path
        int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case TOOLS:
                return insertTool(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not possible for" + " " + uri);
        }
    }

    /**
     * Sub-method of "insert" to insert a single item
     *
     * @param uri
     * @param values
     * @return
     */
    private Uri insertTool(Uri uri, ContentValues values) {
        //Getting writable table for our variable
        SQLiteDatabase db = mToolsDbHelper.getWritableDatabase();

        //Data check before inserting - Product Name
        if (values.containsKey(ToolsEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(ToolsEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Missing product name");
            }
        }

        //Data check before inserting - Price
        if (values.containsKey(ToolsEntry.COLUMN_PRICE)) {
            int price = values.getAsInteger(ToolsEntry.COLUMN_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Invalid or missing price");
            }
        }

        //Data check before inserting - Quantity
        if (values.containsKey(ToolsEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(ToolsEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Invalid or missing quantity");
            }
        }

        //Data check before inserting - Supplier name
        if (values.containsKey(ToolsEntry.COLUMN_SUPPLIER_NAME)) {
            String suppNameCheck = values.getAsString(ToolsEntry.COLUMN_SUPPLIER_NAME);
            if (suppNameCheck == null) {
                throw new IllegalArgumentException("Invalid or missing supplier name");
            }
        }

        //Data check before inserting - Supplier name
        if (values.containsKey(ToolsEntry.COLUMN_SUPPLIER_PHONE)) {
            String suppPhoneCheck = values.getAsString(ToolsEntry.COLUMN_SUPPLIER_PHONE);
            if (suppPhoneCheck == null) {
                throw new IllegalArgumentException("Invalid or missing phone number");
            }
        }

        //Inserting "values" into table and throw exception in case of error
        long newRowId = db.insert(ToolsEntry.TABLE_NAME, null, values);
        if (newRowId < 0) {
            throw new IllegalArgumentException("Insertion is not possible for" + " " + uri);
        }

        //Giving info to listener that data is updated and to refresh view
        getContext().getContentResolver().notifyChange(uri, null);

        //Sending ID of new row to retrieve and add to item
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Method will delete whole table or just row acc. selection and selectionArgs
     *
     * @param uri
     * @param selection     - for single item delete parameters
     * @param selectionArgs - for single item delete parameters
     * @return
     */
    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {
        //Will define what URI is used for our switch
        int matcher = sUriMatcher.match(uri);

        //Will obtain editable database
        SQLiteDatabase db = mToolsDbHelper.getWritableDatabase();

        switch (matcher) {
            case TOOLS:
                int affRowsDel = db.delete(ToolsEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return affRowsDel;
            case TOOLS_ID:
                // TOOLS_ID delete single item acc. ID in URI
                selection = ToolsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                int affSingleRowDel = db.delete(ToolsEntry.TABLE_NAME, selection, selectionArgs);
                return affSingleRowDel;
            default:
                throw new IllegalArgumentException("Item was not deleted with" + " " + uri);
        }
    }

    /**
     * To update whole table or just rows acc. to selection with new values
     *
     * @param uri
     * @param values        - new values
     * @param selection     - selected rows acc.
     * @param selectionArgs - selected rows acc.
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        //Will define what URI is used for our switch
        int matcher = sUriMatcher.match(uri);

        //Switch will decide how to update data in table
        switch (matcher) {
            case TOOLS:
                return updateTools(uri, values, selection, selectionArgs);
            case TOOLS_ID:
                // TOOLS_ID update single item acc. ID in URI
                selection = ToolsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Will update item acc. to ID above (in URI/selection, selectionArgs)
                return updateTools(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + " " + uri);
        }


    }

    /**
     * Sub-method for update
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    private int updateTools(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mToolsDbHelper.getWritableDatabase();

        //Data check before inserting - Product Name
        if (values.containsKey(ToolsEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(ToolsEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Missing product name");
            }
        }

        //Data check before inserting - Price
        if (values.containsKey(ToolsEntry.COLUMN_PRICE)) {
            int price = values.getAsInteger(ToolsEntry.COLUMN_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Invalid or missing price");
            }
        }

        //Data check before inserting - Quantity
        if (values.containsKey(ToolsEntry.COLUMN_QUANTITY)) {
            int quantity = values.getAsInteger(ToolsEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Invalid or missing quantity");
            }
        }

        //Data check before inserting - Supplier name
        if (values.containsKey(ToolsEntry.COLUMN_SUPPLIER_NAME)) {
            String suppNameCheck = values.getAsString(ToolsEntry.COLUMN_SUPPLIER_NAME);
            if (suppNameCheck == null) {
                throw new IllegalArgumentException("Invalid or missing supplier name");
            }
        }

        //Data check before inserting - Supplier name
        if (values.containsKey(ToolsEntry.COLUMN_SUPPLIER_PHONE)) {
            String suppPhoneCheck = values.getAsString(ToolsEntry.COLUMN_SUPPLIER_PHONE);
            if (suppPhoneCheck == null) {
                throw new IllegalArgumentException("Invalid or missing phone number");
            }
        }

        //Will update selected row with data from "values" and return number of affected rows
        int affRows = db.update(ToolsEntry.TABLE_NAME, values, selection, selectionArgs);

        //Will initialize listener to update view with data
        getContext().getContentResolver().notifyChange(uri, null);

        //Returning number of updated rows
        return affRows;
    }
}
