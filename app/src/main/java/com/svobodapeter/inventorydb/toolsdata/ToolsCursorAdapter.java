package com.svobodapeter.inventorydb.toolsdata;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.svobodapeter.inventorydb.MainActivity;
import com.svobodapeter.inventorydb.R;
import com.svobodapeter.inventorydb.toolsdata.ToolsContract.ToolsEntry;

/**
 * Adapter is helping to put data obtained from database into views of UI
 */
public class ToolsCursorAdapter extends CursorAdapter {

    //Constructor implementing also Cursor
    public ToolsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    //Inflating new views with new data na list_item.xml
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //Binding data to each compoment of ListView and creating or updating views.
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        //Find views to bind data
        TextView productNameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity_in_stock);
        Button sellButton = view.findViewById(R.id.sell_button);


        //Indexing columns in table through cursor and getting data
        //Will provide id which is used later for sellButton
        final int id = cursor.getInt(cursor.getColumnIndex(ToolsEntry._ID));
        //Data for product name
        String name = cursor.getString(cursor.getColumnIndex(ToolsEntry.COLUMN_PRODUCT_NAME));
        //Data for price
        int mPrice = cursor.getInt(cursor.getColumnIndex(ToolsEntry.COLUMN_PRICE));
        String price = String.valueOf(mPrice);
        //Data for quantity
        final int mQuantity = cursor.getInt(cursor.getColumnIndex(ToolsEntry.COLUMN_QUANTITY));
        String quantity = String.valueOf(mQuantity);

        //Setting the product name
        productNameTextView.setText(name);

        //Setting the price and adding currency
        priceTextView.setText(price);
        String priceF = context.getResources().getString(R.string.local_currency);
        priceTextView.append(" " + priceF);

        //Setting the quantity and adding shortcut for peaces
        quantityTextView.setText(quantity);

        //Calling method from MainActivity to sell one piece of item
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.sellOneItem(id, mQuantity);
            }
        });

    }
}

