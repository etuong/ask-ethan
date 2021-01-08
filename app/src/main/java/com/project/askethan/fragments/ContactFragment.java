package com.project.askethan.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.askethan.BaseFragment;
import com.project.askethan.R;
import com.project.askethan.utilities.CustomSupportMapFragment;

import java.io.IOException;
import java.util.List;

public class ContactFragment extends BaseFragment {
    private GoogleMap googleMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

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
        double latitude = 34.085285;
        double longitude = -117.960899;
        Geocoder geocoder = new Geocoder(this.getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String result = addresses.get(0).getLocality() + " : ";
            result += addresses.get(0).getCountryName();
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng).title(result));
            googleMap.setMaxZoomPreference(20);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void requestMyLocation() {
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
        callIntent.setData(Uri.parse("tel:16264665656"));
        startActivity(callIntent);
    }

    private void openWebsite(String urlAddress) {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(Uri.parse(urlAddress));
        startActivity(websiteIntent);
    }
}
