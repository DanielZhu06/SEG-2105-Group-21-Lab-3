package com.example.lab3databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_cmd = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + "INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " DOUBLE " + ")";

        db.execSQL(create_table_cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null); // returns "cursor" all products from the table
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, product.getProductPrice());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteProduct(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                COLUMN_PRODUCT_NAME + " = ? AND " + COLUMN_PRODUCT_PRICE + " = ?",
                new String[]{name, String.valueOf(price)});
        db.close();
    }

    public Product findProduct(String name, double price) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query;
        String[] selectionArgs;

        if (!name.isEmpty() && price >= 0) {
            // Search by both name and price
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " = ? AND " + COLUMN_PRODUCT_PRICE + " = ?";
            selectionArgs = new String[]{name, String.valueOf(price)};
        } else if (!name.isEmpty()) {
            // Search by name only
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " = ?";
            selectionArgs = new String[]{name};
        } else if (price >= 0) {
            // Search by price only
            query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_PRICE + " = ?";
            selectionArgs = new String[]{String.valueOf(price)};
        } else {
            // No search criteria provided
            db.close();
            return null;
        }

        Cursor cursor = db.rawQuery(query, selectionArgs);
        Product product = new Product();

        if (cursor.moveToFirst()) {
            product.setId(cursor.getInt(0));
            product.setProductName(cursor.getString(1));
            product.setProductPrice(cursor.getDouble(2));
            cursor.close();
            db.close();
            return product;
        } else {
            cursor.close();
            db.close();
            return null; // Product not found
        }
    }
}
