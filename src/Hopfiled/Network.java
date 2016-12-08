package Hopfiled;

import java.util.*;

class Network {
    private int dimension;
    private ArrayList<Double[]> trainData = new ArrayList<>();
    private Double[][] weights;
    private Double[] threshold;
    private Double[] input;

    public Network(ArrayList<Double[]> trainData) {
        this.trainData = trainData;
        this.dimension = trainData.get(0).length;
        weights = new Double[dimension][dimension];
        input = new Double[dimension];
        threshold = new Double[dimension];
        for (int i = 0; i < dimension; i++)
            weights[i][i] = 0.0;
    }

    void train() {
        for (int j = 1; j < dimension; j++) {
            for (int i = 0; i < j; i++) {
                for (Double[] data : trainData) {
                    weights[i][j] = weights[j][i] = data[i] * data[j] / dimension;
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

    Double[] recall(ArrayList<Double[]> testData) {
        for (Double[] data : testData) {
            System.arraycopy(data, 0, input, 0, dimension);
            for (int times = 0; times < 100; times++) {
                for (int i = 0; i < dimension; i++) {
                    if (getExcitedState(i) > 0.0) {
                        input[i] = 1.0;
                    } else {
                        input[i] = -1.0;
                    }
                }
            }
        }
        return input;
    }

    private Double getExcitedState(int i) {
        float temp = 0.0f;
        for (int j = 0; j < dimension; j++) {
            temp += weights[i][j] * input[j];
        }
        return temp - threshold[i];
    }
}
