package de.gebelclaus.snake;

import static de.gebelclaus.snake.Globals.snakeTileFactor;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Snake {
    private Bitmap bitmap, bitmap_head_down, bitmap_head_left, bitmap_head_right, bitmap_head_up,
            bitmap_body_vertical, bitmap_body_horizontal, bitmap_body_bottom_left,
            bitmap_body_bottom_right, bitmap_body_top_left, bitmap_body_top_right,
            bitmap_tail_up, bitmap_tail_down, bitmap_tail_right, bitmap_tail_left;
    private ArrayList<SnakeTiles> arrSnakeParts = new ArrayList<>();
    private int length;
    private boolean move_left, move_right, move_up, move_down;

    public Snake(Bitmap bitmap, int x, int y, int length) {
        this.bitmap = bitmap;
        this.length = length;

        // Snake bitmap consists
        bitmap_body_bottom_left = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_body_bottom_right = Bitmap.createBitmap(bitmap, bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_body_horizontal = Bitmap.createBitmap(bitmap, 2 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_body_top_left = Bitmap.createBitmap(bitmap, 3 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_body_top_right = Bitmap.createBitmap(bitmap, 4 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_body_vertical = Bitmap.createBitmap(bitmap, 5 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_head_down = Bitmap.createBitmap(bitmap, 6 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_head_left = Bitmap.createBitmap(bitmap, 7 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_head_right = Bitmap.createBitmap(bitmap, 8 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_head_up = Bitmap.createBitmap(bitmap, 9 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_tail_up = Bitmap.createBitmap(bitmap, 10 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_tail_right = Bitmap.createBitmap(bitmap, 11 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_tail_left = Bitmap.createBitmap(bitmap, 12 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        bitmap_tail_down = Bitmap.createBitmap(bitmap, 13 * bitmap.getWidth() / snakeTileFactor, 0, bitmap.getWidth() / snakeTileFactor, bitmap.getHeight());
        setMove_right(true);
        arrSnakeParts.add(new SnakeTiles(bitmap_head_right, x, y));
        for (int i = 1; i < length-1; i++){
            this.arrSnakeParts.add(new SnakeTiles(bitmap_body_horizontal, this.arrSnakeParts.get(i - 1).getX() - LawnView.singleTileWidth, y));
        }
        arrSnakeParts.add(new SnakeTiles(bitmap_tail_right, arrSnakeParts.get(length - 2).getX() - LawnView.singleTileWidth, arrSnakeParts.get(length-2).getY()));
    }

    public void update(){
        for(int i = length-1; i > 0; i--){
            arrSnakeParts.get(i).setX(arrSnakeParts.get(i - 1).getX());
            arrSnakeParts.get(i).setY(arrSnakeParts.get(i - 1).getY());
        }
        if(move_right){
            arrSnakeParts.get(0).setX(arrSnakeParts.get(0).getX()+ LawnView.singleTileWidth);
            arrSnakeParts.get(0).setBitmap(bitmap_head_right);
        }else if(move_down){
            arrSnakeParts.get(0).setY(arrSnakeParts.get(0).getY()+ LawnView.singleTileWidth);
            arrSnakeParts.get(0).setBitmap(bitmap_head_down);
        }else if(move_up){
            arrSnakeParts.get(0).setY(arrSnakeParts.get(0).getY()- LawnView.singleTileWidth);
            arrSnakeParts.get(0).setBitmap(bitmap_head_up);
        }else{
            arrSnakeParts.get(0).setX(arrSnakeParts.get(0).getX()- LawnView.singleTileWidth);
            arrSnakeParts.get(0).setBitmap(bitmap_head_left);
        }
        for (int i = 1; i < length - 1; i++){
            if(arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    || arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())){
                arrSnakeParts.get(i).setBitmap(bitmap_body_bottom_left);
            }else if (arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    || arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    && arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())){
                arrSnakeParts.get(i).setBitmap(bitmap_body_top_left);
            }else if (arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    || arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    && arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())) {
                arrSnakeParts.get(i).setBitmap(bitmap_body_top_right);
            }else if(arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    || arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    && arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())){
                arrSnakeParts.get(i).setBitmap(bitmap_body_bottom_right);
            }else if(arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    && arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    || arrSnakeParts.get(i).getLeftRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getRightRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())){
                arrSnakeParts.get(i).setBitmap(bitmap_body_horizontal);
            }else if(arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())
                    && arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    || arrSnakeParts.get(i).getTopRect().intersect(arrSnakeParts.get(i + 1).getBodyRect())
                    && arrSnakeParts.get(i).getBottomRect().intersect(arrSnakeParts.get(i - 1).getBodyRect())){
                arrSnakeParts.get(i).setBitmap(bitmap_body_vertical);
            }else{
                if(move_right){
                    arrSnakeParts.get(i).setBitmap(bitmap_body_horizontal);
                }else if(move_down){
                    arrSnakeParts.get(i).setBitmap(bitmap_body_vertical);
                }else if(move_up){
                    arrSnakeParts.get(i).setBitmap(bitmap_body_vertical);
                }else{
                    arrSnakeParts.get(i).setBitmap(bitmap_body_horizontal);
                }
            }
        }
        if(arrSnakeParts.get(length - 1).getRightRect().intersect(arrSnakeParts.get(length - 2).getBodyRect())){
            arrSnakeParts.get(length - 1).setBitmap(bitmap_tail_right);
        }else if(arrSnakeParts.get(length - 1).getLeftRect().intersect(arrSnakeParts.get(length - 2).getBodyRect())){
            arrSnakeParts.get(length - 1).setBitmap(bitmap_tail_left);
        }else if(arrSnakeParts.get(length - 1).getBottomRect().intersect(arrSnakeParts.get(length - 2).getBodyRect())){
            arrSnakeParts.get(length - 1).setBitmap(bitmap_tail_down);
        }else{
            arrSnakeParts.get(length - 1).setBitmap(bitmap_tail_up);
        }
    }

    public void drawSnake(Canvas canvas){
        for(int i = length-1; i >= 0; i--){
            canvas.drawBitmap(arrSnakeParts.get(i).getBitmap(), arrSnakeParts.get(i).getX(), arrSnakeParts.get(i).getY(), null);
        }
    }

    public ArrayList<SnakeTiles> getArrPartSnake() {
        return arrSnakeParts;
    }


    public boolean isMove_left() {
        return move_left;
    }

    public void setMove_left(boolean move_left) {
        this.setup();
        this.move_left = move_left;
    }

    public boolean isMove_right() {
        return move_right;
    }

    public void setMove_right(boolean move_right) {
        this.setup();
        this.move_right = move_right;
    }

    public boolean isMove_up() {
        return move_up;
    }

    public void setMove_up(boolean move_up) {
        this.setup();
        this.move_up = move_up;
    }

    public boolean isMove_down() {
        return move_down;
    }

    public void setMove_down(boolean move_down) {
        this.setup();
        this.move_down = move_down;
    }

    public void setup(){
        this.move_right = false;
        this.move_down = false;
        this.move_left = false;
        this.move_up = false;
    }

    public void addPart() {
        SnakeTiles p = this.arrSnakeParts.get(length-1);
        this.length += 1;
        if(p.getBitmap()== bitmap_tail_right){
            this.arrSnakeParts.add(new SnakeTiles(bitmap_tail_right, p.getX()- LawnView.singleTileWidth, p.getY()));
        }else if(p.getBitmap()== bitmap_tail_left){
            this.arrSnakeParts.add(new SnakeTiles(bitmap_tail_left, p.getX()+ LawnView.singleTileWidth, p.getY()));
        }else if(p.getBitmap()== bitmap_tail_up){
            this.arrSnakeParts.add(new SnakeTiles(bitmap_tail_up, p.getX(), p.getY()+ LawnView.singleTileWidth));
        }else if(p.getBitmap()== bitmap_tail_down){
            this.arrSnakeParts.add(new SnakeTiles(bitmap_tail_up, p.getX(), p.getY()- LawnView.singleTileWidth));
        }
    }
}
