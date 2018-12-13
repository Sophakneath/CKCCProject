package com.example.phakneath.ckcc_homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakneath.ckcc_homework.Data.Database;
import com.example.phakneath.ckcc_homework.Entity.person;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login;
    TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btnlogin);
        register = findViewById(R.id.signup);

        login.setOnClickListener(v->
        {
            person per = new person(username.getText().toString(),password.getText().toString());

            List<person> p = Database.getPersonInMemory().getPersons();
            person personsession = null;
            for(person person1:p)
            {
                if(per.getUsername().equals(person1.getUsername()) && per.getPassword().equals(person1.getPassword()))
                {
                    personsession = person1;
                    break;
                }
            }

            if(personsession != null)
            {
                Preferences.save(this,personsession);
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("username", personsession.getUsername());
                intent.putExtra("email", personsession.getEmail());
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(this,"Login Again", Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(v->
        {
            Intent intent1 = new Intent(this, RegisterActivity.class);
            startActivity(intent1);
            finish();
        });

    }
}
