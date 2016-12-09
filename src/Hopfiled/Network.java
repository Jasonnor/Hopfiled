package Hopfiled;

import javafx.util.Pair;

import java.util.*;

class Network {
    private int dimension;
    private ArrayList<double[]> trainData = new ArrayList<>();
    private double[][] weights;
    private double[] threshold;
    private boolean thresholdZero;

    public Network(ArrayList<double[]> trainData, boolean thresholdZero) {
        this.trainData = trainData;
        this.dimension = trainData.get(0).length;
        this.thresholdZero = thresholdZero;
        weights = new double[dimension][dimension];
        threshold = new double[dimension];
        Arrays.fill(threshold, 0);
        for (int i = 0; i < dimension; i++)
            Arrays.fill(weights[i], 0);
    }

    void train() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < i; j++) {
                for (double[] data : trainData) {
                    weights[i][j] += data[i] * data[j];
                }
                weights[i][j] /= dimension;
                weights[j][i] = weights[i][j];
            }
        }
        if (!thresholdZero) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    threshold[i] += weights[i][j];
                }
            }
        }
    }

    Pair<ArrayList<double[]>, Integer> recall(ArrayList<double[]> testData) {
        ArrayList<double[]> result = new ArrayList<>();
        int runTimes = 0;
        for (double[] data : testData) {
            double[] x = data.clone(), xPerv;
            runTimes = 0;
            do {
                xPerv = x.clone();
                for (int i = 0; i < dimension; i++) {
                    x[i] = sgn(getExcitedState(i, xPerv));
                }
                runTimes++;
            } while (!Arrays.equals(x, xPerv) && runTimes < 10000);
            result.add(x);
        }
        return new Pair<>(result, runTimes);
    }

    private double getExcitedState(int i, double[] x) {
        double sigma = 0.0;
        for (int j = 0; j < dimension; j++)
            if (i != j)
                sigma += weights[i][j] * x[j];
        return sigma - threshold[i];
    }

    private double sgn(double x) {
        return (x > 0.0) ? 1.0 : (x == 0.0) ? x : -1.0;
    }
}
