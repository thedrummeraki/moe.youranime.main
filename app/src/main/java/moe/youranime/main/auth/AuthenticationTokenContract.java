package moe.youranime.main.auth;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akinyele on 17-04-22.
 */

public final class AuthenticationTokenContract {

    private AuthenticationTokenContract() {}

    private static class AuthenticationToken implements BaseColumns {
        private static final String TABLE_NAME = "auth_token";
        private static final String COLUMN_NAME_TITLE = "token";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + AuthenticationToken.TABLE_NAME + " (" +
                        AuthenticationToken.COLUMN_NAME_TITLE + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AuthenticationToken.TABLE_NAME;
    }

    private static class AuthenticationTokenDBHelper extends SQLiteOpenHelper {
        // Increment value below should the schema change
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "auth_token.db";

        AuthenticationTokenDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AuthenticationToken.SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(AuthenticationToken.SQL_DELETE_ENTRIES);
            this.onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            this.onUpgrade(db, oldVersion, newVersion);
        }
    }

    private boolean saveToken(Context context, String token) {
        System.out.println("Saving token " + token + "...");
        AuthenticationTokenDBHelper dbHelper = new AuthenticationTokenDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AuthenticationToken.COLUMN_NAME_TITLE, token);

        long newId = db.insert(AuthenticationToken.TABLE_NAME, null, values);
        if (newId < 0) {
            System.err.println("Could not save the token " + token);
        }
        db.close();
        return newId >= 0;
    }

    private String getToken(Context context) {
        AuthenticationTokenDBHelper dbHelper = new AuthenticationTokenDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + AuthenticationToken.TABLE_NAME, null);

        List<String> foundTokens = new ArrayList<>();
        while(cursor.moveToNext()) {
            String ft = cursor.getString(cursor.getColumnIndex(AuthenticationToken.COLUMN_NAME_TITLE));
            foundTokens.add(ft);
        }
        cursor.close();
        if (foundTokens.size() == 0) {
            return null;
        }
        db.close();
        return foundTokens.get(foundTokens.size() - 1);
    }

    private boolean deleteTokens(Context context) {
        AuthenticationTokenDBHelper dbHelper = new AuthenticationTokenDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(AuthenticationToken.TABLE_NAME, null, null);
        db.close();
        return true;
    }

    private static String CURRENT_TOKEN;
    private static AuthenticationTokenContract INSTANCE;

    private static AuthenticationTokenContract getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthenticationTokenContract();
        }
        return INSTANCE;
    }

    public static boolean save(Context context, String text) {
        CURRENT_TOKEN = text;
        return getInstance().saveToken(context, text);
    }

    public static String get(Context context) {
        return getInstance().getToken(context);
    }

    public static boolean delete(Context context) {
        return getInstance().deleteTokens(context);
    }

    public static boolean exists(Context context) {
        return exists(context, false);
    }

    public static boolean exists(Context context, boolean fetch) {
        String token = get(context);
        boolean exists = token != null && !token.trim().isEmpty();
        if (fetch && exists) {
            setCurrentToken(token);
        }
        return exists;
    }

    private static void setCurrentToken(String currentToken) {
        CURRENT_TOKEN = currentToken;
    }

    public static String getCurrentToken() {
        return CURRENT_TOKEN;
    }
}

