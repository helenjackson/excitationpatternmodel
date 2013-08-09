
public class ToneSelecter {
	// works out which four tones need to be used in this interval and returns their filenames in a string[]
	
	final String mFilenameBase;
	
	public ToneSelecter(String iFilenameBase)
	{
		mFilenameBase = iFilenameBase;
	}
	
	public String[] selectTones(int iTrialNum, int iInterval)
	{
		String [] lToneNames = new String[4];
		String lTone1Name, lTone2Name, lTone3Name, lTone4Name;

		if (iInterval == 1) //hihi interval
		{
			int HToneANumber = (iTrialNum*6)-5;
			lTone1Name = ""+mFilenameBase+"_h"+HToneANumber;
			lToneNames[0] = lTone1Name;
			//System.out.println("lTone1Name = "+lTone1Name);

			int IToneANumber = (iTrialNum*2)-1;
			lTone2Name = ""+mFilenameBase+"_i"+IToneANumber;
			lToneNames[1] = lTone2Name;
			//System.out.println("lTone2Name = "+lTone2Name);

			int HToneBNumber = (iTrialNum*6)-4;
			lTone3Name = ""+mFilenameBase+"_h"+HToneBNumber;
			lToneNames[2] = lTone3Name;
			//System.out.println("lTone3Name = "+lTone3Name);

			int IToneBNumber = (iTrialNum*2);
			lTone4Name = ""+mFilenameBase+"_i"+IToneBNumber;
			lToneNames[3] = lTone4Name;
			//System.out.println("lTone4Name = "+lTone4Name);
		}

		else //hhhh interval
		{
			int HToneCNumber = (iTrialNum*6)-3;
			lTone1Name = ""+mFilenameBase+"_h"+HToneCNumber;
			lToneNames[0] = lTone1Name;
			//System.out.println("lTone1Name = "+lTone1Name);

			int HToneDNumber = (iTrialNum*6)-2;
			lTone2Name = ""+mFilenameBase+"_h"+HToneDNumber;
			lToneNames[1] = lTone2Name;
			//System.out.println("lTone2Name = "+lTone2Name);

			int HToneENumber = (iTrialNum*6)-1;
			lTone3Name = ""+mFilenameBase+"_h"+HToneENumber;
			lToneNames[2] = lTone3Name;
			//System.out.println("lTone3Name = "+lTone3Name);

			int HToneFNumber = (iTrialNum*6);
			lTone4Name = ""+mFilenameBase+"_h"+HToneFNumber;
			lToneNames[3] = lTone4Name;
			//System.out.println("lTone4Name = "+lTone4Name);
		}
		return lToneNames;
	}

}
