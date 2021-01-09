package com.project.askethan.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.askethan.BaseFragment;
import com.project.askethan.R;
import com.project.askethan.utilities.AuthorHelper;
import com.project.askethan.utilities.CustomSupportMapFragment;
import com.project.askethan.utilities.FirebaseModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LOCATION_SERVICE;

public class ContactFragment extends BaseFragment {
    private GoogleMap googleMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private TextView locationTextView;
    private List<ImageView> moods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        locationTextView = view.findViewById(R.id.map_text_location);

        TextView contactEmail = view.findViewById(R.id.contactEmail);
        contactEmail.setOnClickListener(this::sendEmail);

        TextView contactPhone = view.findViewById(R.id.contactPhone);
        contactPhone.setOnClickListener(this::callPhone);

        TextView contactCode = view.findViewById(R.id.contactCode);
        contactCode.setOnClickListener(v ->
                openWebsite("https://github.com/etuong")
        );

        TextView contactWebsite = view.findViewById(R.id.contactWebsite);
        contactWebsite.setOnClickListener(v ->
                openWebsite("https://www.ethanuong.com/")
        );

        moods = new ArrayList<>();
        moods.add(view.findViewById(R.id.mood1));
        moods.add(view.findViewById(R.id.mood2));
        moods.add(view.findViewById(R.id.mood3));

        DatabaseReference dbMoodRef = FirebaseModule.getMoodDatabaseReference();
        dbMoodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                if (isAdded()) {
                    for (ImageView imageView : moods) {
                        if (imageView.getTag().equals(status)) {
                            imageView.setColorFilter(ContextCompat.getColor(ContactFragment.this.getContext(), R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                        } else {
                            imageView.clearColorFilter();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        if (AuthorHelper.isOwner()) {
            CircleImageView profilePic = view.findViewById(R.id.profileImageView);
            profilePic.setOnClickListener(this::requestMyLocation);
        }

        CustomSupportMapFragment mapFragment = (CustomSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(gMap -> {
            ScrollView scrollView = view.findViewById(R.id.contact_scrollview);
            mapFragment.setListener(() -> scrollView.requestDisallowInterceptTouchEvent(true));
            googleMap = gMap;
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            getEthanLocation();
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            getMyLocation();
        }
    }

    private void getEthanLocation() {
        DatabaseReference dbLocRef = FirebaseModule.getLocationDatabaseReference();
        dbLocRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double latitude = (double) dataSnapshot.child("latitude").getValue();
                double longitude = (double) dataSnapshot.child("longitude").getValue();
                Geocoder geocoder = new Geocoder(ContactFragment.this.getContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    Address address = addresses.get(0);
                    String result = address.getAddressLine(0);
                    LatLng latLng = new LatLng(latitude, longitude);
                    locationTextView.setText(result);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(result));
                    googleMap.setMaxZoomPreference(20);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    FirebaseModule.getLocationDatabaseReference().child("latitude").setValue(latitude);
                    FirebaseModule.getLocationDatabaseReference().child("longitude").setValue(longitude);
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void requestMyLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getMyLocation();
        }
    }

    private void sendEmail(View view) {
        String[] to = {"etuong@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email from Ask Ethan!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Ethan an e-mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this.getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callPhone(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:14123978149"));
        startActivity(callIntent);
    }

    private void openWebsite(String urlAddress) {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(Uri.parse(urlAddress));
        startActivity(websiteIntent);
    }
}
