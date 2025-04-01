package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;

public class Puzzle extends AbstractModel{

    public static final char BLOCK_CHAR = '*';
    public static final char BLANK_CHAR = ' ';
    private final HashMap<String, Word> words;
    private final HashSet<String> guessed;
    private final String name, description;
    private final Integer height, width;
    private final Character[][] letters;
    private final Integer[][] numbers;
    private boolean solved = false;
    private final StringBuilder cluesAcrossBuffer, cluesDownBuffer;
    private boolean isSolved = false;
    private static final String PREFS_NAME = "CrosswordMagic";
    private static final String KEY_GUESSED_WORDS = "guessed_words";
    private static final String KEY_LETTERS_GRID = "letters_grid";

    public Puzzle(HashMap<String, String> params) {
        this.name = params.get("name");
        this.description = params.get("description");
        this.height = Integer.parseInt(Objects.requireNonNull(params.get("height")));
        this.width = Integer.parseInt(Objects.requireNonNull(params.get("width")));
        guessed = new HashSet<>();
        words = new HashMap<>();
        letters = new Character[height][width];
        numbers = new Integer[height][width];
        cluesAcrossBuffer = new StringBuilder();
        cluesDownBuffer = new StringBuilder();
        /* fill initial grids with solid zeroes and blocks */
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                letters[i][j] = BLOCK_CHAR;
                numbers[i][j] = 0;
            }
        }
    }

    public void addWordsToPuzzle(ArrayList<Word> words) {
        if (words != null) {
            for (Word w : words) {
                addWordToPuzzle(w);
            }
        }
    }

    public void addWordToPuzzle(Word word) {
        String key = (word.getBox() + word.getDirection().toString());
        /* add to collection */
        words.put(key, word);
        /* get word properties */
        int row = word.getRow();
        int column = word.getColumn();
        int length = word.getWord().length();
        /* add box number to grid of numbers */
        numbers[row][column] = word.getBox();
        /* "hollow out" letters; replace with blanks */
        for (int i = 0; i < length; ++i) {
            letters[row][column] = BLANK_CHAR;
            if (word.isAcross())
                column++;
            else if (word.isDown())
                row++;
        }
        /* append clue (across or down) to corresponding StringBuilder */
        if (word.isAcross()) {
            cluesAcrossBuffer.append(word.getBox()).append(": ");
            cluesAcrossBuffer.append(word.getClue()).append(System.lineSeparator());
        }
        else if (word.isDown()) {
            cluesDownBuffer.append(word.getBox()).append(": ");
            cluesDownBuffer.append(word.getClue()).append(System.lineSeparator());
        }
        /* add word to guessed list (for development only!) */

        // addWordToGuessed(key); // remove this later!

    }

    public void setPlayerGuess(Pair<Integer, String> input) {
        int boxNumber = input.first;
        String guess = input.second;

        WordDirection direction = checkGuess(boxNumber, guess);

        if (direction != null) {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, new Pair<>(boxNumber, direction));
        } else {
            firePropertyChange(CrosswordMagicController.GUESS_RESULT_PROPERTY, null, boxNumber);
        }
        if (this.solved && !isSolved) {
            firePropertyChange(CrosswordMagicController.PUZZLE_SOLVED_PROPERTY, null, true);
            isSolved = true;
        }
    }

    public WordDirection checkGuess(Integer num, String guess) {
        WordDirection result = null;
        String acrossKey = num + WordDirection.ACROSS.toString();
        String downKey = num + WordDirection.DOWN.toString();
        /* get the words across and down in the selected box */
        Word across = words.get(acrossKey);
        Word down = words.get(downKey);
        /* compare guess to both words; if a match is found, add word to guessed list */
        if (across != null) {
            if (across.getWord().equals(guess) && !(guessed.contains(acrossKey))) {
                result = WordDirection.ACROSS;
                addWordToGuessed(acrossKey);
            }
        }
        if (down != null) {
            if (down.getWord().equals(guess) && !(guessed.contains(downKey))) {
                result = WordDirection.DOWN;
                addWordToGuessed(downKey);
            }
        }
        /* check if any blank squares remain after guess; if not, the puzzle is solved */
        this.solved = true;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                if (letters[i][j] == BLANK_CHAR) {
                    this.solved = false;
                    break;
                }
            }
        }
        /* return direction of guessed word (across, down, or null for a wrong guess) */
        return result;
    }

    public void addWordToGuessed(String key) {
        Word w = words.get(key);
        guessed.add(key);
        /* get word properties */
        int row = w.getRow();
        int column = w.getColumn();
        String word = w.getWord();
        int length = word.length();
        /* place letters in letter grid */
        for (int i = 0; i < length; ++i) {
            letters[row][column] = word.charAt(i);
            if (w.isAcross())
                column++;
            else if (w.isDown())
                row++;
        }
    }

    // Saved state for return use
    public void saveState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CrosswordMagic", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_GUESSED_WORDS, String.join(",", guessed));

        StringBuilder gridBuilder = new StringBuilder();
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                gridBuilder.append(letters[i][j]);
            }
        }
        editor.putString(KEY_LETTERS_GRID, gridBuilder.toString());
        editor.apply();
    }

    // Load state from saved state
    public void loadState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CrosswordMagic", Context.MODE_PRIVATE);
        String guessedWords = prefs.getString(KEY_GUESSED_WORDS, "");
        if (!guessedWords.isEmpty()) {
            String[] keys = guessedWords.split(",");
            for (String key : keys) {
                addWordToGuessed(key);
            }
        }
        String grid = prefs.getString(KEY_LETTERS_GRID, "");
        if (!grid.isEmpty() && grid.length() == height * width) {
            int index = 0;
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    letters[i][j] = grid.charAt(index);
                    index++;
                }
            }
        }
        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, letters);
        firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, numbers);
        firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, new Integer[]{height, width});
    }

    // Clear progress of puzzle
    public void clearProgress(Context context) {
        guessed.clear();
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                if (letters[i][j] != BLOCK_CHAR) {
                    letters[i][j] = BLANK_CHAR;
                }
            }
        }

        // Clear SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Clear solved
        solved = false;
        isSolved = false;

        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, letters);
    }


    public Word getWord(String key) { return words.get(key); }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Integer getWidth() { return width; }

    public Integer getHeight() { return height; }

    public String getCluesAcross() { return cluesAcrossBuffer.toString(); }

    public String getCluesDown() { return cluesDownBuffer.toString(); }

    public int getSize() { return words.size(); }

    public Character[][] getLetters() { return letters; }

    public Integer[][] getNumbers() { return numbers; }

    public boolean isSolved() { return solved; }
}