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
    private static final String COLUMN_SYNC_STATUS = "SYNC";

    public DBHelper(@Nullable Context context) {
        super(context, "createdproducts.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + PRODUCT_TABLE + " ( "
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_PRODUCT_NAME + " TEXT, "
                + COLUMN_PRODUCT_PRICE + " REAL, "
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + COLUMN_PRODUCT_IMAGE + " TEXT, "
                + COLUMN_OWNER_ID + " TEXT, "
                + COLUMN_SYNC_STATUS + " INTEGER)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        // Recreate the table
        onCreate(db);
    }

    public boolean addProduct(ProductModel productModel) {
        if (productModel == null) {
            Log.e(TAG, "Product object is null");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, productModel.getProductId());
        values.put(COLUMN_PRODUCT_NAME, productModel.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, String.valueOf(productModel.getProductPrice()));
        values.put(COLUMN_PRODUCT_DESCRIPTION, productModel.getProductDesciption());
        values.put(COLUMN_PRODUCT_IMAGE, productModel.getProductImage());
        values.put(COLUMN_OWNER_ID, productModel.getOwnerId());
        values.put(COLUMN_SYNC_STATUS, productModel.getSyncStatus());

        db.insert(PRODUCT_TABLE, null, values);
        db.close();

        return  true;
    }

    public boolean deleteProduct(ProductModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PRODUCT_TABLE + " WHERE " + COLUMN_ID + " = '" + productModel.getProductId() + "'";
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
                String productId = cursor.getString(0);
                String productName = cursor.getString(1);
                double productPrice = cursor.getDouble(2);
                String productDescription = cursor.getString(3);

                String productImage = cursor.getString(4);
                //Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                String ownerId = cursor.getString(5);

                boolean syncStatus;
                if (cursor.getInt(6) == 1) {
                    syncStatus = true;
                } else {
                    syncStatus = false;
                }
                ProductModel productModel = new ProductModel(productId,productName,productPrice,productDescription,productImage,ownerId, syncStatus);
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
        cv.put(COLUMN_SYNC_STATUS, productModel.getSyncStatus());


        long insert = db.update(PRODUCT_TABLE, cv, "id=?", new String[]{String.valueOf(productModel.getProductId())});
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ProductModel getProductById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_PRODUCT_NAME,
                DBHelper.COLUMN_PRODUCT_PRICE,
                DBHelper.COLUMN_PRODUCT_DESCRIPTION,
                DBHelper.COLUMN_PRODUCT_IMAGE,
                DBHelper.COLUMN_OWNER_ID,
                DBHelper.COLUMN_SYNC_STATUS
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
            String productImage = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_IMAGE));
            String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OWNER_ID));
            boolean syncStatus;
            if (cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SYNC_STATUS)) == 1) {
                syncStatus = true;
            }else {
                syncStatus = false;
            }
            productModel = new ProductModel(productId, productName, productPrice, productDescription, productImage, ownerId, syncStatus);
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
                DBHelper.COLUMN_OWNER_ID,
                DBHelper.COLUMN_SYNC_STATUS
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
                String productId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_NAME));
                double productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_PRICE));
                String productDescription = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_DESCRIPTION));
                String productImage = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_IMAGE));
                //String ownerId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OWNER_ID));
                boolean syncStatus;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SYNC_STATUS)) == 1) {
                    syncStatus = true;
                }else {
                    syncStatus = false;
                }
                productModel = new ProductModel(productId, productName, productPrice, productDescription, productImage, productOwnerId, syncStatus);

                returnList.add(productModel);

            } while (cursor.moveToNext());
        } else {

        }

        cursor.close();
        db.close();

        return returnList;
    }
    //add getUnsyncedProducts method
    public List<ProductModel> getUnsyncedProducts() {
        List<ProductModel> unsyncedProducts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DBHelper.COLUMN_ID,
                DBHelper.COLUMN_PRODUCT_NAME,
                DBHelper.COLUMN_PRODUCT_PRICE,
                DBHelper.COLUMN_PRODUCT_DESCRIPTION,
                DBHelper.COLUMN_PRODUCT_IMAGE,
                DBHelper.COLUMN_OWNER_ID,
                DBHelper.COLUMN_SYNC_STATUS
        };

        String selection = DBHelper.COLUMN_SYNC_STATUS + " = ?";
        String[] selectionArgs = { String.valueOf(0) };

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
                String productId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_NAME));
                double productPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_PRICE));
                String productDescription = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_DESCRIPTION));
                String productImage = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRODUCT_IMAGE));
                String productOwnerId = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OWNER_ID));
                boolean syncStatus;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_SYNC_STATUS)) == 1) {
                    syncStatus = true;
                }else {
                    syncStatus = false;
                }
                productModel = new ProductModel(productId, productName, productPrice, productDescription, productImage, productOwnerId, syncStatus);

                unsyncedProducts.add(productModel);

            } while (cursor.moveToNext());
        } else {

        }

        cursor.close();
        db.close();

        return unsyncedProducts;
    }
}
