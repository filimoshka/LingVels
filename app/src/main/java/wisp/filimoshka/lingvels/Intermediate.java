package wisp.filimoshka.lingvels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Intermediate extends AppCompatActivity {

    private SharedPreferences save_lesson;
    private SharedPreferences save_level;
    private SharedPreferences save_result;

    private String lvl_lsn_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intermediate);

        if ((Lesson.indoor_music != null)&&(Lesson.indoor_music.isPlaying())) {
            Lesson.indoor_music.stop();
        }

        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        final int task = save.getInt("Task", 1);

        save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
        final int lesson = save_lesson.getInt("Lesson", 1);

        save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
        final String level = save_level.getString("Level", Consts.INTERMEDIATE);
        final String level_txt = save_level.getString("Level_txt", Consts.INTERMEDIATE_TEXT);


        lvl_lsn_key = "_" + level + "_" + lesson;


        final int[] task_buttons = {
                R.id.btn_task1,
                R.id.btn_task2,
                R.id.btn_task3
        };



        // кнопка "назад"
        TextView back_btn2 = (TextView) findViewById(R.id.btn_back2);
        back_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intermediate.this, Menu.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });

        Button task1 = (Button) findViewById(R.id.btn_task1);
        task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed1 = save_level.edit();
                ed1.putString("Level", Consts.INTERMEDIATE);
                ed1.putString("Level_txt", Consts.INTERMEDIATE_TEXT);
                ed1.commit();
                SharedPreferences.Editor ed = save_lesson.edit();
                ed.putInt("Lesson", 1);
                ed.commit();
                try {
                    if (task >= 1) {
                        Intent intent = new Intent(Intermediate.this, SplashScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // пусто
                    }
                } catch (Exception e) {

                }
            }
        });


        Button task2 = (Button) findViewById(R.id.btn_task2);
        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed1 = save_level.edit();
                ed1.putString("Level", Consts.INTERMEDIATE);
                ed1.putString("Level_txt", Consts.INTERMEDIATE_TEXT);
                ed1.commit();
                SharedPreferences.Editor ed = save_lesson.edit();
                ed.putInt("Lesson", 2);
                ed.commit();
                try {
                    if (task >= 2) {
                        Intent intent = new Intent(Intermediate.this, SplashScreen.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // пусто
                    }
                } catch (Exception e) {

                }
            }
        });


        Button b1 = (Button) findViewById(task_buttons[task-1]);
        b1.setBackgroundResource(R.drawable.style_btn_task);
        b1.setEnabled(true);

        for (int i=0; i<task-1; i++) {
            Button b = (Button) findViewById(task_buttons[i]);
            int les = i + 1;
            lvl_lsn_key = "_" + level + "_" + les;
            save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
            float result_saved = save_result.getFloat("ResultSaved"+lvl_lsn_key, 0);
            int mark = (int) result_saved;
            b.setEnabled(false);

            if (result_saved>=80) {
                b.setText("excellent!\uD83D\uDE04 (" + mark + "%)");
                b.setBackgroundResource(R.drawable.btn_excellent);
            } else if (result_saved>=70) {
                b.setText("fine\uD83D\uDE0C (" + mark + "%)");
                b.setBackgroundResource(R.drawable.btn_good);
            } else if (result_saved>=50) {
                b.setText("could be better\uD83D\uDE14 (" + mark + "%)");
                b.setBackgroundResource(R.drawable.btn_bad);
            } else {
                b.setText("so-so...\uD83D\uDE25 (" + mark + "%)");
                b.setBackgroundResource(R.drawable.btn_awful);
            }
        }


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
