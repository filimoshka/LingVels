package wisp.filimoshka.lingvels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Final extends AppCompatActivity {

    private int countCorrect;
    private int maxCorrect;

    public ArrayList<Float> results = new ArrayList<Float>();
    private SharedPreferences save_result;
    private DatabaseReference database;
    private SharedPreferences save_user_exist;
    private SharedPreferences save_user_email;
    private SharedPreferences save_lesson;
    private SharedPreferences save_level;

    private String USERS_KEY = Consts.USERS_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final1);

        getIntentSplash();

        final TextView correctNum = (TextView) findViewById(R.id.correctNum2);
        final TextView maxNum = (TextView) findViewById(R.id.maxNum2);

        correctNum.setText(String.valueOf(countCorrect));
        maxNum.setText(String.valueOf(maxCorrect));

        save_user_email = getSharedPreferences("SaveUserEmail", MODE_PRIVATE);
        final String save_email = save_user_email.getString("Email", null);

        save_user_exist = getSharedPreferences("SaveUserExist", MODE_PRIVATE);
        final boolean save_exist = save_user_exist.getBoolean("Exist" + save_email, false);

        System.out.println("updating?????????????????????????????????????????");
        updateDB(save_exist, save_email);

        // кнопка "назад"
        TextView back_btn3 = (TextView) findViewById(R.id.btn_back3);
        back_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Final.this, Intermediate.class);
                    startActivity(intent); finish();
                } catch (Exception e) {

                }
            }
        });



        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void getIntentSplash() {

        Intent intent = getIntent();
        if (intent != null) {
            countCorrect = intent.getIntExtra("countCorrect", 0);
            maxCorrect = intent.getIntExtra("maxCorrect", 0);

        }
    }

    public void updateDB(boolean save_exist, String save_email) {

        if (save_exist) {

            save_lesson = getSharedPreferences("SaveLesson", MODE_PRIVATE);
            final int lesson = save_lesson.getInt("Lesson", 1);

            save_level = getSharedPreferences("SaveLevel", MODE_PRIVATE);
            final String level = save_level.getString("Level", Consts.INTERMEDIATE);

            for (int i=1; i<=lesson; i++) {

                String lvl_lsn_key = "_" + level + "_" + i;

                save_result = getSharedPreferences("SaveResult", MODE_PRIVATE);
                float result_saved = save_result.getFloat("ResultSaved" + lvl_lsn_key, 0);

                results.add(result_saved);
            }

            if (save_email != null) {
                database = FirebaseDatabase.getInstance("https://lingvels-default-rtdb.europe-west1.firebasedatabase.app").getReference(USERS_KEY);
                ValueEventListener vListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UsersDB user = ds.getValue(UsersDB.class);
                            if (user.email.equals(save_email)) {
                                ds.getRef().child("results").setValue(results);
                                System.out.println("results updated");
                                break;
                            } } }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }; database.addListenerForSingleValueEvent(vListener);
            }
        }
    }
}
