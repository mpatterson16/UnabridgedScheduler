package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        Intent intent = this.getIntent();
        String event = intent.getStringExtra("event");
        String date = intent.getStringExtra("date");
        String desc = intent.getStringExtra("desc");
        final String id = intent.getStringExtra("id");
        boolean authorized = intent.getBooleanExtra("authorized", false);
        Log.d("ID:", "onCreate: id: " + id);

        eventText.setText(event, TextView.BufferType.NORMAL);
        dateText.setText(date, TextView.BufferType.NORMAL);
        descriptionText.setText(desc, TextView.BufferType.NORMAL);

        Button saveButton = (Button)findViewById(R.id.saveButton);
        Button deleteButton = (Button)findViewById(R.id.deleteButton);

        if(authorized) {
            eventText.setEnabled(true);
            dateText.setEnabled(true);
            descriptionText.setEnabled(true);
            saveButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            eventText.setEnabled(false);
            dateText.setEnabled(false);
            descriptionText.setEnabled(false);
            saveButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

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
