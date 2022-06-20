package wisp.filimoshka.lingvels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Upper extends AppCompatActivity {

    private SharedPreferences save_lesson;
    private SharedPreferences save_level;

    private String lvl_lsn_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upper);

        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        final int task = save.getInt("Task", 1);

        save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
        final int lesson = save_lesson.getInt("Lesson", 1);

        save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
        final String level = save_level.getString("Level", Consts.UPPER);
        final String level_txt = save_level.getString("Level_txt", Consts.UPPER_TEXT);

        lvl_lsn_key = "_" + level + "_" + lesson;
        System.out.println(lvl_lsn_key);

        // кнопка "назад"
        TextView back_btn2 = (TextView) findViewById(R.id.btn_back2);
        back_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Upper.this, Menu.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });

//        Button task1 = (Button) findViewById(R.id.btn_task1);
//        task1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor ed1 = save_level.edit();
//                ed1.putString("Level", Consts.UPPER);
//                ed1.commit();
//                SharedPreferences.Editor ed = save_lesson.edit();
//                ed.putInt("Lesson", 1);
//                ed.commit();
//                try {
//                    if (task >= 1) {
//                        Intent intent = new Intent(Upper.this, SplashScreen.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        // пусто
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        });

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
