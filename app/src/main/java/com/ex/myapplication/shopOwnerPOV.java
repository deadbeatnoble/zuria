package com.ex.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.ex.myapplication.databinding.ActivityShopOwnerPovBinding;

public class shopOwnerPOV extends AppCompatActivity {

    ActivityShopOwnerPovBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityShopOwnerPovBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new StoreFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.store_bottom_nav) {
                replaceFragment(new StoreFragment());
            } else if (item.getItemId() == R.id.order_bottom_nav) {
                replaceFragment(new OrderFragment());
            } else if (item.getItemId() == R.id.review_bottom_nav) {
                replaceFragment(new ReviewFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}