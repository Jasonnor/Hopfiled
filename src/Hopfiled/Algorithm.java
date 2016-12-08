package Hopfiled;

import java.util.*;

class Algorithm {
    private int dimension;
    private ArrayList<float[]> trainData = new ArrayList<>();
    private float[][] weights;
    private float[] threshold;
    private float[] inputCells;

    public Algorithm(ArrayList<float[]> trainData) {
        this.trainData = trainData;
        this.dimension = trainData.get(0).length;
        weights = new float[dimension][dimension];
        inputCells = new float[dimension];
        threshold = new float[dimension];
        for (int i = 0; i < dimension; i++)
            weights[i][i] = 0;
    }

    public void train() {
        for (int j = 1; j < dimension; j++) {
            for (int i = 0; i < j; i++) {
                for (float[] data : trainData) {
                    weights[i][j] = weights[j][i] = regularize(data[i]) * regularize(data[j]) / dimension;
                }
            }
        }
        for (int i = 0; i < dimension; i++) {
            threshold[i] = 0.0f;
            for (int j = 0; j < i; j++) {
                threshold[i] += weights[i][j];
            }
        }
    }

    public float[] recall(float[] inputs, int maxTimes) {
        System.arraycopy(inputs, 0, inputCells, 0, dimension);
        for (int ii = 0; ii < maxTimes; ii++) {
            for (int i = 0; i < dimension; i++) {
                if (getExcitedState(i) > 0.0f) {
                    inputCells[i] = 1.0f;
                } else {
                    inputCells[i] = -1.0f;
                }
            }
        }
        return inputCells;
    }

    private float regularize(float x) {
        if (x < 0.0f) return -1.0f;
        return 1.0f;
    }

    private float getExcitedState(int i) {
        float temp = 0.0f;
        for (int j = 0; j < dimension; j++) {
            temp += weights[i][j] * inputCells[j];
        }
        return temp - threshold[i];
    }
}
