package com.example.socialstorybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.socialstorybuilder.ui.login.AdultLoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
    }

    public void toChildLogin(View view){
        Intent intent = new Intent(this, ChildLoginActivity.class);
        startActivity(intent);
    }

    public void toAdultLogin(View view){
        Intent intent = new Intent(this, AdultLoginActivity.class);
        startActivity(intent);
    }

}
