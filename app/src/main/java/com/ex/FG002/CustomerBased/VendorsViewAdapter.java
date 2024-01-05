package com.ex.FG002.CustomerBased;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.ProductBased.CustomerProductModel;
import com.ex.FG002.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VendorsViewAdapter extends RecyclerView.Adapter<VendorsViewAdapter.MyViewHolder> {

    List<Users> vendors = new ArrayList<Users>();
    Context context;

    public VendorsViewAdapter(List<Users> vendors, Context context) {
        this.vendors = vendors;
        this.context = context;
    }

    public void setFilteredList(List<Users> filteredList) {
        this.vendors = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VendorsViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_vendor_design, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VendorsViewAdapter.MyViewHolder holder, int position) {
        Users users = vendors.get(position);
        holder.storeName.setText(vendors.get(position).getStoreName());
        Picasso.get().load(users.getStoreImage()).into(holder.storeImage);
        holder.storeStatus.setText(vendors.get(position).isLocationSyncStatus() ? "OPEN" : "CLOSED");
        if (holder.storeStatus.getText().equals("CLOSED")) {
            holder.storeStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }
        //holder.storePic.setText(vendors.get(position).getLatitude());

        holder.singleRowVendorDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CustomerBasedVendorStoreView.class);
                intent.putExtra("mobileNumber",vendors.get(position).getMobileNumber());
                intent.putExtra("storeImage", vendors.get(position).getStoreImage());
                //send vendor based data
                context.startActivity(intent);


            }
        });

        holder.storeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMiniMapPopup(users);
            }
        });

    }

    private void showMiniMapPopup(Users user) {
        //not really sure how this works. figure out for later
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_map);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        MapView miniMapView = dialog.findViewById(R.id.mini_map_view);
        miniMapView.onCreate(dialog.onSaveInstanceState());
        miniMapView.onResume();

        miniMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GoogleMap mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                // Add a marker on selected store and move the camera
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(user.getStoreLatitude()), Double.parseDouble(user.getStoreLongitude())))
                        .title(user.getStoreName())
                        .icon(bitmapDescriptorFromVector(context, R.drawable.storeonmap)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(user.getStoreLatitude()), Double.parseDouble(user.getStoreLongitude())), 17.0f));
            }
        });

        dialog.show();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResTd) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResTd);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        TextView storeStatus;
        ImageView storeImage;
        CardView singleRowVendorDesign;
        ImageView storeLocation;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.tv_vendorBasedStoreName);
            storeImage = itemView.findViewById(R.id.iv_vendorBasedStoreImage);
            storeStatus = itemView.findViewById(R.id.tv_vendorBasedStoreStatus);
            //storePic = itemView.findViewById(R.id.textView6);
            singleRowVendorDesign = itemView.findViewById(R.id.cv_singleRowVendorDesign);
            storeLocation = itemView.findViewById(R.id.iv_storeLocation);
        }
    }
}