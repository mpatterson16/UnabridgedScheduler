package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        final EditText eventText = (EditText)findViewById(R.id.eventText);
        final EditText dateText = (EditText)findViewById(R.id.dateText);
        final EditText descriptionText = (EditText)findViewById(R.id.descriptionText);
        final EditText locationText = findViewById(R.id.locationText);

        Intent intent = this.getIntent();
        final String event = intent.getStringExtra("event");
        String date = intent.getStringExtra("date");
        String desc = intent.getStringExtra("desc");
        String loc = intent.getStringExtra("loc");
        final String id = intent.getStringExtra("id");
        boolean authorized = intent.getBooleanExtra("authorized", false);
        Log.d("ID:", "onCreate: id: " + id);

        eventText.setText(event, TextView.BufferType.NORMAL);
        dateText.setText(date, TextView.BufferType.NORMAL);
        descriptionText.setText(desc, TextView.BufferType.NORMAL);
        locationText.setText(loc, TextView.BufferType.NORMAL);

        Button saveButton = (Button)findViewById(R.id.saveButton);
        Button deleteButton = (Button)findViewById(R.id.deleteButton);

        final Calendar calendar = Calendar.getInstance();
        final int month;
        final int day;
        final int hour;
        int tempHour;
        final int minute;

        String fullDate = dateText.getText().toString();
        if(!fullDate.equals("")) {
            month = Integer.parseInt(fullDate.substring(0, 2));
            day = Integer.parseInt(fullDate.substring(3, 5));
            tempHour = Integer.parseInt(fullDate.substring(6, 8));
            minute = Integer.parseInt(fullDate.substring(9, 11));
            if(fullDate.substring(12).equals("PM")) {
                tempHour += 12;
            }
        } else {
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            tempHour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        hour = tempHour;
        if(authorized) {
            eventText.setEnabled(true);
            dateText.setEnabled(true);
            descriptionText.setEnabled(true);
            locationText.setEnabled(true);
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            eventText.setEnabled(false);
            dateText.setEnabled(false);
            descriptionText.setEnabled(false);
            locationText.setEnabled(false);
            saveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }


        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                String format = "MM/dd hh:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

                dateText.setText(sdf.format(calendar.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                new TimePickerDialog(EventActivity.this, timeSetListener, hour, minute, false).show();
            }

        };
        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventActivity.this, dateSetListener, calendar.get(Calendar.YEAR), month, day).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventText.getText().toString().equals("") || dateText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Event must have name and date", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference eventRef;
                    if (id == null) {
                        eventRef = ref.child("events").push();
                    } else {
                        eventRef = ref.child("events").child(id);
                    }
                    eventRef.child("name").setValue(eventText.getText().toString());
                    eventRef.child("date").setValue(dateText.getText().toString());
                    eventRef.child("desc").setValue(descriptionText.getText().toString());
                    eventRef.child("loc").setValue(locationText.getText().toString());

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ref.child("events").child(id).removeValue();

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                } catch(NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Cannot delete unsaved event", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
