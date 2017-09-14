package com.mill.cropcut;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mill.cropcut.utils.VideoFFCrop;

public class VcActivity extends Activity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        VideoFFCrop.getInstance().init(this);


        ((Button) findViewById(R.id.runbtn)).setOnClickListener(this);
    }

    public void onClick(View v) {
        final String srcVideo = ((EditText) findViewById(R.id.editText1)).getText().toString();
        final String srcAudio = ((EditText) findViewById(R.id.editText2)).getText().toString();
        final String destPath = ((EditText) findViewById(R.id.editText3)).getText().toString();
        final int start = Integer.valueOf(((EditText) findViewById(R.id.editText4)).getText().toString());
        final int end = Integer.valueOf(((EditText) findViewById(R.id.editText5)).getText().toString());


        final String tempPath = "/mnt/sdcard/temp.mp4";


        int duration = 0;
        int srcW = 0;
        int srcH = 0;
        int width = 0;
        int height = 0;
        int x = 0;
        int y = 0;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            mmr.setDataSource(srcVideo);
            duration = (int) (Long.valueOf(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000);
            srcW = Integer.valueOf(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            srcH = Integer.valueOf(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

            Log.d(VideoFFCrop.TAG, "MediaMetadataRetriever " + srcVideo + "===" + duration + "===" + srcW + "====" + srcH);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        if (srcW <= srcH) {
            return;
        }
        width = srcH * 2 / 3;
        height = srcH;
        x = (srcW - width) / 2;
        y = 0;


        VideoFFCrop.getInstance().cropVideo(VcActivity.this, srcVideo, destPath, 0, duration, width, height, x, y, new VideoFFCrop.FFListener() {

            public void onProgress(Integer progress) {
//                Log.d("VcActivity", "progress: " + progress);
                ((TextView) findViewById(R.id.textView6)).setText("progress: " + progress);
            }

            public void onFinish() {
                Log.d("VcActivity", "finished");
                ((TextView) findViewById(R.id.textView6)).setText("finished");
            }

            public void onFail(String msg) {
                Log.d("VcActivity", "failed");
                ((TextView) findViewById(R.id.textView6)).setText("failed");
            }
        });
    }

}
