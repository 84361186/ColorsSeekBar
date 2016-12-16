package com.xq.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xq.myapplication.view.GradientColorPicker;

public class MainActivity extends AppCompatActivity {
    private GradientColorPicker colorbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorbar = (GradientColorPicker) findViewById(R.id.colorbar);
        colorbar.setOnColorChangeListener(new GradientColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int color) {
                Toast.makeText(getApplicationContext(),"color:"+color,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
