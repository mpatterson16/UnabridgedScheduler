package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        List<Event> events = new ArrayList<>();
        events.add(new Event("event1", "date1"));
        events.add(new Event("event2", "date2"));
        events.add(new Event("event3", "date3"));

        RVAdapter adapter = new RVAdapter(events);
        recyclerView.setAdapter(adapter);

        Button addButton = (Button)findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                startActivity(intent);
            }
        });



    }
}
