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
        String event = intent.getStringExtra("event");
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

        if(authorized) {
            eventText.setEnabled(true);
            dateText.setEnabled(true);
            descriptionText.setEnabled(true);
            locationText.setEnabled(true);
            saveButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            eventText.setEnabled(false);
            dateText.setEnabled(false);
            descriptionText.setEnabled(false);
            locationText.setEnabled(false);
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }



        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                String myFormat = "MM/dd hh:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dateText.setText(sdf.format(calendar.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                new TimePickerDialog(EventActivity.this, timeSetListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
            }

        };
        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference eventRef;
                if(id == null) {
                    eventRef = ref.push();
                } else {
                    eventRef = ref.child(id);
                }
                eventRef.child("name").setValue(eventText.getText().toString());
                eventRef.child("date").setValue(dateText.getText().toString());
                eventRef.child("desc").setValue(descriptionText.getText().toString());
                eventRef.child("loc").setValue(locationText.getText().toString());

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(id).removeValue();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
