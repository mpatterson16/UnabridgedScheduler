package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        final Button addButton = (Button)findViewById(R.id.addButton);

        final List<String> users = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                List<Event> events = new ArrayList<>();
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    Iterable<DataSnapshot> data = next.getChildren();
                    for(DataSnapshot item : data) {
                        Log.d("DB: ", "onDataChange: " + item.getKey() + " " + item.getChildren());
                        if(next.getKey().equals("events")) {
                            events.add(new Event(item.child("name").getValue().toString(), item.child("date").getValue().toString(), item.child("desc").getValue().toString(), item.getKey()));
                        } else if(next.getKey().equals("users")) {
                            users.add(item.child("uid").getValue().toString());
                        }
                    }
                    Log.d("DB: ", "onDataChange: " + next.getKey());
                    Log.d("DB: ", "onDataChange: " + next.getChildren());

                    //events.add(new Event(next.child("name").getValue().toString(), next.child("date").getValue().toString(), next.child("desc").getValue().toString(), next.getKey()));
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser account = auth.getCurrentUser();

                boolean authorized = account != null && users.contains(account.getUid());

                RVAdapter adapter = new RVAdapter(events, authorized);
                recyclerView.setAdapter(adapter);

                if(authorized) {
                    addButton.setEnabled(true);
                } else {
                    addButton.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("DB", "Failed to read value.", error.toException());
            }
        });



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                intent.putExtra("users", users.toArray());
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
}
