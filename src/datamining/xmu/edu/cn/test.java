
package datamining.xmu.edu.cn;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.REPTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.WekaPackageManager;
import weka.core.converters.ArffLoader;
//import weka.core.pmml.jaxbbindings.SupportVector;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.Evaluation;

import java.util.Arrays;
import java.util.Random;

//import wlsvm.WLSVM;



/**
 * This example trains NaiveBayes incrementally on data obtained from the
 * ArffLoader.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class test {

	/**
	 * Expects an ARFF file as first argument (class attribute is assumed to be the
	 * last attribute).
	 *
	 * @param args
	 *            the commandline arguments
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static void main(String[] args) throws Exception {

//		String filepath = "C:\\biology\\单细胞分类\\merge\\arffmerge";
//		File file = new File(filepath);
//		String[] filelist = file.list();

		BufferedWriter bw = new BufferedWriter(new FileWriter( "D:merge_EM.txt"));
		int folds=10;

		Classifier cls1 = new RandomForest();
 	 	//Classifier cls1=new Bagging();
//		((Bagging) cls1).setBagSizePercent(100);
//		((Bagging) cls1).setCalcOutOfBag(false);
//
//		((Bagging) cls1).setDebug(false);
//		((Bagging) cls1).setNumExecutionSlots(1);
//		((Bagging) cls1).setNumIterations(10);
//		((Bagging) cls1).setSeed(1);

	//	((RandomForest) cls1).setOptions(weka.core.Utils.splitOptions(" -I 100 -K 0   -S 1 "));
//		WekaPackageManager.loadPackages( false, true, false );
//		AbstractClassifier cls2 = ( AbstractClassifier ) Class.forName("weka.classifiers.functions.LibSVM" ).newInstance();
//		Classifier cls3 = new J48();
//		Classifier cls4 = new Bagging();
//		Classifier cls5 = new IBk();

		((RandomForest) cls1).setMaxDepth(0);
		((RandomForest) cls1).setNumTrees(100);
		((RandomForest) cls1).setNumFeatures(0);
		((RandomForest) cls1).setSeed(1);
//		Classifier cls1=new LibSVM();
//		((LibSVM) cls1).setCacheSize(40.0);
//		((LibSVM) cls1).setCoef0(0.0);
//		((LibSVM) cls1).setCost(1.0);
//		((LibSVM) cls1).setDegree(3);
//		((LibSVM) cls1).setEps(0.001);
//		((LibSVM) cls1).setGamma(0.0);
//		((LibSVM) cls1).setKernelType(  new SelectedTag(2, LibSVM.TAGS_KERNELTYPE));
//		((LibSVM) cls1).setDoNotReplaceMissingValues(false);
//		((LibSVM) cls1).setLoss(0.1);
//		((LibSVM) cls1).setNormalize(false);
//		((LibSVM) cls1).setNu(0.5);
//		((LibSVM) cls1).setProbabilityEstimates(false);
//		((LibSVM) cls1).setShrinking(true);
//		((LibSVM) cls1).setSVMType(new SelectedTag(0, LibSVM.TAGS_SVMTYPE));

		System.out.println(Arrays.toString(((RandomForest) cls1).getOptions()));
		//System.exit(0);
//		for (int i = 1; i < filelist.length; i++) {

			System.out.println("-----------------------------------"+"arff文件"+"的结果---------------------------------\n");
			bw.write("-----------------------------------"+"arff文件"+"的结果---------------------------------\r\n");

			// load unlabeled data
			Instances data = new Instances(new BufferedReader(new FileReader("188D.arff")));

			// set class attribute
			data.setClassIndex(data.numAttributes() - 1);

			// randomize data
			Random rand = new Random();
			Instances randData = new Instances(data);
			randData.randomize(rand);
			if (randData.classAttribute().isNominal())
				randData.stratify(folds);

			Evaluation evalAll = new Evaluation(randData);
			for (int n = 0; n < folds; n++) {
				Evaluation eval = new Evaluation(randData);
				Instances train = randData.trainCV(folds, n);
				Instances test = randData.testCV(folds, n);
				// the above code is used by the StratifiedRemoveFolds filter, the
				// code below by the Explorer/Experimenter:
				// Instances train = randData.trainCV(folds, n, rand);

				// build and evaluate classifier
				Classifier clsCopy = AbstractClassifier.makeCopy(cls1);
				clsCopy.buildClassifier(train);
				eval.evaluateModel(clsCopy, test);
				evalAll.evaluateModel(clsCopy, test);

				// output evaluation
				bw.newLine();
				bw.write(eval.toMatrixString("=== Confusion matrix for fold " + (n+1) + "/" + folds + " ===\r\n\r\n"));

				System.out.println();
				System.out.println(eval.toMatrixString("=== Confusion matrix for fold " + (n+1) + "/" + folds + " ===\n"));
				System.out.println(eval.pctCorrect());
			}

			// output evaluation
			bw.newLine();
			bw.write(evalAll.toMatrixString("=== Confusion matrix for 10-fold "  + " ===\r\n\r\n"));
			bw.write(evalAll.toClassDetailsString());

			System.out.println();
			System.out.println(evalAll.toMatrixString("=== Confusion matrix for 10-fold "  + " ===\n"));
			System.out.println(evalAll.toClassDetailsString());
		    System.out.println(evalAll.pctCorrect());

//		}
		bw.flush();
		bw.close();

	}
}