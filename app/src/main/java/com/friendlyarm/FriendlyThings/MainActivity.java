package com.friendlyarm.FriendlyThings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity{

    private static final String TAG = "PowerCtl-MainActivity";
    private final static int GPIO_NUM = 33;
    private Button mButton;
    private SharedPreferences sharedPreferences;

/*3.0V
     ("Pin11",33);
     ("Pin12",50);
     ("Pin15",36);
     ("Pin16",54);
     ("Pin18",55);
     ("Pin22",56);

1.8V
     ("Pin37",96);
     ("Pin38",125);
     ("Pin40",126);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.power_bt);

        if (HardwareControler.exportGPIOPin(GPIO_NUM) != 0) {
            Toast.makeText(this, String.format("exportGPIOPin(%d) failed!", GPIO_NUM),
                    Toast.LENGTH_SHORT).show();
        }
        int currentDirection = HardwareControler.getGPIODirection(GPIO_NUM);

        Log.v(TAG, String.format("currentDirection(%d) == %d", GPIO_NUM, currentDirection));
        if (currentDirection < 0 || currentDirection != GPIOEnum.OUT) {
            if (HardwareControler.setGPIODirection(GPIO_NUM, GPIOEnum.OUT) != 0) {
                Log.e(TAG, String.format("setGPIODirection(%d) to OUT failed", GPIO_NUM));
            } else {
                Log.e(TAG, String.format("setGPIODirection(%d) to OUT OK", GPIO_NUM));
            }
        }

        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(HardwareControler.setGPIOValue(GPIO_NUM, GPIOEnum.LOW) == 0){
                                Log.d(TAG, "Button UP ");
                            }
                        }
                    };
                    handler.postDelayed(runnable, 300);
                    //mButton.setBackgroundResource(R.drawable.power);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(HardwareControler.setGPIOValue(GPIO_NUM, GPIOEnum.HIGH) == 0){
                        Log.d(TAG, "Button DOWN ");
                        //mButton.setBackgroundResource(R.drawable.power_press);
                    }
                }
                return true;
            }
        });

    }
}
