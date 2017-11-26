package bajtahack.speech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bajtahack.common.Global;
import de.daslaboratorium.machinelearning.classifier.Classifier;

public class BayesClassifier {
	//private static Classifier<String, String> bayesClassifier;	
	
	public BayesClassifier(){
		//this.bayesClassifier = Global.bayesClassifier;
	}
	
	public static void Train(){
		//train
        List<String[]> trainSet = new ArrayList<String[]>(); 

        /*trainSet.add(new String[] { "130", "floor one light three off key"});
        trainSet.add(new String[] { "120", "floor one light to off key"});
        trainSet.add(new String[] { "131", "floor 1 Flight 3 on key"});
        trainSet.add(new String[] { "230", "for 2/3 of key"});
        trainSet.add(new String[] { "110", "Florida law on light one-off key"});
        trainSet.add(new String[] { "121", "for one light to on key"});
        trainSet.add(new String[] { "220", "42 Lac Du off key"});
        trainSet.add(new String[] { "231", "42 light 3 on key"});*/
        /*trainSet.add(new String[] { "210", "for two lights one of key"});
        trainSet.add(new String[] { "221", "floor to lie to on key"});
        trainSet.add(new String[] { "211", "42 black one on key"});*/
        
        trainSet.add(new String[] {"110", "for one plus one"});
        trainSet.add(new String[] {"110", "41 + 1"});
        trainSet.add(new String[] {"110", "book one plus one"});
        trainSet.add(new String[] {"110", "short one like one up"});
        trainSet.add(new String[] {"110", "for one night one off"});
        
        trainSet.add(new String[] {"111", "Florida law on light one on"});
        trainSet.add(new String[] {"111", "Florida water light one on"});
        trainSet.add(new String[] {"111", "for one plus one"});
        trainSet.add(new String[] {"111", "for one light one on"});
        trainSet.add(new String[] {"111", "so the OnePlus One on"});
        
        trainSet.add(new String[] {"121", "4102"});
        trainSet.add(new String[] {"121", "go to one room to"});
        trainSet.add(new String[] {"121", "call 812"});
        trainSet.add(new String[] {"121", "Florida one room to park"});
        trainSet.add(new String[] {"121", "call 212"});
        	
        trainSet.add(new String[] {"120", "41 Room 2"});
        trainSet.add(new String[] {"120", "one room to up"});
        trainSet.add(new String[] {"120", "add one room to up"});
        trainSet.add(new String[] {"120", "Sword walkthrough"});
        trainSet.add(new String[] {"120", "Florida one room 2"});
        
        trainSet.add(new String[] {"130", "go to 103"});
        trainSet.add(new String[] {"130", "go to 1 2 3"});
        trainSet.add(new String[] {"130", "floor one room suite of"});
        trainSet.add(new String[] {"130", "413"});
        trainSet.add(new String[] {"130", "for one room free app"});
        
        trainSet.add(new String[] {"131", "41 Broome Street"});
        trainSet.add(new String[] {"131", "call 213"});
        trainSet.add(new String[] {"131", "413"});
        trainSet.add(new String[] {"131", "413"});
        trainSet.add(new String[] {"131", "go to one room 3 on"});
        
        trainSet.add(new String[] {"000", "all lights off"});
        trainSet.add(new String[] {"000", "lights off"});
        
        trainSet.add(new String[] {"222", "all lights on"});
        trainSet.add(new String[] { "000", "all lights off key"});
        trainSet.add(new String[] { "222", "all lights on key"});

        
        /*trainSet.add(new String[] {"211", "Florence to Rome one on"});
        trainSet.add(new String[] {"211", "floor to room one on"});
        trainSet.add(new String[] {"211", "Rogue one on"});
        trainSet.add(new String[] {"211", "floss to room one on"});
        trainSet.add(new String[] {"211", "42411"});
        
        trainSet.add(new String[] {"210", "fox21"});
        trainSet.add(new String[] {"210", "talk to someone"});
        trainSet.add(new String[] {"210", "fox21 off"});
        trainSet.add(new String[] {"210", "floor to room one up"});
        trainSet.add(new String[] {"210", "Saw 2 room one-off"});
        
        trainSet.add(new String[] {"220", "floor to Room 2"});
        trainSet.add(new String[] {"220", "room to go"});
        trainSet.add(new String[] {"220", "Room 2"});
        trainSet.add(new String[] {"220", "Saw 2 Room 2 off"});
        trainSet.add(new String[] {"220", "go to room to off"});
        
        trainSet.add(new String[] {"221", "42 Room 2"});
        trainSet.add(new String[] {"221", "Room 2 on"});
        trainSet.add(new String[] {"221", "floor to Room 2"});
        trainSet.add(new String[] {"221", "room 201"});
        
        trainSet.add(new String[] {"230", "call 203"});
        trainSet.add(new String[] {"230", "423"});
        trainSet.add(new String[] {"230", "Fox23 off"});
        trainSet.add(new String[] {"230", "room 3 off"});
        
        trainSet.add(new String[] {"231", "room 3 on"});
        trainSet.add(new String[] {"231", "book to room 3 on"});
        trainSet.add(new String[] {"231", "423 van"});
        trainSet.add(new String[] {"231", "423"});
        trainSet.add(new String[] {"231", "room 3 on"});*/
	
        Global.bayesClassifier.setMemoryCapacity(500);
        
		for (String[] sentence : trainSet){
			Global.bayesClassifier.learn(sentence[0], Arrays.asList(sentence[1].split("\\s")));
		}
		
		
	}
	
	public static String Classify(String[] sentence){
		//List<String> sentenceAsList =  Arrays.asList(sentence[1].split("\\s"));
		List<String> sentenceAsList = Arrays.asList(sentence);
		String result = "";
		
		result = Global.bayesClassifier.classify(sentenceAsList).getCategory();
		return result;
	}
	
}
