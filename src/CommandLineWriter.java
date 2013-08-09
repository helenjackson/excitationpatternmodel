import java.io.*;

public class CommandLineWriter {

	public static void runCommandNoIn(String[] commandToRun, String outFile)
	{
		Runtime run = Runtime.getRuntime();
		try {

			final FileOutputStream fos = new FileOutputStream(outFile);
			final Process pp = run.exec(commandToRun);

			Thread outputThread = new Thread(new Runnable() {public void run() {
				try {
					InputStream inputStream = pp.getInputStream();
					IOUtils.copy(inputStream, fos);
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} } );
			outputThread.start();


			new Thread(new Runnable() {public void run() {
				try {
					InputStream errorStream = pp.getErrorStream();
					IOUtils.copy(errorStream, System.err);
					errorStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} } ).start();



			//Check the exit value

			int exitVal = pp.waitFor();

			outputThread.join();
			fos.close();
			pp.getOutputStream().close();
			pp.destroy();

			System.out.println("Process exitValue: " + exitVal);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}	


	public static void runCommand(String[] commandToRun, String inFile, String outFile)
	{
		Runtime run = Runtime.getRuntime();
		try {

			final FileInputStream fis = new FileInputStream(inFile);
			final FileOutputStream fos = new FileOutputStream(outFile);

			final Process pp = run.exec(commandToRun);

			Thread inputThread = new Thread(new Runnable() {public void run() {
				try {
					OutputStream outputStream = pp.getOutputStream();
					IOUtils.copy(fis, outputStream);
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} } );
			inputThread.start();

			Thread outputThread = new Thread(new Runnable() {public void run() {
				try {
					InputStream inputStream = pp.getInputStream();
					IOUtils.copy(inputStream, fos);
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} } );
			outputThread.start();


			new Thread(new Runnable() {public void run() {
				try {
					InputStream errorStream = pp.getErrorStream();
					IOUtils.copy(errorStream, System.err);
					errorStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} } ).start();



			//Check the exit value

			int exitVal = pp.waitFor();

			inputThread.join();
			outputThread.join();
			fis.close();
			fos.close();
			pp.destroy();

			System.out.println("Process exitValue: " + exitVal);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
} 