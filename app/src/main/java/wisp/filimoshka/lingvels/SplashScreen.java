package wisp.filimoshka.lingvels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SplashScreen extends AppCompatActivity {

    public TextView text_lesson;
    public TextView text_level;

    public ArrayList<Object> levels = new ArrayList<Object>();

    private FirebaseDatabase database;
    private DatabaseReference databaseLines;
    private DatabaseReference databaseBg;
    private DatabaseReference databaseCharacters;
    private DatabaseReference databaseOptions;


    private String LINES_KEY = Consts.LINES_KEY;
    private String BACKGROUNDS_KEY = Consts.BACKGROUNDS_KEY;
    private String CHARACTERS_KEY = Consts.CHARACTERS_KEY;
    private String TASK_OPTIONS_KEY = Consts.TASK_OPTIONS_KEY;

    public ArrayList<LinesDB> allLines = new ArrayList<LinesDB>();
    public ArrayList<BackgroundsDB> allBg = new ArrayList<BackgroundsDB>();
    public ArrayList<CharactersDB> allCharacters = new ArrayList<CharactersDB>();
    public ArrayList<OptionsDB> allOptions = new ArrayList<OptionsDB>();

    // массивы для хранения данных из таблицы строк
    public ArrayList<String> lines_text = new ArrayList<String>();
    public ArrayList<Integer> lines_with_speech = new ArrayList<Integer>();
    public ArrayList<Integer> speech_character = new ArrayList<Integer>();
    public ArrayList<Integer> lines_with_task_opt = new ArrayList<Integer>();
    public ArrayList<Integer> task_opt_id = new ArrayList<Integer>();
    public ArrayList<Integer> lines_id = new ArrayList<Integer>();
    public int first_id_number; // параметр, который будет использоваться для перевода id в номера слайдов

    // массивы для хранения данных из таблицы фонов
    public ArrayList<String> bg_images = new ArrayList<String>();
    public ArrayList<Integer> bg_id = new ArrayList<Integer>();
    public ArrayList<ArrayList<Integer>> lines_num = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> arr1 = new ArrayList<Integer>();
    ArrayList<Integer> arr2 = new ArrayList<Integer>();

    // массивы для хранения данных из таблицы персонажей
    public ArrayList<String> characters_name = new ArrayList<String>();
    public ArrayList<Integer> characters_id = new ArrayList<Integer>();
    public ArrayList<String> characters_images = new ArrayList<String>();
    public ArrayList<Boolean> characters_main = new ArrayList<Boolean>();
    public ArrayList<Boolean> characters_minor = new ArrayList<Boolean>();
    public boolean[] characters_main_arr;
    public boolean[] characters_minor_arr;
    public static ArrayList<RequestBuilder<Drawable>> characters_imgs = new ArrayList<RequestBuilder<Drawable>>();

    // массивы для хранения данных из таблицы вариантов ответов
    public ArrayList<Integer> task_option_id = new ArrayList<Integer>();
    public ArrayList<String> first_opt = new ArrayList<String>();
    public ArrayList<String> second_opt = new ArrayList<String>();
    public ArrayList<String> third_opt = new ArrayList<String>();
    public ArrayList<Integer> correct_num = new ArrayList<Integer>();

    private SharedPreferences save_lesson;
    private SharedPreferences save_level;

    private String lvl_lsn_key;

    int lineCount = 128;
    int bgCount = 5;
    int charCount = 7;
    int toCount = 14;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
        final int lesson = save_lesson.getInt("Lesson", 1);

        save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
        final String level = save_level.getString("Level", Consts.INTERMEDIATE);
        final String level_txt = save_level.getString("Level_txt", Consts.INTERMEDIATE_TEXT);

        lvl_lsn_key = "_" + level + "_" + lesson;


        if ((level.equals(Consts.INTERMEDIATE))&&(lesson == 1)) {
            // пусто
        } else {
            LINES_KEY = LINES_KEY + lvl_lsn_key;
            BACKGROUNDS_KEY = BACKGROUNDS_KEY + lvl_lsn_key;
            CHARACTERS_KEY = CHARACTERS_KEY + lvl_lsn_key;
            TASK_OPTIONS_KEY = TASK_OPTIONS_KEY + lvl_lsn_key;

            lineCount = StartSplashScreen.line_count.get(lesson-1);
            bgCount = StartSplashScreen.bg_count.get(lesson-1);
            charCount = StartSplashScreen.char_count.get(lesson-1);
            toCount = StartSplashScreen.to_count.get(lesson-1);
        }


        levels.add(Intermediate.class);
        levels.add(Upper.class);
        levels.add(Advanced.class);

        database = FirebaseDatabase.getInstance("https://lingvels-default-rtdb.europe-west1.firebasedatabase.app");
        databaseBg = database.getReference(BACKGROUNDS_KEY);
        databaseLines = database.getReference(LINES_KEY);
        databaseCharacters = database.getReference(CHARACTERS_KEY);
        databaseOptions = database.getReference(TASK_OPTIONS_KEY);


        // загружаем данные из бд
        getDataFromDB(lesson, level);


        text_lesson = (TextView) findViewById(R.id.text_lesson);
        text_level = (TextView) findViewById(R.id.text_level);

        text_lesson.setText("Lesson " + lesson);
        text_level.setText(level_txt + "   Level");

    }


    boolean check = false;

    private boolean getDataFromDB(int lesson, String level) {

        ValueEventListener vListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // очищаем все массивы
                if (allLines.size() > 0) allLines.clear();

                if (lines_text.size() > 0) lines_text.clear();
                if (lines_with_speech.size() > 0) lines_with_speech.clear();
                if (lines_with_task_opt.size() > 0) lines_with_task_opt.clear();
                if (speech_character.size() > 0) speech_character.clear();
                if (task_opt_id.size() > 0) task_opt_id.clear();
                if (lines_id.size() > 0) lines_id.clear();


                // получаем из бд все строки
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LinesDB line = ds.getValue(LinesDB.class);
                    assert line != null;
                    allLines.add(line);
                }


                // сортируем полученные данные
                Collections.sort(allLines);

                // добавляем полученные данные о строках в массивы
                for (int i = 0; i < allLines.size(); i++) {
                    lines_text.add(allLines.get(i).text_line);
                    lines_id.add(allLines.get(i).id_line);

                    if (allLines.get(i).speech) {
                        lines_with_speech.add(allLines.get(i).id_line);
                        speech_character.add(allLines.get(i).character_id);
                    }

                    if (allLines.get(i).task_opt) {
                        lines_with_task_opt.add(allLines.get(i).id_line);
                        task_opt_id.add(allLines.get(i).task_opt_id);
                    }
                }


                // приводим id в полученных массивах в соответствие с нумерацией слайдов (1, 2, 3, ...)
                first_id_number = lines_id.get(0);
                for (int i = 0; i < lines_id.size(); i++) {
                    lines_id.set(i, lines_id.get(i) - first_id_number + 1);
                }
                for (int i = 0; i < lines_with_speech.size(); i++) {
                    lines_with_speech.set(i, lines_with_speech.get(i) - first_id_number + 1);
                }
                for (int i = 0; i < lines_with_task_opt.size(); i++) {
                    lines_with_task_opt.set(i, lines_with_task_opt.get(i) - first_id_number + 1);
                }


                // если массив строк остается пустым больше 20 сек, появляется оповещение об ошибке загрузки
                long time = System.currentTimeMillis();
                check = false;
                do {
                    if (lines_text.size() == lineCount) {
                        check = true;
                        break;
                    } else check = false;
                } while (System.currentTimeMillis() - time < 20000);

                if (check == false) {
                    Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashScreen.this, Intermediate.class);
                    startActivity(intent);
                }






                ValueEventListener bgListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // очищаем все массивы
                        if (allBg.size() > 0) allBg.clear();

                        if (bg_id.size() > 0) bg_id.clear();
                        if (bg_images.size() > 0) bg_images.clear();
                        if (lines_num.size() > 0) lines_num.clear();

                        // получаем из бд все фоны
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            BackgroundsDB bg = ds.getValue(BackgroundsDB.class);
                            assert bg != null;
                            allBg.add(bg);
                        }

                        // сортируем полученные данные
                        Collections.sort(allBg);

                        final ImageView back = (ImageView) findViewById(R.id.task_background);

                        // добавляем полученные данные о фонах в массивы
                        for (int i = 0; i < allBg.size(); i++) {
                            bg_images.add(allBg.get(i).bg_img);
                            bg_id.add(allBg.get(i).id_bg);
                            if (allBg.get(i).line_numbers.size() > 0) {
                                lines_num.add(allBg.get(i).line_numbers);
                            }
                        }

                        arr1.clear();
                        arr2.clear();
                        for (int i=0; i<lines_num.size(); i++) {
                            for (int j=0; j<lines_num.get(i).size(); j++) {
                                arr1.add(lines_num.get(i).get(j));
                                arr2.add(bg_id.get(i));
                            }
                        }


                        // если массив строк остается пустым больше 20 сек, появляется оповещение об ошибке загрузки
                        long time = System.currentTimeMillis();
                        check = false;
                        do {
                            if (bg_id.size() == bgCount) {
                                check = true;
                                break;
                            } else check = false;
                        } while (System.currentTimeMillis() - time < 20000);

                        if (check == false) {
                            Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplashScreen.this, Intermediate.class);
                            startActivity(intent);
                        }







                        ValueEventListener charListener = new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                // очищаем все массивы
                                if (allCharacters.size() > 0) allCharacters.clear();

                                if (characters_name.size() > 0) characters_name.clear();
                                if (characters_id.size() > 0) characters_id.clear();
                                if (characters_images.size() > 0) characters_images.clear();
                                if (characters_main.size() > 0) characters_main.clear();
                                if (characters_minor.size() > 0) characters_minor.clear();


                                // получаем из бд всех персонажей
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    CharactersDB characters = ds.getValue(CharactersDB.class);
                                    assert characters != null;
                                    allCharacters.add(characters);
                                }

                                // сортируем полученные данные
                                Collections.sort(allCharacters);

                                final ImageView pers_main = (ImageView) findViewById(R.id.pers_main);
                                final ImageView pers_minor = (ImageView) findViewById(R.id.pers_minor);

                                // добавляем полученные данные о персонажах в массивы
                                for (int i = 0; i < allCharacters.size(); i++) {
                                    characters_id.add(allCharacters.get(i).id_character);
                                    characters_images.add(allCharacters.get(i).character_img);
                                    characters_name.add(allCharacters.get(i).character_name);
                                    characters_main.add(allCharacters.get(i).main_character);
                                    characters_minor.add(allCharacters.get(i).minor_character);


                                }

                                // intent extra не читает allaylist of boolean, поэтому переводим в array
                                characters_main_arr = new boolean[characters_main.size()];
                                characters_minor_arr = new boolean[characters_minor.size()];
                                for (int i = 0; i < characters_main.size(); i++) {
                                    characters_main_arr[i] = characters_main.get(i);
                                }
                                for (int i = 0; i < characters_minor.size(); i++) {
                                    characters_minor_arr[i] = characters_minor.get(i);
                                }


                                // если массив строк остается пустым больше 20 сек, появляется оповещение об ошибке загрузки
                                // как проверить, что массив строк загрузился до конца? (кол-во записей должно быть равно 128)
                                long time = System.currentTimeMillis();
                                check = false;
                                do {
                                    if (characters_images.size() == charCount) {
                                        check = true;
                                        break;
                                    } else check = false;
                                } while (System.currentTimeMillis() - time < 20000);

                                if (check == false) {
                                    Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SplashScreen.this, Intermediate.class);
                                    startActivity(intent);
                                }






                                ValueEventListener optListener = new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        // очищаем все массивы
                                        if (allOptions.size() > 0) allOptions.clear();

                                        if (task_option_id.size() > 0) task_option_id.clear();
                                        if (first_opt.size() > 0) first_opt.clear();
                                        if (second_opt.size() > 0) second_opt.clear();
                                        if (third_opt.size() > 0) third_opt.clear();
                                        if (correct_num.size() > 0) correct_num.clear();


                                        // получаем из бд все варианты ответов к заданиям
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            OptionsDB opt = ds.getValue(OptionsDB.class);
                                            assert opt != null;
                                            allOptions.add(opt);
                                        }

                                        // сортируем полученные данные
                                        Collections.sort(allOptions);


                                        // добавляем полученные данные о вариантах ответов в массивы
                                        for (int i = 0; i < allOptions.size(); i++) {
                                            task_option_id.add(allOptions.get(i).id_task_opt);
                                            correct_num.add(allOptions.get(i).correct_num);
                                            first_opt.add(allOptions.get(i).first_opt);
                                            second_opt.add(allOptions.get(i).second_opt);
                                            third_opt.add(allOptions.get(i).third_opt);
                                        }


                                        // если массив строк остается пустым больше 20 сек, появляется оповещение об ошибке загрузки
                                        // как проверить, что массив строк загрузился до конца? (кол-во записей должно быть равно 128)
                                        long time = System.currentTimeMillis();
                                        check = false;
                                        do {
                                            if (task_option_id.size() == toCount) {
                                                check = true;
                                                break;
                                            } else check = false;

                                        } while (System.currentTimeMillis() - time < 20000);

                                        if (check == false) {
                                            Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                                            int lvl = -1;
                                            if (level.equals(Consts.INTERMEDIATE)) {
                                                lvl = 1;
                                            } else if (level.equals(Consts.UPPER)) {
                                                lvl = 2;
                                            } else if (level.equals(Consts.ADVANCED)) {
                                                lvl = 3;
                                            }
                                            Intent intent = new Intent(SplashScreen.this, (Class<?>) levels.get(lvl - 1));
                                            startActivity(intent);
                                        }

                                        if ((lines_text.size()==lineCount)&&(bg_images.size()==bgCount)&&(characters_images.size()==charCount)&&(task_option_id.size()==toCount)) {
                                            Intent intent = new Intent(SplashScreen.this, Lesson.class);
                                            intent.putExtra("lines_text", lines_text);
                                            intent.putExtra("lines_with_speech", lines_with_speech);
                                            intent.putExtra("speech_character", speech_character);
                                            intent.putExtra("lines_with_task_opt", lines_with_task_opt);
                                            intent.putExtra("task_opt_id", task_opt_id);

                                            intent.putExtra("bg_id", bg_id);
                                            intent.putExtra("bg_images", bg_images);
                                            intent.putExtra("lines_num", lines_num);
                                            intent.putExtra("arr1", arr1);
                                            intent.putExtra("arr2", arr2);

                                            intent.putExtra("characters_id", characters_id);
                                            intent.putExtra("characters_images", characters_images);
                                            intent.putExtra("characters_name", characters_name);
                                            intent.putExtra("characters_main", characters_main_arr);
                                            intent.putExtra("characters_minor", characters_minor_arr);

                                            intent.putExtra("task_option_id", task_option_id);
                                            intent.putExtra("correct_num", correct_num);
                                            intent.putExtra("first_opt", first_opt);
                                            intent.putExtra("second_opt", second_opt);
                                            intent.putExtra("third_opt", third_opt);

                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SplashScreen.this, Intermediate.class);
                                            startActivity(intent);
                                        }


                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                };
                                databaseOptions.addValueEventListener(optListener);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        };
                        databaseCharacters.addValueEventListener(charListener);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                databaseBg.addValueEventListener(bgListener);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseLines.addValueEventListener(vListener);

        return check;
    }
}