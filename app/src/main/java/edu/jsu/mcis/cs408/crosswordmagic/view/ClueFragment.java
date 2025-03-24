package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.beans.PropertyChangeEvent;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;

public class ClueFragment extends Fragment implements AbstractView {
    private final String TAG = "ClueFragment";
    private TextView aContainer, dContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aContainer = view.findViewById(R.id.aContainer);
        dContainer = view.findViewById(R.id.dContainer);

        // Get controller
        CrosswordMagicController controller = ((MainActivity) getActivity()).getController();
        controller.addView(this);

        // Load clue data
        loadClues();
    }

    // Load clue data
    public void loadClues() {
        CrosswordMagicController controller = ((MainActivity) getActivity()).getController();
        controller.getCluesAcross();
        controller.getCluesDown();
    }

    // Update clue view
    public void updateClues(String across, String down) {
        if (across != null) {
            aContainer.setText(across);
        }
        if (down != null) {
            dContainer.setText(down);
        }
    }

    // Handle property change
    public void modelPropertyChange(final PropertyChangeEvent evt) {
    }
}
