package edu.jsu.mcis.cs408.crosswordmagic.model;

public class PuzzleMenuItem {
    // Puzzle id and name
    private final int id;
    private final String name;

    // Constructor
    public PuzzleMenuItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }

    // toString()
    @Override
    public String toString() {
        return name;
    }
}
