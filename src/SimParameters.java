
public class SimParameters {
	
	boolean isSharp;
	Float internalNoise;
	int firstTrial;
	int numTrials;
	int numTemplateTrials = 2; //FIXME
	ModelParameters modelParameters;

	public SimParameters(boolean iIsSharp, Float iInternalNoise, int iFirstTrial, int iNumTrials, ModelParameters iModelParameters)
	{
		isSharp = iIsSharp;
		internalNoise = iInternalNoise;
		firstTrial = iFirstTrial;
		numTrials = iNumTrials;
		modelParameters = iModelParameters;
	}
	
	public int getNumTemplateTrials()
	{
		return numTemplateTrials;
	}
	
	public boolean getIsSharp(){
		return isSharp;
	}
	
	public Float getInternalNoise(){
		return internalNoise;
	}
	
	public int getFirstTrial() {
		return firstTrial;
	}

	public int getNumTrials() {
		return numTrials;
	}

	public ModelParameters getModelParameters() {
		return modelParameters;
	}
}
