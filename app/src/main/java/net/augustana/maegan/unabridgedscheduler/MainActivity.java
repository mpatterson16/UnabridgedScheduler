package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button addButton;

    ArrayList<String> users;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        events = new ArrayList<>();
        users = new ArrayList<>();

        //Recycler view of Events
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        addButton = findViewById(R.id.addButton);

        //set up database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        //Set up and read from the database as it changes
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                events = new ArrayList<>();
                users = new ArrayList<>();

                //there are two things in this iterator: the events and the users
                while (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    Iterable<DataSnapshot> data = next.getChildren();

                    //get all the children of both events and users, and then put them in their respective lists
                    for(DataSnapshot item : data) {
                        Log.d("DB: ", "onDataChange: " + item.getKey() + " " + item.getChildren());
                        if(next.getKey().equals("events")) {
                            try {
                                events.add(new Event(item.child("name").getValue().toString(),
                                        item.child("date").getValue().toString(),
                                        item.child("desc").getValue().toString(),
                                        item.child("loc").getValue().toString(), item.getKey()));
                            } catch (NullPointerException e) {
                                Log.d("NullPointerException: ", "onDataChange: " + item.child("name").getValue());
                                Log.d("NullPointerException: ", "onDataChange: " + item.child("date").getValue());
                                Log.d("NullPointerException: ", "onDataChange: " + item.child("desc").getValue());
                                Log.d("NullPointerException: ", "onDataChange: " + item.child("loc").getValue());
                            }
                        } else if(next.getKey().equals("users")) {
                            users.add(item.child("uid").getValue().toString());
                        }
                    }
                }

                boolean authorized = checkUserAuthorization();

                //sort by date
                Collections.sort(events);
                RVAdapter adapter = new RVAdapter(events, authorized);
                recyclerView.setAdapter(adapter);

                //make add button invisible if user can't edit database
                if(authorized) {
                    addButton.setVisibility(View.VISIBLE);
                } else {
                    addButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DB", "Failed to read value.", error.toException());
            }
        });


        //if the user can see the add button, then they are authorized to edit
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                intent.putExtra("authorized", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    /**
     * Check if the user is signed in and if they are authorized to make changes to the database
     *
     * @return if they have permission to write to the database
     */
    public boolean checkUserAuthorization() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser account = auth.getCurrentUser();

        return account != null && users.contains(account.getUid());
    }
}
