package com.example.messenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.messenger.R;
import com.example.messenger.adapter.PageAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import xyz.hasnat.sweettoast.SweetToast;

public class MainActivity extends AppCompatActivity {
  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private PageAdapter pageAdapter;
  private TabItem tabDescription;
  private TabItem tabInspiration;
  private TabItem tabMission, tabRequest;
  private TabItem tabResponsibilities;
  private SwipeRefreshLayout politicalPSRL;

  //getLocation
  //for auto complete search
  private String addressLine;
  private String addressLocality;
  private Marker searchingMarker;
  private Marker marker;
/*
  //for device location
  private FusedLocationProviderClient fusedLocationProviderClient;
  private LocationRequest locationRequest;
  private LocationCallback locationCallback;*/
  //for request code
  public static int REQUEST_CODE_FOR_LOCATION = 1;

  private final static int REQUEST_CODE_PERMISSION_READ_SMS = 456;
  //Current location
  LatLng currentLocation;
  LatLng searchLatLng;

  //for device location latitude and longitude
  private double latitude;
  private double longitude;


  //firebase user
  private FirebaseUser currentUser;
  FirebaseAuth firebaseAuth;
  String currentUserId;

  DatabaseReference roofRef, locationRef;
  FirebaseDatabase database;


  public static MainActivity instance;


  public static MainActivity Instance(){
    return instance;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    init();
    tabLayoutProfileInfo();
    //getDeviceLocation();


    //start------------permision-----------------
    if(checkPermission(Manifest.permission.READ_SMS)){

    }else{
      ActivityCompat.requestPermissions(MainActivity.this, new String[] {
              (Manifest.permission.READ_SMS)}, REQUEST_CODE_PERMISSION_READ_SMS);
    }

  }



  private boolean checkPermission(String permission){
    int checkPermission = ContextCompat.checkSelfPermission(this, permission);
    return checkPermission == PackageManager.PERMISSION_GRANTED;
  }


  public void setMeg(final String smsMsg){
    Toast.makeText(instance, ""+smsMsg, Toast.LENGTH_SHORT).show();

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (currentUser == null) {
      sendUserToLoginActivity();
    } else {
      VerifyUserExistence();
    }
  }

  private void VerifyUserExistence() {
    currentUserId = firebaseAuth.getCurrentUser().getUid();
    roofRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if ((dataSnapshot.child("name").exists())) {
          Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
        } else {
          //sendUserToSettingsActivity();
          //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }


  public void tabLayoutProfileInfo() {
    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#949596"));
    tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));

    pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
    viewPager.setAdapter(pageAdapter);

    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 1) {
          toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                    R.color.white));
          }
        } else if (tab.getPosition() == 2) {
          toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                    R.color.white));
          }
        } else {
          toolbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                  R.color.white));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                    R.color.white));
          }
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  private void init() {
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle(getResources().getString(R.string.app_name));
    setSupportActionBar(toolbar);

    database = FirebaseDatabase.getInstance();
    roofRef = database.getReference();
    locationRef = database.getReference();

    firebaseAuth = FirebaseAuth.getInstance();

    currentUser = firebaseAuth.getCurrentUser();

    tabLayout = findViewById(R.id.tablayout);
    tabDescription = findViewById(R.id.tabDescription);
   // tabInspiration = findViewById(R.id.tabInscription);
    tabRequest = findViewById(R.id.tabRequests);
    tabMission = findViewById(R.id.tabMission);
    viewPager = findViewById(R.id.viewPager);
  }

  public void backBtn(View view) {
    onBackPressed();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.options_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    super.onOptionsItemSelected(item);
    if (item.getItemId() == R.id.main_find_logout_options) {
      firebaseAuth.signOut();
      sendUserToLoginActivity();
    }


    if (item.getItemId() == R.id.main_find_setting_options) {
      sendUserToSettingsActivity();

    }
    if (item.getItemId() == R.id.main_find_friends_options) {
      startActivity(new Intent(this, FindFriendActivity.class));

    }

   /* if (item.getItemId() == R.id.main_create_group_options) {
      requestNewGroup();

    }*/
    return true;
  }

  private void requestNewGroup() {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AleartDialog);
    builder.setTitle("Enter Group Name : ");
    final EditText groupNameField = new EditText(this);
    groupNameField.setHint("Bipul Sarkar");
    builder.setView(groupNameField);

    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {

        String groupName = groupNameField.getText().toString();
        if (TextUtils.isEmpty(groupName)) {

          SweetToast.error(MainActivity.this, "Please write Group Name...");
        } else {
          createNewGroup(groupName);
        }
      }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
      }
    });

    builder.show();
  }

  private void createNewGroup(final String groupName) {

    roofRef.child("Groups").child(groupName).setValue("")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  // Toast.makeText(MainActivity.this, groupName + "is Created", Toast.LENGTH_SHORT).show();
                  SweetToast.success(MainActivity.this, groupName + " is Created");

                }
              }
            });
  }


  private void sendUserToLoginActivity() {
    startActivity(new Intent(MainActivity.this, LoginActivity.class));
  }

  private void sendUserToSettingsActivity() {
    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
    startActivity(i);
  }

/*
  //Get device location
  private void getDeviceLocation() {
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3000)
            .setFastestInterval(1000);
    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        for (Location location : locationResult.getLocations()) {

          latitude = location.getLatitude();
          longitude = location.getLongitude();

          try {
            List<Address> addressList = new Geocoder(MainActivity.this)
                    .getFromLocation(latitude, longitude, 1);

            addressLine = addressList.get(0).getAddressLine(0);
            addressLocality = addressList.get(0).getAddressLine(0);

            //  Toast.makeText(MainActivity.this, "Location Save"+addressLocality, Toast.LENGTH_SHORT).show();

            *//*   String currentId = firebaseAuth.getCurrentUser().getUid();
             *//*
            HashMap<String, Object> locationMap = new HashMap<>();
            locationMap.put("location", addressLocality);


            roofRef.child("users").child(currentUserId).updateChildren(locationMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                          //  Toast.makeText(MainActivity.this, "location save", Toast.LENGTH_SHORT).show();
                        } else {
                          String meg = task.getException().toString();
                          Toast.makeText(MainActivity.this, "error" + meg, Toast.LENGTH_SHORT).show();
                        }
                      }
                    });


            // searchView.setText(addressLocality);


            //moveCamera(new LatLng(latitude,longitude),17,address);


          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };


    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_FOR_LOCATION);
      return;
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
  }*/
}
