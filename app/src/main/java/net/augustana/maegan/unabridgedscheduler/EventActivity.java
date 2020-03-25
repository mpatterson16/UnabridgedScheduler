package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        EditText eventText = (EditText)findViewById(R.id.eventText);
        EditText dateText = (EditText)findViewById(R.id.dateText);
        EditText descriptionText = (EditText)findViewById(R.id.descriptionText);

        Intent intent = this.getIntent();
        String event = intent.getStringExtra("event");
        String date = intent.getStringExtra("date");

        eventText.setText(event, TextView.BufferType.NORMAL);
        dateText.setText(date, TextView.BufferType.NORMAL);
    }
}
