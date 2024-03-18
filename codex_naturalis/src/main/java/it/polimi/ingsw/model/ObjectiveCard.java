package it.polimi.ingsw.model;

public class ObjectiveCard extends Card {
    private ObjectiveType objectiveType;
    private
    //objectiveCard1.getIdObjective().calculate

     ObjectiveCard(int id,Kingdom kingdom,ObjectiveType objectiveType) {
        super(id,kingdom);
        this.objectiveType = objectiveType;
    }
}
