package bajtahack.common;

import de.daslaboratorium.machinelearning.classifier.Classifier;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;

public class Global {
	public static Classifier<String, String> bayesClassifier = new BayesClassifier<String, String>();
}
