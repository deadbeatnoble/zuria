package com.ex.FG002;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String PRODUCT_TABLE = "PRODUCT_TABLE";
    public static final String COLUMN_PRODUCT_NAME = "PRODUCT_NAME";
    public static final String COLUMN_PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";
    public static final String COLUMN_PRODUCT_IMAGE = "PRODUCT_IMAGE";
    public static final String COLUMN_ID = "ID";
    private static final String COLUMN_OWNER_ID = "OID";

    public DBHelper(@Nullable Context context) {
        super(context, "createdproducts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PRODUCT_TABLE + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PRODUCT_NAME + " TEXT, " + COLUMN_PRODUCT_PRICE + " REAL, " + COLUMN_PRODUCT_DESCRIPTION + " TEXT, " + COLUMN_PRODUCT_IMAGE + " BLOB, "+ COLUMN_OWNER_ID + " TEXT)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addProduct(ProductModel productModel) {
        if (productModel == null) {
            Log.e(TAG, "Product object is null");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, productModel.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, String.valueOf(productModel.getProductPrice()));
        values.put(COLUMN_PRODUCT_DESCRIPTION, productModel.getProductDesciption());
        values.put(COLUMN_PRODUCT_IMAGE, productModel.getProductImage());
        values.put(COLUMN_OWNER_ID, productModel.getOwnerId());

        db.insert(PRODUCT_TABLE, null, values);
        db.close();

        return  true;
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
                double productPrice = cursor.getDouble(2);
                String productDescription = cursor.getString(3);

                byte[] productImage = cursor.getBlob(4);
                //Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                String ownerId = cursor.getString(5);

                ProductModel productModel = new ProductModel(productId,productName,productPrice,productDescription,productImage,ownerId);
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
        cv.put(COLUMN_PRODUCT_IMAGE, productModel.getProductImage());
        cv.put(COLUMN_OWNER_ID, productModel.getOwnerId());


        long insert = db.update(PRODUCT_TABLE, cv, "id=?", new String[]{String.valueOf(productModel.getProductId())});
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ProductModel getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_PRODUCT_NAME,
                DBHelper.COLUMN_PRODUCT_PRICE,
                DBHelper.COLUMN_PRODUCT_DESCRIPTION,
                DBHelper.COLUMN_PRODUCT_IMAGE,
                DBHelper.COLUMN_OWNER_ID
        };

        String selection = DBHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(productId) };

        Cursor cursor = db.query(
                DBHelper.PRODUCT_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ProductModel productModel = null;

        if (cursor.moveToFirst()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_NAME));
            double productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_PRICE));
            String productDescription = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_DESCRIPTION));
            byte[] productImage = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_IMAGE));
            String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OWNER_ID));
            productModel = new ProductModel(productId, productName, productPrice, productDescription, productImage, ownerId);
        }

        cursor.close();
        db.close();

        return productModel;
    }

    public List<ProductModel> getOwnerProduct(String productOwnerId) {
        List<ProductModel> returnList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_PRODUCT_NAME,
                DBHelper.COLUMN_PRODUCT_PRICE,
                DBHelper.COLUMN_PRODUCT_DESCRIPTION,
                DBHelper.COLUMN_PRODUCT_IMAGE,
                DBHelper.COLUMN_OWNER_ID
        };

        String selection = DBHelper.COLUMN_OWNER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(productOwnerId) };

        Cursor cursor = db.query(
                DBHelper.PRODUCT_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ProductModel productModel = null;

        if (cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_NAME));
                double productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_PRICE));
                String productDescription = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_DESCRIPTION));
                byte[] productImage = cursor.getBlob(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_IMAGE));
                //String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OWNER_ID));
                productModel = new ProductModel(productId, productName, productPrice, productDescription, productImage, productOwnerId);

                returnList.add(productModel);

            } while (cursor.moveToNext());
        } else {

        }

        cursor.close();
        db.close();

        return returnList;
    }
}
