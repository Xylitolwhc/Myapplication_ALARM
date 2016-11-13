package whc.uniquestudio.materialdesigntest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 吴航辰 on 2016/11/7.
 */

public class My_Adapter extends RecyclerView.Adapter<My_Adapter.MyViewHolder> {
    private Context context;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private List<Integer> list;
    private onItemClickListener onItemClickListener;
    private onSwitchChangedListener onSwitchChangedListener;
    private int LastData = 0;

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface onSwitchChangedListener {
        void onChanged(View view, int position);
    }


    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void onSwitchChangedListener(onSwitchChangedListener onSwitchChangedListener) {
        this.onSwitchChangedListener = onSwitchChangedListener;
    }

    public My_Adapter(Context context, List<Integer> list) {
        this.context = context;
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context, "Alarm.db", null, 1);
        mySQLiteOpenHelper.getWritableDatabase();
        this.list = list;
        getListData();
    }

    //初始化list列表与id的对应关系
    private void getListData() {
        Cursor cs = mySQLiteOpenHelper.getWritableDatabase().query("Alarm", new String[]{"id"}, null, null, null, null, null, null);
        if (cs.moveToFirst()) {
            do {
                list.add(cs.getInt(cs.getColumnIndex("id")));
                LastData = cs.getInt(cs.getColumnIndex("id"));
            } while (cs.moveToNext());
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm");
        String time = myFmt.format(getIntValue(position, "Time"));
        holder.tv.setText(time);
        if (getIntValue(position, "Activate") == 1) {
            holder.sw.setChecked(true);
        } else {
            holder.sw.setChecked(false);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, layoutPos);
                }
            });
        }
        if (onSwitchChangedListener != null) {
            holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SQLiteDatabase sql = mySQLiteOpenHelper.getWritableDatabase();
                    if (getIntValue(position, "Time") < System.currentTimeMillis()) {
                        long temp = getIntValue(position, "Time");
                        while (temp < System.currentTimeMillis()) {
                            temp += 24 * 60 * 60 * 1000;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Time", temp);
                        sql.update("Alarm", contentValues, "id = ?", new String[]{list.get(position) + ""});
                    }
                    if (holder.sw.isChecked()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Activate", 1);
                        sql.update("Alarm", contentValues, "id = ?", new String[]{list.get(position) + ""});
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Activate", 0);
                        sql.update("Alarm", contentValues, "id = ?", new String[]{list.get(position) + ""});
                    }

                    int layoutPos = holder.getLayoutPosition();
                    onSwitchChangedListener.onChanged(holder.sw, layoutPos);
                }
            });
        }
    }

    public void removeAlarm(int position) {
        mySQLiteOpenHelper.getWritableDatabase().delete("Alarm", "id = ?", new String[]{list.get(position).toString()});
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void NewAlarm(long Time, int Repeat, int Special, String Music) {
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Time", Time);
        values.put("Repeat", Repeat);
        values.put("Special", Special);
        values.put("Music", Music);
        values.put("Activate", 0);
        sqLiteDatabase.insert("Alarm", null, values);
        values.clear();
        list.add(LastData + 1);
        LastData++;
        notifyItemInserted(getItemCount());
    }

    public long getIntValue(int position, String key) {
        int id = list.get(position);
        Cursor cursor = getCursor(id);
        return cursor.getLong(cursor.getColumnIndex(key));
    }

    public String getStringValue(int position, String key) {
        int id = list.get(position);
        Cursor cursor = getCursor(id);
        return cursor.getString(cursor.getColumnIndex(key));
    }

    private Cursor getCursor(int id) {
        SQLiteDatabase sql = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cs = sql.query("Alarm", null, null, null, null, null, null, null);
        if (cs.moveToFirst()) {
            do {
                if ((cs.getInt(cs.getColumnIndex("id")) + "").equals(id + "")) {

                    return cs;
                }
            } while (cs.moveToNext());
        }
        return cs;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        Switch sw;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.CardViewTime);
            sw = (Switch) view.findViewById(R.id.CardViewSwitch);
        }
    }
}