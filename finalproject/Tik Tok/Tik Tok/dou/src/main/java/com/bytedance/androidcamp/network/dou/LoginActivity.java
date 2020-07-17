package com.bytedance.androidcamp.network.dou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText StudentID;
    private EditText Pass;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView(){
        Pass = findViewById(R.id.Pass);
        StudentID = findViewById(R.id.ID);
        button = findViewById(R.id.Login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("data", StudentID.getText().toString());
                startActivity(intent);
            }
        });
    }
}
