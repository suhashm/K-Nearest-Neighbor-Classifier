package com.thinkjs.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class KNN {
	public static ArrayList<Integer> trainClassVals;
	public static ArrayList<Hashtable<Integer,Double>> trainAttrVals;
	
	public static ArrayList<Hashtable<Integer,Double>> testAttrVals;
	public static Integer[] categorical = {1,2,3,4,5,6,8,17,20};
	public static Integer[] numeric = {7,9,10,11,12,13,14,15,16,18,19};
	
	public static List<Integer> categoricalList = new ArrayList<Integer>(Arrays.asList(categorical));
	public static List<Integer> numericList = new ArrayList<Integer>(Arrays.asList(numeric));
	
	public static int K_NN = 7;
	
	public static int zeroCount = 0;
	public static int oneCount = 0;
	
	public static ArrayList<Integer> finalClassLabels = new ArrayList<>();
	
	public static void loadTrainData(File file) throws IOException{
		trainClassVals = new ArrayList<>();	
		trainAttrVals = new ArrayList<>();	
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		line = br.readLine();
		String[] attributes = line.split(",");
		System.out.println("number of attributes "+attributes.length);
		while((line = br.readLine()) != null){
			String[] vals = line.split(",");
			Hashtable<Integer,Double> attrVals = new Hashtable<>();
			trainClassVals.add(Integer.parseInt(vals[0]));
			for(int i = 1; i < vals.length; i++){
				attrVals.put(i, Double.parseDouble(vals[i]));
			}
			trainAttrVals.add(attrVals);
		}
		System.out.println("Number of class vals in trainFile is "+trainClassVals.size());
		System.out.println("Number of lines in trainFile is "+trainAttrVals.size());
	}
	
	public static void loadTestData(File testFile) throws IOException{
		testAttrVals = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(testFile));
		String line;
		line = br.readLine();
		String[] testAttributes = line.split(",");
		System.out.println("number of test attributes "+testAttributes.length);
		while((line = br.readLine()) != null){
			String[] vals = line.split(",");
			Hashtable<Integer,Double> attrVals = new Hashtable<>();
			int k = 0;
			for(int i = 0; i < vals.length; i++){
				attrVals.put(++k, Double.parseDouble(vals[i]));
			}
			testAttrVals.add(attrVals);
		}
		System.out.println("Number of lines in testFile is "+testAttrVals.size());
	}
	
	public static ArrayList<Hashtable<Integer,Double>> standardizeNumericValues(ArrayList<Hashtable<Integer,Double>> trainAttrVals1){
		
		//standardize numeric values in train file
		for(int numKey : numericList){
			ArrayList<Double> tempNumVals = new ArrayList<>();
			for(int i = 0; i < trainAttrVals1.size(); i++){
				Hashtable<Integer, Double> temp = new Hashtable<>();
				temp = trainAttrVals1.get(i);
				tempNumVals.add(temp.get(numKey));
			}
			Collections.sort(tempNumVals);
			double minVal = tempNumVals.get(0);
			double maxVal = tempNumVals.get(tempNumVals.size() - 1);
			if(numKey == 18)
				System.out.println("minVal "+minVal+" maxVal "+maxVal);
			for(int i = 0; i < trainAttrVals1.size(); i++){
				Hashtable<Integer, Double> temp = new Hashtable<>();
				temp = trainAttrVals1.get(i);
				double num = temp.get(numKey);
				num = (num - minVal)/((maxVal - minVal) * 1.0);
				temp.put(numKey, num);
				trainAttrVals1.set(i, temp);
			}
		}
		return trainAttrVals1;
	}
	
	public static void calculateDistance(){
		//trainAttrVals, testAttrVals
		for(int i = 0; i < testAttrVals.size(); i++){
			Hashtable<Integer, Double> interimTest = new Hashtable<>();
			interimTest = testAttrVals.get(i);
			ArrayList<PQ> arr = new ArrayList<>();
			for(int j = 0; j < trainAttrVals.size(); j++){
				Hashtable<Integer, Double> interimTrain = new Hashtable<>();
				interimTrain = trainAttrVals.get(j);
				double dist = 0.0;
				double dist2 = 0.0;
				double finalDist = 0.0;
				for(int key: interimTrain.keySet()){
					double trainVal = interimTrain.get(key);
					double testVal = interimTest.get(key);
					if(categoricalList.contains(key)){
						if(trainVal != testVal)
							dist2 += 1.0;
					}else{
						dist += Math.pow(Math.abs(trainVal-testVal), 2);
					}
				}
				finalDist = Math.sqrt(dist) + dist2;
				arr.add(new PQ(j,trainClassVals.get(j),finalDist));
			}
			Collections.sort(arr);
			preditClassLabels(arr);
			System.out.println("Current Iteration is "+i);
		}
	}
	
	public static void preditClassLabels(ArrayList<PQ> indexClassDistances){
		//K_NN;
		int zz = 0;
		Hashtable<Integer,Integer> interimClasses = new Hashtable<>();
		interimClasses.put(0, 0);
		interimClasses.put(1, 0);
		for(PQ ir : indexClassDistances){
			if(interimClasses.get(ir.getClassLabel()) != null){
				int count = interimClasses.get(ir.getClassLabel());
				interimClasses.put(ir.getClassLabel(), count+1);
			}else{
				interimClasses.put(ir.getClassLabel(), 1);
			}
			++zz;
			if(zz == K_NN)
				break;
		}
		int predClass = 1;
		if(interimClasses.get(1) >=2)
			predClass = 1;
		else
			predClass = 0;
		
		finalClassLabels.add(predClass);
		if(predClass == 0)
			zeroCount++;
		else
			oneCount++;
		
	}
	
	public static void writeResultToFile() throws FileNotFoundException, UnsupportedEncodingException{
		//finalClassLabels
		PrintWriter writer = new PrintWriter("result.txt", "UTF-8");
		for(int i = 0; i < finalClassLabels.size(); i++){
			writer.println(finalClassLabels.get(i));
		}
		writer.close();
	}
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		String[] files = {
				"training.csv",
				"test.csv"
				
		};
		File trainFile = new File(files[0]);
		File testFile = new File(files[1]);
		loadTrainData(trainFile);
		loadTestData(testFile);
		
		ArrayList<Hashtable<Integer,Double>> t1 =standardizeNumericValues(trainAttrVals);
		System.out.println(" test attr standardization");
		
		ArrayList<Hashtable<Integer,Double>> t2 =standardizeNumericValues(testAttrVals);
		trainAttrVals = (ArrayList<Hashtable<Integer, Double>>) t1.clone();
		testAttrVals = (ArrayList<Hashtable<Integer, Double>>) t2.clone();
		System.out.println("train size "+trainAttrVals.size()+" test size "+ testAttrVals.size());
		
		calculateDistance();
		writeResultToFile();
		System.out.println("class 0 count "+zeroCount);
		System.out.println("class 1 count "+oneCount);
		 long endTime = System.currentTimeMillis();
	     System.out.println("It took " + (endTime - startTime) + " milliseconds");
	}

}
