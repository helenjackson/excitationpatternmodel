import java.io.File;
import java.io.IOException;


public class RawWriter_test {

	public static void main(String[] args) throws IOException, Exception{
		for (int i=0;i<10; i++)
		{
			
		String iPathOut = "TestData/h1_75n9_1r_0.1f0_0dB_31SPL.raw"+i;
		File file = new File(iPathOut);
		if (file.exists())
		{
			file.delete();
		}
		
		}
		
		long starttime = System.currentTimeMillis();
		for (int i=0;i<10; i++)
		{
			
		String iPathOut = "TestData/h1_75n9_1r_0.1f0_0dB_31SPL.raw"+i;
		RawWriter.writeSection("TestData/1_75n9_1r_0.1f0_0dB_31SPL.wav", iPathOut, 14401, 9600);
		}
		System.out.println(System.currentTimeMillis() - starttime);
	}
}
