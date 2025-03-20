package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;

public class MainActivity extends AppCompatActivity {
    private CrosswordMagicController controller;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        /* Create Controller and Model */
        controller = new CrosswordMagicController();
        CrosswordMagicModel model = new CrosswordMagicModel(this);

        /* Register View(s) and Model(s) with Controller */
        controller.addModel(model);
    }

    public CrosswordMagicController getController() {
        return controller;
    }

}