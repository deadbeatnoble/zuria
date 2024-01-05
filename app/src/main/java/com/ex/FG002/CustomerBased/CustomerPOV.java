package com.ex.FG002.CustomerBased;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.ex.FG002.R;
import com.ex.FG002.databinding.ActivityCustomerPovBinding;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerPOV extends AppCompatActivity {

    ActivityCustomerPovBinding binding;

    public static final String SHARED_PREFRS = "productsInCart";
    public static final String TEXT = "No data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerPovBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new CustomerBasedVendorsViewFragment());

        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.shops_bottom_nav) {
                replaceFragment(new CustomerBasedVendorsViewFragment());
            } else if (item.getItemId() == R.id.my_order_bottom_nav) {
                replaceFragment(new CustomerBasedOrderFragment());
            } else if (item.getItemId() == R.id.map_bottom_nav) {
                replaceFragment(new CustomerBasedMapViewFragment());
            } else if (item.getItemId() == R.id.news_bottom_nav) {
                replaceFragment(new CustomerBasedNewsFragment());
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

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRS, MODE_PRIVATE);
        String sharedPreferencesSelectedProductStringForm = sharedPreferences.getString(TEXT, "");

        if (!sharedPreferencesSelectedProductStringForm.isEmpty()) {

            BadgeDrawable badgeDrawable = binding.bottomNavigationView2.getOrCreateBadge(R.id.my_order_bottom_nav);
            badgeDrawable.setVisible(true);

            List<String> sharedPreferenceSelectedProducts = new ArrayList<>();
            sharedPreferenceSelectedProducts = new ArrayList<>(Arrays.asList(sharedPreferencesSelectedProductStringForm.split(",")));

            badgeDrawable.setNumber(sharedPreferenceSelectedProducts.size());
            badgeDrawable.setMaxCharacterCount(10);
            badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.gold));
            badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this, R.color.black));

        } else {

            BadgeDrawable badgeDrawable = binding.bottomNavigationView2.getOrCreateBadge(R.id.my_order_bottom_nav);
            badgeDrawable.setVisible(false);
        }

    }

}