package com.svobodapeter.inventorydb;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.svobodapeter.inventorydb.toolsdata.ToolsContract.ToolsEntry;
import com.svobodapeter.inventorydb.toolsdata.ToolsCursorAdapter;
import com.svobodapeter.inventorydb.toolsdata.ToolsDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ToolsDbHelper mDbHelper;

    ToolsCursorAdapter toolsCursorAdapter;

    private static final String[] projection = new String[]{ToolsEntry._ID, ToolsEntry.COLUMN_PRODUCT_NAME,
            ToolsEntry.COLUMN_QUANTITY, ToolsEntry.COLUMN_PRICE};

    long currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Floating button which will add dummy data into our database by clicking on it
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ToolDetail.class);
                startActivity(intent);
            }
        });
        //Loading or creating the database
        mDbHelper = new ToolsDbHelper(this, ToolsDbHelper.DB_NAME, null, ToolsDbHelper.DB_VERSION);

        //Content Resolver is going into PetProvider class, where will gather data through Query method
        Cursor cursor = getContentResolver().query(ToolsEntry.CONTENT_URI, projection, null, null, null);
        Log.i("Used URI", String.valueOf(ToolsEntry.CONTENT_URI));

        final ListView displayListView = findViewById(R.id.list_view_tools);
        View emptyView = findViewById(R.id.empty_view);
        displayListView.setEmptyView(emptyView);

        toolsCursorAdapter = new ToolsCursorAdapter(this, cursor);

        displayListView.setAdapter(toolsCursorAdapter);
        displayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent (MainActivity.this,ToolDetail.class);
                intent.setData(ContentUris.withAppendedId(ToolsEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);


    }


    private void sellOneButton (View v) {
        TextView quantityInStock = findViewById(R.id.quantity_in_stock);
        String mQuantityIS = quantityInStock.getText().toString();
        int quantity = Integer.getInteger(mQuantityIS);
        if (quantity > 0) {
            quantity--;
            mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ToolsEntry.COLUMN_QUANTITY, quantity);
            Uri currentUri = ContentUris.withAppendedId(ToolsEntry.CONTENT_URI, currentId);
            if (currentUri != null) {
                int rowsAffected = getContentResolver().update(currentUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, "The item was sold", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


    /*
    Creating menu
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
        switch (item.getItemId()) {
            //When is menu selected, message is visible to the user.
            //noinspection SimplifiableIfStatement
            case (R.id.insert_dummy_data):
                insertDummyTools();
                return true;
            case (R.id.delete_all_items):
                getContentResolver().delete(ToolsEntry.CONTENT_URI, null, null);
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
        String supplierPhone = "429666444";

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Parsing our dummy values to the ContentVulues
        ContentValues values = new ContentValues();
        values.put(ToolsEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ToolsEntry.COLUMN_PRICE, price);
        values.put(ToolsEntry.COLUMN_QUANTITY, quantity);
        values.put(ToolsEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ToolsEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        //Inserting dummy values into database through ContentValues
        getContentResolver().insert(ToolsEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ToolsEntry.CONTENT_URI, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        toolsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        toolsCursorAdapter.swapCursor(null);
    }
}
