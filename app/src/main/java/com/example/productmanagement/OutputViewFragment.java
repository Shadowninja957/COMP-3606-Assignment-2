package com.example.productmanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class OutputViewFragment extends Fragment {

    private SQLiteDatabase db;
    private Cursor cursor;
    private View root;

    public OutputViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_output_view, container, false);

        SQLiteOpenHelper productDatabaseHelper = new ProductDatabaseHelper(inflater.getContext());
        try {
            db = productDatabaseHelper.getReadableDatabase();
            cursor = db.query("PRODUCT",
                    new String [] {"NAME", "STOCK_ON_HAND", "STOCK_IN_TRANSIT", "PRICE",
                            "REORDER_QUANTITY", "REORDER_AMOUNT"}, null, null,
                    null, null, null);

            if (cursor.moveToFirst()){

                double price = cursor.getDouble(3);
                double valuation = price * cursor.getDouble(1);
                double in_Transit_Valuation = price * cursor.getDouble(2);

                StringBuilder detailsFormatted = new StringBuilder();
                detailsFormatted.append("Product Name: ").append(cursor.getString(0)).append('\n');
                detailsFormatted.append("Stock On Hand: ").append(cursor.getString(1)).append('\n');
                detailsFormatted.append("Stock In Transit: ").append(cursor.getString(2)).append('\n');
                detailsFormatted.append("Reorder Quantity: ").append(cursor.getString(4)).append('\n');
                detailsFormatted.append("Reorder Amount: ").append(cursor.getString(5)).append('\n');
                detailsFormatted.append("Valuation: ").append(valuation).append('\n');
                detailsFormatted.append("In-Transit Valuation: ").append(in_Transit_Valuation).append('\n');
                detailsFormatted.append('\n');

                for (int i=1; i<cursor.getCount(); i++) {
                    if (cursor.moveToNext()) {
                        price = cursor.getDouble(3);
                        valuation = price * cursor.getDouble(1);
                        in_Transit_Valuation = price * cursor.getDouble(2);
                        valuation = Math.round(valuation * 100.0) / 100.0;
                        in_Transit_Valuation = Math.round(in_Transit_Valuation * 100.0) / 100.0;

                        detailsFormatted.append("Product Name: ").append(cursor.getString(0)).append('\n');
                        detailsFormatted.append("Stock On Hand: ").append(cursor.getString(1)).append('\n');
                        detailsFormatted.append("Stock In Transit: ").append(cursor.getString(2)).append('\n');
                        detailsFormatted.append("Reorder Quantity: ").append(cursor.getString(4)).append('\n');
                        detailsFormatted.append("Reorder Amount: ").append(cursor.getString(5)).append('\n');
                        detailsFormatted.append("Valuation: ").append(valuation).append('\n');
                        detailsFormatted.append("In-Transit Valuation: ").append(in_Transit_Valuation).append('\n');
                        detailsFormatted.append('\n');
                    }
                }

                TextView textView = (TextView) root.findViewById(R.id.output_view_textView);
                textView.setText(detailsFormatted);
            }


        }
        catch (SQLiteException e){
            Toast toast = Toast.makeText(inflater.getContext(),
                    "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        return root;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }
}