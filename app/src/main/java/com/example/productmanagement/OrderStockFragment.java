package com.example.productmanagement;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OrderStockFragment extends Fragment {

    public OrderStockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_order_stock, container, false);

        SQLiteOpenHelper productDatabaseHelper = new ProductDatabaseHelper(inflater.getContext());
        try {
            SQLiteDatabase db = productDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("PRODUCT",
                    new String [] {"NAME", "STOCK_ON_HAND", "STOCK_IN_TRANSIT",
                    "REORDER_QUANTITY", "REORDER_AMOUNT"}, null, null,
                    null, null, null);

                    if (cursor.moveToFirst()){

                        String[] names = new String[cursor.getColumnCount()];
                        names[0] = cursor.getString(0);

                        for (int i=1; i< cursor.getColumnCount(); i++){

                            if (cursor.moveToNext()){
                                names[i] = cursor.getString(0);
                            }
                        }

                        Spinner spinner = (Spinner) root.findViewById(R.id.order_stock_spinner);
                        TextView textView = (TextView) root.findViewById(R.id.order_stock_textView);


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                inflater.getContext(),
                                android.R.layout.simple_spinner_dropdown_item, names
                        );

                        spinner.setAdapter(adapter);

                        //Listen for selected item in spinner then query that name in database
                       /* StringBuilder brandsFormatted = new StringBuilder();
                        for (String brand : brandsList) {
                            brandsFormatted.append(brand).append('\n');
                        }*/
                    }
                    cursor.close();
                    db.close();

        }catch (SQLiteException e){
            Toast toast = Toast.makeText(inflater.getContext(),
                    "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();


        }


        return root;
    }
}