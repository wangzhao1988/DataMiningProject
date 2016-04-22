import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;

public class MultinomialClassifier {
	private List<String> processedData;
	private ArrayList<Attribute> attributes;
	private Map<String, Integer> dest2Index;
	private Map<Integer, String> index2Dest;
	
	public MultinomialClassifier() {
		this.processedData = new ArrayList<String>();
		this.attributes = new ArrayList<Attribute>();	
		this.dest2Index = new HashMap<String, Integer>();
		this.index2Dest = new HashMap<Integer, String>();
	}
	
	public void init() {
		Preprocess p = new Preprocess();
		for (String example: p.processedData) {
			this.processedData.add(example);
		}
		
		for (String dest: p.destinationMap.keySet()) {
			int index = p.destinationMap.get(dest);
			this.dest2Index.put(dest, index);
			this.index2Dest.put(index, dest);
		}
		
		List<String> attribute1List = new ArrayList<String>(p.attributeMap.get("booking2CreateLag"));
		Attribute att1 = new Attribute("booking2CreateLag", attribute1List);
		this.attributes.add(att1);
		
		List<String> attribute2List = new ArrayList<String>(p.attributeMap.get("booking2ActiveLag"));
		Attribute att2 = new Attribute("booking2ActiveLag", attribute2List);
		this.attributes.add(att2);
		
		List<String> attribute3List = new ArrayList<String>(p.attributeMap.get("gender"));
		Attribute att3 = new Attribute("gender", attribute3List);
		this.attributes.add(att3);
		
		Attribute att4 = new Attribute("age");
		this.attributes.add(att4);
		
		List<String> attribute5List = new ArrayList<String>(p.attributeMap.get("signupMethod"));
		Attribute att5 = new Attribute("signupMethod", attribute5List);
		this.attributes.add(att5);
		
		Attribute att6 = new Attribute("signupFlow");
		this.attributes.add(att6);
		
		List<String> attribute7List = new ArrayList<String>(p.attributeMap.get("language"));
		Attribute att7 = new Attribute("language", attribute7List);
		this.attributes.add(att7);
		
		List<String> attribute8List = new ArrayList<String>(p.attributeMap.get("channel"));
		Attribute att8 = new Attribute("channel", attribute8List);
		this.attributes.add(att8);
		
		List<String> attribute9List = new ArrayList<String>(p.attributeMap.get("provider"));
		Attribute att9 = new Attribute("provider", attribute9List);
		this.attributes.add(att9);
		
		List<String> attribute10List = new ArrayList<String>(p.attributeMap.get("tracked"));
		Attribute att10 = new Attribute("tracked", attribute10List);
		this.attributes.add(att10);
		
		List<String> attribute11List = new ArrayList<String>(p.attributeMap.get("signupApp"));
		Attribute att11 = new Attribute("signupApp", attribute11List);
		this.attributes.add(att11);
		
		List<String> attribute12List = new ArrayList<String>(p.attributeMap.get("deviceType"));
		Attribute att12 = new Attribute("deviceType", attribute12List);
		this.attributes.add(att12);
		
		List<String> attribute13List = new ArrayList<String>(p.attributeMap.get("browser"));
		Attribute att13 = new Attribute("browser", attribute13List);
		this.attributes.add(att13);
		
		List<String> classList = new ArrayList<String>(p.attributeMap.get("destination"));
		Attribute classAttribute = new Attribute("destination", classList);
		this.attributes.add(classAttribute);
		System.out.println("Loading data succussfully");
	}
	
	public void process() {
		int totalSize = this.processedData.size();
//		System.out.println(totalSize);
		int trainingSize = (totalSize * 8)/10;
		
		List<List<Double>> errorRates = new ArrayList<List<Double>>();
		for (int i = 0; i < 7; i++) {
			errorRates.add(new ArrayList<Double>());
		}
		
		int[] sizeArray = {1000, 2000, 5000, 8000, 10000, 20000, 50000, trainingSize};
		
		for (int i = 0; i < sizeArray.length; i++) {
			int size = sizeArray[i];
			Collections.shuffle(processedData);
			System.out.println("Current training data size: " + size);
			Instances trainingSet = new Instances("Airbnb Users", this.attributes, size);
			trainingSet.setClassIndex(this.attributes.size()-1);
			getInstances(trainingSet, 0, size);
//			System.out.println("Training size: " + trainingSet.size());
			
			Instances testingSet = new Instances("Airbnb Users", this.attributes, totalSize-trainingSize);
			testingSet.setClassIndex(this.attributes.size()-1);
			getInstances(testingSet, trainingSize, totalSize);
//			System.out.println("Testing size: " + testingSet.size());
			
			double errorRate = 0;
			
//			Classifier naiveBayesModel = (Classifier)new NaiveBayes();
//			errorRate = getErrorRate(naiveBayesModel, trainingSet, testingSet);
//			errorRates.get(0).add(errorRate);
//			System.out.printf("Naive Bayes: %.4f", errorRate);
//			System.out.println();
			
			Classifier j48 = (Classifier)new J48();
			errorRate = getErrorRate(j48, trainingSet, testingSet);
			errorRates.get(1).add(errorRate);
			System.out.printf("Decision Tree: %.4f", errorRate);
			System.out.println();
			
//			Classifier logistic = (Classifier)new LMT();
//			errorRate = getErrorRate(logistic, trainingSet, testingSet);
//			errorRates.get(2).add(errorRate);
//			System.out.printf("Logistic Regression: %.4f", errorRate);
//			System.out.println();
//			
//			Classifier svm = (Classifier)new SMO();
//			errorRate = getErrorRate(svm, trainingSet, testingSet);
//			errorRates.get(3).add(errorRate);
//			System.out.printf("Support Vector Machine: %.4f", errorRate);
//			System.out.println();
			
			Classifier randomForest = (Classifier)new RandomForest();
			errorRate = getErrorRate(randomForest, trainingSet, testingSet);
			errorRates.get(4).add(errorRate);
			System.out.printf("Random Forest: %.4f", errorRate);
			System.out.println();
			
			AdaBoostM1 adaboost = new AdaBoostM1();
			adaboost.setClassifier(new J48());
			errorRate = getErrorRate(adaboost, trainingSet, testingSet);
			errorRates.get(5).add(errorRate);
			System.out.printf("AdaBoosting with DT: %.4f", errorRate);
			System.out.println();
			
			LogitBoost logitBoost = new LogitBoost();
			logitBoost.setClassifier(new REPTree());
			errorRate = getErrorRate(logitBoost, trainingSet, testingSet);
			errorRates.get(6).add(errorRate);
			System.out.printf("Logit Boost with Regression Tree: %.4f", errorRate);
			System.out.println();
		}
		
		for (int i = 0; i < 7; i++) {
			List<Double> list = errorRates.get(i);
			for (int j = 0; j < list.size(); j++) {
				System.out.printf("%.4f,", list.get(j));
			}
			System.out.println();
		}
		
	}
	
