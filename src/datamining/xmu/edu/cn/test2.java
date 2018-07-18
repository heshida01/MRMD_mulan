package datamining.xmu.edu.cn;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.FileReader;
import java.util.Random;

public class test2 {
    public static void main(String[] args) throws Exception{
        featureRate("C:\\Users\\shida\\Desktop\\task\\0714交叉问题\\out_45.arff");
    }
    public static double featureRate(String myarff) throws Exception{

        FileReader reader=new FileReader(myarff);

        Instances data=new Instances(reader);
        //System.out.println(data.numAttributes());
        data.setClassIndex(data.numAttributes()-1);//设置训练集中，target的index



        Classifier classify=new RandomForest();

        classify.buildClassifier(data);      //slow

        Evaluation eval=new Evaluation(data);


        eval.crossValidateModel(classify, data,10, new Random(1000));

        eval.evaluateModel(classify, data);
        reader.close();
        //System.out.println(eval.toSummaryString());
        System.out.println(eval.pctCorrect());
        System.out.println(1-eval.errorRate());
        return 1-eval.errorRate();

    }
}
