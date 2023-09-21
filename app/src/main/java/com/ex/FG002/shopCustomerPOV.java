package com.ex.FG002;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.ex.FG002.databinding.ActivityShopCustomerPovBinding;
import com.ex.FG002.databinding.ActivityShopOwnerPovBinding;

public class shopCustomerPOV extends AppCompatActivity {

    ActivityShopCustomerPovBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopCustomerPovBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ShopsFragment());

        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.shops_bottom_nav) {
                replaceFragment(new ShopsFragment());
            } else if (item.getItemId() == R.id.my_order_bottom_nav) {
                replaceFragment(new MyOrderFragment());
            } else if (item.getItemId() == R.id.map_bottom_nav) {
                replaceFragment(new CustomerBasedMapFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.customerFrameLayout,fragment);
        fragmentTransaction.commit();
    }
}