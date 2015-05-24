package com.changfeng.officesummoner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by changfeng on 2015/5/10.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_RECENT = "recent";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PATH = "path";

    public static final String CREATE_RECENT = "create table " + TABLE_RECENT + "(" +
            COLUMN_ID + " integer primary key autoincrement," +
            COLUMN_TITLE + " text," +
            COLUMN_PATH + " text)";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist " + TABLE_RECENT);
        onCreate(db);
    }

    public long insertFileInfo(FileInfo fileInfo) {
        return getWritableDatabase().insert(TABLE_RECENT, null, getContentValues(fileInfo));
    }

    private ContentValues getContentValues(FileInfo fileInfo) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, fileInfo.getName());
        cv.put(COLUMN_PATH, fileInfo.getPath());
        return cv;
    }

    private List<FileInfo> getFileInfos(Cursor cursor) {
        List<FileInfo> list = new ArrayList<>();
        if (cursor.moveToLast()) {
            do {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                fileInfo.setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)));
                list.add(fileInfo);
            } while (cursor.moveToPrevious());
        }
        return list;
    }
    public List<FileInfo> loadFileInfos() {
        Cursor cursor = getReadableDatabase().query(TABLE_RECENT, null, null,null,null,null, null);
        return getFileInfos(cursor);
    }


    public boolean isExist(String path) {
        String selection = "(" + COLUMN_PATH + "=='" + path + "')";
        Cursor cursor = getReadableDatabase().query(TABLE_RECENT,null,selection,null,null,null,null);
        return cursor.moveToFirst();
    }

}
