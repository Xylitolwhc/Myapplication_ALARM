package whc.uniquestudio.materialdesigntest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 吴航辰 on 2016/11/12.
 */

public class AlarmService extends Service {
    private SQLiteOpenHelper sqLiteOpenHelper = new MySQLiteOpenHelper(this, "Alarm.db", null, 1);
    private ArrayList<Long> arrayList = new ArrayList<Long>();
    private ArrayList<String> MusicPath = new ArrayList<String>();
    private ArrayList<Integer> GetID = new ArrayList<Integer>();
    private int number;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentInfo("AlarmService");
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Alarm");
        builder.setContentText("Alarm is running");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        startForeground(1, notification);

//       sqLiteDatabase=sqLiteOpenHelper.getReadableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new Alarming(), 1000, 1000);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private class Alarming extends TimerTask {
        @Override
        public void run() {
            build();
            for (int i = 0; i < number; i++) {
                if (arrayList.get(i) < System.currentTimeMillis()) {
                    Intent intent = new Intent(getBaseContext(), ClockActivity.class);
                    intent.putExtra("Music", MusicPath.get(i));
                    intent.putExtra("Time", arrayList.get(i));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);

                    //关闭闹钟
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Activate", 0);
                    SQLiteDatabase sql = sqLiteOpenHelper.getWritableDatabase();
                    sql.update("Alarm", contentValues, "id = ?", new String[]{GetID.get(i) + ""});
                    break;
                }
            }
        }
    }

    private void build() {
        number = 0;
        arrayList = new ArrayList<Long>();
        MusicPath = new ArrayList<String>();
        GetID = new ArrayList<Integer>();
        SQLiteDatabase sql = sqLiteOpenHelper.getWritableDatabase();
        Cursor cs = sql.query("Alarm", null, null, null, null, null, null, null);
        if (cs.moveToFirst()) {
            do {
                if (cs.getInt(cs.getColumnIndex("Activate")) == 1) {
                    int id = cs.getInt(cs.getColumnIndex("id"));
                    String Musicpath = cs.getString(cs.getColumnIndex("Music"));
                    long temp = cs.getLong(cs.getColumnIndex("Time"));
//                    if (temp < System.currentTimeMillis()) {
//                        temp = temp + 24 * 60 * 60 * 1000;
//                        ContentValues v = new ContentValues();
//                        int id = cs.getInt(cs.getColumnIndex("id"));
//                        v.put("Time", temp);
//                        sql.update("Alarm", v, "id = ?", new String[]{id + ""});
//                    }
                    arrayList.add(temp);
                    MusicPath.add(Musicpath);
                    GetID.add(id);
                    number++;
                }
            } while (cs.moveToNext());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
