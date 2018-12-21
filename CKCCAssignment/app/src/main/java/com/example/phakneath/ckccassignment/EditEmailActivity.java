package com.example.phakneath.ckccassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Dialog.LoadingDialog;
import com.example.phakneath.ckccassignment.sharePreferences.UserPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditEmailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView save;
    CircleImageView back;
    EditText email;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    LoadingDialog dialog = new LoadingDialog();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        initView();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        save.setOnClickListener(this::onClick);
        email.setText(user.getEmail().toString());
        email.setSelection(email.getText().length());
        back.setOnClickListener(this::onClick);
    }

    public void initView()
    {
        save = findViewById(R.id.setting);
        back = findViewById(R.id.back);
        email = findViewById(R.id.email);
    }

    public void changeEmail()
    {
        String password = UserPreferences.getPassword(this);
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail().toString(), password); // Current Login Credentials \\

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.d("ooooo", "User re-authenticated.");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(email.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(EditEmailActivity.this, "Please Check Your Email to Verify", Toast.LENGTH_LONG).show();
                                                        mAuth.signOut();
                                                        PostingActivity.activity.finish();
                                                        ProfileActivity.activity.finish();
                                                        SettingActivity.activity.finish();
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(EditEmailActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditEmailActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEmailActivity.this, e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == save)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(save.getWindowToken(), 0);
            if(!user.getEmail().equals(email.getText().toString().trim()) && !TextUtils.isEmpty(email.getText().toString().trim()))
            new FancyAlertDialog.Builder(EditEmailActivity.this)
                    .setTitle("Verification")
                    .setBackgroundColor(Color.parseColor("#00C4CC"))  //Don't pass R.color.colorvalue
                    .setMessage("By changing your email, you have to verify your new email and log in again. Are you sure you want to change your email?")
                    .setPositiveBtnBackground(Color.parseColor("#00C4CC"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("Yes")
                    .setNegativeBtnText("No")
                    .setAnimation(Animation.SIDE)
                    .isCancellable(false)
                    .setIcon(R.drawable.infos, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            dialog.show(getFragmentManager(), "dialogLoading");
                            changeEmail();
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {

                        }
                    }).build();
            else
            {
                Toast.makeText(this, "Please input a new email", Toast.LENGTH_SHORT).show();
            }
        }
        else if( v == back)
        {
            finish();
        }
    }
}
