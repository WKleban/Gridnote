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

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailText;
    EditText loginPassText;
    Button loginButton;
    Button goToRegisterButton;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.login_et_login);
        loginPassText = findViewById(R.id.password_et_login);
        loginButton = findViewById(R.id.button_login);
        goToRegisterButton = findViewById(R.id.button_dont_have_acc_login);
        loginProgress = findViewById(R.id.login_progress);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginEmail = loginEmailText.getText().toString();
                String loginPassword = loginPassText.getText().toString();

                if (!TextUtils.isEmpty(loginEmail)&&!TextUtils.isEmpty(loginPassword)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendToMain();
                            }
                            else{
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error: "+errorMessage,Toast.LENGTH_LONG).show();
                            }

                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }


            }
        });


        goToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();
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
        Intent mainIntent = new Intent (LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


}
