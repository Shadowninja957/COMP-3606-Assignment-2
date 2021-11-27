package com.example.productmanagement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OrderStockFragment extends Fragment implements View.OnClickListener{

    private SQLiteDatabase db;
    private Cursor cursor;
    private View root;

    public OrderStockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_order_stock, container, false);
        Button makeOrderButton = (Button) root.findViewById(R.id.make_order_button);
        makeOrderButton.setOnClickListener(this);

        View fragmentContainer = root.findViewById(R.id.fragment_View2output);
        if (fragmentContainer!=null) {

            OutputViewFragment outputView = new OutputViewFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_View2output, outputView);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        }

        SQLiteOpenHelper productDatabaseHelper = new ProductDatabaseHelper(inflater.getContext());
        try {
            db = productDatabaseHelper.getReadableDatabase();
            cursor = db.query("PRODUCT",
                    new String [] {"NAME", "STOCK_ON_HAND", "STOCK_IN_TRANSIT",
                    "REORDER_QUANTITY", "REORDER_AMOUNT"}, null, null,
                    null, null, null);

            Spinner spinner = (Spinner) root.findViewById(R.id.order_stock_spinner);

            if (cursor.moveToFirst()) {

                String[] productNames = new String[cursor.getCount()];
                productNames[0] = cursor.getString(0);

                for (int i = 1; i < cursor.getCount(); i++) {

                    if (cursor.moveToNext()) {
                        productNames[i] = cursor.getString(0);
                    }
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        inflater.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, productNames
                );

                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        TextView textView = (TextView) root.findViewById(R.id.order_stock_textView);

                        cursor = db.query("PRODUCT",
                                new String[]{"NAME", "STOCK_ON_HAND", "STOCK_IN_TRANSIT",
                                        "REORDER_QUANTITY", "REORDER_AMOUNT"}, "_id = ?",
                                new String[]{Integer.toString(position + 1)},
                                null, null, null);

                        if (cursor.moveToFirst()) {

                            StringBuilder detailsFormatted = new StringBuilder();
                            detailsFormatted.append("Stock On Hand: ").append(cursor.getString(1)).append('\n');
                            detailsFormatted.append("Stock In Transit: ").append(cursor.getString(2)).append('\n');
                            detailsFormatted.append("Reorder Quantity: ").append(cursor.getString(3)).append('\n');
                            detailsFormatted.append("Reorder Amount: ").append(cursor.getString(4)).append('\n');

                            textView.setText(detailsFormatted);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                }

        }catch (SQLiteException e){
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

    @Override
    public void onClick(View view){
        onMakeOrder();
        View fragmentContainer = root.findViewById(R.id.fragment_View2output);
        if (fragmentContainer!=null) {

            OutputViewFragment outputView = new OutputViewFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_View2output, outputView);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        }
    }

    private void onMakeOrder(){

        Spinner spinner = root.findViewById(R.id.order_stock_spinner);
        int productId = (int) spinner.getSelectedItemId();
        new MakeOrderTask().execute(productId);
    }

    private class MakeOrderTask extends AsyncTask<Integer, Void, Boolean> {
        private ContentValues productItem;
        private int quantity;

        protected void onPreExecute() {
            EditText editText = root.findViewById(R.id.order_quantity_editText);
            quantity = Integer.parseInt(editText.getText().toString());
            productItem = new ContentValues();
        }

        protected Boolean doInBackground(Integer... products) {
            int productId = products[0];
            SQLiteOpenHelper productDatabaseHelper =
                    new ProductDatabaseHelper(root.getContext());
            try {
                SQLiteDatabase db = productDatabaseHelper.getWritableDatabase();

                Cursor cursor = db.query("PRODUCT", new String[]{"NAME", "STOCK_IN_TRANSIT",
                                "REORDER_QUANTITY"},
                        "_id = ?", new String[] {Integer.toString(productId + 1)},
                        null, null, null);

                int stockInTransit = 0;
                int reorder_quantity = 0;

                if (cursor.moveToFirst()){
                    stockInTransit = cursor.getInt(1);
                    reorder_quantity = cursor.getInt(2);
                    reorder_quantity *= quantity;
                    reorder_quantity += stockInTransit;
                    productItem.put("STOCK_IN_TRANSIT", reorder_quantity);
                    productItem.put("DIRTY", true);
                }


                db.update("PRODUCT", productItem,
                        "_id = ?", new String[] {Integer.toString(productId + 1)});
                db.close();
                return true;
            } catch(SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(root.getContext(),
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}