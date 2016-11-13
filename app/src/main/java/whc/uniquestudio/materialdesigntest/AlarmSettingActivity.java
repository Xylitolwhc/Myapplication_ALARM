package whc.uniquestudio.materialdesigntest;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 吴航辰 on 2016/11/8.
 */

public class AlarmSettingActivity extends AppCompatActivity {
    private int special, music, position;
    private boolean repeat, active;
    private TimePickerDialog timePickerDialog;
    private Calendar CurrentTime;
    private TimePicker timePicker;
    private String MusicPath = null;
    private TextView Music, Special;
    private Calendar calendar = Calendar.getInstance();
    private Intent ReceivedIntent;
    private CardView cardView, cardView2;
    private Switch RepeatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmsettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ReceivedIntent = getIntent();
        Music = (TextView) findViewById(R.id.Music);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });

        if (ReceivedIntent.hasExtra("Edit")) {
            calendar.setTimeInMillis(ReceivedIntent.getLongExtra("Time", 0));
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            Log.d("Calendar", calendar.toString());
            position = ReceivedIntent.getIntExtra("Edit", 0);
            MusicPath = ReceivedIntent.getStringExtra("Music");
            File MusicFile = new File(MusicPath);
            Music.setText(MusicFile.getName());
        }
        Special = (TextView) findViewById(R.id.Special);
        cardView = (CardView) findViewById(R.id.MusicPathViewCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CardView", "Clicked");
                Intent MusicPathIntent = new Intent(Intent.ACTION_GET_CONTENT);
                MusicPathIntent.setType("audio/*");
                MusicPathIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(MusicPathIntent, 1);
            }
        });
        RepeatSwitch = (Switch) findViewById(R.id.RepeatSwitch);
        RepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                errorMessage();
                RepeatSwitch.setChecked(false);
            }
        });
        cardView2 = (CardView) findViewById(R.id.SpecialViewCard);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            MusicPath = uri.getPath();
            File file = new File(MusicPath);
            Music.setText(file.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("完成").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    private void errorMessage() {
        AlertDialog.Builder y_or_n = new AlertDialog.Builder(AlarmSettingActivity.this);
        y_or_n.setTitle("感谢支持！");
        y_or_n.setMessage("本APP暂时还未增加此功能！");
        y_or_n.setCancelable(true);
        y_or_n.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        y_or_n.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d("Pressed", "back");
            showAlart();
        } else if (item.getTitle().toString().equals("完成")) {
            if (MusicPath == null) {
                AlertDialog.Builder y_or_n = new AlertDialog.Builder(AlarmSettingActivity.this);
                y_or_n.setTitle("您尚未选择响铃音乐！");
                y_or_n.setMessage("请选择响铃音乐！");
                y_or_n.setCancelable(true);
                y_or_n.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                y_or_n.show();
            } else {
                Intent returnIntent = new Intent();
                Log.d("Time", calendar.getTimeInMillis() + "");
                returnIntent.putExtra("Time", calendar.getTimeInMillis());
                returnIntent.putExtra("Special", 0);
                returnIntent.putExtra("Music", MusicPath);
                if (ReceivedIntent.hasExtra("Edit")) {
                    returnIntent.putExtra("Position", position);
                    setResult(2, returnIntent);
                } else {
                    setResult(1, returnIntent);
                }
                finish();
            }
        }
        return true;
    }

    private void showAlart() {
        AlertDialog.Builder y_or_n = new AlertDialog.Builder(AlarmSettingActivity.this);
        y_or_n.setTitle("你确定要返回吗？");
        y_or_n.setMessage("你可能会丢失尚未保存的内容！");
        y_or_n.setCancelable(true);
        y_or_n.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(0);
                finish();
            }
        });
        y_or_n.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        y_or_n.show();
    }

    @Override
    public void onBackPressed() {
        showAlart();
    }
}
