package de.gebelclaus.snake;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class SnakeTiles {
    private Bitmap bitmap;
    private int x, y;
    private Rect bodyRect, topRect, bottomRect, rightRect, leftRect;

    public SnakeTiles(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Rect getTopRect() {
        return new Rect(this.x, this.y - 10 * Globals.screenHeight / 1920, this.x+ LawnView.singleTileWidth, this.y);
    }

    public Rect getBottomRect() {
        return new Rect(this.x, this.y + LawnView.singleTileWidth, this.x + LawnView.singleTileWidth, this.y + LawnView.singleTileWidth +10* Globals.screenHeight /1920);
    }

    public Rect getRightRect() {
        return new Rect(this.x + LawnView.singleTileWidth, this.y, this.x + LawnView.singleTileWidth +10* Globals.screenWidth /1080, this.y+ LawnView.singleTileWidth);
    }

    public Rect getLeftRect() {
        return new Rect(this.x - 10 * Globals.screenWidth / 1080, this.y, this.x, this.y + LawnView.singleTileWidth);
    }

    public Rect getBodyRect() {
        return new Rect(this.x, this.y, this.x + LawnView.singleTileWidth, this.y + LawnView.singleTileWidth);
    }

}
