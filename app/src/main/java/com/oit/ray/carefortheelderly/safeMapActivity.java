package com.oit.ray.carefortheelderly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class safeMapActivity extends AppCompatActivity {


    SeekBar sB_radius;
    TextView tv_radius;
    int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_map);


        sB_radius = (SeekBar)findViewById(R.id.seekBar_radius);
        tv_radius = (TextView)findViewById(R.id.tv_radius);
        sB_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               radius = progress;
                tv_radius.setText(String.valueOf(radius)+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
