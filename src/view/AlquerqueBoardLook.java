package view;

import boardifier.model.ContainerElement;
import boardifier.view.ClassicBoardLook;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.AlquerqueBoard;


public class AlquerqueBoardLook extends ClassicBoardLook {

    public AlquerqueBoardLook(int size, ContainerElement element) {
        super(size / 5, element, -1, Color.BURLYWOOD, Color.BURLYWOOD, 0, Color.BLACK, 3, Color.web("#5C3A1E"), true);
    }

    // We overrided this method to add the lines of the Alquerque board's to the actual board
    @Override
    protected void render() {
        super.render();

        Color lineColor = Color.web("#3A2000");

        // Set the horizontal lines
        for (int i = 0; i < 5; i++) {
            double y = gapYToCells + i * rowHeight + rowHeight / 2.0;
            double x0 = gapXToCells + 0 * colWidth + colWidth / 2.0;
            double x4 = gapXToCells + 4 * colWidth + colWidth / 2.0;

            addShape(makeLine(x0, y, x4, y, lineColor));
        }

        // Set the Vertical lines between each cell
        for (int j = 0; j < 5; j++) {
            double x = gapXToCells + j * colWidth + colWidth / 2.0;
            double y0 = gapYToCells + 0 * rowHeight + rowHeight / 2.0;
            double y4 = gapYToCells + 4 * rowHeight + rowHeight / 2.0;

            addShape(makeLine(x, y0, x, y4, lineColor));
        }

        // Set the diagonals of the board
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i + j) % 2 == 0) {
                    double cx = gapXToCells + j * colWidth + colWidth / 2.0;
                    double cy = gapYToCells + i * rowHeight + rowHeight / 2.0;

                    if (i - 1 >= 0 && j + 1 <= 4) {
                        double nx = gapXToCells + (j+1) * colWidth + colWidth / 2.0;
                        double ny = gapYToCells + (i-1) * rowHeight + rowHeight / 2.0;
                        addShape(makeLine(cx, cy, nx, ny, lineColor));
                    }
                    if (i - 1 >= 0 && j - 1 >= 0) {
                        double nx = gapXToCells + (j-1) * colWidth + colWidth / 2.0;
                        double ny = gapYToCells + (i-1) * rowHeight + rowHeight / 2.0;
                        addShape(makeLine(cx, cy, nx, ny, lineColor));
                    }
                }
            }
        }
    }

    // Sub-function used to define a line with the following coordinates
    private Line makeLine(double x1, double y1, double x2, double y2, Color c) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(c);
        l.setStrokeWidth(1.5);
        return l;
    }


    // Set the color of the cells according to the state of the two tables 'reach' and 'capture'
    @Override
    public void onFaceChange() {
        AlquerqueBoard board = (AlquerqueBoard) element;

        boolean[][] reach   = board.getReachableCells();
        boolean[][] capture = board.getCaptureCells();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (capture[i][j]) {
                    cells[i][j].setFill(Color.RED);
                }
                else if (reach[i][j]) {
                    cells[i][j].setFill(Color.GREEN);
                }
                else {
                    cells[i][j].setFill(Color.BURLYWOOD);
                }
            }
        }
    }
}