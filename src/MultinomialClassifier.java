import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MultinomialClassifier {
	private List<String> processedData;
	private ArrayList<Attribute> attributes;
	
	public MultinomialClassifier() {
		this.processedData = new ArrayList<String>();
		this.attributes = new ArrayList<Attribute>();		
	}
	
	public void init() {
		Preprocess p = new Preprocess();
		for (String example: p.processedData) {
			this.processedData.add(example);
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
		
		int[] sizeArray = {1000, 2000, 5000, 8000, 10000, 20000, 30000};
		
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
//			System.out.println("Complete Naive Bayes");
//			
//			Classifier j48 = (Classifier)new J48();
//			errorRate = getErrorRate(j48, trainingSet, testingSet);
//			errorRates.get(1).add(errorRate);
//			System.out.println("Complete Decision Tree");
			
			Classifier logistic = (Classifier)new Logistic();
			errorRate = getErrorRate(logistic, trainingSet, testingSet);
			errorRates.get(2).add(errorRate);
			System.out.println("Complete Logistic Regression");
			
			Classifier smo = (Classifier)new SMO();
			errorRate = getErrorRate(smo, trainingSet, testingSet);
			errorRates.get(3).add(errorRate);
			System.out.println("Complete Support Vector Machine");
			
//			Classifier randomForest = (Classifier)new RandomForest();
//			errorRate = getErrorRate(randomForest, trainingSet, testingSet);
//			errorRates.get(4).add(errorRate);
//			System.out.println("Complete Random Forest");
//			
//			AdaBoostM1 adaboost = new AdaBoostM1();
//			adaboost.setClassifier(new NaiveBayes());
//			errorRate = getErrorRate1(adaboost, trainingSet, testingSet);
//			errorRates.get(5).add(errorRate);
//			System.out.println("Complete AdaBoosting with NB");
//			
//			AdaBoostM1 adaboost2 = new AdaBoostM1();
//			adaboost2.setClassifier(new J48());
//			errorRate = getErrorRate1(adaboost2, trainingSet, testingSet);
//			errorRates.get(6).add(errorRate);
//			System.out.println("Complete AdaBoosting with DT");
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
	
	private double getErrorRate1(AdaBoostM1 model, Instances trainingSet, Instances testingSet) {
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
	
	public static void main(String[] args) {
		MultinomialClassifier mc = new MultinomialClassifier();
		mc.init();
		System.out.println(new Date());
		mc.process();
		System.out.println(new Date());
	}
}
