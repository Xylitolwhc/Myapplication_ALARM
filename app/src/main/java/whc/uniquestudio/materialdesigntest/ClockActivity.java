package whc.uniquestudio.materialdesigntest;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by 吴航辰 on 2016/11/8.
 */

public class ClockActivity extends Activity {
    private IntentFilter intentFilter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private String MusicPath;
    private Long time;
    private TextView AlarmingTime;
    private Button button;
    private int ClickedTimes = 0;
    private Toast TimeToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.clocklayout);
        Intent intent = getIntent();
        MusicPath = intent.getStringExtra("Music");
        time = intent.getLongExtra("Time", 0);
        AlarmingTime = (TextView) findViewById(R.id.AlarmingTime);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm");
        String T = myFmt.format(time);
        AlarmingTime.setText(T);

        button = (Button) findViewById(R.id.AlarmingButton);

        //获取电源管理器对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //得到键盘锁管理器对象
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();

        try {
            mediaPlayer.setDataSource(MusicPath);
            mediaPlayer.prepare();
            Log.d("Music", "isPlaying");
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mediaPlayer.setVolume(0, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        button.setText("请点击五次来取消闹铃");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickedTimes++;
                if (ClickedTimes == 5) {
                    finish();
                }
            }
        });
        Log.d("Clock", "Receive");
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }
}
