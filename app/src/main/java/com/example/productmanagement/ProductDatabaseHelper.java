package com.example.productmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "christmas_items";
    private static final int DB_VERSION = 1;

    ProductDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        updateMyDatabase(db,0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private static void insertProduct(SQLiteDatabase db, String name, int stock_hand,
                                      int stock_transit, double price, int reorder_quantity,
                                      double reorder_amount){

        ContentValues productValues = new ContentValues();
        productValues.put("NAME", name);
        productValues.put("STOCK_ON_HAND", stock_hand);
        productValues.put("STOCK_IN_TRANSIT", stock_transit);
        productValues.put("PRICE", price);
        productValues.put("REORDER_QUANTITY", reorder_quantity);
        productValues.put("REORDER_AMOUNT", reorder_amount);
        db.insert("PRODUCT", null, productValues);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 1){
            db.execSQL("CREATE TABLE PRODUCT (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "NAME TEXT, "
                    + "STOCK_ON_HAND INTEGER, "
                    + "STOCK_IN_TRANSIT INTEGER,"
                    + "PRICE REAL, "
                    + "REORDER_QUANTITY INTEGER,"
                    + "REORDER_AMOUNT REAL);");

            insertProduct(db,"7.5 Foot Artificial Christmas Tree", 43, 0,
                    120.99, 50, 95.99);
            insertProduct(db,"6 Foot Christmas Tree", 200, 0,
                    79.99, 150, 65.99);
            insertProduct(db, " Christmas Inflatable Snowman and Penguins with Led Lights",
                    12, 20, 129.99, 20, 125.99);
            insertProduct(db, "100-Count Clear Green Wire Christmas Lights Set",326,
                    200, 15.99, 200, 12.99);
            insertProduct(db, "300 LED Christmas Tree Lights Outdoor Waterproof", 147,
                    0, 29.99, 300, 26.99);
            insertProduct(db, "Christmas Tree Ornaments", 357, 200,
                    19.99, 800, 15.99);
        }

        /*if (oldVersion < 2){
            // updates the stock quantities
        }*/

    }
}
