package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class DetailActivity extends AppCompatActivity {

        private static final String TAG = "DetailActivity";
        private Calendar calendar;

        private EditText titleField;
        private EditText bodyField;
        private Button addEditButton;
        private String userId;
        private String ref;
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;
        String Day;


    @RequiresApi(api = Build.VERSION_CODES.O)//for (LocalDate.now)
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            CalendarView calendarView = findViewById(R.id.cal);
            calendarView.setClickable(true);

            titleField = findViewById(R.id.title_field);
            titleField.addTextChangedListener(new TitleListener());

            bodyField = findViewById(R.id.body_field);
            bodyField.addTextChangedListener(new BodyListener());

            addEditButton = findViewById(R.id.add_edit_button);

            userId = getIntent().getStringExtra("uid");



            mFirebaseDatabase = FirebaseDatabase.getInstance();
            ref = getIntent().getStringExtra("ref");


            calendar = new Calendar();
            calendar.setBody("n/a");
            calendar.setTitle("n/a");
            Day = String.valueOf(LocalDate.now()).replace('-', '/');
            Log.d(TAG, String.valueOf(Day.charAt(5)));
            if (String.valueOf(Day.charAt(5)).equals("0")){
                StringBuilder str = new StringBuilder(Day);
                str.setCharAt(5, ' ');
                String s = String.valueOf(str);
                Day = s.replaceAll("\\s+","");
            }
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    Day = year + "/" + month + "/" + dayOfMonth;
                    Log.d(TAG, Day);
                }
            });

            if (ref != null) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("cal_item").child(ref);
                ValueEventListener calListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        calendar = snapshot.getValue(Calendar.class);
                        setUi();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                mDatabaseReference.addValueEventListener(calListener);
            } else {

                addEditButton.setOnClickListener(new OnAddButtonClick());
                mFirebaseDatabase = mFirebaseDatabase.getInstance();
                mDatabaseReference = mFirebaseDatabase.getReference("cal_item");
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setUi(){
            if(calendar != null) {
                String selectedDate = (String.valueOf(LocalDate.now()).replace('-', '/'));
                try {
                    final CalendarView calendarView = findViewById(R.id.cal);
                    calendarView.setDate(new SimpleDateFormat("yyyy/MM/dd").parse(selectedDate).getTime(), true, true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                titleField.setText(calendar.getTitle());
                bodyField.setText(calendar.getBody());
                addEditButton.setText("update");
                addEditButton.setOnClickListener(new OnUpdateButtonClick());
            }
        }



private class TitleListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        calendar.setTitle(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}

private class BodyListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calendar.setBody(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }

private class OnAddButtonClick implements View.OnClickListener{
    @Override
    public void onClick(View v){



        String userIdDay = userId + "_"+ Day;

        calendar.setUid(userIdDay);

        Log.d(TAG, String.valueOf(calendar));
        mDatabaseReference.push().setValue(calendar);
        finish();
    }
}

private class OnUpdateButtonClick implements View.OnClickListener{
  @Override
   public void onClick(View v) {
       mDatabaseReference.setValue(calendar);
        finish();
   }
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.delete_menu,menu);
        //getMenuInflater().inflate(R.menu.add_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete) {
            mDatabaseReference.removeValue();
            finish();
        }else if (item.getItemId() == R.id.add){
            mDatabaseReference.push().setValue(calendar);
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}