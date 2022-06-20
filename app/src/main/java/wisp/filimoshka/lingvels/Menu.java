package wisp.filimoshka.lingvels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private long backPressedTime;
    LinearLayout dotsLayout;
    SliderAdapter adapter;
    ViewPager2 pager2;
    int images[];
    TextView[] dots;

    public static Dialog dialog;

    public ArrayList<Float> results = new ArrayList<Float>();
    private SharedPreferences save_result;
    private SharedPreferences save_lesson;
    private SharedPreferences save_level;
    private DatabaseReference database;
    private String USERS_KEY = Consts.USERS_KEY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        dialog = new Dialog(this); // создаем диалоговое окно
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // без заголовка
        dialog.setContentView(R.layout.settings_dialog); // шаблон диалогового окна
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон
        dialog.setCancelable(true); // возможно закрыть окно системной кнопкой "назад"
        dialog.onBackPressed();

        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();

        wlp.gravity = Gravity.TOP|Gravity.RIGHT;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND; // убрать затемнение фона
        dialog.getWindow().setAttributes(wlp);

        final Button sign_in = (Button) dialog.findViewById(R.id.btn_singin); // закрыть диалоговое окно


        // кнопка "назад"
        TextView back_btn = (TextView) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Menu.this, MainActivity.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });

        // кнопка "настройки"
        TextView back_settings = (TextView) findViewById(R.id.btn_settings);
        back_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                } catch (Exception e) {

                }
            }
        });

        dotsLayout = findViewById(R.id.dots_container);
        pager2 = findViewById(R.id.pager);
        images = new int[3];
        images[0] = R.drawable.bg_intermediate;
        images[1] = R.drawable.bg_upperintermediate;
        images[2] = R.drawable.bg_advanced;

        adapter = new SliderAdapter(images);
        pager2.setAdapter(adapter);

        dots = new TextView[3];
        dotsIndicator();

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                selectedIndicator(position);
                super.onPageSelected(position);
            }
        });

        // кнопка авторизации

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Menu.this, SignIn.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {

                }
            }
        });



        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void selectedIndicator(int position) {
        for (int i=0; i<dots.length; i++) {
            if (i==position){
                dots[i].setTextColor(getResources().getColor(R.color.black));
            }
            else {
                dots[i].setTextColor(getResources().getColor(R.color.black50));
            }
        }
    }

    private void dotsIndicator() {
        for (int i=0; i<dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(18);
            dotsLayout.addView(dots[i]);
        }
    }

    // Системная кнопка "назад"
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Нажмите еще раз, чтобы выйти из приложения", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

}