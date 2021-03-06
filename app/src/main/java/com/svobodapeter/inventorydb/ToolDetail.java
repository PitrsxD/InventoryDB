package com.svobodapeter.inventorydb;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.svobodapeter.inventorydb.toolsdata.ToolsContract.ToolsEntry;
import com.svobodapeter.inventorydb.toolsdata.ToolsDbHelper;

/**
 * Activity where user can create new item, or update existing item with new data
 */
public class ToolDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Global variables for EditTexts in list_item.xml
    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private ToolsDbHelper mToolsDbHelper;

    //Uri used for item update
    private Uri currentToolUri;

    boolean contentCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_detail);
        setTitle(getString(R.string.inventory_add));

        //Setting views for our EditTexts variables
        mProductNameEditText = findViewById(R.id.product_name_edittext);
        mPriceEditText = findViewById(R.id.price_edittext);
        mQuantityEditText = findViewById(R.id.quantity_edittext);
        mSupplierNameEditText = findViewById(R.id.supp_name_edittext);
        mSupplierPhoneEditText = findViewById(R.id.supp_phone_number_edittext);

        String defaultQuantity = "0";
        mQuantityEditText.setText(defaultQuantity);

        //For case of already created item is detail opened
        Intent mainActivityIntent = getIntent();
        currentToolUri = mainActivityIntent.getData();
        if (currentToolUri != null) {
            setTitle(getString(R.string.inventory_detail));
            setDataInView();
        }

        //Setting buttons to variables
        Button minusButton = findViewById(R.id.button_minus);
        Button plusButton = findViewById(R.id.button_plus);
        Button callToSupp = findViewById(R.id.call_to_supp);
        FloatingActionButton fabSave = findViewById(R.id.fab_save);

        //Listener of button for quantity decreasing
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method for decreasing and updating view
                minusButton();
            }
        });

        //Listener of button for quantity increasing
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method for increasing and updating view
                plusButton();
            }
        });

        //Listener of button to call on contact of supplier
        callToSupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + mSupplierPhoneEditText.getText().toString()));
                startActivity(callIntent);
            }
        });

//Listener of button to save new data or update data
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validating inserted data
                contentCheckMethod();
                if (contentCheck) {
                    //Saving data into table
                    saveData();
                    //Going back on MainActivity where the ListView is updated
                    backToMainActivity();
                }

            }
        });
    }

    //After creating new item or updating old item is view switched back on MainActivity
    private void backToMainActivity() {
        Intent toMainActivity = new Intent(ToolDetail.this, MainActivity.class);
        startActivity(toMainActivity);
    }

    private void setDataInView() {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{ToolsEntry.COLUMN_PRODUCT_NAME, ToolsEntry.COLUMN_PRICE,
                ToolsEntry.COLUMN_QUANTITY, ToolsEntry.COLUMN_SUPPLIER_NAME, ToolsEntry.COLUMN_SUPPLIER_PHONE};
        return new CursorLoader(this, currentToolUri, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (currentToolUri != null) {
            data.moveToFirst();
            String productName = data.getString(data.getColumnIndex(ToolsEntry.COLUMN_PRODUCT_NAME));
            int price = data.getInt(data.getColumnIndex(ToolsEntry.COLUMN_PRICE));
            int quantity = data.getInt(data.getColumnIndex(ToolsEntry.COLUMN_QUANTITY));
            String suppName = data.getString(data.getColumnIndex(ToolsEntry.COLUMN_SUPPLIER_NAME));
            String suppPhone = data.getString(data.getColumnIndex(ToolsEntry.COLUMN_SUPPLIER_PHONE));

            mProductNameEditText.setText(productName);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(suppName);
            mSupplierPhoneEditText.setText(String.valueOf(suppPhone));


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQuantityEditText.setText("0");
        mSupplierNameEditText.setText(null);
        mSupplierPhoneEditText.setText(null);

    }

    /**
     * Decreasing of quantity
     */
    private void minusButton() {
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
        if (quantity > 0) {
            quantity--;
            mQuantityEditText.setText(String.valueOf(quantity));
        } else {
            Toast.makeText(this, R.string.quantity_less_than_0, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Increasing of quantity
     */
    private void plusButton() {
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString());
        quantity++;
        mQuantityEditText.setText(String.valueOf(quantity));
    }

    /**
     * Method where system is passing inserted data to values and creating new item or updating old item
     */
    private void saveData() {
        String productName = mProductNameEditText.getText().toString().trim();
        int price = Integer.parseInt(mPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
        String suppName = mSupplierNameEditText.getText().toString().trim();
        String phone = mSupplierPhoneEditText.getText().toString().trim();

        mToolsDbHelper = new ToolsDbHelper(this, ToolsDbHelper.DB_NAME, null, ToolsDbHelper.DB_VERSION);
        mToolsDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ToolsEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ToolsEntry.COLUMN_PRICE, price);
        values.put(ToolsEntry.COLUMN_QUANTITY, quantity);
        values.put(ToolsEntry.COLUMN_SUPPLIER_NAME, suppName);
        values.put(ToolsEntry.COLUMN_SUPPLIER_PHONE, phone);

        //currentToolUri is saying if this is a new item (in case of null) or old item and we are just updating
        if (currentToolUri == null) {
            getContentResolver().insert(ToolsEntry.CONTENT_URI, values);
        } else {
            int affRows = getContentResolver().update(currentToolUri, values, null, null);
            if (affRows > 0) {
                backToMainActivity();
            }
        }
    }

    /**
     * Method where system is validating inputs and shows message in case of missing data
     */
    private void contentCheckMethod() {
        String productNameCheck = mProductNameEditText.getText().toString();
        String priceCheck = mPriceEditText.getText().toString();
        String quantity = mQuantityEditText.getText().toString();
        String suppName = mSupplierNameEditText.getText().toString();
        String suppPhone = mSupplierPhoneEditText.getText().toString();

        if (TextUtils.isEmpty(productNameCheck) || TextUtils.isEmpty(priceCheck) ||
                TextUtils.isEmpty(quantity) || TextUtils.isEmpty(suppName) || TextUtils.isEmpty(suppPhone)) {
            if (TextUtils.isEmpty(productNameCheck)) {
                mProductNameEditText.setError("Please, write name of product");
            }
            if (TextUtils.isEmpty(priceCheck)) {
                mPriceEditText.setError("Please, fill the price of product");
            }
            if (TextUtils.isEmpty(quantity)) {
                mQuantityEditText.setError("Please, fill number of pieces in stock");
            }
            if (TextUtils.isEmpty(suppName)){
                mSupplierNameEditText.setError("Please, fill the name of supplier");
            }
            if (TextUtils.isEmpty(suppPhone)){
                mSupplierPhoneEditText.setError("Please, fill the phone of supplier");
            }

        } else {
            contentCheck = true;
        }
    }

    /*
   Creating menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentToolUri != null) {
            getMenuInflater().inflate(R.menu.tool_detail_menu, menu);
        }
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
            case (R.id.delete_item):
                deleteDialogMessage();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method will create alert dialog to alert before deleting the button
     */
    private void deleteDialogMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_one_item_message);
        builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(currentToolUri, null, null);
                backToMainActivity();
            }
        });
        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog == null){
                    dialog.dismiss();
                }
            }
        });
        //Will show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}



