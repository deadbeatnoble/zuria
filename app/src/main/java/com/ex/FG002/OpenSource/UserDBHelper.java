package com.ex.FG002.OpenSource;

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

public class UserDBHelper extends SQLiteOpenHelper {
    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_USER_MOBILE_NUMBER = "USER_NUMBER";
    public static final String COLUMN_STORE_IMAGE = "STORE_IMAGE";
    public static final String COLUMN_STORE_NAME = "STORE_NAME";
    public static final String COLUMN_USER_PASSWORD = "USER_PASSWORD";
    public static final String COLUMN_STORE_LATITUDE = "STORE_LATITUDE";
    public static final String COLUMN_STORE_LONGITUDE = "STORE_LONGITUDE";
    public static final String COLUMN_USER_LOCATION_SHARE = "USER_LOCATION_SHARE";
    public static final String COLUMN_USER_LOCATION_SYNC_STATUS = "USER_LOCATION_SYNC_STATUS";

    public UserDBHelper(@Nullable Context context) {
        super(context, "createdusers.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + USER_TABLE + " ( "
                + COLUMN_USER_MOBILE_NUMBER + " TEXT PRIMARY KEY, "
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_STORE_IMAGE + " TEXT, "
                + COLUMN_STORE_NAME + " TEXT, "
                + COLUMN_STORE_LATITUDE + " TEXT, "
                + COLUMN_STORE_LONGITUDE + " TEXT, "
                + COLUMN_USER_LOCATION_SHARE + " INTEGER, "
                + COLUMN_USER_LOCATION_SYNC_STATUS + " INTEGER)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);

        onCreate(db);
    }

    public boolean addUserData(Users users) {
        if (users == null) {
            Log.e(TAG, "User data is null");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_MOBILE_NUMBER, users.getMobileNumber());
        cv.put(COLUMN_USER_PASSWORD, users.getPassword());
        cv.put(COLUMN_STORE_IMAGE, users.getStoreImage());
        cv.put(COLUMN_STORE_NAME, users.getStoreName());
        cv.put(COLUMN_STORE_LATITUDE, users.getStoreLatitude());
        cv.put(COLUMN_STORE_LONGITUDE, users.getStoreLongitude());
        cv.put(COLUMN_USER_LOCATION_SHARE, users.isShareUserLocation());
        cv.put(COLUMN_USER_LOCATION_SYNC_STATUS, users.isLocationSyncStatus());

        db.insert(USER_TABLE, null, cv);
        db.close();

        return true;
    }

    public boolean checkUserExists(String mobileNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_USER_MOBILE_NUMBER};
        String selection = COLUMN_USER_MOBILE_NUMBER + " = ?";
        String[] selectionArgs = {mobileNumber};

        Cursor cursor = db.query(
                USER_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean recordExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return recordExists;
    }

    public Users getUserData(String userMobileNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                UserDBHelper.COLUMN_USER_MOBILE_NUMBER,
                UserDBHelper.COLUMN_USER_PASSWORD,
                UserDBHelper.COLUMN_STORE_IMAGE,
                UserDBHelper.COLUMN_STORE_NAME,
                UserDBHelper.COLUMN_STORE_LATITUDE,
                UserDBHelper.COLUMN_STORE_LONGITUDE,
                UserDBHelper.COLUMN_USER_LOCATION_SHARE,
                UserDBHelper.COLUMN_USER_LOCATION_SYNC_STATUS
        };

        String selection = UserDBHelper.COLUMN_USER_MOBILE_NUMBER + " = ?";
        String[] selectionArgs = { String.valueOf(userMobileNumber)};


        Cursor cursor = db.query(
                UserDBHelper.USER_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Users user = null;

        if (cursor.moveToFirst()) {
            do {

                String userPassword = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_PASSWORD));
                String storeImage = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_IMAGE));
                String storeName = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_NAME));
                String storeLatitude = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_LATITUDE));
                String storeLongitude = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_LONGITUDE));
                boolean shareUserLocation;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_LOCATION_SHARE)) == 1) {
                    shareUserLocation = true;
                }else {
                    shareUserLocation = false;
                }
                boolean locationSyncStatus;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_LOCATION_SYNC_STATUS)) == 1) {
                    locationSyncStatus = true;
                }else {
                    locationSyncStatus = false;
                }

                user = new Users(userMobileNumber, userPassword, storeImage, storeName, storeLatitude, storeLongitude, shareUserLocation, locationSyncStatus);

            } while (cursor.moveToNext());
        } else {

        }

        cursor.close();
        db.close();

        return user;
    }

    public boolean setUserLocation(String userMobileNumber, String latitude, String longitude, boolean shareLocation, boolean locationSyncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_STORE_LATITUDE, latitude);
        cv.put(COLUMN_STORE_LONGITUDE, longitude);
        cv.put(COLUMN_USER_LOCATION_SHARE, shareLocation ? 1 : 0);
        cv.put(COLUMN_USER_LOCATION_SYNC_STATUS, locationSyncStatus ? 1 : 0);

        long setLocation = db.update(USER_TABLE, cv, " USER_NUMBER=? ", new String[]{userMobileNumber});
        if (setLocation == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<Users> getUnsyncedUserData() {
        List<Users> unsyncedUsers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                UserDBHelper.COLUMN_USER_MOBILE_NUMBER,
                UserDBHelper.COLUMN_USER_PASSWORD,
                UserDBHelper.COLUMN_STORE_IMAGE,
                UserDBHelper.COLUMN_STORE_NAME,
                UserDBHelper.COLUMN_STORE_LATITUDE,
                UserDBHelper.COLUMN_STORE_LONGITUDE,
                UserDBHelper.COLUMN_USER_LOCATION_SHARE,
                UserDBHelper.COLUMN_USER_LOCATION_SYNC_STATUS,
        };

        String selection = UserDBHelper.COLUMN_USER_LOCATION_SYNC_STATUS + " = ?";
        String[] selectionArgs = { String.valueOf(0) };

        Cursor cursor = db.query(
                UserDBHelper.USER_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Users user = null;

        if (cursor.moveToFirst()) {
            do {
                String mobilenumber = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_MOBILE_NUMBER));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_PASSWORD));
                String storeImage = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_IMAGE));
                String storeName = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_NAME));
                String latitude = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_STORE_LONGITUDE));

                boolean locationShareStauts;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_LOCATION_SHARE)) == 1) {
                    locationShareStauts = true;
                }else {
                    locationShareStauts = false;
                }
                boolean syncStatus;
                if (cursor.getInt(cursor.getColumnIndexOrThrow(UserDBHelper.COLUMN_USER_LOCATION_SYNC_STATUS)) == 1) {
                    syncStatus = true;
                }else {
                    syncStatus = false;
                }

                user = new Users(mobilenumber, password, storeImage, storeName, latitude, longitude, locationShareStauts, syncStatus);

                unsyncedUsers.add(user);

            } while (cursor.moveToNext());
        } else {

        }

        cursor.close();
        db.close();

        return unsyncedUsers;

    }

    public boolean setUserSyncStatus(String mobileNumber, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_LOCATION_SYNC_STATUS, status ? 1 : 0);

        long success = db.update(USER_TABLE, cv, "USER_NUMBER=?", new String[]{mobileNumber});

        if (success == -1) {
            return false;
        } else {
            return true;
        }
    }
}
