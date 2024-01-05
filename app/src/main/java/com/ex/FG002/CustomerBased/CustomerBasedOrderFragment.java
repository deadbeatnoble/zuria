package com.ex.FG002.CustomerBased;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ex.FG002.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CustomerBasedOrderFragment extends Fragment {

    private TabLayout tl_orderTabs;
    private ViewPager2 vp_orderTabDisplay;
    private ViewPagerAdapter viewPagerAdapter;


    public static final String SHARED_PREFRS = "productsInCart";
    public static final String TEXT = "No data";

    public CustomerBasedOrderFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_customer_order, container, false);

        tl_orderTabs = rootView.findViewById(R.id.tl_orderTabs);
        vp_orderTabDisplay = rootView.findViewById(R.id.vp_orderTabDisplay);
        viewPagerAdapter = new ViewPagerAdapter(getActivity());
        vp_orderTabDisplay.setAdapter(viewPagerAdapter);

        tl_orderTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_orderTabDisplay.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_orderTabDisplay.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tl_orderTabs.getTabAt(position).select();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        TabLayout tabLayout = getView().findViewById(R.id.tl_orderTabs);

        int cartTabIndex = 0; // Index of the "Cart" tab
        TabLayout.Tab cartTab = tabLayout.getTabAt(cartTabIndex);

        if (cartTab != null) {

            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFRS, getContext().MODE_PRIVATE);
            String sharedPreferencesSelectedProductStringForm = sharedPreferences.getString(TEXT, "");

            if (!sharedPreferencesSelectedProductStringForm.isEmpty()) {

                BadgeDrawable badge = cartTab.getOrCreateBadge();
                badge.setVisible(true);

                List<String> sharedPreferenceSelectedProducts = new ArrayList<>();
                sharedPreferenceSelectedProducts = new ArrayList<>(Arrays.asList(sharedPreferencesSelectedProductStringForm.split(",")));

                badge.setNumber(sharedPreferenceSelectedProducts.size());
                badge.setMaxCharacterCount(10);
                badge.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gold));
                badge.setBadgeTextColor(ContextCompat.getColor(getContext(), R.color.black));

            } else {

                BadgeDrawable badge = cartTab.getOrCreateBadge();
                badge.setVisible(false);
            }
        }
    }
}