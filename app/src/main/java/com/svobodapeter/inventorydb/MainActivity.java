package com.svobodapeter.inventorydb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.svobodapeter.inventorydb.toolsdata.ToolsContract.ToolsEntry;
import com.svobodapeter.inventorydb.toolsdata.ToolsDbHelper;

public class MainActivity extends AppCompatActivity {

    ToolsDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating button which will add dummy data into our database by clicking on it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inserting dummy data
                insertDummyTools();
                //Showing message to user, that new row was created
                Snackbar.make(view, R.string.new_row_created, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                //Updating data on screen
                //displayDatabaseInfo();
            }
        });
        //Loading or creating the database
        mDbHelper = new ToolsDbHelper(this, ToolsDbHelper.DB_NAME, null, ToolsDbHelper.DB_VERSION);
        //Updating data on screen
        //displayDatabaseInfo();
    }

    /*
    This method will update data of database in our TextView and show it to the user
     */
    private void displayDatabaseInfo() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Rule for cursor, which is selecting columns (I could use also null)
        String[] projection = {
                ToolsEntry._ID, ToolsEntry.COLUMN_PRODUCT_NAME, ToolsEntry.COLUMN_PRICE,
                ToolsEntry.COLUMN_QUANTITY, ToolsEntry.COLUMN_SUPPLIER_NAME,
                ToolsEntry.COLUMN_SUPPLIER_PHONE};

        //Cursor is created according to this rules
        Cursor cursor = db.query(
                ToolsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        try {
            //Searching for TextView which will display our data

            //Getting index for columns in tools table
            int idColumnIndex = cursor.getColumnIndex(ToolsEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(ToolsEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ToolsEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ToolsEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ToolsEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ToolsEntry.COLUMN_SUPPLIER_PHONE);

            //Extracting from columns and rows data in loop
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

                //Appending extracted data to our TextView and showing through our TextView

            }
        } finally {
            //Closing our cursor to prevent memory leaks
            cursor.close();

        }
    }

    /*
    Creating menu - not important for now
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    Appending listener on our menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When is menu selected, message is visible to the user.
        Toast.makeText(this, R.string.settings_toast_nothing_to_show, Toast.LENGTH_SHORT).show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Inserting dummy data for our database
     */
    public void insertDummyTools() {
        //Values for our database
        String productName = "Hammer";
        int price = 70;
        int quantity = 1;
        String supplierName = "Hammer Factory";
        int supplierPhone = 429666444;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Parsing our dummy values to the ContentVulues
        ContentValues values = new ContentValues();
        values.put(ToolsEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ToolsEntry.COLUMN_PRICE, price);
        values.put(ToolsEntry.COLUMN_QUANTITY, quantity);
        values.put(ToolsEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ToolsEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        //Inserting dummy values into database through ContentValues
        long newRowId = db.insert(ToolsEntry.TABLE_NAME, null, values);
    }
}
