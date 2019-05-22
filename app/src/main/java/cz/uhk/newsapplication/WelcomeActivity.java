package cz.uhk.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button buttonMode1 = findViewById(R.id.mode1);
        Button buttonMode2 = findViewById(R.id.mode2);
        Button buttonMode3 = findViewById(R.id.mode3);
        Button settingButton = findViewById(R.id.settingButton);

        buttonMode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,
                        WhatIsNewActivity.class));

            }
        });

        buttonMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,
                        FindSomethingActivity.class));
            }
        });

        buttonMode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,
                        FindDomainActivity.class));
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,
                        SettingsActivity.class));
            }
        });

    }
}
