package com.example.productmanagement;

import android.content.ContentValues;
import android.database.Cursor;
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

public class ReceiveStockFragment extends Fragment implements View.OnClickListener{
    private SQLiteDatabase db;
    private Cursor cursor;
    private View root;

    public ReceiveStockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_receive_stock, container, false);
        Button makeOrderButton = (Button) root.findViewById(R.id.update_stocks_button);
        makeOrderButton.setOnClickListener(this);

        View fragmentContainer = root.findViewById(R.id.fragment_View1output);
        if (fragmentContainer!=null) {

            OutputViewFragment outputView = new OutputViewFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_View1output, outputView);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        }

        SQLiteOpenHelper productDatabaseHelper = new ProductDatabaseHelper(inflater.getContext());
        try {
            db = productDatabaseHelper.getReadableDatabase();
            cursor = db.query("PRODUCT",
                    new String [] {"NAME"}, null, null,
                    null, null, null);

            Spinner spinner = (Spinner) root.findViewById(R.id.receive_stock_spinner);

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
        onUpdateStocks();

        View fragmentContainer = root.findViewById(R.id.fragment_View1output);
        if (fragmentContainer!=null) {

            OutputViewFragment outputView = new OutputViewFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_View1output, outputView);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        }
    }

    private void onUpdateStocks(){
        Spinner spinner = root.findViewById(R.id.receive_stock_spinner);
        int productId = (int) spinner.getSelectedItemId();
        new UpdateStocksTask().execute(productId);
    }

    private class UpdateStocksTask extends AsyncTask<Integer, Void, Boolean> {
        private ContentValues productItem;
        private int quantity;

        protected void onPreExecute() {
            EditText editText = root.findViewById(R.id.receive_quantity_editText);
            quantity = Integer.parseInt(editText.getText().toString());
            productItem = new ContentValues();
        }

        protected Boolean doInBackground(Integer... products) {
            int productId = products[0];
            SQLiteOpenHelper productDatabaseHelper =
                    new ProductDatabaseHelper(root.getContext());
            try {
                SQLiteDatabase db = productDatabaseHelper.getWritableDatabase();

                Cursor cursor = db.query("PRODUCT", new String[]{"NAME", "STOCK_ON_HAND",
                                "STOCK_IN_TRANSIT"},
                        "_id = ?", new String[] {Integer.toString(productId + 1)},
                        null, null, null);

                int stockInTransit = 0;
                int stockOnHand = 0;

                if (cursor.moveToFirst()){
                    stockInTransit = cursor.getInt(2);
                    stockInTransit -= quantity;
                    stockOnHand = cursor.getInt(1);
                    stockOnHand += quantity;

                    productItem.put("STOCK_ON_HAND", stockOnHand);
                    productItem.put("STOCK_IN_TRANSIT", stockInTransit);
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