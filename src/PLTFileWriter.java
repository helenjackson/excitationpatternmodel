import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class PLTFileWriter {

	public static void writePLTFile(ModelParameters p, ArrayList<Float> iFreqs, double[] iPatternA, double[] iPatternB, String iFilename, int iTrialNum, int iIntervalNum) throws IOException
	{
		File lPLTFile = new File(""+p.getSimulationFilepath()+"\\trial"+iTrialNum+"_interval"+iIntervalNum+"_diff.plt");
		FileWriter lFw = new FileWriter(lPLTFile);      
		BufferedWriter lBw = new BufferedWriter(lFw);

		//write plt header info
		String[] pltHeader = new String[29];
		pltHeader[0] = "HEADER = NO";
		pltHeader[1] = "CLIP = YES";
		pltHeader[2] = "XLENGTH =     5.0";
		pltHeader[3] = "YLENGTH =     5.0";
		pltHeader[4] = "TICDIR  =     1.0";
		pltHeader[5] = "ANNSIZ  =     1.0";
		pltHeader[6] = "LABSIZ  =     1.2";
		pltHeader[7] = "XCYCLE  =     10.0";
		
		pltHeader[8] = "XANNOT  =     "+Util.fmtNum((p.getFreqLowest()-(2*p.getF0())))+" "+Util.fmtNum((p.getFreqLowest()-p.getF0()))
			+" "+Util.fmtNum((p.getFreqLowest()+(p.getNComps()*p.getF0())))+" " +
					""+Util.fmtNum((p.getFreqLowest()+(p.getNComps()*p.getF0())+p.getF0()));
		pltHeader[9] = "XMIN    =     "+(p.getFreqLowest()-(2*p.getF0()));
		pltHeader[10] = "XMAX    =    "+(p.getFreqLowest()+(p.getNComps()*p.getF0())+p.getF0());
		
		pltHeader[11] = "XINT    =     9.2";
		pltHeader[12] = "YCYCLE  =     0.0";
		pltHeader[13] = "YMIN    =     "+Double.toString((Util.min(iPatternA))-10);//18.0";
		pltHeader[14] = "YMAX    =     "+Double.toString((Util.max(iPatternA))+10);// 6.2";;
		pltHeader[15] = "YINT    =     3";
		pltHeader[16] = "YHOR    =     1.0";
		pltHeader[17] = "PLTYPE  =     2.0";
		pltHeader[18] = "XLLC    =     2.1";
		pltHeader[19] = "YLLC    =     1.9";
		pltHeader[20] = "XANSKP  =     0.0";
		pltHeader[21] = "YANSKP  =     0.0";
		pltHeader[22] = "XPERCENT=     100.0";
		pltHeader[23] = "YPERCENT=     100.0";
		pltHeader[24] = "ANNLWT  =     1.0";
		pltHeader[25] = "LABLWT  =     1.0";
		pltHeader[26] = "XLABEL  =     FREQUENCY (Hz)";
		pltHeader[27] = "YLABEL  =     EXCITATION LEVEL (dB)";
		pltHeader[28] = "NEWFRAME";

		for (int li = 0;li < pltHeader.length;li++)
		{
			lBw.write(pltHeader[li], 0, pltHeader[li].length());
			lBw.newLine();
		}
		
		lBw.write("DATA");
		lBw.newLine();	
		//write toneA to plt file
		for (int li=0; li<iFreqs.size(); li++)
		{
			//frequency
			lBw.write(Float.toString(iFreqs.get(li)));

			//spacer
			lBw.write("    ");

			//excitation level
			lBw.write(Double.toString(iPatternA[li]));
			lBw.newLine();
		}
		lBw.write("PLOT");
		lBw.newLine();
		lBw.newLine();

		lBw.write("LINTYPE = 2");
		lBw.newLine();
		lBw.newLine();
		
		lBw.write("DATA");
		lBw.newLine();
		//write toneB to plt file
		for (int li=0; li<iFreqs.size(); li++)
		{
			//frequency
			lBw.write(Float.toString(iFreqs.get(li)));

			//spacer
			lBw.write("    ");

			//excitation level
			lBw.write(Double.toString(iPatternB[li]));
			lBw.newLine();
		}
		lBw.write("PLOT");
		lBw.newLine();
		lBw.newLine();
		
		lBw.close();
		lFw.close();
	}
	
	public static void writeTemplateFile(ModelParameters p, ArrayList<Float> iFreqs, double[] iPatternA, String iFilename, int iTrialNum, int iIntervalNum) throws IOException
	{
		File lPLTFile = new File(""+p.getSimulationFilepath()+"\\trial"+iTrialNum+"_interval"+iIntervalNum+"_template.plt");
		FileWriter lFw = new FileWriter(lPLTFile);      
		BufferedWriter lBw = new BufferedWriter(lFw);

		//write plt header info
		String[] pltHeader = new String[29];
		pltHeader[0] = "HEADER = NO";
		pltHeader[1] = "CLIP = YES";
		pltHeader[2] = "XLENGTH =     5.0";
		pltHeader[3] = "YLENGTH =     5.0";
		pltHeader[4] = "TICDIR  =     1.0";
		pltHeader[5] = "ANNSIZ  =     1.0";
		pltHeader[6] = "LABSIZ  =     1.2";
		pltHeader[7] = "XCYCLE  =     10.0";
		
		pltHeader[8] = "XANNOT  =     "+Util.fmtNum((p.getFreqLowest()-(2*p.getF0())))+" "+Util.fmtNum((p.getFreqLowest()-p.getF0()))
			+" "+Util.fmtNum((p.getFreqLowest()+(p.getNComps()*p.getF0())))+" " +
					""+Util.fmtNum((p.getFreqLowest()+(p.getNComps()*p.getF0())+p.getF0()));
		pltHeader[9] = "XMIN    =     "+(p.getFreqLowest()-(2*p.getF0()));
		pltHeader[10] = "XMAX    =    "+(p.getFreqLowest()+(p.getNComps()*p.getF0())+p.getF0());
		
		pltHeader[11] = "XINT    =     9.2";
		pltHeader[12] = "YCYCLE  =     0.0";
		pltHeader[13] = "YMIN    =     -5";//18.0";
		pltHeader[14] = "YMAX    =     5";// 6.2";;
		pltHeader[15] = "YINT    =     3";
		pltHeader[16] = "YHOR    =     1.0";
		pltHeader[17] = "PLTYPE  =     2.0";
		pltHeader[18] = "XLLC    =     2.1";
		pltHeader[19] = "YLLC    =     1.9";
		pltHeader[20] = "XANSKP  =     0.0";
		pltHeader[21] = "YANSKP  =     0.0";
		pltHeader[22] = "XPERCENT=     100.0";
		pltHeader[23] = "YPERCENT=     100.0";
		pltHeader[24] = "ANNLWT  =     1.0";
		pltHeader[25] = "LABLWT  =     1.0";
		pltHeader[26] = "XLABEL  =     FREQUENCY (Hz)";
		pltHeader[27] = "YLABEL  =     EXCITATION LEVEL (dB)";
		pltHeader[28] = "NEWFRAME";

		for (int li = 0;li < pltHeader.length;li++)
		{
			lBw.write(pltHeader[li], 0, pltHeader[li].length());
			lBw.newLine();
		}
		
		lBw.write("DATA");
		lBw.newLine();	
		//write toneA to plt file
		for (int li=0; li<iPatternA.length; li++)
		{
			//frequency
			lBw.write(Float.toString(iFreqs.get(li)));

			//spacer
			lBw.write("    ");

			//excitation level
			lBw.write(Double.toString(iPatternA[li]));
			lBw.newLine();
		}
		lBw.write("PLOT");
		lBw.newLine();
		lBw.newLine();
		
		lBw.close();
		lFw.close();
	}
}
