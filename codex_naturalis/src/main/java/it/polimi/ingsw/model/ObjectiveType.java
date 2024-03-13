package it.polimi.ingsw.model;

public enum ObjectiveType implements objectiveEffect {
    SUM {
        @Override
        public double calculate(List<Integer> values) {
            return (double) values.stream().mapToInt(i -> i).sum();
        }
    },
    COUNT {
        @Override
        public double calculate(List<Integer> values) {
            return (double) values.stream().count();
        }
    },
    AVG {
        @Override
        public double calculate(List<Integer> values) {
            return SUM.calculate(values) / COUNT.calculate(values);
        }
    }
}
