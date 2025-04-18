package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        // Load grid data
        loadGrid();

        // Clear button
        Button clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Clear Progress")
                    .setMessage("Are you sure you want to clear your progress?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null) {
                            controller.clearPuzzleProgress(activity);
                        } else {
                            Log.e("PuzzleFragment", "MainActivity is null in clearButton click");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }

    // Load grid data
    public void loadGrid() {
        CrosswordMagicController controller = ((MainActivity) getActivity()).getController();
        controller.getGridLetters();
        controller.getGridNumbers();
        controller.getGridDimensions();
    }

    // Update puzzle view
    public void updatePuzzle(Character[][] letters, Integer[][] numbers, Integer[] dimension) {
        if (gridView != null) {
            if (letters != null) {
                gridView.setLetters(letters);
            }
            if (numbers != null) {
                gridView.setNumbers(numbers);
            }
            if (dimension != null && dimension.length == 2) {
                gridView.setGridSize(dimension[0], dimension[1]);
            }
        }
    }

    // Handle property change
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }
}
