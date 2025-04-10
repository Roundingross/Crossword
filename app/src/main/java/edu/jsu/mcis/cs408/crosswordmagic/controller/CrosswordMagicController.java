package edu.jsu.mcis.cs408.crosswordmagic.controller;

import android.content.Context;
import android.util.Pair;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import edu.jsu.mcis.cs408.crosswordmagic.model.AbstractModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.view.AbstractView;
import edu.jsu.mcis.cs408.crosswordmagic.view.ClueFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.PuzzleFragment;
import edu.jsu.mcis.cs408.crosswordmagic.view.WelcomeActivity;

public class CrosswordMagicController extends AbstractController implements PropertyChangeListener {
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimension";
    public static final String CLUES_ACROSS_PROPERTY = "CluesAcross";
    public static final String CLUES_DOWN_PROPERTY = "CluesDown";
    public static final String PLAYER_GUESS_PROPERTY = "PlayerGuess";
    public static final String GUESS_RESULT_PROPERTY = "GuessResult";
    public static final String PUZZLE_SOLVED_PROPERTY = "PuzzleSolved";
    public static final String PUZZLE_LIST_PROPERTY = "PuzzleList";

    // Handle model updates
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String propertyName = evt.getPropertyName();
        Object newValue = evt.getNewValue();

        for (AbstractView view : getViews()) {

            // Handle guess result for all views
            if (propertyName.equals(GUESS_RESULT_PROPERTY)) {
                view.modelPropertyChange(evt);
                continue;
            }

            // ClueFragment updates
            if (view instanceof ClueFragment) {
                ClueFragment clueView = (ClueFragment) view;
                switch (propertyName) {
                    case CLUES_ACROSS_PROPERTY:
                        clueView.updateClues((String) newValue, null);
                        break;
                    case CLUES_DOWN_PROPERTY:
                        clueView.updateClues(null, (String) newValue);
                        break;
                }
            }

            // PuzzleFragment updates
            else if (view instanceof PuzzleFragment) {
                PuzzleFragment puzzleView = (PuzzleFragment) view;
                Character[][] letters = null;
                Integer[][] numbers = null;
                Integer[] dimension = null;

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

            // WelcomeActivity updates
            else if (view instanceof WelcomeActivity) {
                for (AbstractModel model : getModels()) {
                    if (model instanceof CrosswordMagicModel) {
                        view.modelPropertyChange(evt);
                    }
                }
            }
        }
    }


    // Checks guess
    public void checkGuess(int boxNumber, String guess) {
        Pair<Integer, String> input = new Pair<>(boxNumber, guess);
        setModelProperty(PLAYER_GUESS_PROPERTY, input);
    }

    // Getters for model updates
    public void getGridLetters() { getModelProperty("GridLetters"); }
    public void getGridNumbers() { getModelProperty("GridNumbers"); }
    public void getGridDimensions() { getModelProperty("GridDimension"); }
    public void getCluesAcross() { getModelProperty("CluesAcross"); }
    public void getCluesDown() { getModelProperty("CluesDown"); }
    public void getPuzzleList() { getModelProperty("PuzzleList"); }

    public Puzzle getPuzzle() {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                return ((CrosswordMagicModel) model).getPuzzle();
            }
        }
        return null;
    }

    // Load state of progress
    public void loadState(Context context) {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                ((CrosswordMagicModel) model).loadState(context);
            }
        }
    }

    // Save state of progress
    public void saveState(Context context) {
        for (AbstractModel model : models) {
            if (model instanceof CrosswordMagicModel) {
                ((CrosswordMagicModel) model).saveState(context);
            }
        }
    }

    // Clear puzzle progress
    public void clearPuzzleProgress(Context context) {
        for (AbstractModel model : getModels()) {
            if (model instanceof CrosswordMagicModel) {
                Puzzle puzzle = ((CrosswordMagicModel) model).getPuzzle();
                if (puzzle != null) {
                    puzzle.clearProgress(context);

                    // Refresh views with empty state
                    getGridLetters();
                    getGridNumbers();
                    getGridDimensions();
                }
            }
        }
    }

}