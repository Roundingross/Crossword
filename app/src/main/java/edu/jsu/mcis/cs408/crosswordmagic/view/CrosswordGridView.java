package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.beans.PropertyChangeEvent;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.WordDirection;

public class CrosswordGridView extends View implements AbstractView {
    private final Paint gridPaint;
    private final TextPaint gridTextPaint;
    private int viewWidth, viewHeight, gridWidth, gridHeight;
    private int squareWidth, squareHeight, xBegin, yBegin, xEnd, yEnd;
    private Character[][] letters;
    private Integer[][] numbers;
    private String lastGuess = "";

    public CrosswordGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gridTextPaint = new TextPaint();
        gridTextPaint.setAntiAlias(true);
        gridTextPaint.setColor(Color.BLACK);
        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.STROKE);
        setOnTouchListener(new OnTouchHandler(context));

        CrosswordMagicController controller = ((MainActivity) context).getController();
        controller.addView(this);

        controller.getGridDimensions();
        controller.getGridLetters();
        controller.getGridNumbers();
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        this.viewWidth = xNew;
        this.viewHeight = yNew;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (gridWidth > 0 && gridHeight > 0) {
            int gridSize = Math.min(viewWidth, viewHeight);
            this.squareWidth = (gridSize / gridWidth);
            this.squareHeight = (gridSize / gridHeight);
            this.yBegin = (viewHeight - (squareHeight * gridHeight)) / 2;
            this.xBegin = (viewWidth - (squareWidth * gridWidth)) / 2;
            this.yEnd = yBegin + (squareHeight * gridHeight);
            this.xEnd = xBegin + (squareWidth * gridWidth);

            drawGrid(canvas);
            drawBlocks(canvas);
            drawNumbers(canvas);
            drawLetters(canvas);
        }
    }

    private void drawLetters(Canvas canvas) {
        if (letters != null) {
            float TEXT_LETTER_SCALE = 4f;
            float letterTextSize = (squareWidth / TEXT_LETTER_SCALE);
            gridTextPaint.setTextSize(letterTextSize * getResources().getDisplayMetrics().density);
            for (int y = 0; y < letters.length; ++y) {
                for (int x = 0; x < letters[y].length; ++x) {
                    String text = String.valueOf(letters[y][x]);
                    int width = (int)gridTextPaint.measureText(text);
                    int xBeginLetter = ((x * squareWidth) + xBegin) + ((squareWidth - width) / 2);
                    int yBeginLetter = (y * squareWidth) + yBegin + (int)(squareHeight * 0.3f); // shift letter downward
                    StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                    canvas.save();
                    canvas.translate(xBeginLetter, yBeginLetter);
                    staticLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private void drawNumbers(Canvas canvas) {
        if (numbers != null) {
            float TEXT_NUMBER_SCALE = 7f;
            float numberTextSize = (squareWidth / TEXT_NUMBER_SCALE);
            gridTextPaint.setTextSize(numberTextSize * getResources().getDisplayMetrics().density);
            for (int y = 0; y < numbers.length; ++y) {
                for (int x = 0; x < numbers[y].length; ++x) {
                    if (numbers[y][x] != 0) {
                        String text = String.valueOf(numbers[y][x]);
                        int width = (int) gridTextPaint.measureText(text);
                        int xBeginNumber = (x * squareWidth) + xBegin;
                        int yBeginNumber = (y * squareWidth) + yBegin;
                        StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
                        canvas.save();
                        canvas.translate(xBeginNumber, yBeginNumber);
                        staticLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        if (gridWidth > 0 && gridHeight > 0) {
            // draw bounding rectangle
            canvas.drawRect(xBegin, yBegin, xEnd, yEnd, gridPaint);
            // draw grid lines (vertical)
            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(((i * squareWidth) + xBegin), yBegin, ((i * squareWidth) + xBegin), yEnd, gridPaint);
            }
            // draw grid lines (horizontal)
            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(xBegin, ((i * squareHeight) + yBegin), xEnd, ((i * squareHeight) + yBegin), gridPaint);
            }
        }
    }

    private void drawBlocks(Canvas canvas) {
        if (letters != null) {
            gridPaint.setStyle(Paint.Style.FILL);
            for (int y = 0; y < letters.length; ++y) {
                for (int x = 0; x < letters[y].length; ++x) {
                    char BLOCK = '*';
                    if (letters[y][x] == BLOCK) {
                        int xBeginBlock = (x * squareWidth) + xBegin;
                        int yBeginBlock = (y * squareWidth) + yBegin;
                        int xEndBlock = (xBeginBlock + squareWidth);
                        int yEndBlock = (yBeginBlock + squareHeight);
                        canvas.drawRect(xBeginBlock, yBeginBlock, xEndBlock, yEndBlock, gridPaint);
                    }
                }
            }
            gridPaint.setStyle(Paint.Style.STROKE);
        }
    }



    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        Object value = evt.getNewValue();

        switch (name) {
            // Update view for letters
            case CrosswordMagicController.GRID_LETTERS_PROPERTY:
                if (value instanceof Character[][]) {
                    this.letters = (Character[][]) value;
                    invalidate();
                }
                break;
            // Update view for numbers
            case CrosswordMagicController.GRID_NUMBERS_PROPERTY:
                if (value instanceof Integer[][]) {
                    this.numbers = (Integer[][]) value;
                    invalidate();
                }
                break;
            // Update view for dimension
            case CrosswordMagicController.GRID_DIMENSION_PROPERTY:
                if (value instanceof Integer[]) {
                    Integer[] dimension = (Integer[]) value;
                    this.gridHeight = dimension[0];
                    this.gridWidth = dimension[1];
                    invalidate();
                }
                break;
            // Update view for guesses
            case CrosswordMagicController.GUESS_RESULT_PROPERTY: {
                // Display toast message for correct guess
                if (value instanceof android.util.Pair) {
                    android.util.Pair<Integer, WordDirection> result = (android.util.Pair<Integer, WordDirection>) value;
                    int box = result.first;
                    WordDirection direction = result.second;


                    String message = getResources().getString(R.string.toast_correct_guess, lastGuess.toUpperCase(), box, direction.toString());
                    Toast toast = Toast.makeText(getRootView().getContext(), message, Toast.LENGTH_SHORT);
                    toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();

                // Display toast message for wrong guess
                } else if (value instanceof Integer) {
                    int box = (int) value;

                    String message = getResources().getString(R.string.toast_wrong_guess, lastGuess.toUpperCase(), box);
                    Toast toast = Toast.makeText(getRootView().getContext(), message, Toast.LENGTH_SHORT);
                    toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.CENTER_HORIZONTAL, 0, 200);
                    toast.show();
                }
                break;
            }
            // Update view for puzzle solved
            case CrosswordMagicController.PUZZLE_SOLVED_PROPERTY:
                if (value instanceof Boolean) {
                    Toast toast = Toast.makeText(getContext(), "🎉 Congratulations! You completed the puzzle!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 250);
                    toast.show();
                    break;
                }



        }
    }

    private class OnTouchHandler implements View.OnTouchListener {
        private final Context context;
        public OnTouchHandler(Context context) {
            this.context = context;
        }
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int eventX = (int)event.getX();
            int eventY = (int)event.getY();
            if (eventX >= xBegin && eventX <= xEnd && eventY >= yBegin && eventY <= yEnd) {
                int x = ((eventX - xBegin) / squareWidth);
                int y = ((eventY - yBegin) / squareHeight);
                int boxNumber = numbers[y][x];
                if (boxNumber != 0) {
                    promptUserForGuess(boxNumber);
                }
            }
            return false;
        }

        // Prompt user for input
        private void promptUserForGuess(int boxNumber) {
            // Create dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_title_enter_guess_for, boxNumber));

            // Set up input field
            final EditText inputField = new EditText(context);
            inputField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            builder.setView(inputField);

            // Set up buttons
            builder.setPositiveButton(getResources().getString(R.string.button_ok), (dialog, which) -> {
                String input = inputField.getText().toString().trim().toUpperCase();
                if (!input.isEmpty()) {
                    lastGuess = input;
                    CrosswordMagicController controller = ((MainActivity) context).getController();
                    controller.checkGuess(boxNumber, input);
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.button_cancel), (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();

            // Auto-submit when pressing DONE on keyboard
            inputField.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                    return true;
                }
                return false;
            });

            // Auto-open keyboard when dialog shows
            dialog.setOnShowListener(dialogInterface -> {
                inputField.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            dialog.show();
        }
    }



    // Getters for view updates
    public void setLetters(Character[][] letters) {
        this.letters = letters;
        invalidate();
    }
    public void setNumbers(Integer[][] numbers) {
        this.numbers = numbers;
        invalidate();
    }
    public void setGridSize(int width, int height) {
        this.gridWidth = width;
        this.gridHeight = height;
        invalidate();
    }
}

