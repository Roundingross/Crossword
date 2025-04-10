package edu.jsu.mcis.cs408.crosswordmagic.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleMenuItem;
import edu.jsu.mcis.cs408.crosswordmagic.view.MainActivity;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    // Puzzle list data and context
    private List<PuzzleMenuItem> data;
    private final Context context;

    // Constructor
    public MenuAdapter(Context context, List<PuzzleMenuItem> data) {
        this.context = context;
        this.data = data;
    }

    // Inflate row layout and create ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PuzzleMenuItem item = data.get(position);
        holder.menuLabel.setText(item.getName());

        // TEMP: always send ID 1 until Part 3
        holder.playButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("puzzleid", 1); // ← TEMP HARDCODED
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<PuzzleMenuItem> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuLabel;
        Button playButton;

        public ViewHolder(View itemView) {
            super(itemView);
            menuLabel = itemView.findViewById(R.id.menuLabel);
            playButton = itemView.findViewById(R.id.playButton);
        }
    }
}
