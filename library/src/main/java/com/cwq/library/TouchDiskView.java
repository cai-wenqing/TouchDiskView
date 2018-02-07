package com.cwq.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义控制圆盘
 */

public class TouchDiskView extends View {

    private float mWidth;
    private float mHeight;
    private float mClockRadio;//表针半径
    private float mBackRadio;//背景图片圆半径
    private Bitmap bg_bitmap;

    private float mLongClockLine = 40;//长表盘指针长度
    private int mClockNum = 120;//最大指针数量
    private float mPreAngle;//每个指针间偏移角度

    private Paint mClockPaint;//指针画笔
    private Paint mBgPaint;//背景图片画笔

    private int mPaintColor = Color.WHITE;
    private boolean mHasInit = false;
    private boolean mTouchRotate = true;//是否禁止触摸旋转
    private boolean mPicRotate = true;//图片是否旋转

    private float touchRotate = 0;
    private float downX;
    private float downY;
    private float movingX;
    private float movingY;

    public TouchDiskView(Context context) {
        this(context, null);
    }

    public TouchDiskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchDiskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray attribute = getContext().obtainStyledAttributes(attrs, R.styleable.TouchDisk);
        Drawable bg = attribute.getDrawable(R.styleable.TouchDisk_disk_bg);
        if (null != bg)
            bg_bitmap = drawableToBitmap(bg);
        attribute.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //长短指针所在的圆半径
        mClockRadio = Math.min(mWidth, mHeight) / 2 - mLongClockLine;
        //背景图片圆半径
        mBackRadio = mClockRadio - 20;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mHasInit) {
            mClockPaint = new Paint();
            mClockPaint.setAntiAlias(true);
            mClockPaint.setStrokeWidth(3);
            mClockPaint.setColor(mPaintColor);

            mPreAngle = 360f / mClockNum;
            mHasInit = true;
        }

        if (mPicRotate) {//图片可旋转
            canvas.rotate(touchRotate, mWidth / 2, mHeight / 2);
            drawBg(canvas);
        } else {//图片不可旋转
            drawBg(canvas);
            canvas.rotate(touchRotate, mWidth / 2, mHeight / 2);
        }

        //画长短指针
        for (int i = 0; i < mClockNum; i++) {
            if (i % 3 == 0)
                canvas.drawLine(mWidth / 2, mHeight / 2 - mClockRadio, mWidth / 2, mHeight / 2 - mClockRadio - mLongClockLine, mClockPaint);
            else
                canvas.drawLine(mWidth / 2, mHeight / 2 - mClockRadio, mWidth / 2, mHeight / 2 - mClockRadio - mLongClockLine / 2, mClockPaint);
            canvas.rotate(mPreAngle, mWidth / 2, mHeight / 2);
        }
    }

    /**
     * 画背景图片
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        if (null != bg_bitmap) {
            Bitmap squareBitmap;
            Bitmap scaledSrcBmp;
            int bgWidth = bg_bitmap.getWidth();
            int bgHeight = bg_bitmap.getHeight();
            int scaleWidth = 0, scaleHeight = 0;
            int x = 0, y = 0;
            if (bgWidth > bgHeight) {
                scaleWidth = scaleHeight = bgHeight;
                x = (bgWidth - bgHeight) / 2;
                y = 0;
                squareBitmap = Bitmap.createBitmap(bg_bitmap, x, y, scaleWidth, scaleHeight);
            } else if (bgWidth < bgHeight) {
                scaleWidth = scaleHeight = bgWidth;
                y = (bgHeight - bgWidth) / 2;
                x = 0;
                squareBitmap = Bitmap.createBitmap(bg_bitmap, x, y, scaleWidth, scaleHeight);
            } else {
                squareBitmap = bg_bitmap;
            }

            if (squareBitmap.getWidth() != mBackRadio * 2 || squareBitmap.getHeight() != mBackRadio * 2) {
                scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, (int) mBackRadio * 2,
                        (int) mBackRadio * 2, true);
            } else {
                scaledSrcBmp = squareBitmap;
            }

            Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                    scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bgCanvas = new Canvas(output);

            Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            bgCanvas.drawARGB(0, 0, 0, 0);
            bgCanvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            bgCanvas.drawBitmap(scaledSrcBmp, rect, rect, paint);

            //画圆形背景
            final Rect rectSrc = new Rect(0, 0, output.getWidth(), output.getHeight());
            final Rect rectDest = new Rect((int) (mWidth / 2 - mBackRadio), (int) (mHeight / 2 - mBackRadio),
                    (int) (mWidth / 2 + mBackRadio), (int) (mHeight / 2 + mBackRadio));
            mBgPaint = new Paint();
            canvas.drawBitmap(output, rectSrc, rectDest, mBgPaint);
        }
    }


    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 计算两个点与圆心的夹角
     *
     * @param p1X
     * @param p1Y
     * @param centerX
     * @param centerY
     * @param p2X
     * @param p2Y
     * @return
     */
    private float calcAngle(float p1X, float p1Y, float centerX, float centerY, float p2X, float p2Y) {
        float dis1 = calcDisBetweenPoint(p1X, p1Y, centerX, centerY);
        float sin1 = (centerX - p1X) / dis1;
        float dis2 = calcDisBetweenPoint(centerX, centerY, p2X, p2Y);
        float sin2 = (p2X - centerX) / dis2;
        return (float) ((Math.asin(sin1) + Math.asin(sin2)) / 2 * Math.PI * 360);
    }

    /**
     * 计算两个点中间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float calcDisBetweenPoint(float x1, float y1, float x2, float y2) {
        float disX = Math.abs(x1 - x2);
        float disY = Math.abs(y1 - y2);
        return (float) Math.sqrt(disX * disX + disY * disY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchRotate)
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    movingX = event.getX();
                    movingY = event.getY();
                    //计算下落点、圆心、移动点三点间的夹角
                    float angle = calcAngle(downX, downY, mWidth / 2, mHeight / 2, movingX, movingY) / 18;
//                    Log.i("TouchDisk测试", "angle:" + angle);
                    if (Math.abs(angle) >= mPreAngle) {
                        if (mHeight / 2 - downY >= 0) {
                            if ((mHeight / 2 - movingY >= 0 && angle > 0) || (mHeight / 2 - movingY < 0 && angle < 0))
                                touchRotate += mPreAngle;
                            else
                                touchRotate -= mPreAngle;
                        } else {
                            if ((movingY - mHeight / 2 >= 0 && angle > 0) || (movingY - mHeight / 2 < 0 && angle < 0))
                                touchRotate -= mPreAngle;
                            else
                                touchRotate += mPreAngle;
                        }

                        //旋转监听
                        if (null != mOnRotateListener) {
                            if (angle > 0 && movingY < mHeight / 2 || angle < 0 && movingY > mHeight / 2)
                                mOnRotateListener.onRotateRight();
                            else
                                mOnRotateListener.onRotateLeft();
                        }
                        downX = movingX;
                        downY = movingY;

                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        return super.onTouchEvent(event);
    }


    /**
     * 向左旋转一格
     */
    public void rotateLeft() {
        touchRotate += -mPreAngle;
        if (null != mOnRotateListener)
            mOnRotateListener.onRotateLeft();
        invalidate();
    }

    /**
     * 向右旋转一格
     */
    public void rotateRight() {
        touchRotate += mPreAngle;
        if (null != mOnRotateListener)
            mOnRotateListener.onRotateRight();
        invalidate();
    }


    /**
     * 设置是否可以触摸旋转
     *
     * @param isTouchRotate
     */
    public void setTouch(boolean isTouchRotate) {
        mTouchRotate = isTouchRotate;
    }


    /**
     * 获取是否可以触摸旋转
     *
     * @return
     */
    public boolean isTouch() {
        return mTouchRotate;
    }


    /**
     * 设置背景图片是否可旋转
     *
     * @param isRotate
     */
    public void setPictureRotate(boolean isRotate) {
        mPicRotate = isRotate;
        touchRotate = 0;
        invalidate();
    }


    /**
     * 获取图片是否可旋转
     *
     * @return
     */
    public boolean isPictureRotate() {
        return mPicRotate;
    }


    //设置滚动监听
    private onRotateListener mOnRotateListener;

    public interface onRotateListener {
        void onRotateLeft();

        void onRotateRight();
    }

    public void setOnRotate(onRotateListener onRotateListener) {
        mOnRotateListener = onRotateListener;
    }
}
