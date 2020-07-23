package pl.wotu.gridnote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameET;
    private EditText emailET;
    private EditText passwordET1;
    private EditText passwordET2;
    private Button registerButton;
    private Button goToLoginButton;
    private ProgressBar regProgress;
    private FirebaseAuth mAuth;
//    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailET = findViewById(R.id.login_et_register);
        usernameET = findViewById(R.id.username_et_register);
        passwordET1 = findViewById(R.id.password1_et_register);
        passwordET2 = findViewById(R.id.password2_et_register);
        registerButton = findViewById(R.id.button_register);
        goToLoginButton = findViewById(R.id.button_have_acc_register);
        regProgress = findViewById(R.id.reg_progress);

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String regEmail = emailET.getText().toString();
                final String regUsername = usernameET.getText().toString();
                final String regPassword = passwordET1.getText().toString();
                String regPassword2 = passwordET2.getText().toString();

                if (!TextUtils.isEmpty(regEmail)&&!TextUtils.isEmpty(regUsername)&&!TextUtils.isEmpty(regPassword)&&!TextUtils.isEmpty(regPassword2)){
                    regProgress.setVisibility(View.VISIBLE);
                    if (regPassword.equals(regPassword2)){
                        mAuth.createUserWithEmailAndPassword(regEmail,regPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mAuth.signInWithEmailAndPassword(regEmail,regPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()){

                                                storeUserSettings(regUsername,mAuth.getCurrentUser().getUid());
                                                sendToMain();
                                            }
                                            else{
                                                String errorMessage = task.getException().getMessage();
                                                Toast.makeText(RegisterActivity.this,"Error: "+errorMessage,Toast.LENGTH_LONG).show();
                                            }

//                                            loginProgress.setVisibility(View.INVISIBLE);
                                        }
                                    });

//                                    Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
//                                    startActivity(setupIntent);
//                                    finish();
                                }
                                else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error: "+errorMessage,Toast.LENGTH_LONG).show();
                                }
                                regProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Wpisane hasła są różne!",Toast.LENGTH_LONG).show();
                    }


                }


            }
        });

    }

    private void storeUserSettings(String regUsername,String user_id) {
        Toast.makeText(RegisterActivity.this,"user_id"+user_id,Toast.LENGTH_LONG).show();

        Map<String,String> userMap = new HashMap<>();
        userMap.put("name",regUsername);
        db.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Użytkownik został zarejestrowany",Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else {
                    String errorMessage = task.getException().getMessage().toString();
                    Toast.makeText(RegisterActivity.this,"Firestore Error: "+errorMessage,Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }


    }

    private void sendToMain() {
        Intent mainIntent = new Intent (RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
