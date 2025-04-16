package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleMenuItem;
import edu.jsu.mcis.cs408.crosswordmagic.controller.MenuAdapter;

public class MenuActivity extends AppCompatActivity implements AbstractView, MenuAdapter.OnPuzzleSelectedListener {
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private CrosswordMagicController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        controller = new CrosswordMagicController();
        CrosswordMagicModel model = new CrosswordMagicModel(this);
        controller.addModel(model);
        controller.addView(this);

        recyclerView = findViewById(R.id.menuRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        controller.getPuzzleMenu();
    }

    // Receives data from model and updates RecyclerView
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (CrosswordMagicController.PUZZLE_MENU_PROPERTY.equals(evt.getPropertyName())) {
            PuzzleMenuItem[] menuItems = (PuzzleMenuItem[]) evt.getNewValue();
            ArrayList<PuzzleMenuItem> list = new ArrayList<>(Arrays.asList(menuItems));
            adapter = new MenuAdapter(this, list, this);  // Pass listener
            recyclerView.setAdapter(adapter);
        }
    }

    // Called when user taps "Download and Play"
    @Override
    public void onPuzzleSelected(PuzzleMenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        // Pass selected puzzle ID
        intent.putExtra("puzzleid", item.getId());
        startActivity(intent);
    }
}
