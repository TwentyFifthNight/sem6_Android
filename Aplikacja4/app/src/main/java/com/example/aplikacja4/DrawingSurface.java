package com.example.aplikacja4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
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

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if(bitmap.getHeight() != height || bitmap.getWidth() != width) {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            mCanvas = new Canvas(bitmap);
        }
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
}
