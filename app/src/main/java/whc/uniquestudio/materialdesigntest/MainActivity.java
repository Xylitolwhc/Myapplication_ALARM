package whc.uniquestudio.materialdesigntest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button bt_1;
    private CardView cd;
    private RecyclerView rv;
    private List<Integer> mDatas = new ArrayList<>();
    private My_Adapter my_adapter;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent StartServiceIntent = new Intent(MainActivity.this, AlarmService.class);
        startService(StartServiceIntent);
        rv = (RecyclerView) findViewById(R.id.RecyclerV);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(my_adapter = new My_Adapter(this, mDatas));

        my_adapter.setOnItemClickListener(new My_Adapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("Click", "short" + position);
                Intent intentToEdit = new Intent(MainActivity.this, AlarmSettingActivity.class);
                intentToEdit.putExtra("Edit", position);
                intentToEdit.putExtra("Time", my_adapter.getIntValue(position, "Time"));
                intentToEdit.putExtra("Repeat", my_adapter.getIntValue(position, "Repeat"));
                intentToEdit.putExtra("Music", my_adapter.getStringValue(position, "Music"));
                startActivityForResult(intentToEdit, 1);
            }
        });
        my_adapter.onSwitchChangedListener(new My_Adapter.onSwitchChangedListener() {
            @Override
            public void onChanged(View view, int position) {
                Log.d("item", "" + position);
            }
        });
        itemTouchHelper = new ItemTouchHelper(new MyItemTouchHelperCallback(mDatas, my_adapter));
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            if (data.hasExtra("Time")) {
                long AlarmTime = data.getLongExtra("Time", 0);
                int Special = data.getIntExtra("special", 0);
                int Repeat = 0;
                String Music = data.getStringExtra("Music");
                Log.d("Time", AlarmTime + "");
                Log.d("Special", Special + "");
                Log.d("Music", Music + "");
                my_adapter.NewAlarm(AlarmTime, Repeat, Special, Music);
            }
        } else if (resultCode == 2) {
            if (data.hasExtra("Position")) {
                int position = data.getIntExtra("Position", 0);
                long AlarmTime = data.getLongExtra("Time", 0);
                int Special = data.getIntExtra("special", 0);
                int Repeat = 0;
                String Music = data.getStringExtra("Music");
                Log.d("Time", AlarmTime + "");
                Log.d("Special", Special + "");
                Log.d("Music", Music + "");
                my_adapter.removeAlarm(position);
                my_adapter.NewAlarm(AlarmTime, Repeat, Special, Music);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("添加");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        my_adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "添加": {
                Intent add = new Intent(MainActivity.this, AlarmSettingActivity.class);
                startActivityForResult(add, 0);
                break;
            }
        }
        return true;
    }
}
