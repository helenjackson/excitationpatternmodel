import java.io.File;
import java.io.FilenameFilter;

public class AssociatedFilesMover {

	public static void moveSignals(final ModelParameters p, SimParameters sp) throws Exception
	{	
		String lFromFolder = ""+p.getSimulationFilepath();
		String lToFolder = ""+p.getSimulationFilepath()+"\\raw_stimuli\\"+Util.fmtNum(p.getF0())+"n"+p.getN()+"_"+p.getNComps()+p.getPhase()+"_"+p.getShift()+"f0_"+Util.fmtNum(p.getAmpPert())+"dB_"+Util.fmtNum(p.getSignalLevel())+"SPL";

		
		// move everything that starts with e.g. 200
		File lFromDir = new File(lFromFolder);
		FilenameFilter filter = new FilenameFilter()
		{ 
			public boolean accept(File lFromDir, String name) 
			{ 
				return name.startsWith(""+Util.fmtNum(p.getF0())); 
			} 
		};
		
		FilenameFilter filter2 = new FilenameFilter()
		{ 
			public boolean accept(File lFromDir, String name) 
			{ 
				return name.startsWith("trial"); 
			} 
		};
		
		FilenameFilter filter3 = new FilenameFilter()
		{ 
			public boolean accept(File lFromDir, String name) 
			{ 
				return name.startsWith("template"); 
			} 
		};
		
		
		String [] lFilesToMove = lFromDir.list(filter); 
		for (int li=0; li<lFilesToMove.length; li++)
		{
			//System.out.println("lFilesToMove["+li+"] = "+lFilesToMove[li]+"");
			File lFromFile = new File(""+lFromFolder+"\\"+lFilesToMove[li]);
			
			boolean success = lFromFile.renameTo(new File(""+lToFolder+"\\"+lFilesToMove[li]));
			if (!success) {
				// File was not successfully moved
				System.out.println("Error moving file "+lFromFile);
			}	
		}
		
		String [] lFilesToMove2 = lFromDir.list(filter2); 
		for (int li=0; li<lFilesToMove2.length; li++)
		{
			//System.out.println("lFilesToMove["+li+"] = "+lFilesToMove[li]+"");
			File lFromFile = new File(""+lFromFolder+"\\"+lFilesToMove2[li]);
			
			boolean success = lFromFile.renameTo(new File(""+lToFolder+"\\"+lFilesToMove2[li]));
			if (!success) {
				// File was not successfully moved
				System.out.println("Error moving file "+lFromFile);
			}	
		}
		
		String [] lFilesToMove3 = lFromDir.list(filter3); 
		for (int li=0; li<lFilesToMove3.length; li++)
		{
			//System.out.println("lFilesToMove["+li+"] = "+lFilesToMove[li]+"");
			File lFromFile = new File(""+lFromFolder+"\\"+lFilesToMove3[li]);
			
			boolean success = lFromFile.renameTo(new File(""+lToFolder+"\\"+lFilesToMove3[li]));
			if (!success) {
				// File was not successfully moved
				System.out.println("Error moving file "+lFromFile);
			}	
		}
	}
}
