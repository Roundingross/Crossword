package edu.jsu.mcis.cs408.crosswordmagic.controller;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CrosswordMagicController extends AbstractController implements PropertyChangeListener {
    public static final String GRID_LETTERS_PROPERTY = "GridLetters";
    public static final String GRID_NUMBERS_PROPERTY = "GridNumbers";
    public static final String GRID_DIMENSION_PROPERTY = "GridDimension";


    public void getGridLetters() {
        Log.d("DEBUG", "CrosswordMagicController getGridLetters");
        getModelProperty("GridLetters");
    }

    public void getGridNumbers() {
        Log.d("DEBUG", "CrosswordMagicController getGridNumbers");
        getModelProperty("GridNumbers");
    }

    public void getGridDimensions() {
        Log.d("DEBUG", "CrosswordMagicController getGridDimensions");
        getModelProperty("GridDimension");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Log.d("DEBUG", "CrosswordMagicController Property changed: " + evt.getPropertyName());
        super.propertyChange(evt);
    }

}