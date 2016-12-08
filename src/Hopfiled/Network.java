package Hopfiled;

import java.util.*;

class Network {
    private int dimension;
    private ArrayList<double[]> trainData = new ArrayList<>();
    private double[][] weights;
    private double[] threshold;

    public Network(ArrayList<double[]> trainData) {
        this.trainData = trainData;
        this.dimension = trainData.get(0).length;
        weights = new double[dimension][dimension];
        threshold = new double[dimension];
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                weights[i][j] = 0.0;
    }

    void train() {
        for (int j = 1; j < dimension; j++) {
            for (int i = 0; i < j; i++) {
                for (double[] data : trainData) {
                    weights[i][j] = weights[j][i] = data[i] * data[j] + weights[i][j];
                }
                weights[i][j] = weights[j][i] = weights[i][j] / dimension;
            }
        }
        for (int i = 0; i < dimension; i++) {
            threshold[i] = 0.0;
            for (int j = 0; j < i; j++) {
                threshold[i] += weights[i][j];
            }
        }
    }

    ArrayList<double[]> recall(ArrayList<double[]> testData) {
        ArrayList<double[]> result = new ArrayList<>();
        for (double[] data : testData) {
            double[] input = data.clone();
            for (int times = 0; times < 100000; times++) {
                for (int i = 0; i < dimension; i++) {
                    if (getExcitedState(i, input) > 0.0) {
                        input[i] = 1.0;
                    } else if (getExcitedState(i, input) < 0.0) {
                        input[i] = -1.0;
                    }
                }
            }
            result.add(input);
        }
        return result;
    }

    private double getExcitedState(int i, double[] input) {
        float temp = 0.0f;
        for (int j = 0; j < dimension; j++) {
            temp += weights[i][j] * input[j];
        }
        return temp - threshold[i];
    }
}
