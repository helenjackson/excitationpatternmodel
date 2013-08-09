public class SignalGenerator {

	public static void generateSignals(ModelParameters p, SimParameters sp, boolean isTemplate) throws Exception
	{			
		//passed in as parameters as they are changed by the model
		int NumberOfTrials;
		if (isTemplate == true)
		{
			NumberOfTrials = sp.getNumTemplateTrials();
		}
		else
		{
			NumberOfTrials = sp.getNumTrials();
		}

		double FundamentalFrequency = p.getF0();
		int LowestComponentInPassband = p.getFreqLowest();
		int NoOfComponentsWithinPassband = p.getNComps();
		double FrequencyDeviation = p.getStartFreqDev();
		double PerturbationLimit = p.getAmpPert();
		int SignalLevel = (int)p.getSignalLevel();
		int NoiseLevelPer1kERB = (int)p.getTENLevel();


		//these are not passed in as parameters as they don't change (they are for now, just for checking signals)
		int SamplingRate = 48000;
		boolean MaskingInOtherEar = false;
		double sig_dur = p.getStimDur();
		double isi = p.getISI();
		String NoiseType = "TEN_SPL";


		TFS1_class abomination = new TFS1_class();

		if(isTemplate)
		{
			for  (int li=0; li<sp.getNumTemplateTrials(); li++)//
			{
				String filename = ""+(li+1)+"_"+p.getFilenameBase()+".wav"; 
				System.out.println("Generating "+ filename);

				abomination.doSomethingInsane(filename, FundamentalFrequency, LowestComponentInPassband, NoOfComponentsWithinPassband, 
						FrequencyDeviation, PerturbationLimit, SignalLevel, NoiseLevelPer1kERB, SamplingRate, MaskingInOtherEar, sig_dur, isi, NoiseType);
			}
		}
		else
		{
			for  (int li=sp.getNumTemplateTrials(); li<NumberOfTrials+sp.getNumTemplateTrials(); li++)//
			{
				String filename = ""+(li+1)+"_"+p.getFilenameBase()+".wav"; 
				System.out.println("Generating "+ filename);

				abomination.doSomethingInsane(filename, FundamentalFrequency, LowestComponentInPassband, NoOfComponentsWithinPassband, 
						FrequencyDeviation, PerturbationLimit, SignalLevel, NoiseLevelPer1kERB, SamplingRate, MaskingInOtherEar, sig_dur, isi, NoiseType);
			}
		}
	}
}