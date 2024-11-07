// package org.neuroph.eval;
package TwitterGatherDataFollowers.userRyersonU;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.data.DataSet;
import org.neuroph.eval.classification.ClassificationMetrics;
import org.neuroph.eval.classification.ConfusionMatrix;

/**
 * Create class that will hold statistics for all evaluated datasets - avgs, mx, min, std, variation
 * Result of the evaluation procedure
 * 
 * @author zoran
 */
public class EvaluationResult {
    // for now this aggregates hardcoded results from all evaluators
    ConfusionMatrix confusionMatrix;
    double meanSquareError ;
    DataSet dataSet;
    // Sepide NeuralNetwork neuralNetwork;
	MultiLayerPerceptron multiLayerPerceptron;

    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }
    
    public ClassificationMetrics[] getClassificationMetricses() {
        return ClassificationMetrics.createFromMatrix(confusionMatrix);
    }    

    public double getMeanSquareError() {
        return meanSquareError;
    }

    public void setMeanSquareError(double meanSquareError) {
        this.meanSquareError = meanSquareError;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public MultiLayerPerceptron getNeuralNetwork() {
        return multiLayerPerceptron;
    }

    public void setNeuralNetwork(MultiLayerPerceptron multiLayerPerceptron) {
        this.multiLayerPerceptron = multiLayerPerceptron;
    }

    @Override
    public String toString() {
        //-- also display getClassificationMetricses here
          ClassificationMetrics[] cms = getClassificationMetricses();
          StringBuilder sb = new StringBuilder();
          for(ClassificationMetrics c : cms ) {
              sb.append(c).append("\r\n");
          }
          
        return "EvaluationResult{" + "dataSet=" + dataSet.getLabel() + ", meanSquareError=" + meanSquareError + ", \r\n confusionMatrix=\r\n" + confusionMatrix +"\r\n" +sb.toString() +"}\r\n";
    }
                                    
}
