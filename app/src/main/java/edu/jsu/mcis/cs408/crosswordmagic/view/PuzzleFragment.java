package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.beans.PropertyChangeEvent;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;

public class PuzzleFragment extends Fragment implements AbstractView {
    private CrosswordGridView gridView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_puzzle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get controller
        CrosswordMagicController controller = ((MainActivity) getActivity()).getController();
        controller.addView(this);

        // Get grid view
        gridView = view.findViewById(R.id.grid);

        Log.d("DEBUG", "PuzzleFragment Registered with controller" + controller);

        // Get updates from controller
        controller.getGridDimensions();
        controller.getGridLetters();
        controller.getGridNumbers();

        Log.d("DEBUG", "PuzzleFragment PuzzleFragment created");
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        Log.d("DEBUG", "PuzzleFragment: Received update: " + evt.getPropertyName());

        if (gridView != null) {
            Log.d("DEBUG", "PuzzleFragment: Forwarding update to gridView: " + evt.getPropertyName());
            gridView.modelPropertyChange(evt);
        } else {
            Log.e("DEBUG", "PuzzleFragment: GridView is NULL!");
        }
    }

}
