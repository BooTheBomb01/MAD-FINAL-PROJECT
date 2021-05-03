package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.CalendarAdapterOnClickHandler {
    private final static int RC_SIGN_IN = 1;
    private static final String TAG = "MainActivity";


    private String calendarDay;


    private CalendarAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUserId;


    private FirebaseDatabase mFirebaseDatabase;
    //private DatabaseReference mDatabaseReference;


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        ExecutorService executor = Executors.newFixedThreadPool(1);


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
                if (user != null) {
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


        calendarDay = String.valueOf(LocalDate.now()).replace('-', '/');
        Log.d(TAG, String.valueOf(calendarDay.charAt(5)));
        if (String.valueOf(calendarDay.charAt(5)).equals("0")) {
            StringBuilder str = new StringBuilder(calendarDay);
            str.setCharAt(5, ' ');
            String s = String.valueOf(str);
            calendarDay = s.replaceAll("\\s+", "");
        }


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                calendarDay = year + "/" + month + "/" + dayOfMonth;
                Log.d(TAG, calendarDay);
                updateQuery();

            }

            private void updateQuery() {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                Query query = firebaseDatabase.getReference().child("cal_item").orderByChild("uid").equalTo(mUserId + "_" + calendarDay);
                Log.d(TAG, mUserId + "_" + calendarDay);
                FirebaseRecyclerOptions<Calendar> newOptions =
                        new FirebaseRecyclerOptions.Builder<Calendar>()
                                .setQuery(query, Calendar.class)
                                .build();
                adapter.updateOptions(newOptions);
            }
        });
        /*
        This is how I am getting ready to send notifications I will have this run every minute and
        then look through the users information and see if there is a match in their time and day
        to the machines  current time. That part works I am currently stuck on how to not have to
        hardcode the events reference like in dataSnapshot.child("-MZmD5OwvbIsmO5754U9").getValue()))

        This uses executor to make a new thread that only runs this

        It does Log.D and prints the right stuff to the console i just need to compare the time and the
        (mUserId + "_" + GetCurDay) to find the matching days
         */
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String GetCurDay;

                while (true) {
                    GetCurDay = String.valueOf(LocalDate.now()).replace('-', '/');
                    Log.d(TAG, String.valueOf(GetCurDay.charAt(5)));
                    if (String.valueOf(GetCurDay.charAt(5)).equals("0")) {
                        StringBuilder str = new StringBuilder(GetCurDay);
                        str.setCharAt(5, ' ');
                        String s = String.valueOf(str);
                        GetCurDay = s.replaceAll("\\s+", "");
                    }
                    if (String.valueOf(GetCurDay.charAt(7)).equals("0")){
                        StringBuilder str = new StringBuilder(GetCurDay);
                        str.setCharAt(7, ' ');
                        String s = String.valueOf(str);
                        GetCurDay = s.replaceAll("\\s+","");
                    }

                    System.out.println(GetCurDay);
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    //.child("cal_item").orderByChild("uid").equalTo(mUserId + "_" + calendarDay);
                    Query database = firebaseDatabase.getReference().child("cal_item").orderByChild("uid").equalTo(mUserId + "_" + GetCurDay);
                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            int i = 0;
                            String[] GetCurTime;
                            String curTime;
                            String timeInData;
                            GetCurTime = String.valueOf(new Date()).split(" ");
                            curTime = GetCurTime[3];
                            curTime = curTime.substring(0, 5);
                            String reference;
                            //Log.d(TAG, String.valueOf(dataSnapshot.getValue()));
                            String QueryData = String.valueOf(dataSnapshot.getValue());
                            QueryData = QueryData.replaceAll("\\{","");
                            String[] queryData = QueryData.split("=| ");
                            for (String s: queryData) {
                                if(i % 9 == 0){
                                    reference = String.valueOf(queryData[i]);
                                    //curTime = "09:54";
                                    timeInData = String.valueOf(dataSnapshot.child(reference).child("time").getValue());
                                    if(timeInData.equals(curTime)){
                                        Log.d("MAtch",timeInData);
                                        NotificationUtils.remindUser(getBaseContext());
                                    }
                                }
                                i = i + 1;
                            }

                        }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    firebaseDatabase.getReference().child("cal_item").orderByChild("uid").equalTo(mUserId + "_" + calendarDay).get()
//                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            if (!task.isSuccessful()) {
//                                Log.e("firebase", "Error getting data", task.getException());
//                            }
//                            else {
//                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
//                            }
//                        }
//                    });


                    try {
                        Thread.sleep(1 * 100000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Intent showNotification = new Intent(this, NotificationAlertReceiver.class);
        showNotification.setAction(NotificationAlertReceiver.ACTION_REVIEW_REMINDER);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                showNotification,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+5000, 60000, notifyPendingIntent);
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
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        //getMenuInflater().inflate(R.menu.refreshevents, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sign_out) {
            //NotificationUtils.remindUser(this);
            AuthUI.getInstance().signOut(this);
            return true;
            //}else if (item.getItemId() == R.id.refresh){
            // setAdapter();
            // return true;
        } else if (item.getItemId() == R.id.settings) {
            Intent optionsActivity = new Intent(this, OptionsActivity.class);
            startActivity(optionsActivity);
            return true;
        } else {
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
        Query query = firebaseDatabase.getReference().child("cal_item").orderByChild("uid").equalTo(mUserId + "_" + calendarDay);
        Log.d(TAG, mUserId + "_" + calendarDay);
        FirebaseRecyclerOptions<Calendar> options =
                new FirebaseRecyclerOptions.Builder<Calendar> ()
                        .setQuery(query, Calendar.class)
                        .build();
        adapter = new CalendarAdapter(options,this);
        recyclerView.setAdapter(adapter);

    }

}