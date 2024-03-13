package it.polimi.ingsw.model;

import java.util.List;

public interface ObjectiveEffect {
    interface Stats{
        //Calcuate Stats for given list of values
        double calculate(List<Integer> values);
    }
}
