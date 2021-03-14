package com.kiyotaka.jetpackdemo.tencentvideo.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kiyotaka.jetpackdemo.R;
import com.kiyotaka.jetpackdemo.tencentvideo.ITRTCVideoCall;

/**
 * 用于展示历史联系人的界面，目前只有发起通话功能
 *
 * @author guanyifeng
 */
public class TRTCVideoCallHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TRTCVideoCallHistoryActivity";

    private ImageView mStartNewCallImg;
    private TextView mStartNewCallTv;
    private Toolbar mToolbar;

    private ITRTCVideoCall        mITRTCVideoCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_activity_call_history);
        initView();
    }

    /*
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.MICROPHONE, PermissionConstants.CAMERA)
                    .request();
        }
    }

     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mStartNewCallImg = (ImageView) findViewById(R.id.img_start_new_call);
        mStartNewCallTv = (TextView) findViewById(R.id.tv_start_new_call);

        mStartNewCallImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SelectContactActivity.start(TRTCVideoCallHistoryActivity.this);
            }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
