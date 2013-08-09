public class RawWriterOrganiser {

	public static void convertToRaw(ModelParameters iP, SimParameters sp, boolean isTemplate) throws Exception
	{
		//template parameters
		int numTrials;
		int startOffset = 0;

		if (isTemplate){
			numTrials = sp.getNumTemplateTrials();
		}
		else{
			numTrials = sp.getNumTrials()+sp.getNumTemplateTrials();
			startOffset = sp.getNumTemplateTrials();
		}

		// work out what input (wav) and output (raw) paths to give RawWriter
		for (int filecount = 1+startOffset; filecount <= numTrials; filecount++)
		{
			//for each .wav file
			String lInFileName = ""+filecount+"_"+iP.getFilenameBase()+".wav";

			
			//split it into 8 pieces		
			//piece 1
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+((filecount*6)-5)+".raw", 14401, 9600);

			//piece 2
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_i"+((filecount*2)-1)+".raw", 28801, 9600);

			//piece 3
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+((filecount*6)-4)+".raw", 43201, 9600);

			//piece 4
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_i"+(filecount*2)+".raw", 57601, 9600);

			//piece 5
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+((filecount*6)-3)+".raw", 81601, 9600);

			//piece 6
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+((filecount*6)-2)+".raw", 96001, 9600);

			//piece 7
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+((filecount*6)-1)+".raw", 110401, 9600);

			//piece 8
			RawWriter.writeSection(lInFileName, iP.getFilenameBase()+"_h"+(filecount*6)+".raw", 124801, 9600);

		}
	}
}
