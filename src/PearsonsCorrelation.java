
public class PearsonsCorrelation {

	public static double getPearsonsCorrelation(double[] scores1,double[] scores2){
		double result = 0;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = scores1[0];
		double mean_y = scores2[0];
		for(int i=2;i<scores1.length+1;i+=1){
			double sweep =Float.valueOf(i-1)/i;
			double delta_x = scores1[i-1]-mean_x;
			double delta_y = scores2[i-1]-mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x/scores1.length);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y/scores1.length);
		double cov_x_y = sum_coproduct / scores1.length;
		result = cov_x_y / (pop_sd_x*pop_sd_y);
		return result;
	}

	/*
	public static void main(String[] args) throws Exception
	{	
		//PearsonsCorrelation lPC = new PearsonsCorrelation();
		
		float [] scores1 = new float[47];
		scores1[0] = 52.9f;
		scores1[1] = 52.9f;
		scores1[2] = 52.8f;
		scores1[3] = 52.8f;
		scores1[4] = 52.9f;
		scores1[5] = 53.1f;
		scores1[6] = 53.5f;
		scores1[7] = 53.9f;
		scores1[8] = 54.3f;
		scores1[9] = 54.6f;
		scores1[10] = 54.6f;
		scores1[11] = 54.3f;
		scores1[12] = 54.0f;
		scores1[13] = 53.6f;
		scores1[14] = 53.4f;
		scores1[15] = 53.2f;
		scores1[16] = 53.3f;
		scores1[17] = 53.4f;
		scores1[18] = 53.5f;
		scores1[19] = 53.5f;
		scores1[20] = 53.4f;
		scores1[21] = 53.4f;
		scores1[22] = 53.6f;
		scores1[23] = 53.9f;
		scores1[24] = 54.3f;
		scores1[25] = 54.7f;
		scores1[26] = 55.0f;
		scores1[27] = 55.0f;
		scores1[28] = 55.0f;
		scores1[29] = 55.0f;
		scores1[30] = 55.1f;
		scores1[31] = 55.3f;
		scores1[32] = 55.6f;
		scores1[33] = 55.9f;
		scores1[34] = 56.0f;
		scores1[35] = 56.0f;
		scores1[36] = 56.0f;
		scores1[37] = 56.0f;
		scores1[38] = 56.2f;
		scores1[39] = 56.4f;
		scores1[40] = 56.6f;
		scores1[41] = 56.6f;
		scores1[42] = 56.4f;
		scores1[43] = 56.3f;
		scores1[44] = 56.2f;
		scores1[45] = 56.2f;
		scores1[46] = 56.2f;
		
		float [] scores2 = new float[47];
		scores2[0] = 	52.2f;
		scores2[1] = 	52.6f;
		scores2[2] = 	53.1f;
		scores2[3] = 	53.6f;
		scores2[4] = 	54.0f;
		scores2[5] = 	54.1f;
		scores2[6] = 	54.0f;
		scores2[7] = 	53.7f;
		scores2[8] = 	53.6f;
		scores2[9] = 	53.5f;
		scores2[10] = 	53.5f;
		scores2[11] = 	53.7f;
		scores2[12] = 	53.9f;
		scores2[13] = 	54.1f;
		scores2[14] = 	54.1f;
		scores2[15] = 	54.0f;
		scores2[16] = 	53.7f;
		scores2[17] = 	53.5f;
		scores2[18] = 	53.4f;
		scores2[19] = 	53.4f;
		scores2[20] = 	53.5f;
		scores2[21] = 	53.7f;
		scores2[22] = 	53.9f;
		scores2[23] = 	53.8f;
		scores2[24] = 	53.7f;
		scores2[25] = 	53.6f;
		scores2[26] = 	53.7f;
		scores2[27] = 	53.9f;
		scores2[28] = 	54.3f;
		scores2[29] = 	54.7f;
		scores2[30] = 	55.0f;
		scores2[31] = 	55.2f;
		scores2[32] = 	55.3f;
		scores2[33] = 	55.6f;
		scores2[34] = 	56.0f;
		scores2[35] = 	56.4f;
		scores2[36] = 	56.8f;
		scores2[37] = 	57.1f;
		scores2[38] = 	57.1f;
		scores2[39] = 	57.0f;
		scores2[40] = 	57.0f;
		scores2[41] = 	57.0f;
		scores2[42] = 	57.1f;
		scores2[43] = 	57.2f;
		scores2[44] = 	57.1f;
		scores2[45] = 	56.9f;
		scores2[46] = 	56.7f;
		
		float lCorr = PearsonsCorrelation.getPearsonCorrelation(scores1, scores2);
		System.out.println("Correlation is "+lCorr);
	}
	*/
}
