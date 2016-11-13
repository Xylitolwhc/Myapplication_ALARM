package whc.uniquestudio.materialdesigntest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 吴航辰 on 2016/11/8.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_SQL = "create table Alarm("
            + "id integer primary key autoincrement,"
            + "Time Integer, "
            + "Repeat int, "
            + "Special int, "
            + "Activate int, "
            + "Music text, "
            + "Name text)";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
