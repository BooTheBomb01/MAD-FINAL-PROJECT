package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CalendarView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.CalendarAdapterOnClickHandler {
    private final static int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";


    private CalendarAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUserId;
    String calendarDay;
    private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference mDatabaseReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CalendarView calendarView = findViewById(R.id.Calendar);
        calendarView.setClickable(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("uid", mUserId);
                startActivity(intent);

            }
        });
        setAdapter();

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user !=null){
                    mUserId = user.getUid();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build())).build(), RC_SIGN_IN);
                }
            }
        };

//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                calendarDay = dayOfMonth + " " + month + " " + year;
//
//                Toast.makeText(MainActivity.this, year + " " + month + " " + dayOfMonth, Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }
    @Override
    public void onClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("uid", mUserId);
        DatabaseReference ref = adapter.getRef(position);
        String id = ref.getKey();
        detailIntent.putExtra("ref", id);
        startActivity(detailIntent);
    }
    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sign_out){
            AuthUI.getInstance().signOut(this);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user != null){
                    mUserId = user.getUid();
                    setAdapter();
                }
            }
            if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }
    private void setAdapter(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference().child("cal_item").orderByChild("uid").equalTo(mUserId);

        FirebaseRecyclerOptions<Calendar> options =
                new FirebaseRecyclerOptions.Builder<Calendar> ()
                        .setQuery(query, Calendar.class)
                        .build();
        adapter = new CalendarAdapter(options,this);
        recyclerView.setAdapter(adapter);

    }
//    private void queryDateForRV() {
//        CalendarView calendarView = findViewById(R.id.Calendar);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                calendarDay = dayOfMonth + " " + month + " " + year;
//
//                Toast.makeText(MainActivity.this, year + " " + month + " " + dayOfMonth, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
}