package com.ex.FG002.CustomerBased;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ex.FG002.R;

public class CustomerBasedPendingTabOrderFragment extends Fragment {

    public CustomerBasedPendingTabOrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_based_pending_tab_order, container, false);
    }
}