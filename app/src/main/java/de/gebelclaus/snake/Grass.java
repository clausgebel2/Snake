package de.gebelclaus.snake;

import android.graphics.Bitmap;

public class Grass {
    private Bitmap bitmap;
    private int x, y, width, height;

    public Grass(Bitmap bitmap, int x, int y, int width, int height) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
