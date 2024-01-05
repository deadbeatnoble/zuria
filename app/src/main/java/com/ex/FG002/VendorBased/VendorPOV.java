package com.ex.FG002.VendorBased;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.ex.FG002.R;
import com.ex.FG002.databinding.ActivityVendorPovBinding;

public class VendorPOV extends AppCompatActivity {

    ActivityVendorPovBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVendorPovBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new VendorBasedStoreFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.store_bottom_nav) {
                replaceFragment(new VendorBasedStoreFragment());
            } else if (item.getItemId() == R.id.order_bottom_nav) {
                replaceFragment(new VendorBasedOrderFragment());
            } else if (item.getItemId() == R.id.profile_bottom_nav) {
                replaceFragment(new VendorBasedProfileFragment());
            } else if (item.getItemId() == R.id.news_bottom_nav) {
                replaceFragment(new VendorBasedNewsFragment());
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