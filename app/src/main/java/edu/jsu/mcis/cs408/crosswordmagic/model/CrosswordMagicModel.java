package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;

public class CrosswordMagicModel extends AbstractModel {
    private final Puzzle puzzle;
    DAOFactory daoFactory;

    public CrosswordMagicModel(Context context) {
        this.daoFactory = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        int DEFAULT_PUZZLE_ID = 1;
        this.puzzle = puzzleDAO.find(DEFAULT_PUZZLE_ID);

        fireGridUpdates();
    }

    public void fireGridUpdates() {
        if (puzzle != null) {
            firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, puzzle.getLetters());
            firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, puzzle.getNumbers());
            firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, new Integer[] {puzzle.getHeight(), puzzle.getWidth()});
        } else {
            Log.d("DEBUG", "CrosswordMagicModel Puzzle is null");
        }
    }

    public void getGridLetters() {
        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, puzzle.getLetters());
    }

    public void getGridNumbers() {
        firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, puzzle.getNumbers());
    }

    public void getGridDimension() {
        firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, new Integer[] {puzzle.getHeight(), puzzle.getWidth()});
    }

    public void getCluesAcross() {
        firePropertyChange(CrosswordMagicController.CLUES_ACROSS_PROPERTY, null, puzzle.getCluesAcross());
    }

    public void getCluesDown() {
        firePropertyChange(CrosswordMagicController.CLUES_DOWN_PROPERTY, null, puzzle.getCluesDown());
    }

    public void getPuzzleList() {
        PuzzleListItem[] list = daoFactory.getPuzzleDAO().list();
        Log.d("Model", "Firing puzzle list to controller: " + list.length);
        firePropertyChange(CrosswordMagicController.PUZZLE_LIST_PROPERTY, null, list);
    }

    // Check player guess and update the grid
    public void setPlayerGuess(Pair<Integer, String> input) {
        Integer boxNumber = input.first;
        String guess = input.second;
        WordDirection direction = puzzle.checkGuess(boxNumber, guess);

        // Update the view with the result of the guess
        if (direction != null) {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, new Pair<>(boxNumber, direction));
        } else {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, boxNumber);
        }

        // Update the grid so the letters refresh
        fireGridUpdates();
    }

    // Load and save puzzle state
    public void loadState(Context context) {
        if (puzzle != null) {
            puzzle.loadState(context);
        }
    }
    public void saveState(Context context) {
        if (puzzle != null) {
            puzzle.saveState(context);
        }
    }

    // Clear progress
    public void clearProgress(Context context) {
        if (puzzle != null) {
            puzzle.clearProgress(context);
        }
    }

    public Puzzle getPuzzle() {
        return this.puzzle;
    }
}