package net.augustana.maegan.unabridgedscheduler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder>{

    List<Event> events;
    boolean authorized;

    RVAdapter(List<Event> events, boolean authorized){
        this.events = events;
        this.authorized = authorized;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EventViewHolder holder = new EventViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.event.setText(events.get(i).name);
        eventViewHolder.date.setText(events.get(i).date);
        final Event item = events.get(i);
        final Context context = eventViewHolder.itemView.getContext();
        eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("event", item.name);
                intent.putExtra("date", item.date);
                intent.putExtra("desc", item.desc);
                intent.putExtra("id", item.id);
                intent.putExtra("authorized", authorized);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView event;
        TextView date;

        EventViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.cardView);
            event = (TextView)itemView.findViewById(R.id.event);
            date = (TextView)itemView.findViewById(R.id.date);
        }
    }

}