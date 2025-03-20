package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import edu.jsu.mcis.cs408.crosswordmagic.model.Word;

public class WordDAO {
    private final DAOFactory daoFactory;
    WordDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    /* add a new word entry to the database */
    public int create(Word newWord) {
        /* use this method if there is NOT already a SQLiteDatabase open */
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        int result = create(db, newWord);
        db.close();
        return result;
    }

    public int create(SQLiteDatabase db, Word newWord) {
        /* use this method if there IS already a SQLiteDatabase open */
        int key = 0;
        String puzzleid = daoFactory.getProperty("sql_field_puzzleid");
        String row = daoFactory.getProperty("sql_field_row");
        String column = daoFactory.getProperty("sql_field_column");
        String box = daoFactory.getProperty("sql_field_box");
        String direction = daoFactory.getProperty("sql_field_direction");
        String word = daoFactory.getProperty("sql_field_word");
        String clue = daoFactory.getProperty("sql_field_clue");

        ContentValues values = new ContentValues();
        values.put(puzzleid, newWord.getPuzzleid());
        values.put(row, newWord.getRow());
        values.put(column, newWord.getColumn());
        values.put(box, newWord.getBox());
        values.put(direction, newWord.getDirection().ordinal());
        values.put(word, newWord.getWord());
        values.put(clue, newWord.getClue());

        key = (int) db.insert(daoFactory.getProperty("sql_table_words"), null, values);
        return key;
    }

    /* return a list of existing word entries from the database */
    public ArrayList<Word> list(int puzzleid) {
        /* use this method if there is NOT already a SQLiteDatabase open */
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        ArrayList<Word> result = list(db, puzzleid);
        db.close();
        return result;
    }

    public ArrayList<Word> list(SQLiteDatabase db, int puzzleid) {
        ArrayList<Word> result = new ArrayList<>();
        String query = daoFactory.getProperty("sql_get_words");
        Cursor cursor = db.rawQuery(query, new String[]{ String.valueOf(puzzleid) });

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                HashMap<String, String> params = new HashMap<>();
                /* get data for the next word in the puzzle */
                query = daoFactory.getProperty("sql_get_words");
                cursor = db.rawQuery(query, new String[]{ String.valueOf(puzzleid) });
                if (cursor.moveToFirst()) {
                    do {
                        int wordid = cursor.getInt(0);
                        result.add(find(db, wordid));
                    }
                    while ( cursor.moveToNext() );
                    cursor.close();
                }
            }
            while ( cursor.moveToNext() );
            cursor.close();
        }
        return result;
    }

    /* return an individual word entry from the database */
    public Word find(int id) {
        /* use this method if there is NOT already a SQLiteDatabase open */
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Word result = find(db, id);
        db.close();
        return result;
    }

    public Word find(SQLiteDatabase db, int id) {
        /* use this method if there IS already a SQLiteDatabase open */
        Word word = null;

        String query = daoFactory.getProperty("sql_get_word");
        Cursor cursor = db.rawQuery(query, new String[]{ String.valueOf(id) });

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> params = new HashMap<>();

                /* INSERT YOUR CODE HERE */
                params.put(daoFactory.getProperty("sql_field_id"), cursor.getString(0));
                params.put(daoFactory.getProperty("sql_field_puzzleid"), cursor.getString(1));
                params.put(daoFactory.getProperty("sql_field_row"), cursor.getString(2));
                params.put(daoFactory.getProperty("sql_field_column"), cursor.getString(3));
                params.put(daoFactory.getProperty("sql_field_box"), cursor.getString(4));
                params.put(daoFactory.getProperty("sql_field_direction"), cursor.getString(5));
                params.put(daoFactory.getProperty("sql_field_word"), cursor.getString(6));
                params.put(daoFactory.getProperty("sql_field_clue"), cursor.getString(7));

                word = new Word(params);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return word;
    }
}