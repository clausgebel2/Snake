package de.gebelclaus.snake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Cherry {
    private Bitmap bitmap;
    private int x, y;

    public Cherry(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public void reset(int nx, int ny){
        this.x = nx;
        this.y = ny;
    }

    public Rect getR() {
        return new Rect(this.x, this.y, this.x + LawnView.singleTileWidth, this.y + LawnView.singleTileWidth);
    }
}
