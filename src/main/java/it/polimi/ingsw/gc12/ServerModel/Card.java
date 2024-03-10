
public class Card {
    public static final int ID = 0; // Placeholder for unique ID
    public static final int POINTS = 0; // Placeholder for points
    public static final Image FRONT_SPRITE = null; // Placeholder for image
    public static final Image BACK_SPRITE = null; // Placeholder for image
    private Side playedShownSide;

    public Card(int id, int points, Image frontSprite, Image backSprite) {
        // Initialization logic goes here
    }

    public Side getShownSide() {
        return playedShownSide;
    }

    public void setShownSide(Side side) {
        this.playedShownSide = side;
    }

    public int calculatePoints() {
        // Implementation depends on card logic
        return 0; // Placeholder
    }
}
