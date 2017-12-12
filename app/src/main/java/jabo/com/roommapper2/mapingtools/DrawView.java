package jabo.com.roommapper2.mapingtools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();
    int[][] coords;
    int offsetx=0;
    int offsety=0;

    private void init() {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        for(int i =0;i<this.coords.length;i++) {
            try{
                canvas.drawLine(0+offsetx,0+offsety,(this.coords[i][0]+this.offsetx),(this.coords[i][1]+this.offsety),paint);
            }
            catch (Exception e){

            }
        }
    }

    public void update(int[][] coords,int offsetx,int offsety){
        this.coords = coords;
        this.offsetx = offsetx;
        this.offsety = offsety;
        invalidate();
    }
}