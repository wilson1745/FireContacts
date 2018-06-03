package e.wilso.firecontacts;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
      String email = ed_email.getText().toString();
      String password = ed_password.getText().toString();

      SharedPreferences setting = getSharedPreferences("login", MODE_PRIVATE);
      setting.edit().putString("USER", email).apply();

      Log.d("AUTH", email + "/" + password);
      auth.signInWithEmailAndPassword(email, password);
   }
}
