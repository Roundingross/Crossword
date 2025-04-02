package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;

public class MainActivity extends AppCompatActivity {
    private CrosswordMagicController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create model
        CrosswordMagicModel model = new CrosswordMagicModel(this);

        // Create controller and register model
        controller = new CrosswordMagicController();
        controller.addModel(model);

        // Load puzzle saved state
        controller.loadState(this);
    }

    // Save puzzle progress
    @Override
    protected void onPause() {
        super.onPause();
        if (controller.getPuzzle() != null) {
            controller.getPuzzle().saveState(this);
        }
    }


    public CrosswordMagicController getController() {
        return controller;
    }
}