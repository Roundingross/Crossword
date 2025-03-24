package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.util.Log;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;

public class CrosswordMagicModel extends AbstractModel {
    private final Puzzle puzzle;

    public CrosswordMagicModel(Context context) {
        DAOFactory daoFactory = new DAOFactory(context);
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
}