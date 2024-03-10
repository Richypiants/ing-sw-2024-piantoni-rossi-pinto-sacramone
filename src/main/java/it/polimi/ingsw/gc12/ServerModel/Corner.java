package it.polimi.ingsw.gc12.ServerModel;
public class Corner {
    public final Resource RESOURCE_TYPE;
    public final boolean VALID;
    private Side position;

    protected Corner(Resource resourceType, boolean valid, Side position) {
        this.RESOURCE_TYPE = resourceType;
        this.VALID = valid;
        this.position = position;
    }

    protected Side getPosition() {
        return position;
    }

    protected void togglePosition() {
        // Implementation depends on how position should be toggled
    }
}
