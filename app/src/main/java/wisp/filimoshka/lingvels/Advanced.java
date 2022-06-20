package wisp.filimoshka.lingvels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Advanced extends AppCompatActivity {

    private SharedPreferences save_lesson;
    private SharedPreferences save_level;

    private String lvl_lsn_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced);

        SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
        final int task = save.getInt("Task", 1);

        save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
        final int lesson = save_lesson.getInt("Lesson", 1);

        save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
        final String level = save_level.getString("Level", Consts.ADVANCED);
        final String level_txt = save_level.getString("Level_txt", Consts.ADVANCED_TEXT);

        lvl_lsn_key = "_" + level + "_" + lesson;
        System.out.println(lvl_lsn_key);


        // кнопка "назад"
        TextView back_btn2 = (TextView) findViewById(R.id.btn_back2);
        back_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Advanced.this, Menu.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
