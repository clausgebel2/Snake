package de.gebelclaus.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static ImageView img_swipe;
    public static Dialog dialogScore;
    public static TextView txt_score, txt_best_score, txt_dialog_score, txt_dialog_best_score;
    private LawnView lawn_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set to full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get and save screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Globals.screenWidth = dm.widthPixels;
        Globals.screenHeight = dm.heightPixels;

        setContentView(R.layout.activity_main);

        img_swipe = findViewById(R.id.img_swipe);
        lawn_view = findViewById(R.id.lawn_view);
        txt_score = findViewById(R.id.txt_score);
        txt_best_score = findViewById(R.id.txt_best_score);

        // Set an overlay of the dialog screen
        dialogScore();
    }

    private void dialogScore() {
        int bestScore = 0;

        // Create SharedPReferences object to save game settings within the game
        SharedPreferences sp = this.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);

        // If sp object exists, load bestscore
        if(sp!=null){
            bestScore = sp.getInt("bestscore",0);
        }
        MainActivity.txt_best_score.setText("x " + bestScore+"");

        // Show start dialog
        dialogScore = new Dialog(this);
        dialogScore.setContentView(R.layout.dialog_start);

        txt_dialog_score = dialogScore.findViewById(R.id.txt_dialog_score);
        txt_dialog_best_score = dialogScore.findViewById(R.id.txt_dialog_best_score);
        txt_dialog_best_score.setText(bestScore + "");
        dialogScore.setCanceledOnTouchOutside(false);

        Button bt_start = dialogScore.findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show swipe icon to show the gamer how to play the game
                img_swipe.setVisibility(View.VISIBLE);

                // Prepare lawn for a new game
                lawn_view.reset();

                // Remove dialog from screen
                dialogScore.dismiss();
            }
        });

        dialogScore.show();
    }
}