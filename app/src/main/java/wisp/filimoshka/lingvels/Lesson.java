package wisp.filimoshka.lingvels;

import static wisp.filimoshka.lingvels.SplashScreen.characters_imgs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Lesson extends AppCompatActivity {

    private long backPressedTime;

    public ArrayList<String> lines_text = new ArrayList<String>();
    public ArrayList<Integer> lines_with_speech = new ArrayList<Integer>();
    public ArrayList<Integer> speech_character = new ArrayList<Integer>();
    public ArrayList<Integer> lines_with_task_opt = new ArrayList<Integer>();
    public ArrayList<Integer> task_opt_id = new ArrayList<Integer>();
    public ArrayList<String> bg_images = new ArrayList<String>();
    public ArrayList<Integer> bg_id = new ArrayList<Integer>();
    public ArrayList<Integer> lines_num = new ArrayList<Integer>();
    public ArrayList<Integer> bg_id_lines_num = new ArrayList<Integer>();
    public ArrayList<Integer> bg_lines_num = new ArrayList<Integer>();;
    public ArrayList<String> characters_name = new ArrayList<String>();
    public ArrayList<Integer> characters_id = new ArrayList<Integer>();
    public ArrayList<String> characters_images = new ArrayList<String>();
    public boolean[] characters_main;
    public boolean[] characters_minor;
    public ArrayList<Integer> task_option_id = new ArrayList<Integer>();
    public ArrayList<String> first_opt = new ArrayList<String>();
    public ArrayList<String> second_opt = new ArrayList<String>();
    public ArrayList<String> third_opt = new ArrayList<String>();
    public ArrayList<Integer> correct_num = new ArrayList<Integer>();

    public static Dialog dialog;
    public static MediaPlayer outside_music;
    public static MediaPlayer indoor_music;

    private int numSlide = 0; // номер слайда
    private int numOption = 0; // номер выбора вариантов ответа (задания)
    public boolean flag = false; // прилагаются ли к данной части текста вариант ответов

    private int bg = 1;
    public int pers = 0;

    public static int countCorrect_final = 0;
    public static int maxCorrect_final = 0;

    private int countCorrect = 0; // счетчик правильных ответов
    private int maxCorrect = 0; // максимально возможный балл

    private SharedPreferences save_slide;
    private SharedPreferences save_option;
    private SharedPreferences save_bg;
    private SharedPreferences save_correct;
    private SharedPreferences save_lesson;
    private SharedPreferences save_level;

    private SharedPreferences save_result;

    private String lvl_lsn_key;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson);

        save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
        final int lesson = save_lesson.getInt("Lesson", 1);

        save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
        final String level = save_level.getString("Level", Consts.INTERMEDIATE);

        lvl_lsn_key = "_" + level + "_" + lesson;


        getIntentSplash();


        save_slide = getSharedPreferences("SaveSlide"+lvl_lsn_key, MODE_PRIVATE);
        final int numSlide_saved = save_slide.getInt("NumSlideSaved"+lvl_lsn_key, 0);

        save_option = getSharedPreferences("SaveOption"+lvl_lsn_key, MODE_PRIVATE);
        final int numOption_saved = save_option.getInt("NumOptionSaved"+lvl_lsn_key, 0);

        save_bg = getSharedPreferences("SaveBg"+lvl_lsn_key, MODE_PRIVATE);
        final int numBg_saved = save_bg.getInt("NumBgSaved"+lvl_lsn_key, 0);

        save_correct = getSharedPreferences("SaveCorrect"+lvl_lsn_key, MODE_PRIVATE);
        final int numCorrect_saved = save_correct.getInt("NumCorrectSaved"+lvl_lsn_key, 0);

        save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
        final float result_saved = save_result.getFloat("ResultSaved"+lvl_lsn_key, 0);


        dialog = new Dialog(this); // создаем диалоговое окно
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // без заголовка
        dialog.setContentView(R.layout.results_dialog); // шаблон диалогового окна
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон
        dialog.setCancelable(false); // невозможно закрыть окно системной кнопкой "назад"

        // кнопка "назад"
        TextView back_btn = (TextView) findViewById(R.id.btn_back_task2);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Lesson.this, Intermediate.class);
                    stopSound(indoor_music);
                    startActivity(intent);


                    if (numSlide>0){
                        SharedPreferences.Editor editor2 = save_slide.edit();
                        editor2.putInt("NumSlideSaved"+lvl_lsn_key, numSlide);
                        editor2.commit();
                    }
                    if (numOption>0){
                        SharedPreferences.Editor editor3 = save_option.edit();
                        editor3.putInt("NumOptionSaved"+lvl_lsn_key, numOption);
                        editor3.commit();
                    }
                    if (bg>0){
                        SharedPreferences.Editor editor4 = save_bg.edit();
                        editor4.putInt("NumBgSaved"+lvl_lsn_key, bg-1);
                        editor4.commit();
                    }
                    if (countCorrect>0){
                        SharedPreferences.Editor editor6 = save_correct.edit();
                        editor6.putInt("NumCorrectSaved"+lvl_lsn_key, countCorrect);
                        editor6.commit();
                    }

                    finish();
                } catch (Exception e) {

                }
            }
        });


        final TextView textView = (TextView) findViewById(R.id.task_text);
        final Button btn_option_1 = (Button) findViewById(R.id.btn_var1); // кнопка для 1 варианта ответа
        final Button btn_option_2 = (Button) findViewById(R.id.btn_var2); // кнопка для 2 варианта ответа
        final Button btn_option_3 = (Button) findViewById(R.id.btn_var3); // кнопка для 3 варианта ответа
        final LinearLayout l = (LinearLayout) findViewById(R.id.container_options); // лейаут с кнопками для выбора ответа
        final TextView correctNum = (TextView) dialog.findViewById(R.id.correctNum); // для вывода результатав диалоговом окне
        final TextView maxNum = (TextView) dialog.findViewById(R.id.maxNum); // для вывода результатав диалоговом окне
        final Button finish = (Button) dialog.findViewById(R.id.btn_continue); // закрыть диалоговое окно
        final TextView name = (TextView) findViewById(R.id.name);
        final ImageView back = (ImageView) findViewById(R.id.task_background);
        final ImageView pers_main = (ImageView) findViewById(R.id.pers_main);
        final ImageView pers_minor = (ImageView) findViewById(R.id.pers_minor);
        final Animation a = AnimationUtils.loadAnimation(Lesson.this, R.anim.bg_change);
        final Animation anim_text = AnimationUtils.loadAnimation(Lesson.this, R.anim.text_change);
        final Animation anim_main = AnimationUtils.loadAnimation(Lesson.this, R.anim.main_character);
        final Animation anim_minor = AnimationUtils.loadAnimation(Lesson.this, R.anim.minor_character);
        outside_music = MediaPlayer.create(this, R.raw.outside);
        indoor_music = MediaPlayer.create(this, R.raw.indoor);


        LinearLayout.LayoutParams layout_for_name = (LinearLayout.LayoutParams) name.getLayoutParams();

            numSlide = numSlide_saved;
            numOption = numOption_saved;
            countCorrect = numCorrect_saved;
            bg = numBg_saved + 1;



            //back.setImageResource(arr.task1_bg[numBg_saved]); // первый фон
            Glide.with(getApplicationContext()).load(bg_images.get(numBg_saved)).into(back);
            back.startAnimation(a);
            textView.setText(lines_text.get(numSlide)); // первый кусок текста из массива
            textView.startAnimation(anim_text);


            playSound(indoor_music);


            for (int i = 0; i < lines_with_task_opt.size(); i++) {
                if (numSlide + 1 == lines_with_task_opt.get(i)) {
                    flag = true;
                    l.setVisibility(View.VISIBLE); // если есть варианты ответа, то делаем кнопки видимыми
                    l.startAnimation(anim_text);
                    btn_option_1.setText(first_opt.get(numOption)); // заполняем текст кнопок вариантами ответов из массива
                    btn_option_2.setText(second_opt.get(numOption));
                    btn_option_3.setText(third_opt.get(numOption));
                    break;
                } else {
                    flag = false;
                    l.setVisibility(View.INVISIBLE); // иначе - скрываем лейаут с кнопками
                }
            }

            // добавляем персонажей
            for (int i=0; i<lines_with_speech.size(); i++) {
                pers = 0;
                if (numSlide+1 == lines_with_speech.get(i)) {
                    pers = speech_character.get(i);
                    break;
                }
            }

            if (pers >= 1) {
                if (characters_main[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.START;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_main);
                                pers_main.setVisibility(View.VISIBLE);
                                pers_main.startAnimation(anim_main);
                                pers_minor.setVisibility(View.INVISIBLE);
                            } else if (characters_minor[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.END;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                pers_main.setVisibility(View.INVISIBLE);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_minor);
                                pers_minor.setVisibility(View.VISIBLE);
                                pers_minor.startAnimation(anim_minor);
                } else {
                    name.setVisibility(View.INVISIBLE);
                    pers_main.setVisibility(View.INVISIBLE);
                    pers_minor.setVisibility(View.INVISIBLE);
                }
            } else {
                name.setVisibility(View.INVISIBLE);
                pers_main.setVisibility(View.INVISIBLE);
                pers_minor.setVisibility(View.INVISIBLE);
            }


        // клик на фон (если нет вариантов ответа)
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                back.setEnabled(false);


                //  если массив с текстом не закончился и кнопок нет (так как если есть кнопки, то фон должен быть не кликабельным)
                if ((numSlide < lines_text.size() - 1)&&(!flag)) {

                    // переходим к следующему куску текста
                    numSlide++;
                    textView.setText(lines_text.get(numSlide));
                    textView.startAnimation(anim_text);


                    // устанавливаем фон
                    int bg_prev = bg;
                    for (int i=0; i<bg_lines_num.size(); i++) {
                        if ((numSlide >= bg_lines_num.get(i)) && (numSlide < bg_lines_num.get(i+1))) {
                            bg = bg_id_lines_num.get(i);
                            break;
                        }
                    }

                        Glide.with(getApplicationContext()).load(bg_images.get(bg-1)).into(back);
                        if (bg_prev != bg){
                            back.startAnimation(a);
                        }



                    // добавляем персонажей
                    for (int i=0; i<lines_with_speech.size(); i++) {
                        pers = 0;
                        if (numSlide+1 == lines_with_speech.get(i)) {
                            pers = speech_character.get(i);
                            break;
                        }
                    }

                    if (pers >= 1) {
                        if (characters_main[pers - 1]) {
                            name.setText(characters_name.get(pers - 1));
                            layout_for_name.gravity = Gravity.START;
                            name.setLayoutParams(layout_for_name);
                            name.setVisibility(View.VISIBLE);
                            name.startAnimation(anim_text);
                            Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_main);
                            pers_main.setVisibility(View.VISIBLE);
                            pers_main.startAnimation(anim_main);
                            pers_minor.setVisibility(View.INVISIBLE);
                        } else if (characters_minor[pers - 1]) {
                            name.setText(characters_name.get(pers - 1));
                            layout_for_name.gravity = Gravity.END;
                            name.setLayoutParams(layout_for_name);
                            name.setVisibility(View.VISIBLE);
                            name.startAnimation(anim_text);
                            pers_main.setVisibility(View.INVISIBLE);
                            Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_minor);
                            pers_minor.setVisibility(View.VISIBLE);
                            pers_minor.startAnimation(anim_minor);
                        } else {
                            name.setVisibility(View.INVISIBLE);
                            pers_main.setVisibility(View.INVISIBLE);
                            pers_minor.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        name.setVisibility(View.INVISIBLE);
                        pers_main.setVisibility(View.INVISIBLE);
                        pers_minor.setVisibility(View.INVISIBLE);
                    }


                    // варианты ответов
                    for (int i = 0; i < lines_with_task_opt.size(); i++) {
                        if (numSlide + 1 == lines_with_task_opt.get(i)) {
                            flag = true;
                            l.setVisibility(View.VISIBLE); // если есть варианты ответа, то делаем кнопки видимыми
                            l.startAnimation(anim_text);
                            btn_option_1.setText(first_opt.get(numOption)); // заполняем текст кнопок вариантами ответов из массива
                            btn_option_2.setText(second_opt.get(numOption));
                            btn_option_3.setText(third_opt.get(numOption));
                            break;
                        }
                        else {
                            flag = false;
                            l.setVisibility(View.INVISIBLE); // иначе - скрываем лейаут с кнопками
                        }
                    }


                } else if (!flag) {
                    // открываем диалоговое окно с результатами
                    correctNum.setText(String.valueOf(countCorrect));
                    maxNum.setText(String.valueOf(maxCorrect));
                    dialog.show();
                    countCorrect_final = countCorrect;
                    maxCorrect_final = maxCorrect;


                    SharedPreferences.Editor editor2 = save_slide.edit();
                    editor2.putInt("NumSlideSaved"+lvl_lsn_key, 0);
                    editor2.commit();
                    SharedPreferences.Editor editor3 = save_option.edit();
                    editor3.putInt("NumOptionSaved"+lvl_lsn_key, 0);
                    editor3.commit();
                    SharedPreferences.Editor editor4 = save_bg.edit();
                    editor4.putInt("NumBgSaved"+lvl_lsn_key, 0);
                    editor4.commit();
                    SharedPreferences.Editor editor6 = save_correct.edit();
                    editor6.putInt("NumCorrectSaved"+lvl_lsn_key, 0);
                    editor6.commit();
                    SharedPreferences.Editor editor7 = save_result.edit();
                    float corr = countCorrect;
                    float max = maxCorrect;

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    float result = corr/max*100;
                    String res = df.format(result);
                    res = res.replace(",", ".");
                    result = Float.parseFloat(res);

                    editor7.putFloat("ResultSaved"+lvl_lsn_key, result);
                    editor7.commit();
                }
                back.setEnabled(true);
            }
        });


        btn_option_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                btn_option_1.setEnabled(false);

                if (numSlide < lines_text.size() - 1) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        // блокируем остальные кнопки
                        btn_option_2.setEnabled(false);
                        btn_option_3.setEnabled(false);

                        // если номер нажатой кнопки совпадает с номером правильного ответа, то прибавляем 1
                        if (correct_num.get(numOption) == 0) {
                            btn_option_1.setBackgroundResource(R.drawable.var_btn_style_correct); // при нажатии кнопка станет зеленой
                        } else {
                            btn_option_1.setBackgroundResource(R.drawable.var_btn_style_incorrect); // при нажатии кнопка станет красной
                            if (correct_num.get(numOption) == 1) {
                                btn_option_2.setBackgroundResource(R.drawable.var_btn_style_correct);
                            } else if (correct_num.get(numOption) == 2) {
                                btn_option_3.setBackgroundResource(R.drawable.var_btn_style_correct);
                            }
                        }


                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        // разблокируем остальные кнопки
                        btn_option_2.setEnabled(true);
                        btn_option_3.setEnabled(true);

                        if (correct_num.get(numOption) == 0) {
                            countCorrect++;
                        }

                        btn_option_1.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_2.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_3.setBackgroundResource(R.drawable.var_btn_style);

                        // переходим к следующей части текста и следующему блоку вариантов ответа
                        numSlide++;
                        numOption++;

                        textView.setText(lines_text.get(numSlide));
                        textView.startAnimation(anim_text);

                        // устанавливаем фон
                        int bg_prev = bg;
                        for (int i=0; i<bg_lines_num.size(); i++) {
                            if ((numSlide >= bg_lines_num.get(i)) && (numSlide < bg_lines_num.get(i+1))) {
                                bg = bg_id_lines_num.get(i);
                                break;
                            }
                        }

                        Glide.with(getApplicationContext()).load(bg_images.get(bg-1)).into(back);
                        if (bg_prev != bg){
                            back.startAnimation(a);
                        }


                        // добавляем персонажей
                        for (int i=0; i<lines_with_speech.size(); i++) {
                            pers = 0;
                            if (numSlide+1 == lines_with_speech.get(i)) {
                                pers = speech_character.get(i);
                                break;
                            }
                        }


                        if (pers >= 1) {
                            if (characters_main[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.START;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_main);
                                pers_main.setVisibility(View.VISIBLE);
                                pers_main.startAnimation(anim_main);
                                pers_minor.setVisibility(View.INVISIBLE);
                            } else if (characters_minor[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.END;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                pers_main.setVisibility(View.INVISIBLE);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_minor);
                                pers_minor.setVisibility(View.VISIBLE);
                                pers_minor.startAnimation(anim_minor);
                            } else {
                                name.setVisibility(View.INVISIBLE);
                                pers_main.setVisibility(View.INVISIBLE);
                                pers_minor.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            name.setVisibility(View.INVISIBLE);
                            pers_main.setVisibility(View.INVISIBLE);
                            pers_minor.setVisibility(View.INVISIBLE);
                        }


                        // варианты ответов
                        for (int i = 0; i < lines_with_task_opt.size(); i++) {
                            if (numSlide + 1 == lines_with_task_opt.get(i)) {
                                flag = true;
                                l.setVisibility(View.VISIBLE); // если есть варианты ответа, то делаем кнопки видимыми
                                l.startAnimation(anim_text);
                                btn_option_1.setText(first_opt.get(numOption)); // заполняем текст кнопок вариантами ответов из массива
                                btn_option_2.setText(second_opt.get(numOption));
                                btn_option_3.setText(third_opt.get(numOption));
                                break;
                            } else {
                                flag = false;
                                l.setVisibility(View.INVISIBLE); // иначе - скрываем лейаут с кнопками
                            }
                        }
                    }

                } else {
                    if (correct_num.get(numOption) == 0) {
                        countCorrect++;
                    }

                    correctNum.setText(String.valueOf(countCorrect));
                    maxNum.setText(String.valueOf(maxCorrect));
                    dialog.show();
                    countCorrect_final = countCorrect;
                    maxCorrect_final = maxCorrect;

                    SharedPreferences.Editor editor2 = save_slide.edit();
                    editor2.putInt("NumSlideSaved"+lvl_lsn_key, 0);
                    editor2.commit();
                    SharedPreferences.Editor editor3 = save_option.edit();
                    editor3.putInt("NumOptionSaved"+lvl_lsn_key, 0);
                    editor3.commit();
                    SharedPreferences.Editor editor4 = save_bg.edit();
                    editor4.putInt("NumBgSaved"+lvl_lsn_key, 0);
                    editor4.commit();
                    SharedPreferences.Editor editor6 = save_correct.edit();
                    editor6.putInt("NumCorrectSaved"+lvl_lsn_key, 0);
                    editor6.commit();
                    SharedPreferences.Editor editor7 = save_result.edit();
                    float corr = countCorrect;
                    float max = maxCorrect;

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    float result = corr/max*100;
                    String res = df.format(result);
                    res = res.replace(",", ".");
                    result = Float.parseFloat(res);

                    editor7.putFloat("ResultSaved"+lvl_lsn_key, result);
                    editor7.commit();
                }
                btn_option_1.setEnabled(true);
                return true;
            };
        });


        btn_option_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btn_option_2.setEnabled(false);

                if (numSlide < lines_text.size() - 1) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        // блокируем остальные кнопки
                        btn_option_1.setEnabled(false);
                        btn_option_3.setEnabled(false);

                        // если номер нажатой кнопки совпадает с номером правильного ответа, то прибавляем 1
                        if (correct_num.get(numOption) == 1) {
                            btn_option_2.setBackgroundResource(R.drawable.var_btn_style_correct); // при нажатии кнопка станет зеленой
                        } else {
                            btn_option_2.setBackgroundResource(R.drawable.var_btn_style_incorrect); // при нажатии кнопка станет красной
                            if (correct_num.get(numOption) == 0) {
                                btn_option_1.setBackgroundResource(R.drawable.var_btn_style_correct);
                            } else if (correct_num.get(numOption) == 2) {
                                btn_option_3.setBackgroundResource(R.drawable.var_btn_style_correct);
                            }
                        }


                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        // разблокируем остальные кнопки
                        btn_option_1.setEnabled(true);
                        btn_option_3.setEnabled(true);

                        if (correct_num.get(numOption) == 1) {
                            countCorrect++;
                        }

                        btn_option_1.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_2.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_3.setBackgroundResource(R.drawable.var_btn_style);

                        // переходим к следующей части текста и следующему блоку вариантов ответа
                        numSlide++;
                        numOption++;

                        textView.setText(lines_text.get(numSlide));
                        textView.startAnimation(anim_text);

                        /// устанавливаем фон
                        int bg_prev = bg;
                        for (int i=0; i<bg_lines_num.size(); i++) {
                            if ((numSlide >= bg_lines_num.get(i)) && (numSlide < bg_lines_num.get(i+1))) {
                              bg = bg_id_lines_num.get(i);
                              break;
                            }
                        }


                            Glide.with(getApplicationContext()).load(bg_images.get(bg-1)).into(back);
                            if (bg_prev != bg){
                                back.startAnimation(a);
                            }


                        // добавляем персонажей
                        for (int i=0; i<lines_with_speech.size(); i++) {
                            pers = 0;
                            if (numSlide+1 == lines_with_speech.get(i)) {
                                pers = speech_character.get(i);
                                break;
                            }
                        }

                        if (pers >= 1) {
                            if (characters_main[pers - 1]) {
                    name.setText(characters_name.get(pers - 1));
                    layout_for_name.gravity = Gravity.START;
                    name.setLayoutParams(layout_for_name);
                    name.setVisibility(View.VISIBLE);
                    name.startAnimation(anim_text);
                    Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_main);
                    pers_main.setVisibility(View.VISIBLE);
                    pers_main.startAnimation(anim_main);
                    pers_minor.setVisibility(View.INVISIBLE);
                } else if (characters_minor[pers - 1]) {
                    name.setText(characters_name.get(pers - 1));
                    layout_for_name.gravity = Gravity.END;
                    name.setLayoutParams(layout_for_name);
                    name.setVisibility(View.VISIBLE);
                    name.startAnimation(anim_text);
                    pers_main.setVisibility(View.INVISIBLE);
                    Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_minor);
                    pers_minor.setVisibility(View.VISIBLE);
                    pers_minor.startAnimation(anim_minor);
                            } else {
                                name.setVisibility(View.INVISIBLE);
                                pers_main.setVisibility(View.INVISIBLE);
                                pers_minor.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            name.setVisibility(View.INVISIBLE);
                            pers_main.setVisibility(View.INVISIBLE);
                            pers_minor.setVisibility(View.INVISIBLE);
                        }



                        // варианты ответов
                        for (int i = 0; i < lines_with_task_opt.size(); i++) {
                            if (numSlide + 1 == lines_with_task_opt.get(i)) {
                                flag = true;
                                l.setVisibility(View.VISIBLE); // если есть варианты ответа, то делаем кнопки видимыми
                                l.startAnimation(anim_text);
                                btn_option_1.setText(first_opt.get(numOption)); // заполняем текст кнопок вариантами ответов из массива
                                btn_option_2.setText(second_opt.get(numOption));
                                btn_option_3.setText(third_opt.get(numOption));
                                break;
                            } else {
                                flag = false;
                                l.setVisibility(View.INVISIBLE); // иначе - скрываем лейаут с кнопками
                            }
                        }
                    }

                } else {
                    if (correct_num.get(numOption) == 1) {
                        countCorrect++;
                    }

                    correctNum.setText(String.valueOf(countCorrect));
                    maxNum.setText(String.valueOf(maxCorrect));
                    dialog.show();
                    countCorrect_final = countCorrect;
                    maxCorrect_final = maxCorrect;

                    SharedPreferences.Editor editor2 = save_slide.edit();
                    editor2.putInt("NumSlideSaved"+lvl_lsn_key, 0);
                    editor2.commit();
                    SharedPreferences.Editor editor3 = save_option.edit();
                    editor3.putInt("NumOptionSaved"+lvl_lsn_key, 0);
                    editor3.commit();
                    SharedPreferences.Editor editor4 = save_bg.edit();
                    editor4.putInt("NumBgSaved"+lvl_lsn_key, 0);
                    editor4.commit();
                    SharedPreferences.Editor editor6 = save_correct.edit();
                    editor6.putInt("NumCorrectSaved"+lvl_lsn_key, 0);
                    editor6.commit();
                    SharedPreferences.Editor editor7 = save_result.edit();
                    float corr = countCorrect;
                    float max = maxCorrect;

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    float result = corr/max*100;
                    String res = df.format(result);
                    res = res.replace(",", ".");
                    result = Float.parseFloat(res);

                    editor7.putFloat("ResultSaved"+lvl_lsn_key, result);
                    editor7.commit();
                }
                btn_option_2.setEnabled(true);
                return true;
            };
        });

        btn_option_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btn_option_3.setEnabled(false);

                if (numSlide < lines_text.size() - 1) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        // блокируем остальные кнопки
                        btn_option_1.setEnabled(false);
                        btn_option_2.setEnabled(false);

                        // если номер нажатой кнопки совпадает с номером правильного ответа, то прибавляем 1
                        if (correct_num.get(numOption) == 2) {
                            btn_option_3.setBackgroundResource(R.drawable.var_btn_style_correct); // при нажатии кнопка станет зеленой
                        } else {
                            btn_option_3.setBackgroundResource(R.drawable.var_btn_style_incorrect); // при нажатии кнопка станет красной
                            if (correct_num.get(numOption) == 0) {
                                btn_option_1.setBackgroundResource(R.drawable.var_btn_style_correct);
                            } else if (correct_num.get(numOption) == 1) {
                                btn_option_2.setBackgroundResource(R.drawable.var_btn_style_correct);
                            }
                        }


                    } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        // разблокируем остальные кнопки
                        btn_option_1.setEnabled(true);
                        btn_option_2.setEnabled(true);

                        if (correct_num.get(numOption) == 2) {
                            countCorrect++;
                        }

                        btn_option_1.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_2.setBackgroundResource(R.drawable.var_btn_style);
                        btn_option_3.setBackgroundResource(R.drawable.var_btn_style);

                        // переходим к следующей части текста и следующему блоку вариантов ответа
                        numSlide++;
                        numOption++;

                        textView.setText(lines_text.get(numSlide));
                        textView.startAnimation(anim_text);

                        // устанавливаем фон
                        int bg_prev = bg;
                        for (int i=0; i<bg_lines_num.size(); i++) {
                            if ((numSlide >= bg_lines_num.get(i)) && (numSlide < bg_lines_num.get(i+1))) {
                                bg = bg_id_lines_num.get(i);
                                break;
                            }
                        }

                        Glide.with(getApplicationContext()).load(bg_images.get(bg-1)).into(back);
                        if (bg_prev != bg){
                            back.startAnimation(a);
                        }


                        // добавляем персонажей
                        for (int i=0; i<lines_with_speech.size(); i++) {
                            pers = 0;
                            if (numSlide+1 == lines_with_speech.get(i)) {
                                pers = speech_character.get(i);
                                break;
                            }
                        }

                        if (pers >= 1) {
                            if (characters_main[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.START;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_main);
                                pers_main.setVisibility(View.VISIBLE);
                                pers_main.startAnimation(anim_main);
                                pers_minor.setVisibility(View.INVISIBLE);
                            } else if (characters_minor[pers - 1]) {
                                name.setText(characters_name.get(pers - 1));
                                layout_for_name.gravity = Gravity.END;
                                name.setLayoutParams(layout_for_name);
                                name.setVisibility(View.VISIBLE);
                                name.startAnimation(anim_text);
                                pers_main.setVisibility(View.INVISIBLE);
                                Glide.with(getApplicationContext()).load(characters_images.get(pers - 1)).into(pers_minor);
                                pers_minor.setVisibility(View.VISIBLE);
                                pers_minor.startAnimation(anim_minor);
                            } else {
                                name.setVisibility(View.INVISIBLE);
                                pers_main.setVisibility(View.INVISIBLE);
                                pers_minor.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            name.setVisibility(View.INVISIBLE);
                            pers_main.setVisibility(View.INVISIBLE);
                            pers_minor.setVisibility(View.INVISIBLE);
                        }


                        // варианты ответов
                        for (int i = 0; i < lines_with_task_opt.size(); i++) {
                            if (numSlide + 1 == lines_with_task_opt.get(i)) {
                                flag = true;
                                l.setVisibility(View.VISIBLE); // если есть варианты ответа, то делаем кнопки видимыми
                                l.startAnimation(anim_text);
                                btn_option_1.setText(first_opt.get(numOption)); // заполняем текст кнопок вариантами ответов из массива
                                btn_option_2.setText(second_opt.get(numOption));
                                btn_option_3.setText(third_opt.get(numOption));
                                break;
                            } else {
                                flag = false;
                                l.setVisibility(View.INVISIBLE); // иначе - скрываем лейаут с кнопками
                            }
                        }
                    }

                } else {
                    if (correct_num.get(numOption) == 2) {
                        countCorrect++;
                    }

                    correctNum.setText(String.valueOf(countCorrect));
                    maxNum.setText(String.valueOf(maxCorrect));
                    dialog.show();
                    countCorrect_final = countCorrect;
                    maxCorrect_final = maxCorrect;

                    SharedPreferences.Editor editor2 = save_slide.edit();
                    editor2.putInt("NumSlideSaved"+lvl_lsn_key, 0);
                    editor2.commit();
                    SharedPreferences.Editor editor3 = save_option.edit();
                    editor3.putInt("NumOptionSaved"+lvl_lsn_key, 0);
                    editor3.commit();
                    SharedPreferences.Editor editor4 = save_bg.edit();
                    editor4.putInt("NumBgSaved"+lvl_lsn_key, 0);
                    editor4.commit();
                    SharedPreferences.Editor editor6 = save_correct.edit();
                    editor6.putInt("NumCorrectSaved"+lvl_lsn_key, 0);
                    editor6.commit();
                    SharedPreferences.Editor editor7 = save_result.edit();
                    float corr = countCorrect;
                    float max = maxCorrect;

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    float result = corr/max*100;
                    String res = df.format(result);
                    res = res.replace(",", ".");
                    result = Float.parseFloat(res);

                    editor7.putFloat("ResultSaved"+lvl_lsn_key, result);
                    editor7.commit();
                }
                btn_option_3.setEnabled(true);
                return true;
            };
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Lesson.this, Final.class);
                    intent.putExtra("countCorrect", countCorrect_final);
                    intent.putExtra("maxCorrect", maxCorrect_final);
                    startActivity(intent);
                    stopSound(indoor_music);

                    // сохранение результатов и разблокировка следующего задания
                    SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                    int task = save.getInt("Task", 1);
                    if (task>lesson) {
                        //
                    } else {
                        task = task+1;
                    }
                    SharedPreferences.Editor editor = save.edit();
                    editor.putInt("Task", task);
                    editor.commit();

                    finish();

                } catch (Exception e) {

                }
            }
        });

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void playSound(MediaPlayer sound) {
        sound.setLooping(true);
        sound.start();
    }
    private void stopSound(MediaPlayer sound) {
        sound.stop();
    }


    // Системная кнопка "назад"
    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            if ((Lesson.indoor_music != null)&&(Lesson.indoor_music.isPlaying())) {
                Lesson.indoor_music.stop();
            }
            if (numSlide>0){
                SharedPreferences.Editor editor2 = save_slide.edit();
                editor2.putInt("NumSlideSaved"+lvl_lsn_key, numSlide);
                editor2.commit();
            }
            if (numOption>0){
                SharedPreferences.Editor editor3 = save_option.edit();
                editor3.putInt("NumOptionSaved"+lvl_lsn_key, numOption);
                editor3.commit();
            }
            if (bg>0){
                SharedPreferences.Editor editor4 = save_bg.edit();
                editor4.putInt("NumBgSaved"+lvl_lsn_key, bg-1);
                editor4.commit();
            }
            if (countCorrect>0){
                SharedPreferences.Editor editor6 = save_correct.edit();
                editor6.putInt("NumCorrectSaved"+lvl_lsn_key, countCorrect);
                editor6.commit();
            }
            return;
        } else {
            Toast.makeText(getBaseContext(), "Нажмите еще раз, чтобы выйти в главное меню", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    public void getIntentSplash() {

        Intent intent = getIntent();
        if (intent != null) {
            lines_text = intent.getStringArrayListExtra("lines_text");
            lines_with_speech = intent.getIntegerArrayListExtra("lines_with_speech");
            speech_character = intent.getIntegerArrayListExtra("speech_character");
            lines_with_task_opt = intent.getIntegerArrayListExtra("lines_with_task_opt");
            task_opt_id = intent.getIntegerArrayListExtra("task_opt_id");

            bg_id = intent.getIntegerArrayListExtra("bg_id");
            bg_images = intent.getStringArrayListExtra("bg_images");
            lines_num = intent.getIntegerArrayListExtra("lines_num");
            bg_lines_num = intent.getIntegerArrayListExtra("arr1");
            bg_id_lines_num = intent.getIntegerArrayListExtra("arr2");

            bg_lines_num.add(lines_text.size()+1);
            bg_id_lines_num.add(-1);
            for (int i = 0; i < bg_lines_num.size(); i++) {
                int min = bg_lines_num.get(i);
                int min_i = i;
                for (int j = i+1; j < bg_lines_num.size(); j++) {
                    if (bg_lines_num.get(j) < min) {
                        min = bg_lines_num.get(j);
                        min_i = j;
                    }
                }
                if (i != min_i) {
                    int tmp = bg_lines_num.get(i);
                    int tmp2 = bg_id_lines_num.get(i);
                    bg_lines_num.set(i, bg_lines_num.get(min_i));
                    bg_lines_num.set(min_i, tmp);
                    bg_id_lines_num.set(i, bg_id_lines_num.get(min_i));
                    bg_id_lines_num.set(min_i, tmp2);
                }
            }

            characters_id = intent.getIntegerArrayListExtra("characters_id");
            characters_images = intent.getStringArrayListExtra("characters_images");
            characters_name = intent.getStringArrayListExtra("characters_name");
            characters_main = intent.getBooleanArrayExtra("characters_main");
            characters_minor = intent.getBooleanArrayExtra("characters_minor");

            task_option_id = intent.getIntegerArrayListExtra("task_option_id");
            correct_num = intent.getIntegerArrayListExtra("correct_num");
            first_opt = intent.getStringArrayListExtra("first_opt");
            second_opt = intent.getStringArrayListExtra("second_opt");
            third_opt = intent.getStringArrayListExtra("third_opt");
            maxCorrect = correct_num.size();
        }
    }

}

