package com.ex.FG002.CustomerBased;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.R;
import com.ex.FG002.databinding.ActivityCustomerMapsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomerBasedMapViewFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCustomerMapsBinding binding;
    SupportMapFragment mapFragment;

    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference databaseReference;
    List<Users> availableStores = new ArrayList<>();


    public static final int REQUEST_CODE = 101;
    public static final int REQUEST_CHECK_SETTING = 1001;

    public CustomerBasedMapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_map_view, container, false);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng addisAbaba = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        float zoomLevel = 18.0f;

        mMap.addMarker(new MarkerOptions().position(addisAbaba).title("ME").icon(bitmapDescriptorFromVector(getActivity(),R.drawable.customer)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addisAbaba, zoomLevel));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    availableStores.add(user);
                }

                for (int i = 0; i < availableStores.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(availableStores.get(i).getStoreLatitude()), Double.parseDouble(availableStores.get(i).getStoreLongitude())))
                            .title(availableStores.get(i).getStoreName())
                            .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.storeonmap)));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "No Store Available", Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                for (int i = 0; i < availableStores.size(); i++) {
                    if (marker.getTitle().equals(availableStores.get(i).getStoreName())) {
                        showDialog(availableStores.get(i));
                    }
                }
                return false;
            }
        });
    }

    private void showDialog(Users user) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.store_on_map_dialog);

        ImageView closeLayout = dialog.findViewById(R.id.iv_closeBottomSheetDialog);
        ImageView storeImage = dialog.findViewById(R.id.civ_storeImage);
        TextView storeName = dialog.findViewById(R.id.tv_storeName);
        Button openStore = dialog.findViewById(R.id.btn_openStore);

        Picasso.get().load(user.getStoreImage()).into(storeImage);
        storeName.setText(user.getStoreName());

        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        openStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerBasedVendorStoreView.class);
                intent.putExtra("mobileNumber",user.getMobileNumber());
                intent.putExtra("storeImage", user.getStoreImage());
                getActivity().startActivity(intent);

                Toast.makeText(getActivity(), "Opening store: " + user.getStoreName(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void getLastLocation() {

        com.google.android.gms.location.LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    //if no error then that means location is enabled!!
                    //Toast.makeText(getActivity(), "already on", Toast.LENGTH_SHORT).show();


                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Task<Location> task1 = fusedLocationProviderClient.getLastLocation();
                        task1.addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    currentLocation = location;

                                    mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                                    if (mapFragment == null) {
                                        FragmentManager fragmentManager = getChildFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        mapFragment = SupportMapFragment.newInstance();
                                        fragmentTransaction.replace(R.id.map, mapFragment).commit();
                                    }
                                    mapFragment.getMapAsync(CustomerBasedMapViewFragment.this);
                                } else {
                                    getLastLocation();
                                }
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    }


                } catch (ApiException e) {

                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(getActivity(),REQUEST_CHECK_SETTING);
                        } catch (IntentSender.SendIntentException ex) {
                            throw new RuntimeException(ex);
                        }
                        return;
                    } else if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(getActivity(), "setting not available", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });





    }

    @Override
    public void onResume() {
        super.onResume();
        //onResume is neccessary as its the one that takes over once the button "ok" of the turn onlocation, is clicked it assumes the fragments as resumed and the following code gets excuted
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isLocationEnabled) {
            //Toast.makeText(getActivity(), "has been turned on", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> task = fusedLocationProviderClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;

                            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                            if (mapFragment == null) {
                                FragmentManager fragmentManager = getChildFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                mapFragment = SupportMapFragment.newInstance();
                                fragmentTransaction.replace(R.id.map, mapFragment).commit();
                            }
                            mapFragment.getMapAsync(CustomerBasedMapViewFragment.this);
                        } else {
                            Toast.makeText(getActivity(), "Retrieving current location...", Toast.LENGTH_SHORT).show();
                            getLastLocation();
                        }
                    }
                });

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }

        } else {
            //Toast.makeText(getActivity(), "Location is off...turn it on", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //this one is for checking which button the user clicked whether its "ok" turn on location or the "no thanks"
        if (requestCode == REQUEST_CHECK_SETTING) {
            if (requestCode == Activity.RESULT_OK) {
                //turned on locaiton
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Location permission is required! Please allow", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this one is for the permission. the one that asks the user to allow location access!
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getActivity(), "Location permission is required! Please allow", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResTd) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResTd);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}