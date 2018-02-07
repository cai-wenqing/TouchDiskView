package com.cwq.touchdiskview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cwq.library.TouchDiskView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TouchDiskView mTouchDiskView;
    TextView tv_show;
    Button btn_left, btn_right, btn_switch, btn_picrotate, btn_jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTouchDiskView = findViewById(R.id.main_touchdiskview);
        tv_show = findViewById(R.id.main_tv_count);
        btn_left = findViewById(R.id.main_btn_rotateleft);
        btn_right = findViewById(R.id.main_btn_rotateright);
        btn_switch = findViewById(R.id.main_btn_switch);
        btn_picrotate = findViewById(R.id.main_btn_picrotate);
        btn_jump = findViewById(R.id.main_jump);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
        btn_picrotate.setOnClickListener(this);
        btn_jump.setOnClickListener(this);

        mTouchDiskView.setOnRotate(new TouchDiskView.onRotateListener() {
            @Override
            public void onRotateLeft() {
                tv_show.setText("左转");
            }

            @Override
            public void onRotateRight() {
                tv_show.setText("右转");
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_btn_rotateleft://左转
                mTouchDiskView.rotateLeft();
                break;
            case R.id.main_btn_rotateright://右转
                mTouchDiskView.rotateRight();
                break;
            case R.id.main_btn_switch://禁止、开启触摸
                if (mTouchDiskView.isTouch()) {
                    mTouchDiskView.setTouch(false);
                    btn_switch.setText("已关闭触摸");
                } else {
                    mTouchDiskView.setTouch(true);
                    btn_switch.setText("已开启触摸");
                }
                break;
            case R.id.main_btn_picrotate://开启、关闭图片旋转
                if (mTouchDiskView.isPictureRotate()) {
                    mTouchDiskView.setPictureRotate(false);
                    btn_picrotate.setText("图片旋转已关");
                } else {
                    mTouchDiskView.setPictureRotate(true);
                    btn_picrotate.setText("图片旋转已开");
                }
                break;
            case R.id.main_jump://跳转USB打印测试页面
                startActivity(new Intent(this, USBTestActivity.class));
                break;
        }
    }
}
