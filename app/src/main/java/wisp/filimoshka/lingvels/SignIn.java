package wisp.filimoshka.lingvels;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class SignIn extends AppCompatActivity {

    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;
    TextView signOutBtn;
    TextView accText;
    public static String userEmail;
    public static String userName;
    public static boolean exist = false;
    public ArrayList<Float> results = new ArrayList<Float>();
    public int res_size_db = 0;

    private DatabaseReference database;
    private String USERS_KEY = Consts.USERS_KEY;

    private SharedPreferences save_result;
    private SharedPreferences save_lesson;
    private SharedPreferences save_level;
    private SharedPreferences save_res_size;

    private SharedPreferences save_user_exist;
    private SharedPreferences save_user_email;

    public static Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sign_in);

        // кнопка "назад"
        TextView back_btn = (TextView) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SignIn.this, Menu.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });

        signInButton = findViewById(R.id.btn_google_signin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signOutBtn = (TextView) findViewById(R.id.sign_out);
        signOutBtn.setPaintFlags(signOutBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        accText = (TextView) findViewById(R.id.acc_text);

        save_user_email = getSharedPreferences("SaveUserEmail", MODE_PRIVATE);
        String save_email = save_user_email.getString("Email", null);

        save_user_exist = getSharedPreferences("SaveUserExist", MODE_PRIVATE);

        //диалог для восстановления прогресса
        dialog = new Dialog(this); // создаем диалоговое окно
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // без заголовка
        dialog.setContentView(R.layout.restore_dialog); // шаблон диалогового окна
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачный фон
        dialog.setCancelable(false); // возможно закрыть окно системной кнопкой "назад"

        final Button btn_yes = (Button) dialog.findViewById(R.id.btn_yes);
        final Button btn_no = (Button) dialog.findViewById(R.id.btn_no);


        // проверяем последнего авторизовавшегося пользователя
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            accText.setText("Вы вошли в аккаунт: " + acct.getEmail());
            signOutBtn.setText("Выйти из аккаунта");
            signInButton.setEnabled(false);

            userEmail = acct.getEmail();
            userName = acct.getDisplayName();

            SharedPreferences.Editor ed = save_user_email.edit();
            ed.putString("Email", userEmail);
            ed.commit();


        } else {
            accText.setText("");
            signOutBtn.setText("");
            signInButton.setEnabled(true);
        }

        
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences save = getSharedPreferences("Save", MODE_PRIVATE);
                    int task = res_size_db+1;
                    SharedPreferences.Editor editor = save.edit();
                    editor.putInt("Task", task);
                    editor.commit();

                    save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
                    int lesson = res_size_db;
                    SharedPreferences.Editor editor2 = save_lesson.edit();
                    editor2.putInt("Lesson", lesson);
                    editor2.commit();

                    dialog.cancel();
                    Toast.makeText(getBaseContext(), "Прогресс восстановлен!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.cancel();
                } catch (Exception e) {

                }
            }
        });


        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {

            GoogleSignInAccount account = task.getResult(ApiException.class);
            google_sign_in_ok(account);

        } catch (Exception e) {
            google_sign_in_ok(null);
        }
    }

    private void google_sign_in_ok(GoogleSignInAccount account) {

        if (account == null) { // ошибка авторизации
            Toast.makeText(getBaseContext(), "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            signInButton.setEnabled(true);

        } else {

            accText.setText("Вы вошли в аккаунт: " + account.getEmail());
            signOutBtn.setText("Выйти из аккаунта");
            signInButton.setEnabled(false);

            exist = false;

            save_user_email = getSharedPreferences("SaveUserEmail", MODE_PRIVATE);
            save_user_exist = getSharedPreferences("SaveUserExist", MODE_PRIVATE);
            boolean save_exist = save_user_exist.getBoolean("Exist" + account.getEmail(), false);

            exist = save_exist;

            userEmail = account.getEmail();
            userName = account.getDisplayName();

            SharedPreferences.Editor ed = save_user_email.edit();
            ed.putString("Email", userEmail);
            ed.commit();

            String str = "Добро пожаловать, " + account.getDisplayName() + "!";
            Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();

            save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
            final int lesson = save_lesson.getInt("Lesson", 1);
            save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
            final String level = save_level.getString("Level", Consts.INTERMEDIATE);
            save_res_size = getSharedPreferences("SaveResSize", MODE_PRIVATE);
            res_size_db = save_res_size.getInt("ResSizeDB", 0);


            if (results.size() > 0) results.clear();

            save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
            float first_res = save_result.getFloat("ResultSaved_intermediate_1", 0);

            if ((res_size_db>=lesson)&& (first_res != 0)) {

                // запросить восстановление прогресса после входа в аккаунт
                // при подтверждении сохранить все из БД в SharedPreferences
                dialog.show();

                for (int i = 1; i <= res_size_db; i++) {
                    String lvl_lsn_key = "_" + level + "_" + i;
                    save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
                    float result_saved = save_result.getFloat("ResultSaved" + lvl_lsn_key, 0);
                    results.add(result_saved);
                }

            } else {
                for (int i = 1; i <= lesson; i++) {
                    String lvl_lsn_key = "_" + level + "_" + i;
                    save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
                    float result_saved = save_result.getFloat("ResultSaved" + lvl_lsn_key, 0);
                    results.add(result_saved);
                }
            }

            if (exist) {
                database = FirebaseDatabase.getInstance("https://lingvels-default-rtdb.europe-west1.firebasedatabase.app").getReference(USERS_KEY);
                ValueEventListener vListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UsersDB user = ds.getValue(UsersDB.class);
                            if (user.email.equals(account.getEmail())) {
                                ds.getRef().child("results").setValue(results);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }; database.addListenerForSingleValueEvent(vListener);

            } else {
                database = FirebaseDatabase.getInstance("https://lingvels-default-rtdb.europe-west1.firebasedatabase.app").getReference(USERS_KEY);

                // добавляем пользователя в бд
                UsersDB newUser = new UsersDB(account.getEmail(), account.getDisplayName(), results);
                database.push().setValue(newUser);

                SharedPreferences.Editor ed1 = save_user_exist.edit();
                ed1.putBoolean("Exist" + account.getEmail(), true);
                ed1.commit();
                SharedPreferences.Editor ed2 = save_user_email.edit();
                ed2.putString("Email", account.getEmail());
                ed2.commit();
            }

        }
    }

    void signOut(){
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                accText.setText("");
                signOutBtn.setText("");
                signInButton.setEnabled(true);
            }
        });
    }
}