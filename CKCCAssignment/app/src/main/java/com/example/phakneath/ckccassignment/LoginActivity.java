package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.sharePreferences.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import br.com.forusers.heinsinputdialogs.HeinsInputDialog;
import br.com.forusers.heinsinputdialogs.interfaces.OnInputStringListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView gotoRegister, forgetPassword;
    EditText email, password;
    Button login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        mAuth = FirebaseAuth.getInstance();
        gotoRegister.setOnClickListener(this::onClick);
        forgetPassword.setOnClickListener(this::onClick);
        login.setOnClickListener(this::onClick);
    }

    public void initView()
    {
        gotoRegister = findViewById(R.id.gotoRegister);
        forgetPassword = findViewById(R.id.forgetPassword);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
    }

    public void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //progressBar.setVisibility(View.GONE);
                    if(mAuth.getCurrentUser().isEmailVerified()) {
                        UserPreferences.save(LoginActivity.this, password);
                        Intent intent = new Intent(LoginActivity.this, PostingActivity.class);
                        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        new FancyAlertDialog.Builder(LoginActivity.this)
                                .setTitle("Verification")
                                .setBackgroundColor(Color.parseColor("#00C4CC"))  //Don't pass R.color.colorvalue
                                .setMessage("Thank you for signing in, Please verify your email before login !")
                                .setPositiveBtnBackground(Color.parseColor("#00C4CC"))  //Don't pass R.color.colorvalue
                                .setPositiveBtnText("VERIFY")
                                .setNegativeBtnText("Cancel")
                                .setAnimation(Animation.SIDE)
                                .isCancellable(false)
                                .setIcon(R.drawable.infos, Icon.Visible)
                                .OnPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(LoginActivity.this, "Please Check Your Email to Verify", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                }
                                            }
                                        });
                                    }
                                })
                                .OnNegativeClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {

                                    }
                                }).build();
                    }
                    Log.d("ooooo", "onComplete: " + mAuth.getCurrentUser().getIdToken(true));
                    Log.d("ooooo", "onComplete: " + mAuth.getCurrentUser().getUid());
                }
                else
                {
                    //progressBar.setVisibility(View.GONE);
                    String message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void openForgetPasswordDialog()
    {
        HeinsInputDialog dialog = new HeinsInputDialog(this);
        dialog.setPositiveButton("Submit", new OnInputStringListener()
        {
            @Override
            public boolean onInputString(AlertDialog alertDialog, String s) {
                if(TextUtils.isEmpty(s))
                {
                    Toast.makeText(LoginActivity.this, "Please write your valid email address before you submit.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                {
                    mAuth.sendPasswordResetEmail(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Please Check Your Email Account.", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error Occure : " + message + " Please try again.", Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    });

                }
                return false;
            }
        });
        dialog.setTitle("Reset Password");
        dialog.setHint("Email");
        dialog.setMessage("We will send you the link to reset your password after you submit.");
        dialog.setIcon(R.drawable.common_google_signin_btn_icon_dark);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == gotoRegister)
        {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
        else if(v == forgetPassword)
        {
            openForgetPasswordDialog();
        }
        else if(v == login)
        {
            if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
                Toast.makeText(this, "Please Enter Require Information above !", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email.getText().toString().trim(), password.getText().toString().trim());
            }
        }
    }
}