	private double getErrorRate(Classifier model, Instances trainingSet, Instances testingSet) {
		try {
			model.buildClassifier(trainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		Evaluation eTest = null;
		try {
			eTest = new Evaluation(trainingSet);
			eTest.evaluateModel(model, testingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eTest.errorRate();
	}

	private void getInstances(Instances instances, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			String[] fields = this.processedData.get(i).split(",");
			Instance instance = new DenseInstance(fields.length);
//			System.out.println(fields.length);
			for (int attIndex = 0; attIndex < fields.length; attIndex++) {
				if (attIndex == 3) {
					instance.setValue(this.attributes.get(attIndex), Double.parseDouble(fields[attIndex]));
				} else if (attIndex == 5) {
					instance.setValue(this.attributes.get(attIndex), Integer.parseInt(fields[attIndex]));
				} else {
//					System.out.println("Attribute: " + attIndex + " " + fields[attIndex]);
					instance.setValue(this.attributes.get(attIndex), fields[attIndex]);
				}
			}
			instances.add(instance);
		}
	}
	
	
	public void cluster(double epsilon) throws Exception {
		Collections.shuffle(processedData);
		int total = this.processedData.size();
		int trainingSize = (total*8)/10;
		Instances training = new Instances("Airbnb Users", this.attributes, trainingSize);
		Instances testing = new Instances("Airbnb Users", this.attributes, total-trainingSize);
		getInstances(training, 0, trainingSize);
		getInstances(testing, trainingSize, total);
		
		int k = 1;
		double prevWGSS = 0;
		SimpleKMeans kmeans = null;
		while (true) {
			kmeans = new SimpleKMeans();
			kmeans.setNumClusters(k);
			kmeans.buildClusterer(training);
			
			DistanceFunction df = kmeans.getDistanceFunction();
			Instances centroids = kmeans.getClusterCentroids();
			
			double WGSS = 0;
			for (Instance instance: training) {
				int index = kmeans.clusterInstance(instance);
				WGSS += df.distance(instance, centroids.get(index));
			}
			
			System.out.printf("%.4f, ", WGSS);
			if (prevWGSS != 0 && Math.abs(prevWGSS-WGSS)/prevWGSS < epsilon) {
				break;
			} else {
				k++;
				prevWGSS = WGSS;
			}
		}
		System.out.println();
		System.out.println(k + ": "  + prevWGSS);
		
		Instances centroids = kmeans.getClusterCentroids();
		System.out.println("Total clusters: " + kmeans.getNumClusters());
		
		HashMap<Integer, int[]> cluster2Dest = new HashMap<Integer, int[]>();
		for (int i = 0; i < centroids.size(); i++) {
			cluster2Dest.put(i, new int[this.dest2Index.size()]);
		}
		
		for (Instance instance: training) {
			String dest = instance.stringValue(this.attributes.get(13));
			//System.out.println(dest);
			int cluster = kmeans.clusterInstance(instance);
			int dstIndex = this.dest2Index.get(dest);
			if (!cluster2Dest.containsKey(cluster)) System.out.println(cluster);
			cluster2Dest.get(cluster)[dstIndex]++;
		}
		
		HashMap<Integer, String> cluster2Label = new HashMap<Integer, String>();
		for (int cluster: cluster2Dest.keySet()) {
			int[] nums = cluster2Dest.get(cluster);
			int max = 0;
			String label = "";
			for (int i = 0; i < nums.length; i++) {
				if (nums[i] > max) {
					max = nums[i];
					label = this.index2Dest.get(i);
				}
			}
			
			cluster2Label.put(cluster, label);
		}
		
		double incorrect = 0;
		for (Instance instance: testing) {
			int cluster = kmeans.clusterInstance(instance);
			String label = instance.stringValue(this.attributes.get(13));
			String predictLabel = cluster2Label.get(cluster);
			if (!label.equals(predictLabel)) {
				incorrect++;
			}
		}
		System.out.println("Error rate: " + incorrect/(total-trainingSize));
	}
	
	public static void main(String[] args) throws Exception {
		MultinomialClassifier mc = new MultinomialClassifier();
		mc.init();
		System.out.println(new Date());
		mc.process();
//		double[] params = {0.003, 0.001, 0.0003, 0.0001, 0.00003, 0.00001};
//		for (double epsilon: params) {
//			System.out.println("Current Epsilon: " + epsilon);
//			mc.cluster(epsilon);
//		}		
		System.out.println(new Date());
	}
}
