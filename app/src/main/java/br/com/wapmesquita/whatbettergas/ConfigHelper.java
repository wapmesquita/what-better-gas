package br.com.wapmesquita.whatbettergas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

/**
 * Created by walter on 14/02/16.
 */
public class ConfigHelper extends SQLiteOpenHelper {

    public ConfigHelper(Context context) {
        super(context, "config.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CONFIG(_ID INTEGER PRIMARY KEY AUTOINCREMENT, VALUE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean set(Properties properties) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS CONFIG");
        db.execSQL("CREATE TABLE CONFIG(_ID INTEGER PRIMARY KEY AUTOINCREMENT, VALUE TEXT);");
        ContentValues value = new ContentValues();
        StringWriter stringWriter = new StringWriter();
        try {
            properties.store(stringWriter, "");
        } catch (IOException e) {
            return false;
        }

        value.put("VALUE", stringWriter.toString());
        try {
            stringWriter.close();
        } catch (IOException e) {
            return false;
        }

        try {
            return db.insert("CONFIG", null, value) > -1;
        } finally {
            db.close();
        }
    }

    public Properties load() {
        Cursor cursor;
        String[] columns = {"_ID", "VAlUE"};
        SQLiteDatabase db = getReadableDatabase();
        try {
            cursor = db.query("CONFIG", columns, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    StringReader reader = new StringReader(cursor.getString(1));
                    Properties prop = new Properties();
                    try {
                        prop.load(reader);
                        reader.close();
                        return prop;
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        } finally {
            db.close();
        }
        return null;
    }
}
