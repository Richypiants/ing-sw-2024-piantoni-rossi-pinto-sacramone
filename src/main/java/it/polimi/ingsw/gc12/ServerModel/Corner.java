package it.polimi.ingsw.gc12.ServerModel;
public class Corner {
    public final Resource RESOURCE_TYPE;
    public final boolean VALID;
    private Side position;

    public Corner(Resource resourceType, boolean valid, Side position) {
        this.RESOURCE_TYPE = resourceType;
        this.VALID = valid;
        this.position = position;
    }

    public Side getPosition() {
        return position;
    }

    public void togglePosition() {
        // Implementation depends on how position should be toggled
    }
}
