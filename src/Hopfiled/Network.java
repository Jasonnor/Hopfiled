package Hopfiled;

import java.util.*;

class Network {
    private int dimension;
    private ArrayList<Double[]> trainData = new ArrayList<>();
    private Double[][] weights;
    private Double[] threshold;
    private Double[] inputCells;

    public Network(ArrayList<Double[]> trainData) {
        this.trainData = trainData;
        this.dimension = trainData.get(0).length;
        weights = new Double[dimension][dimension];
        inputCells = new Double[dimension];
        threshold = new Double[dimension];
        for (int i = 0; i < dimension; i++)
            weights[i][i] = 0.0;
    }

    void train() {
        for (int j = 1; j < dimension; j++) {
            for (int i = 0; i < j; i++) {
                for (Double[] data : trainData) {
                    weights[i][j] = weights[j][i] = regularize(data[i]) * regularize(data[j]) / dimension;
                }
            }
        }
        for (int i = 0; i < dimension; i++) {
            threshold[i] = 0.0;
            for (int j = 0; j < i; j++) {
                threshold[i] += weights[i][j];
            }
        }
    }

    Double[] recall(Double[] inputs, int maxTimes) {
        System.arraycopy(inputs, 0, inputCells, 0, dimension);
        for (int ii = 0; ii < maxTimes; ii++) {
            for (int i = 0; i < dimension; i++) {
                if (getExcitedState(i) > 0.0) {
                    inputCells[i] = 1.0;
                } else {
                    inputCells[i] = -1.0;
                }
            }
        }
        return inputCells;
    }

    private Double regularize(Double x) {
        if (x < 0.0) return -1.0;
        return 1.0;
    }

    private Double getExcitedState(int i) {
        float temp = 0.0f;
        for (int j = 0; j < dimension; j++) {
            temp += weights[i][j] * inputCells[j];
        }
        return temp - threshold[i];
    }
}
