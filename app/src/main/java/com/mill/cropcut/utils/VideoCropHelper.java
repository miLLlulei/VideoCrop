package com.mill.cropcut.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;

import com.mill.cropcut.bean.LocalVideoBean;
import com.mill.cropcut.view.VHwCropView;

/**
 * Created by lulei-ms on 2017/8/23.
 */
public class VideoCropHelper {
    public static final float WHA = 2 / 3f; //尺寸裁切成宽高比 2：3

    /**
     * 裁切横屏 视频
     *
     * @param context
     * @param videoBean
     * @param mVCropView
     * @param listener
     */
    public static void cropWpVideo(Context context, LocalVideoBean videoBean, VHwCropView mVCropView, VideoFFCrop.FFListener listener) {
        if (videoBean == null) {
            return;
        }

        String srcVideo = videoBean.src_path;
        int startPo = 0;
        int duration = 0;
        int srcW = videoBean.width;
        int srcH = videoBean.height;
        int width = 0;
        int height = 0;
        int x = 0;
        int y = 0;

        if (srcW <= srcH) {
            return;
        }
        width = (int) (srcH * WHA);
        height = srcH;

        if (mVCropView != null) {
            RectF rectF = mVCropView.getOverlayView().getCropViewRect();
            x = (int) (srcW * rectF.left / mVCropView.getWidth());
            startPo = mVCropView.getVideoView().getStartPo() / 1000;
            duration = mVCropView.getVideoView().getEndPo() / 1000 - startPo;
        } else {
            x = (srcW - width) / 2;
            startPo = 0;
            duration = (int) (videoBean.duration / 1000);
        }
        duration = duration <= 0 ? 1 : duration; //最小为1
        y = 0;

        Log.d(VideoFFCrop.TAG, "Media " + videoBean + "====" + x);

        int start = srcVideo.lastIndexOf(".");
        if (start == -1) {
            start = srcVideo.length();
        }
        String destPath = srcVideo.substring(0, start) + "_wp.mp4";

        VideoFFCrop.getInstance().cropVideo(context, srcVideo, destPath, startPo, duration, width, height, x, y, listener);
    }

    /**
     * 获取本地视频信息
     */
    public static LocalVideoBean getLocalVideoInfo(String path) {
        LocalVideoBean info = new LocalVideoBean();
        info.src_path = path;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            info.duration = (Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            info.width = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            info.height = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        return info;
    }

    /**
     * 获取视频帧列表
     *
     * @param path
     * @param count    期望个数
     * @param width    期望压缩后宽度
     * @param height   期望压缩后高度
     * @param listener
     */
    public static void getLocalVideoBitmap(final String path, final int count, final int width, final int height, final OnBitmapListener listener) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                try {
                    mmr.setDataSource(path);
                    long duration = (Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) * 1000;
                    long inv = (duration / count);

                    for (long i = 0; i < duration; i += inv) {
                        //注意getFrameAtTime方法的timeUs 是微妙， 1us * 1000 * 1000 = 1s
                        Bitmap bitmap = mmr.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST);
//                        Log.d(VideoFFCrop.TAG, "getFrameAtTime "+ i + "===" + bitmap.getWidth() + "===" + bitmap.getHeight());
                        Bitmap destBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        Log.d(VideoFFCrop.TAG, "getFrameAtTime " + i + "===" + destBitmap.getWidth() + "===" + destBitmap.getHeight());
                        bitmap.recycle();

                        publishProgress(destBitmap);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    mmr.release();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                if (listener != null) {
                    listener.onBitmapGet((Bitmap) values[0]);
                }
            }

            @Override
            protected void onPostExecute(Object result) {

            }
        };
        task.execute();
    }

    public interface OnBitmapListener {
        void onBitmapGet(Bitmap bitmap);
    }
}
