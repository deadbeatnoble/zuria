package com.ex.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ProductFragment();
            case 1: return new OrderFragment();
            case 2: return new ReviewFragment();
            default: return new ProductFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
