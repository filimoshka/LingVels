package wisp.filimoshka.lingvels;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class StartSplashScreen extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference databaseCounts;
    private DatabaseReference databaseUsers;
    private SharedPreferences save_user_exist;
    private SharedPreferences save_user_email;
    private SharedPreferences save_result;
    private SharedPreferences save_res_size;

    private String COUNTS_KEY = Consts.COUNTS_KEY;
    private String USERS_KEY = Consts.USERS_KEY;

    public ArrayList<CountsDB> allCounts = new ArrayList<CountsDB>();

    // массивы для хранения данных из таблицы
    public static ArrayList<Integer> id_count = new ArrayList<Integer>();
    public static ArrayList<Integer> char_count = new ArrayList<Integer>();
    public static ArrayList<Integer> bg_count = new ArrayList<Integer>();
    public static ArrayList<Integer> line_count = new ArrayList<Integer>();
    public static ArrayList<Integer> to_count = new ArrayList<Integer>();

    public static ArrayList<UsersDB> allUsers = new ArrayList<UsersDB>();
    public static ArrayList<String> emails = new ArrayList<String>();
    public static ArrayList<ArrayList<Float>> results = new ArrayList<ArrayList<Float>>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_splash_screen);


        database = FirebaseDatabase.getInstance("https://lingvels-default-rtdb.europe-west1.firebasedatabase.app");
        databaseCounts = database.getReference(COUNTS_KEY);
        databaseUsers = database.getReference(USERS_KEY);

        // загружаем данные из бд
        getDataFromDB();

    }

    boolean check = false;

    private boolean getDataFromDB() {

        ValueEventListener vListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // очищаем все массивы
                if (allCounts.size() > 0) allCounts.clear();

                // получаем из бд все строки
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CountsDB count = ds.getValue(CountsDB.class);
                    assert count != null;
                    allCounts.add(count);
                }

                // сортируем полученные данные
                Collections.sort(allCounts);

                // добавляем полученные данные о строках в массивы
                for (int i = 0; i < allCounts.size(); i++) {

                    id_count.add(allCounts.get(i).id_count);
                    bg_count.add(allCounts.get(i).bg_count);
                    char_count.add(allCounts.get(i).char_count);
                    to_count.add(allCounts.get(i).to_count);
                    line_count.add(allCounts.get(i).line_count);
                }



                // если массив строк остается пустым больше 20 сек, появляется оповещение об ошибке загрузки
                // как проверить, что массив строк загрузился до конца? (кол-во записей должно быть равно 128)
                long time = System.currentTimeMillis();
                check = false;
                do {
                    if (id_count.size() == 2) {
                        check = true;
                        break;
                    } else check = false;
                } while (System.currentTimeMillis() - time < 20000);


                if (check == false) {
                    Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartSplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }

                if (id_count.size()==2) {
                    Intent intent = new Intent(StartSplashScreen.this, Menu.class);
                    intent.putExtra("line_count", line_count);
                    intent.putExtra("bg_count", bg_count);
                    intent.putExtra("char_count", char_count);
                    intent.putExtra("to_count", to_count);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StartSplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }

                ValueEventListener uListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (allUsers.size() > 0) allUsers.clear();
                        if (emails.size() > 0) emails.clear();
                        if (results.size() > 0) results.clear();


                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UsersDB user = ds.getValue(UsersDB.class);
                            assert user != null;
                            allUsers.add(user);
                        }

                        save_user_email = getSharedPreferences("SaveUserEmail", MODE_PRIVATE);

                        save_user_exist = getSharedPreferences("SaveUserExist", MODE_PRIVATE);

                        save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);

                        save_res_size = getSharedPreferences("SaveResSize", MODE_PRIVATE);

                        for (int i = 0; i < allUsers.size(); i++) {
                            emails.add(allUsers.get(i).email);
                            results.add(allUsers.get(i).results);

                            SharedPreferences.Editor ed = save_user_exist.edit();
                            ed.putBoolean("Exist" + allUsers.get(i).email, true);
                            ed.commit();

                            for (int j = 0; j < allUsers.get(i).results.size(); j++) {

                                String lvl_lsn_key = "_intermediate" + "_" + (j+1);
                                SharedPreferences.Editor ed1 = save_result.edit();
                                ed1.putFloat("ResultSaved" + lvl_lsn_key, allUsers.get(i).results.get(j));
                                ed1.commit();

                                SharedPreferences.Editor ed2 = save_res_size.edit();
                                ed2.putInt("ResSizeDB", j+1);
                                ed2.commit();

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                databaseUsers.addValueEventListener(uListener);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseCounts.addValueEventListener(vListener);

        return check;
    }
}