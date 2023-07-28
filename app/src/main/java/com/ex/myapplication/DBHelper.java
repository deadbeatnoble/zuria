package com.ex.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String PRODUCT_TABLE = "PRODUCT_TABLE";
    public static final String COLUMN_PRODUCT_NAME = "PRODUCT_NAME";
    public static final String COLUMN_PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";
    public static final String COLUMN_ID = "ID";
    private static final String COLUMN_OWNER_ID = "OID";

    public DBHelper(@Nullable Context context) {
        super(context, "createdproducts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PRODUCT_TABLE + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PRODUCT_NAME + " TEXT, " + COLUMN_PRODUCT_PRICE + " INTEGER, " + COLUMN_PRODUCT_DESCRIPTION + " TEXT, " + COLUMN_OWNER_ID + " TEXT)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addProduct(ProductModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_NAME, productModel.getProductName());
        cv.put(COLUMN_PRODUCT_PRICE, productModel.getProductPrice());
        cv.put(COLUMN_PRODUCT_DESCRIPTION, productModel.getProductDesciption());
        cv.put(COLUMN_OWNER_ID, productModel.getOwnerId());

        long insert = db.insert(PRODUCT_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteProduct(ProductModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PRODUCT_TABLE + " WHERE " + COLUMN_ID + " = " + productModel.getProductId();
        Cursor cursor = db.rawQuery(deleteQuery, null);

        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public List<ProductModel> getProducts() {
        List<ProductModel> returnList = new ArrayList<>();

        String sqlQuery = "SELECT * FROM " + PRODUCT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(sqlQuery,null);

        if (cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(0);
                String productName = cursor.getString(1);
                int productPrice = cursor.getInt(2);
                String productDescription = cursor.getString(3);
                String ownerId = cursor.getString(4);

                ProductModel productModel = new ProductModel(productId,productName,productPrice,productDescription, ownerId);
                returnList.add(productModel);

            } while(cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean updateProduct(ProductModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        cv.put(COLUMN_PRODUCT_NAME, productModel.getProductName());
        cv.put(COLUMN_PRODUCT_PRICE, productModel.getProductPrice());
        cv.put(COLUMN_PRODUCT_DESCRIPTION, productModel.getProductDesciption());
        cv.put(COLUMN_OWNER_ID, productModel.getOwnerId());


        long insert = db.update(PRODUCT_TABLE, cv, "id=?", new String[]{String.valueOf(productModel.getProductId())});
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }
}
