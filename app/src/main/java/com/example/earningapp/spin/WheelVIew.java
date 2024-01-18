package com.example.earningapp.spin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.earningapp.R;

import java.util.List;

public class WheelVIew  extends RelativeLayout implements  PieView.PieRotateListener{
    private int mBackgroundColor, mTextColor;
    private Drawable mCenterImage, mCursorImage;
    private PieView pieView;
    private ImageView imgCursor;
    private LuckyRoundItemSelectedListener itemSelectedListener;
    public interface LuckyRoundItemSelectedListener{
        void LuckyRoundItemSelected(int index);
    }
    public void LuckyRoundItemSelectedListener(LuckyRoundItemSelectedListener listener){
        this.itemSelectedListener = listener;
    }
    @Override
    public void rotateDone(int index) {
        if (itemSelectedListener != null){
            itemSelectedListener.LuckyRoundItemSelected(index);
        }
    }
    public WheelVIew(Context context){
        super(context);
        inits(context, null);
    }
    public WheelVIew(Context context, AttributeSet attrs){
        super(context, attrs);
        inits(context, attrs);
    }
    private void inits(Context context, AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelVIew);
            mBackgroundColor = typedArray.getColor(R.styleable.WheelVIew_BackgroundColor, 0xffcc0000);
            mTextColor = typedArray.getColor(R.styleable.WheelVIew_TextColor, 0xfffffff);

            mCursorImage = typedArray.getDrawable(R.styleable.WheelVIew_CursorImage);
            mCenterImage = typedArray.getDrawable(R.styleable.WheelVIew_CenterImage);

            typedArray.recycle();
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.wheel_layout, this, false);

        pieView = (PieView) frameLayout.findViewById(R.id.pieView);
        imgCursor = (ImageView) frameLayout.findViewById(R.id.cursorView);

        pieView.setPieRotateListener(this);
        pieView.setPieBackgroundColor(mBackgroundColor);
        pieView.setPieCenterImage(mCenterImage);
        pieView.setPieTextColor(mTextColor);

        imgCursor.setImageDrawable(mCursorImage);
        addView(frameLayout);

    }
    public void setWheelBackgroundColor(int color){
        pieView.setPieBackgroundColor(color);
    }
    public void setWheelCursor(int drawable){
        imgCursor.setBackgroundResource(drawable);
    }
    public void setWheelCenterImage(Drawable drawable){
        pieView.setPieCenterImage(drawable);
    }
    public void setWheelTextColor(int color){
        pieView.setPieTextColor(color);
    }
    public void setData(List<SpinItem> data){
        pieView.setData(data);
    }
    public void setRound( int numberOfRound){
        pieView.setRound(numberOfRound);
    }
    public void startWheelWithTargetIndex(int index){
        pieView.rotateTo(index);
    }
}
