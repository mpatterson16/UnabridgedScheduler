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
    DatabaseReference ref;
    EditText eventText;
    EditText dateText;
    EditText descriptionText;
    EditText locationText;
    Button saveButton;
    Button deleteButton;

    Calendar calendar;
    int[] calendarUnits;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        eventText = (EditText)findViewById(R.id.eventText);
        dateText = (EditText)findViewById(R.id.dateText);
        descriptionText = (EditText)findViewById(R.id.descriptionText);
        locationText = findViewById(R.id.locationText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        //get all the data passed into this activity by the RVAdaptor
        Intent intent = this.getIntent();
        String event = intent.getStringExtra("event");
        String date = intent.getStringExtra("date");
        String desc = intent.getStringExtra("desc");
        String loc = intent.getStringExtra("loc");
        id = intent.getStringExtra("id");
        boolean authorized = intent.getBooleanExtra("authorized", false);

        eventText.setText(event, TextView.BufferType.NORMAL);
        dateText.setText(date, TextView.BufferType.NORMAL);
        descriptionText.setText(desc, TextView.BufferType.NORMAL);
        locationText.setText(loc, TextView.BufferType.NORMAL);

        //fields are only editable if authorized
        eventText.setEnabled(authorized);
        dateText.setEnabled(authorized);
        descriptionText.setEnabled(authorized);
        locationText.setEnabled(authorized);

        //buttons disappear if user can't write to database
        if(authorized) {
            saveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        dateText.setShowSoftInputOnFocus(false);
        setupCalendar();

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
                new TimePickerDialog(EventActivity.this, timeSetListener, calendarUnits[2], calendarUnits[3], false).show();
            }

        };

        //when the user clicks the date field, open a calendar and clock to pick date and time
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendarUnits[0] - 1, calendarUnits[1]).show();
            }
        });
    }

    /**
     * Set up the month, day, hour, and minute of the calendar for the date picker
     */
    public void setupCalendar() {
        calendar = Calendar.getInstance();
        calendarUnits = new int[4];
        String fullDate = dateText.getText().toString();

        if(!fullDate.equals("")) {
            for(int i = 0; i < 4; i++) {
                calendarUnits[i] = Integer.parseInt(fullDate.substring(i * 3, i * 3 + 2));
            }
            if(fullDate.substring(12).equals("PM")) {
                calendarUnits[2] += 12;
            }
        } else {
            calendarUnits[0] = calendar.get(Calendar.MONTH);
            calendarUnits[1] = calendar.get(Calendar.DAY_OF_MONTH);
            calendarUnits[2] = calendar.get(Calendar.HOUR_OF_DAY);
            calendarUnits[3] = calendar.get(Calendar.MINUTE);
        }
    }

    /**
     * When the user presses the save button, save the different fields to the database. If the user
     * didn't include a name or date, don't save it and just display a warning.
     *
     * @param view
     */
    public void save(View view) {
        if(eventText.getText().toString().equals("") || dateText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Event must have name and date", Toast.LENGTH_LONG).show();
        } else {
            DatabaseReference eventRef;

            //if the event is brand new, create a new ID for it
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

    /**
     * Delete the event from the database unless it is a new event that does not exist in the database
     *
     * @param view
     */
    public void delete(View view) {
        try {
            ref.child("events").child(id).removeValue();

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } catch(NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Cannot delete unsaved event", Toast.LENGTH_LONG).show();
        }
    }
}
