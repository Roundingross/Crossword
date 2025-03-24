package edu.jsu.mcis.cs408.crosswordmagic.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import edu.jsu.mcis.cs408.crosswordmagic.view.AbstractView;
import edu.jsu.mcis.cs408.crosswordmagic.view.ClueFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.PuzzleFragment;

public class CrosswordMagicController extends AbstractController implements PropertyChangeListener {
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimension";
    public static final String CLUES_ACROSS_PROPERTY = "CluesAcross";
    public static final String CLUES_DOWN_PROPERTY = "CluesDown";


    public void getGridLetters() {
        getModelProperty("GridLetters");
    }

    public void getGridNumbers() {
        getModelProperty("GridNumbers");
    }

    public void getGridDimensions() {
        getModelProperty("GridDimension");
    }

    public void getCluesAcross() {
        getModelProperty("CluesAcross");
    }

    public void getCluesDown() {
        getModelProperty("CluesDown");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();

        // Update clue view
        for (AbstractView view : getViews()) {
            if (view instanceof ClueFragment) {
                ClueFragment clueView = (ClueFragment) view;

                // Set values for clue view
                switch (propertyName) {
                    case CLUES_ACROSS_PROPERTY:
                        clueView.updateClues((String) newValue, null);
                        break;
                    case CLUES_DOWN_PROPERTY:
                        clueView.updateClues(null, (String) newValue);
                        break;
                }
            }

            // Update puzzle view
            else if (view instanceof PuzzleFragment) {
                PuzzleFragment puzzleView = (PuzzleFragment) view;
                Character[][] letters = null;
                Integer[][] numbers = null;
                Integer[] dimension = null;

                // Set values for puzzle view
                switch (propertyName) {
                    case GRID_LETTERS_PROPERTY:
                        letters = (Character[][]) newValue;
                        break;
                    case GRID_NUMBERS_PROPERTY:
                        numbers = (Integer[][]) newValue;
                        break;
                    case GRID_DIMENSION_PROPERTY:
                        dimension = (Integer[]) newValue;
                        break;
                }
                puzzleView.updatePuzzle(letters, numbers, dimension);
            }
        }
    }
}