package com.example.phakneath.ckcc_homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.phakneath.ckcc_homework.Data.Database;
import com.example.phakneath.ckcc_homework.Data.PersonInMemory;
import com.example.phakneath.ckcc_homework.Entity.person;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password;
    Button register;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.btnregister);
        login = findViewById(R.id.login);

        register.setOnClickListener(v->{
            person per = new person(username.getText().toString(), email.getText().toString(),password.getText().toString());

            PersonInMemory repository = Database.getPersonInMemory();
            repository.savePerson(per);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        login.setOnClickListener(v->
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
