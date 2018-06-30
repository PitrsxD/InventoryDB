package com.svobodapeter.inventorydb;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.svobodapeter.inventorydb.toolsdata.ToolsDbHelper;

import java.util.Currency;

public class ToolDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private ToolsDbHelper mToolsDbHelper;

    private Uri currentToolUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tool_detail);
        setTitle(getString(R.string.inventory_add));

        mProductNameEditText = findViewById(R.id.product_name_edittext);
        mPriceEditText = findViewById(R.id.price_edittext);
        mQuantityEditText = findViewById(R.id.quantity_edittext);
        mSupplierNameEditText = findViewById(R.id.supp_name_edittext);
        mSupplierPhoneEditText = findViewById(R.id.supp_phone_number_edittext);

        String defaultQuantity = "0";
        mQuantityEditText.setText(defaultQuantity);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
