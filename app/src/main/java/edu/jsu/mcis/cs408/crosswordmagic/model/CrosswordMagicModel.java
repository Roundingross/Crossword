package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;

public class CrosswordMagicModel extends AbstractModel {
    private Puzzle puzzle;
    DAOFactory daoFactory;

    // Constructor with puzzle ID (used when launching a specific puzzle)
    public CrosswordMagicModel(Context context, int puzzleId) {
        this.daoFactory = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        this.puzzle = puzzleDAO.find(puzzleId);
        Log.d("DEBUG", "New model created for puzzle ID: " + puzzleId);
        fireGridUpdates();
    }

    // Default constructor for initial load (defaults to puzzle ID 1)
    public CrosswordMagicModel(Context context) {
        this(context, 1);
        Log.d("DEBUG", "Default constructor called, loading fallback puzzle ID: 1");
    }

    // Fires the current puzzle data to the view
    public void fireGridUpdates() {
        if (puzzle != null) {
            Log.d("DEBUG", "Firing grid updates for puzzle ID: " + puzzle.getId() + " (" + puzzle.getName() + "), size: " + puzzle.getHeight() + "x" + puzzle.getWidth());
            firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, puzzle.getLetters());
            firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, puzzle.getNumbers());
            firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, new Integer[]{puzzle.getHeight(), puzzle.getWidth()});
        }
    }

    // Individual grid and clue getters
    public void getGridLetters() {
        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, puzzle.getLetters());
    }

    public void getGridNumbers() {
        firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, puzzle.getNumbers());
    }

    public void getGridDimension() {
        firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, new Integer[]{puzzle.getHeight(), puzzle.getWidth()});
    }

    public void getCluesAcross() {
        firePropertyChange(CrosswordMagicController.CLUES_ACROSS_PROPERTY, null, puzzle.getCluesAcross());
    }

    public void getCluesDown() {
        firePropertyChange(CrosswordMagicController.CLUES_DOWN_PROPERTY, null, puzzle.getCluesDown());
    }

    // Load list of puzzles already on the device
    public void getPuzzleList() {
        Log.d("SPINNER", "getPuzzleList() called");

        try {
            PuzzleListItem[] list = daoFactory.getPuzzleDAO().list();
            Log.d("SPINNER", "DAO returned list (not null)");

            if (list.length > 0) {
                Log.d("SPINNER", "Model: Firing puzzle list with " + list.length + " items");
                firePropertyChange(CrosswordMagicController.PUZZLE_LIST_PROPERTY, null, list);
            } else {
                Log.d("SPINNER", "Model: list is empty");
            }
        }
        catch (Exception e) {
            Log.d("SPINNER", "getPuzzleList() failed", e);
        }
    }

    // Load puzzle menu from the web
    public void getPuzzleMenu() {
        PuzzleMenuItem[] list = daoFactory.getWebServiceDAO().list();
        Log.d("DEBUG", "Firing puzzle menu to controller: " + list.length);
        firePropertyChange(CrosswordMagicController.PUZZLE_MENU_PROPERTY, null, list);
    }

    // Check a guess and refresh the view
    public void setPlayerGuess(Pair<Integer, String> input) {
        Integer boxNumber = input.first;
        String guess = input.second;
        WordDirection direction = puzzle.checkGuess(boxNumber, guess);

        if (direction != null) {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, new Pair<>(boxNumber, direction));
        } else {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, boxNumber);
        }

        fireGridUpdates();
    }

    // Download and store puzzle from web service, update internal model and views
    public void downloadPuzzle(int webId) {
        // Check for duplicates
        String puzzleName = "Web Puzzle " + webId;
        Puzzle existing = daoFactory.getPuzzleDAO().findByName(puzzleName);

        // If duplicate, notify controller and return
        if (existing != null) {
            Log.d("DEBUG", "Puzzle already exists in database: " + puzzleName);
            firePropertyChange(CrosswordMagicController.PUZZLE_READY_PROPERTY, null, existing.getId());
            return;
        }

        try {
            Log.d("DEBUG", "Starting downloadPuzzle() for web ID: " + webId);
            JSONObject json = daoFactory.getWebServiceDAO().getPuzzleFromWeb(webId);

            if (json != null) {
                Log.d("DEBUG", "Parsing JSON from web puzzle...");

                // Puzzle metadata
                HashMap<String, String> params = new HashMap<>();
                params.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_name")), "Web Puzzle " + webId);
                params.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_description")), "Downloaded puzzle " + webId);
                params.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_height")), "15");
                params.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_width")), "15");

                Puzzle newPuzzle = new Puzzle(params);
                int puzzleId = daoFactory.getPuzzleDAO().create(newPuzzle);
                newPuzzle.setId(puzzleId);

                // Insert words
                JSONArray words = json.getJSONArray("puzzle");
                ArrayList<Word> wordList = new ArrayList<>();

                for (int i = 0; i < words.length(); i++) {
                    JSONObject wordObj = words.getJSONObject(i);
                    HashMap<String, String> wordParams = new HashMap<>();

                    try {
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_puzzleid")), String.valueOf(puzzleId));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_row")), String.valueOf(wordObj.getInt("row")));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_column")), String.valueOf(wordObj.getInt("column")));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_box")), String.valueOf(wordObj.getInt("box")));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_direction")), String.valueOf(wordObj.getInt("direction")));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_word")), wordObj.getString("word"));
                        wordParams.put(Objects.requireNonNull(daoFactory.getProperty("sql_field_clue")), wordObj.getString("clue"));

                        Word newWord = new Word(wordParams);

                        if (newWord.getDirection() == null) {
                            Log.d("DEBUG", "Null direction in word: " + newWord);
                            continue;
                        }

                        daoFactory.getWordDAO().create(newWord);
                        wordList.add(newWord);

                    } catch (Exception e) {
                        Log.d("DEBUG", "Error creating word from JSON: " + wordObj.toString());
                        e.printStackTrace();
                    }
                }

                newPuzzle.addWordsToPuzzle(wordList);
                Log.d("DEBUG", "Puzzle inserted into database with ID: " + puzzleId);

                // Assign puzzle to model and update views
                this.puzzle = daoFactory.getPuzzleDAO().find(puzzleId);
                fireGridUpdates();

                // Notify controller
                firePropertyChange(CrosswordMagicController.PUZZLE_READY_PROPERTY, null, puzzleId);
            }
        } catch (Exception e) {
            Log.d("DEBUG", "Download puzzle failed", e);
        }
    }



    // Set the download from the website
    public void setDownloadPuzzle(int webId) {
        Log.d("DEBUG", "Model received download request for web puzzle ID: " + webId);
        downloadPuzzle(webId);
    }

    // Save/load/reset puzzle state (progress)
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

    public void clearProgress(Context context) {
        if (puzzle != null) {
            puzzle.clearProgress(context);
        }
    }

    // Getter for the active puzzle object
    public Puzzle getPuzzle() {
        return this.puzzle;
    }
}