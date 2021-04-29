package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CalendarAdapter extends FirebaseRecyclerAdapter<Calendar, CalendarAdapter.CalendarHolder> {
    private static final String TAG = "CalendarAdapter";
    private final CalendarAdapterOnClickHandler clickHandler;
    MainActivity mainActivity = new MainActivity();

    public interface CalendarAdapterOnClickHandler {
        void onClick(int position);
    }

    public CalendarAdapter(@NonNull FirebaseRecyclerOptions<Calendar> options, CalendarAdapterOnClickHandler clickHandler) {
        super(options);
        this.clickHandler = clickHandler;
    }

    @Override
    protected void onBindViewHolder(@NonNull CalendarHolder holder, int position, @NonNull Calendar model) {

        holder.titleTextView.setText(model.getTitle());

    }



    @NonNull
    @Override
    public CalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.calendar_item, parent, false);
        return new CalendarHolder(view);
    }

    class CalendarHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;

        CalendarHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            clickHandler.onClick(adapterPosition);
        }
    }


}
