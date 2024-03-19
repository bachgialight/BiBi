package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bibi.R;
import com.example.bibi.fragment.ExploreFragment;
import com.example.bibi.fragment.HomeFragment;
import com.example.bibi.fragment.ProfileFragment;
import com.example.bibi.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.body_menu, new HomeFragment())
                    .commit();
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.nav_explore) {
                    fragment = new ExploreFragment(); // Thay ExploreFragment bằng Fragment bạn muốn hiển thị
                } else if (itemId == R.id.nav_search) {
                    fragment = new SearchFragment(); // Thay SearchFragment bằng Fragment bạn muốn hiển thị
                } else if (itemId == R.id.nav_profile) {
                    fragment = new ProfileFragment(); // Thay ProfileFragment bằng Fragment bạn muốn hiển thị
                }

                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.body_menu, fragment)
                            .commit();
                }

                return true;
            }
        });
    }

}