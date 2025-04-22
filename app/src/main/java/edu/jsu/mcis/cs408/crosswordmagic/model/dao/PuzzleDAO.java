package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;
import edu.jsu.mcis.cs408.crosswordmagic.model.WordDirection;

public class PuzzleDAO {
    private final DAOFactory daoFactory;
    PuzzleDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    /* add a new puzzle entry to the database */
    public int create(Puzzle newPuzzle) {
        /* use this method if there is NOT already a SQLiteDatabase open */
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        int result = create(db, newPuzzle);
        db.close();
        return result;
    }

    public int create(SQLiteDatabase db, Puzzle newPuzzle) {

        int key;
        /* use this method if there IS already a SQLiteDatabase open */
        String name = daoFactory.getProperty("sql_field_name");
        String description = daoFactory.getProperty("sql_field_description");
        String height = daoFactory.getProperty("sql_field_height");
        String width = daoFactory.getProperty("sql_field_width");

        ContentValues values = new ContentValues();
        values.put(name, newPuzzle.getName());
        values.put(description, newPuzzle.getDescription());
        values.put(height, newPuzzle.getHeight());
        values.put(width, newPuzzle.getWidth());

        key = (int)db.insert(daoFactory.getProperty("sql_table_puzzles"), null, values);
        return key;
    }

    /* return an existing puzzle entry from the database */
    public Puzzle find(int puzzleid) {
        /* use this method if there is NOT already a SQLiteDatabase open */
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Puzzle result = find(db, puzzleid);
        db.close();
        result.setId(puzzleid);
        return result;
    }

    public Puzzle find(SQLiteDatabase db, int puzzleid) {
        Log.d("DEBUG", "PuzzleDAO.find(): Getting puzzle ID " + puzzleid);
        /* use this method if there is NOT already a SQLiteDatabase open */
        Puzzle puzzle = null;
        String query = daoFactory.getProperty("sql_get_puzzle");
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            /* get data for puzzle */
            HashMap<String, String> params = new HashMap<>();
            /* get data for new puzzle */

            /* INSERT YOUR CODE HERE */
            if (cursor.moveToFirst()) {
                params.put(daoFactory.getProperty("sql_field_id"), cursor.getString(0));
                params.put(daoFactory.getProperty("sql_field_name"), cursor.getString(1));
                params.put(daoFactory.getProperty("sql_field_description"), cursor.getString(2));
                params.put(daoFactory.getProperty("sql_field_height"), cursor.getString(3));
                params.put(daoFactory.getProperty("sql_field_width"), cursor.getString(4));
                puzzle = new Puzzle(params);
            }
            cursor.close();

            /* get list of words (if any) to add to puzzle */
            WordDAO wordDao = daoFactory.getWordDAO();
            ArrayList<Word> words = wordDao.list(db, puzzleid);

            if (!words.isEmpty()) {
                puzzle.addWordsToPuzzle(words);
                Log.d("DEBUG", "PuzzleDAO.find(): Added " + words.size() + " words to puzzle ID: " + puzzleid);
            }


            cursor.close();

            /* get already-guessed words (if any) for puzzle */
            query = daoFactory.getProperty("sql_get_guesses");
            int boxColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_box_column_index"));
            int directionColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_direction_column_index"));
            cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                do {
                    Integer box = cursor.getInt(boxColumnIndex);
                    WordDirection direction = WordDirection.values()[cursor.getInt(directionColumnIndex)];
                    puzzle.addWordToGuessed(box + direction.toString());
                }
                while ( cursor.moveToNext() );
                cursor.close();
            }
        }
        puzzle.setId(puzzleid);
        return puzzle;
    }

    public PuzzleListItem[] list() {
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        PuzzleListItem[] result = list(db);
        db.close();
        return result;
    }

    public PuzzleListItem[] list(SQLiteDatabase db) {
        ArrayList<PuzzleListItem> puzzles = new ArrayList<>();
        String query = daoFactory.getProperty("sql_list_puzzles");
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                puzzles.add(new PuzzleListItem(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();

        Log.d("DEBUG", "Loaded puzzles: " + puzzles.size());

        return puzzles.toArray(new PuzzleListItem[]{});
    }

    // Find a puzzle by name (helper for duplicate check)
    public Puzzle findByName(String name) {
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Puzzle result = null;

        String query = "SELECT * FROM " + daoFactory.getProperty("sql_table_puzzles") +
                " WHERE " + daoFactory.getProperty("sql_field_name") + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name});

        if (cursor.moveToFirst()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(daoFactory.getProperty("sql_field_id"), cursor.getString(0));
            params.put(daoFactory.getProperty("sql_field_name"), cursor.getString(1));
            params.put(daoFactory.getProperty("sql_field_description"), cursor.getString(2));
            params.put(daoFactory.getProperty("sql_field_height"), cursor.getString(3));
            params.put(daoFactory.getProperty("sql_field_width"), cursor.getString(4));
            result = new Puzzle(params);
            result.setId(cursor.getInt(0));
        }

        cursor.close();
        db.close();
        return result;
    }

}