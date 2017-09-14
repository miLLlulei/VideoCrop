package com.mill.cropcut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mill.cropcut.bean.LocalVideoBean;
import com.mill.cropcut.utils.VideoCropHelper;
import com.mill.cropcut.utils.VideoFFCrop;
import com.mill.cropcut.view.VDurationCutView;
import com.mill.cropcut.view.VHwCropView;

public class VideoCropPreActivity extends Activity implements View.OnClickListener, VDurationCutView.IOnRangeChangeListener {
    final String srcVideo = "/mnt/sdcard/testvideo.mp4";
    final String destPath = "/mnt/sdcard/outtestvideo.mp4";

    private VHwCropView mVCropView;
    private VDurationCutView mCutView;
    private TextView mCropBtn;

    private LocalVideoBean mLocalVideoInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoFFCrop.getInstance().init(this);
        setContentView(R.layout.video_crop);

        mVCropView = (VHwCropView) findViewById(R.id.crop_view);
        mCutView = (VDurationCutView) findViewById(R.id.cut_view);
        mCropBtn = (TextView) findViewById(R.id.tv_ok);

        mCropBtn.setOnClickListener(this);

        mCutView.setRangeChangeListener(this);

        mLocalVideoInfo = VideoCropHelper.getLocalVideoInfo(srcVideo);
        mVCropView.getVideoView().setLocalPath(srcVideo, (int) mLocalVideoInfo.duration);
        mCutView.setMediaFileInfo(mLocalVideoInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVCropView.getVideoView().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVCropView.getVideoView().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVCropView.getVideoView().stopPlayback();
    }

    public void onClick(View v) {
        Log.d(VideoFFCrop.TAG, "mVideoView " + mVCropView.getVideoView().getWidth() + "===" + mVCropView.getVideoView().getHeight());
        VideoCropHelper.cropWpVideo(VideoCropPreActivity.this, mLocalVideoInfo, mVCropView, new VideoFFCrop.FFListener() {

            public void onProgress(Integer progress) {
//                Log.d("VcActivity", "progress: " + progress);
                mCropBtn.setText("progress: " + progress);
            }

            public void onFinish() {
                Log.d("VcActivity", "finished");
                mCropBtn.setText("finished");
            }

            public void onFail(String msg) {
                Log.d("VcActivity", "failed");
                mCropBtn.setText("failed");
            }
        });
    }

    @Override
    public void onKeyDown() {

    }

    @Override
    public void onKeyUp(int startTime, int endTime) {
        mVCropView.setStarEndPo(startTime, endTime);
    }
}
