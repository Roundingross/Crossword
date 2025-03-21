package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

        Log.d("DEBUG", "MainActivity: Controller Registered with Model");
    }


    public CrosswordMagicController getController() {
        return controller;
    }

}