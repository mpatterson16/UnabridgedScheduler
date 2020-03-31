package net.augustana.maegan.unabridgedscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

        BufferedReader r = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.events)));
        StringBuilder builder = new StringBuilder();
        try {
            String line = r.readLine();
            while (line != null) {
                builder.append(line);
                line = r.readLine();
            }
        } catch (IOException e) {
            Log.e("IOException", "onCreate: failed to read json");
        }
        String json = builder.toString();

        //https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
        Type eventListType = new TypeToken<ArrayList<Event>>(){}.getType();
        List<Event> events = new Gson().fromJson(json, eventListType);

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
