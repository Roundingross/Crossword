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
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;

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

        CrosswordMagicController controller = ((MainActivity) getActivity()).getController();
        DAOFactory daoFactory = new DAOFactory(getContext());

        Puzzle puzzle = daoFactory.getPuzzleDAO().find(1);

        aContainer = view.findViewById(R.id.aContainer);
        dContainer = view.findViewById(R.id.dContainer);

        if (puzzle != null) {
            aContainer.setText(puzzle.getCluesAcross());
            dContainer.setText(puzzle.getCluesDown());
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        String value = evt.getNewValue().toString();

        if (name.equals(CrosswordMagicController.GRID_DIMENSION_PROPERTY) && value instanceof String) {
            aContainer.setText("Updated Across Clues: " + value);
            dContainer.setText("Updated Down Clues: " + value);
        }

    }
}
