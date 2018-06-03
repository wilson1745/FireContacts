package e.wilso.firecontacts;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
   FirebaseAuth auth;
   FirebaseAuth.AuthStateListener authListener;
   private String userUID;
   EditText ed_email;
   EditText ed_password;
   Button btnlogin;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      ed_email = findViewById(R.id.ed_email);
      ed_password = findViewById(R.id.ed_password);
      btnlogin = findViewById(R.id.button2);
      btnlogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            login(v);
         }
      });

      SharedPreferences setting = getSharedPreferences("login", MODE_PRIVATE);
      ed_email.setText(setting.getString("USER", ""));

      auth = FirebaseAuth.getInstance();
      authListener = new FirebaseAuth.AuthStateListener() {
         @Override
         public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
               Log.d("onAuthStateChanged", "登入:" + user.getUid());
               userUID = user.getUid();
            }
            else {
               Log.d("onAuthStateChanged", "已登出");
            }
         }
      };
   }

   @Override
   protected void onStart() {
      super.onStart();
      auth.addAuthStateListener(authListener);
      auth.signOut();
   }

   @Override
   protected void onStop() {
      super.onStop();
      auth.removeAuthStateListener(authListener);
   }

   public void login(View view) {
      final String email = ed_email.getText().toString();
      final String password = ed_password.getText().toString();

      SharedPreferences setting = getSharedPreferences("login", MODE_PRIVATE);
      setting.edit().putString("USER", email).apply();

      Log.d("AUTH", email + "/" + password);
      auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
               Log.d("onComplete", "登入失敗");
               register(email, password);
            }
         }
      });
   }

   private void register(final String email, final String password) {
      new AlertDialog.Builder(LoginActivity.this)
              .setTitle("登入問題")
              .setMessage("無此帳號，是否要以此帳號與密碼註冊?")
              .setPositiveButton("註冊",
                      new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                            createUser(email, password);
                         }
                      })
              .setNeutralButton("取消", null)
              .show();

   }

   private void createUser(String email, String password) {
      auth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                            String message = task.isComplete() ? "註冊成功" : "註冊失敗";
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();
                         }
                      });
   }
}
