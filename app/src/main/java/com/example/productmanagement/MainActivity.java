package com.example.productmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity {

    private String url = "https://christmas.free.beeceptor.com/productmanagement/syncdb.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent(MainActivity.this,
                            ReceivingStocksActivity.class);
                    startActivity(intent);
                }
                else if (position == 1){
                    Intent intent = new Intent(MainActivity.this,
                            OrderingStocksActivity.class);
                    startActivity(intent);
                }
            }
        };

        ListView listView = (ListView) findViewById(R.id.list_options);
        listView.setOnItemClickListener(itemClickListener);
    }

    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> dirtyProductsList;
        dirtyProductsList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM PRODUCT where DIRTY = 1";
        SQLiteOpenHelper productDatabaseHelper = new ProductDatabaseHelper(MainActivity.this);
        SQLiteDatabase database = productDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", cursor.getString(0));
                map.put("STOCK_ON_HAND", cursor.getString(2));
                map.put("STOCK_IN_TRANSIT", cursor.getString(3));
                dirtyProductsList.add(map);
                cursor.moveToNext();
            }


        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(dirtyProductsList);
    }

    public void onSync(View view) {
        Toast.makeText(MainActivity.this, "Attempting to sync...", Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("productsJSON", composeJSONfromSQLite());
        client.post(url, params,new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                clearDirtyBits();
                String byteToString = null;
                try {
                    byteToString = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Successful response: " + byteToString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void clearDirtyBits(){
        new SetDirtyBitsTask().execute();
    }

    private class SetDirtyBitsTask extends AsyncTask<Integer, Void, Boolean> {
        private ContentValues productItem;

        protected void onPreExecute() {
            productItem = new ContentValues();
            productItem.put("DIRTY", false);
        }

        protected Boolean doInBackground(Integer... products) {
            SQLiteOpenHelper productDatabaseHelper =
                    new ProductDatabaseHelper(MainActivity.this);
            try {
                SQLiteDatabase db = productDatabaseHelper.getWritableDatabase();

                db.update("PRODUCT", productItem,
                        "DIRTY = ?", new String[] {Integer.toString(1)});
                db.close();
                return true;
            } catch(SQLiteException e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}