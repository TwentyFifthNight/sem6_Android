package com.example.aplikacja4;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final SurfaceHolder mHolder;

    private Thread mDrawingThread;

    private boolean isThreadRunning = false;

    private final Object mLock = new Object();

    private static Bitmap bitmap;
    private Canvas mCanvas = null;

    public DrawingSurface(Context context, AttributeSet attrs){
        super(context,attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        setFocusable(true);
        if(lastRotation == -1){
            initRotation();
        }
    }

    public void resumeDrawing(){
        if(mDrawingThread == null) {
            mDrawingThread = new Thread(this);
            mDrawingThread.start();
        }
        isThreadRunning = true;
    }

    public void pauseDrawing(){
        if(isThreadRunning) {
            isThreadRunning = false;
            try {
                mDrawingThread.join();
            } catch (InterruptedException ignored) {

            } finally {
                mDrawingThread = null;
            }
        }
    }

    private final Path mPath;
    private final Paint mPaint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        synchronized (mLock){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mCanvas.drawCircle(event.getX(),event.getY(),5,mPaint);
                    mPath.moveTo(event.getX(),event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    mPath.lineTo(event.getX(),event.getY());
                    mCanvas.drawPath(mPath,mPaint);
                    break;
                case MotionEvent.ACTION_UP:
                    mCanvas.drawCircle(event.getX(),event.getY(),5,mPaint);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void run() {
        while (isThreadRunning){
            Canvas canvas = null;
            try {
                synchronized (mHolder){
                    if(!mHolder.getSurface().isValid())
                        continue;
                }
                canvas = mHolder.lockCanvas(null);

                synchronized (mLock){
                    if(isThreadRunning){
                        canvas.drawBitmap(bitmap, 0, 0, null);
                    }
                }
            }finally {
                if(canvas != null){
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try{
                Thread.sleep(1000/25);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if(bitmap == null) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(bitmap);
            mCanvas.drawColor(Color.GRAY);
        }else{
            mCanvas = new Canvas(bitmap);
        }
        resumeDrawing();
    }


    private void initRotation(){
        int rotation = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_90:
                lastRotation = -90;
                break;
            case Surface.ROTATION_180:
                lastRotation = 180;
                break;
            case Surface.ROTATION_270:
                lastRotation = 90;
                break;
            case Surface.ROTATION_0:
                lastRotation = 0;
                break;
        }
    }

    private static int lastRotation = -1;
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if(bitmap.getHeight() == height && bitmap.getWidth() == width)
            return;

        Log.d("Last Rotation", String.valueOf(lastRotation));
        int rotation = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int angle = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                angle = -90;
                break;
            case Surface.ROTATION_180:
                angle = 180;
                break;
            case Surface.ROTATION_270:
                angle = 90;
                break;
            case Surface.ROTATION_0:
                break;
        }
        Log.d("Rotation", String.valueOf(angle));

        if(lastRotation == angle)
            return;

        rotation = (-1) * lastRotation + angle;
        lastRotation = angle;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap = Bitmap.createScaledBitmap(bitmap,width,height,false);
        mCanvas = new Canvas(bitmap);

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        pauseDrawing();
    }

    public void setPaint(int color){
        mPaint.setColor(color);
        mPath.reset();
    }

    public void clear() {
        synchronized (mLock) {
            if (mCanvas != null) {
                mCanvas.drawColor(Color.GRAY);
                mPath.reset();
            }
        }
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public static int getLastRotation(){
        return lastRotation;
    }
}
