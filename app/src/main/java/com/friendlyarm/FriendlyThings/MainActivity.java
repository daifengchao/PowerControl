package com.friendlyarm.FriendlyThings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity{

    private static final String TAG = "###DFC###";
    private final static int GPIO_NUM = 33;
    private Button mButton;
    private TextView mTextView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final long POWEROFF_DELAY_TIME = 3000L;
    private static final long POWERON_DELAY_TIME = 500L;

    private long TOUCH_TIME = 0;
    private boolean isOFF = true;

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
        mTextView = findViewById(R.id.power_tv);
        sharedPreferences = getSharedPreferences("privateDate", MODE_PRIVATE);
        isOFF = sharedPreferences.getBoolean("isOFF", true);

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

        if(isOFF){
            mButton.setTag(R.string.poweron);
            mTextView.setText(R.string.poweron);
            mButton.setBackgroundResource(R.drawable.poweron);
        }else{
            mButton.setTag(R.string.poweroff);
            mTextView.setText(R.string.poweroff);
            mButton.setBackgroundResource(R.drawable.poweroff);
        }
        mButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Long time =  System.currentTimeMillis() - TOUCH_TIME;
                    Log.d(TAG, "Button UP :" + System.currentTimeMillis() + "   time= " + time);
                    Toast.makeText(MainActivity.this, time+"", Toast.LENGTH_SHORT).show();
                    if (TOUCH_TIME != 0) {
                        if (HardwareControler.setGPIOValue(GPIO_NUM, GPIOEnum.LOW) != 0) {
                            Log.e(TAG, "Set GPIO" + GPIO_NUM + " low failed!");
                        } else {
                            if (isOFF) { //关机状态
                                if (System.currentTimeMillis() - TOUCH_TIME >= POWERON_DELAY_TIME) {   //开机
                                    Log.d(TAG, "Button UP");
                                    mButton.setTag(R.string.poweroff);
                                    mTextView.setText(R.string.poweroff);
                                    mButton.setBackgroundResource(R.drawable.poweroff);
                                    isOFF = false;
                                    editor = sharedPreferences.edit();
                                    editor.putBoolean("isOFF", isOFF);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, "开机...", Toast.LENGTH_LONG).show();

                                } else {
                                    mButton.setTag(R.string.poweron);
                                    mTextView.setText(R.string.poweron);
                                    mButton.setBackgroundResource(R.drawable.poweron);
                                }
                            } else { //开机状态
                                if (System.currentTimeMillis() - TOUCH_TIME >= POWEROFF_DELAY_TIME) {   //关机
                                    mButton.setTag(R.string.poweron);
                                    mTextView.setText(R.string.poweron);
                                    mButton.setBackgroundResource(R.drawable.poweron);
                                    isOFF = true;
                                    editor = sharedPreferences.edit();
                                    editor.putBoolean("isOFF", isOFF);
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, "关机...", Toast.LENGTH_LONG).show();
                                    //mButton.setBackground(getResources().getDrawable(R.drawable.poweron));
                                } else {
                                    mButton.setTag(R.string.poweroff);
                                    mTextView.setText(R.string.poweroff);
                                    mButton.setBackgroundResource(R.drawable.poweroff);
                                    //mButton.setBackground(getResources().getDrawable(R.drawable.poweroff));
                                }
                            }
                        }
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (HardwareControler.setGPIOValue(GPIO_NUM, GPIOEnum.HIGH) != 0) {
                        Log.e(TAG, "Set GPIO" + GPIO_NUM + " high failed!");
                        TOUCH_TIME = 0;
                    } else {
                        TOUCH_TIME = System.currentTimeMillis();
                    }
                    Log.d(TAG, "Button DOWN : " + TOUCH_TIME);
                }

                return true;
            }
        });

        /*mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mButton.getTag().equals(R.string.poweron)){
                    if (HardwareControler.setGPIOValue(GPIO_NUM,GPIOEnum.HIGH) == 0) {
                        mButton.setTag(R.string.poweroff);
                        mTextView.setText(R.string.poweroff);
                        mButton.setBackground(getResources().getDrawable(R.drawable.poweroff));
                        Log.e(TAG, "Set power off succeed!");
                    } else {
                        Log.e(TAG, "Set power off failed!");
                    }
                }else if(mButton.getTag().equals(R.string.poweroff)){
                    if (HardwareControler.setGPIOValue(GPIO_NUM,GPIOEnum.LOW) == 0) {
                        mButton.setTag(R.string.poweron);
                        mTextView.setText(R.string.poweron);
                        mButton.setBackground(getResources().getDrawable(R.drawable.poweron));
                        Log.e(TAG, "Set power on succeed!");
                    } else {
                        Log.e(TAG, "Set power on failed!");
                    }
                }
            }
        });*/

    }
}
