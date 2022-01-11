package de.gebelclaus.snake;

import static de.gebelclaus.snake.Globals.screenHeight;
import static de.gebelclaus.snake.Globals.screenWidth;
import static de.gebelclaus.snake.Globals.snakeSpeed;
import static de.gebelclaus.snake.Globals.snakeStartIndexX;
import static de.gebelclaus.snake.Globals.snakeStartIndexY;
import static de.gebelclaus.snake.Globals.snakeStartLength;
import static de.gebelclaus.snake.Globals.snakeTileFactor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class LawnView extends View {
    private Snake snake;
    private Cherry cherry;
    private Bitmap grass1Bitmap, grass2Bitmap, snakeBitmap, cherryBitmap;
    private ArrayList<Grass> lawn = new ArrayList<>();

    // Number of tiles horizontally and vertically
    private static int horizontalTilesNumber = 12, verticalTilesNumber = 20;

    // Set width and height of the grass/cherry tile
    public static int fullHDXResolution = 1080;
    public static int fullHDYResolution = 1920;
    public static int singleTileWidth = screenWidth / (horizontalTilesNumber + 2);
    public static int singleTileHeight = singleTileWidth;
    public static int snakeTileWidth = snakeTileFactor * singleTileWidth;
    public static int snakeTileHeight = singleTileHeight;

    private Handler handler;
    private Runnable r;

    private boolean move = false;
    private float mx, my;
    public static boolean isPlaying = false;
    public static int score = 0;
    public static int bestScore = 0;
    private Context context;
    private int soundEat, soundDie;
    private float volume;
    private boolean soundIsLoadable;
    private SoundPool soundPool;

    public LawnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        // Create bitmaps
        grass1Bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
        grass2Bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass2);
        snakeBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.snake);
        cherryBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.cherry);

        // Scale bitmaps
        grass1Bitmap = Bitmap.createScaledBitmap(grass1Bitmap, singleTileWidth, singleTileHeight, true);
        grass2Bitmap = Bitmap.createScaledBitmap(grass2Bitmap, singleTileWidth, singleTileHeight, true);
        snakeBitmap = Bitmap.createScaledBitmap(snakeBitmap, snakeTileWidth, snakeTileHeight, true);
        cherryBitmap = Bitmap.createScaledBitmap(cherryBitmap, singleTileWidth, singleTileHeight, true);

        // Draw lawn pattern
        for(int row = 0; row < verticalTilesNumber; row++){
            for (int column = 0; column < horizontalTilesNumber; column++){
                if((column + row) % 2 == 0){
                    lawn.add(
                            new Grass(grass1Bitmap,
                                    column * grass1Bitmap.getWidth() + screenWidth / 2 - (horizontalTilesNumber / 2) * grass1Bitmap.getWidth(),
                                    row * grass1Bitmap.getHeight() + screenHeight / verticalTilesNumber / 2,
                                    grass1Bitmap.getWidth(),
                                    grass1Bitmap.getHeight()));
                }else{
                    lawn.add(
                            new Grass(grass2Bitmap,
                                    column * grass2Bitmap.getWidth() + screenWidth / 2 - (horizontalTilesNumber / 2) * grass2Bitmap.getWidth(),
                                    row * grass2Bitmap.getHeight() + screenHeight / verticalTilesNumber  / 2,
                                    grass2Bitmap.getWidth(),
                                    grass2Bitmap.getHeight()));
                }
            }
        }

        snake = new Snake(snakeBitmap, lawn.get(snakeStartIndexX).getX(), lawn.get(snakeStartIndexY).getY(), snakeStartLength);
        cherry = new Cherry(cherryBitmap, lawn.get(randomCherry()[0]).getX(), lawn.get(randomCherry()[1]).getY());

        // Create SharedPreferences object to save game settings within the game
        SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);

        // If sp object exists, load bestscore
        if(sp != null){
            bestScore = sp.getInt("bestscore",0);
        }

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        // Sound initialisation
        if(Build.VERSION.SDK_INT>=21){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundIsLoadable = true;
            }
        });
        soundEat = this.soundPool.load(context, R.raw.chomp, 1);
        soundDie = this.soundPool.load(context, R.raw.lost, 1);
    }

    private int[] randomCherry(){
        int []xy = new int[2];
        Random r = new Random();
        xy[0] = r.nextInt(lawn.size()-1);
        xy[1] = r.nextInt(lawn.size()-1);

        Rect rect = new Rect(lawn.get(xy[0]).getX(),
                lawn.get(xy[1]).getY(),
                lawn.get(xy[0]).getX() + singleTileWidth,
                lawn.get(xy[1]).getY() + singleTileWidth);

        boolean check = true;
        while (check){
            check = false;
            for (int i = 0; i < snake.getArrPartSnake().size(); i++){
                if(rect.intersect(snake.getArrPartSnake().get(i).getBodyRect())){
                    check = true;
                    xy[0] = r.nextInt(lawn.size()-1);
                    xy[1] = r.nextInt(lawn.size()-1);
                    rect = new Rect(lawn.get(xy[0]).getX(),
                            lawn.get(xy[1]).getY(),
                            lawn.get(xy[0]).getX()+ singleTileWidth,
                            lawn.get(xy[1]).getY()+ singleTileWidth);
                }
            }
        }
        return xy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getActionMasked();
        switch (a){
            case  MotionEvent.ACTION_MOVE:{
                if(move==false){
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                }else{
                    if(mx - event.getX() > 100 && !snake.isMove_right()){
                        mx = event.getX();
                        my = event.getY();
                        this.snake.setMove_left(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(event.getX() - mx > 100 &&!snake.isMove_left()){
                        mx = event.getX();
                        my = event.getY();
                        this.snake.setMove_right(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(event.getY() - my > 100 && !snake.isMove_up()){
                        mx = event.getX();
                        my = event.getY();
                        this.snake.setMove_down(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(my - event.getY() > 100 && !snake.isMove_down()){
                        mx = event.getX();
                        my = event.getY();
                        this.snake.setMove_up(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                mx = 0;
                my = 0;
                move = false;
                break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        // set the background to dark green
        canvas.drawColor(0xFF065700);

        // draw checkered lawn as background
        for(int i = 0; i < lawn.size(); i++){
            canvas.drawBitmap(lawn.get(i).getBitmap(), lawn.get(i).getX(), lawn.get(i).getY(), null);
        }

        if(isPlaying){
            snake.update();

            // If snake gets beyond the checkered lawn, the game is over
            if(snake.getArrPartSnake().get(0).getX() < this.lawn.get(0).getX()
                ||snake.getArrPartSnake().get(0).getY() < this.lawn.get(0).getY()
                ||snake.getArrPartSnake().get(0).getY()+ singleTileWidth >this.lawn.get(this.lawn.size()-1).getY() + singleTileWidth
                    ||snake.getArrPartSnake().get(0).getX()+ singleTileWidth >this.lawn.get(this.lawn.size()-1).getX() + singleTileWidth){
                gameOver();
            }

            // If snake bites itself, the game is over
            for (int i = 1; i < snake.getArrPartSnake().size(); i++){
                if (snake.getArrPartSnake().get(0).getBodyRect().intersect(snake.getArrPartSnake().get(i).getBodyRect())){
                    gameOver();
                }
            }
        }

        snake.drawSnake(canvas);
        cherry.draw(canvas);

        // If snake head is on cherry tile...
        if(snake.getArrPartSnake().get(0).getBodyRect().intersect(cherry.getR())){
            // Play chomp sound...
            if(soundIsLoadable){
                int streamId = this.soundPool.play(this.soundEat, (float)0.5, (float)0.5, 1, 0, 1f);
            }
            // put a new cherry on a random grass tile...
            cherry.reset(lawn.get(randomCherry()[0]).getX(), lawn.get(randomCherry()[1]).getY());

            // lengthen the snake by one part...
            snake.addPart();

            // and increment the score...
            score++;

            // and update the score text view
            MainActivity.txt_score.setText("x " + score+"");

            if(score > bestScore){
                bestScore = score;

                // Create SharedPreferences object to save game settings within the game
                SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);

                // Create editor so that you can modify saved dates in SharedPreferences
                SharedPreferences.Editor editor = sp.edit();

                // Save bestscore to the editor
                editor.putInt("bestscore", bestScore);

                // Save the dates from the editor to SharedPreferences
                editor.apply();

                MainActivity.txt_best_score.setText("x " + bestScore+"");
            }
        }
        handler.postDelayed(r, snakeSpeed);
    }

    private void gameOver() {
        isPlaying = false;
        MainActivity.dialogScore.show();
        MainActivity.txt_dialog_best_score.setText(bestScore + "");
        MainActivity.txt_dialog_score.setText(score + "");
        if(soundIsLoadable){
            int streamId = this.soundPool.play(this.soundDie, (float)0.5, (float)0.5, 1, 0, 1f);
        }
    }

    // After play button was clicked...
    public void reset(){
        for(int i = 0; i < verticalTilesNumber; i++){
            for (int j = 0; j < horizontalTilesNumber; j++){
                if((j+i)%2==0){
                    lawn.add(new Grass(grass1Bitmap, j* grass1Bitmap.getWidth() + screenWidth /2 - (horizontalTilesNumber /2)* grass1Bitmap.getWidth(), i* grass1Bitmap.getHeight()+50* screenHeight /1920, grass1Bitmap.getWidth(), grass1Bitmap.getHeight()));
                }else{
                    lawn.add(new Grass(grass2Bitmap, j* grass2Bitmap.getWidth() + screenWidth /2 - (horizontalTilesNumber /2)* grass2Bitmap.getWidth(), i* grass2Bitmap.getHeight()+50* screenHeight /1920, grass2Bitmap.getWidth(), grass2Bitmap.getHeight()));
                }
            }
        }

        snake = new Snake(snakeBitmap, lawn.get(snakeStartIndexX).getX(), lawn.get(snakeStartIndexY).getY(), snakeStartLength);
        cherry = new Cherry(cherryBitmap, lawn.get(randomCherry()[0]).getX(), lawn.get(randomCherry()[1]).getY());
        score = 0;
        MainActivity.txt_score.setText("x " + score + "");
    }
}
