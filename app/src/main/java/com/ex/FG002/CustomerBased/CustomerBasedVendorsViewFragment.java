package com.ex.FG002.CustomerBased;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerBasedVendorsViewFragment extends Fragment {

    List<Users> vendors = new ArrayList<Users>();
    private RecyclerView vendorRecyclerView;
    private RecyclerView.Adapter vendorAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    public CustomerBasedVendorsViewFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_vendors_view, container, false);

        //where the built in data is being passed on is down here
        //fillVendorInfo();

        searchView = rootView.findViewById(R.id.sv_vendorStoreSearch);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });

        vendorRecyclerView = rootView.findViewById(R.id.rv_vendorView);
        vendorRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        vendorRecyclerView.setLayoutManager(layoutManager);

        vendorAdapter = new VendorsViewAdapter(vendors, getActivity());
        vendorRecyclerView.setAdapter(vendorAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vendors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    vendors.add(user);
                }
                vendorAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "No Store Available", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    private void filterList(String text) {
        List<Users> filteredList = new ArrayList<>();
        for (Users vendor : vendors) {
            if (vendor.getStoreName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(vendor);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data Found", Toast.LENGTH_SHORT).show();
        }
            ((VendorsViewAdapter) vendorAdapter).setFilteredList(filteredList);

    }

    /*private void fillVendorInfo() {
        Users vendor1 = new Users("911699410", "0000", "200", "100", true, false);
        Users vendor2 = new Users("912079405", "1111", "300", "200", false, true);
        Users vendor3 = new Users("942197921", "2222", "400", "300", false, true);
        Users vendor4 = new Users("994850480", "3333", "500", "400", false, true);
        Users vendor5 = new Users("913547035", "4444", "600", "500", false, true);
        Users vendor6 = new Users("911699410", "0000", "200", "100", true, false);
        Users vendor7 = new Users("912079405", "1111", "300", "200", false, true);
        Users vendor8 = new Users("942197921", "2222", "400", "300", false, true);
        Users vendor9 = new Users("994850480", "3333", "500", "400", false, true);
        Users vendor10 = new Users("913547035", "4444", "600", "500", false, true);

        vendors.addAll(Arrays.asList(new Users[] {vendor1, vendor2, vendor3, vendor4, vendor5, vendor6, vendor7, vendor8, vendor9, vendor10}));
    }*/
}