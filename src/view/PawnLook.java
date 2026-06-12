package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import model.Pawn;

public class PawnLook extends ElementLook {

    private Circle circle;
    private int radius;

    public PawnLook(int radius, GameElement element) {
        super(element);
        this.radius = radius;
        render();
    }

    @Override
    public void onSelectionChange() {
        Pawn pawn = (Pawn) getElement();
        if (pawn.isSelected()) {
            circle.setStrokeWidth(3);
            circle.setStrokeMiterLimit(10);
            circle.setStrokeType(StrokeType.CENTERED);
            circle.setStroke(Color.valueOf("0xFFFF00"));
        }
        else {
            circle.setStrokeWidth(0);
        }
    }

    @Override
    public void onFaceChange() { }

    protected void render() {
        Pawn pawn = (Pawn) element;
        circle = new Circle();
        circle.setRadius(radius);
        if (pawn.getColor() == Pawn.PAWN_WHITE) {
            circle.setFill(Color.web("#F5E6C8"));
            circle.setStroke(Color.web("#8B6914"));
            circle.setStrokeWidth(2);
        }
        else {
            circle.setFill(Color.web("#1A0A00"));
            circle.setStroke(Color.web("#5C3A1E"));
            circle.setStrokeWidth(2);
        }
        addShape(circle);
    }
}