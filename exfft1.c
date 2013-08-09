/* exfft.c    can use without Hamming window if required */

#include <stdio.h>
#include <math.h>
#include <stdlib.h>

#define	PI			3.141592653589793
#define	TWOPI			(2. * PI)
#define	MAX2N			13 /* 13 -> 8192 data points maximum */
#define	MAXFFTPTS		(2 << (MAX2N-1))
#define	HAM_CORR		4.0 /* dB level correction for a Hamming window */
#define	SHORTS			sizeof(short)
/*----------------------------------------------------------*/
/* function realft()  Numerical Recipes in C page 417
* Calculates the fourier Transform of a set of 2N real-valued data points.
* Replaces this data (data()) by the positive frequency half of its complex FT.
* The real valued 1st and last components of the transform are returned as
* elements data(1) & data(2) resp. N must be a power of 2. id isign = -1 will
* calc the inverse transform, results need to be mult by 1/n in this case.
*/

void realft(data, n, isign)
float data[];
int n,isign;
{
int i,i1,i2,i3,i4,n2p3;
float c1=0.5,c2,h1r,h1i,h2r,h2i;
double wr, wi, wpr, wpi, wtemp, theta;
void four1();

	theta = 3.141592653589793/(double)n;
	if(isign == 1){
	  c2 = -0.5;
	  four1(data, n, 1);
	} else {
	  c2 = 0.5;
	  theta = -theta;
	}
	wtemp = sin(0.5 * theta);
	wpr = -2.0 * wtemp * wtemp;
	wpi = sin(theta);
	wr = 1.0 + wpr;
	wi = wpi;
	n2p3 = 2*n+3;
	for( i = 2; i <= n/2 ; i++){
	  i4=1+(i3=n2p3-(i2=1+(i1=i+i-1)));
	  h1r = c1*(data[i1]+data[i3]);
	  h1i = c1*(data[i2]-data[i4]);
	  h2r = -c2*(data[i2]+data[i4]);
	  h2i = c2*(data[i1]-data[i3]);
	  data[i1] =  h1r+wr*h2r-wi*h2i;
	  data[i2] =  h1i+wr*h2i+wi*h2r;
	  data[i3] =  h1r-wr*h2r+wi*h2i;
	  data[i4] = -h1i+wr*h2i+wi*h2r;
	  wr = (wtemp = wr)*wpr-wi*wpi+wr;
	  wi = wi*wpr+wtemp*wpi+wi;
	}
	if(isign == 1){
	  data[1] = (h1r=data[1])+data[2];
	  data[2] = h1r-data[2];
	}else{
	  data[1] = c1*((h1r = data[1])+data[2]);
	  data[2] = c1*(h1r-data[2]);
	  four1(data, n, -1);
	}
}
/*-------------------------------------------------------*/
/* function four1() from Numerical Recipes in C  called by realft() */
#define	SWAP(a,b) tempr = (a);(a) = (b);(b) = tempr
void	four1(data,nn,isign)
float	data[];
int	nn,isign;
{
int	n,mmax,m,j,istep,i;
double	wtemp,wr,wpr,wpi,wi,theta;
float	tempr,tempi;

  n = nn << 1;
  j = 1;
  for (i=1;i<n;i+=2){
    if (j>i){
      SWAP(data[j],data[i]);
      SWAP(data[j+1],data[i+1]);
    }
    m=n >> 1;
    while ((m >= 2) && (j > m)){
      j -= m;
      m >>= 1;
    }
    j += m;
  }
  mmax = 2;
  while (n > mmax){
    istep = 2 * mmax;
    theta = 6.28318530717959/(isign * mmax);
    wtemp = sin (0.5 * theta);
    wpr = -2.0 * wtemp * wtemp;
    wpi = sin (theta);
    wr = 1.0;
    wi = 0.0;
    for (m=1; m<mmax; m+=2){
      for (i=m; i<=n; i+=istep){
	j=i+mmax;
	tempr = (wr * data[j]) - (wi * data[j+1]);
	tempi = (wr * data[j+1]) + (wi * data[j]);
	data[j] = data[i] - tempr;
	data[j+1] = data[i+1] - tempi;
	data[i] += tempr;
	data[i+1] += tempi;
      }
      wr=(wtemp=wr) * wpr -wi*wpi +wr;
      wi=wi*wpr+wtemp*wpi+wi;
    }
    mmax=istep;
  }
}
/*-------------------------------------------------------*/
void info(pname) /* get this by just typing the program name */
char *pname;
{

  printf("in info\n");

  fprintf(stderr," \nUse: %s -i Input_file [-n n -s s -f f -m m -F F -h -p -x]\n\n",pname);
  fprintf(stderr,"Required flag\n");
  fprintf(stderr," -i input file name\n\n");
  fprintf(stderr,"Optional flags: [defaults in square brackets]\n");
  fprintf(stderr," -n number of 16 bit integer points to read [128] max %d\n",MAXFFTPTS);
  fprintf(stderr," -s number of points to skip at start of file [0]\n");
  fprintf(stderr," -f sample frequency of data [44100 Hz]\n");
  fprintf(stderr," -m max dB out for a full scale 16 bit sinusoid [100dB SPL]\n");
  fprintf(stderr," -F fft size. [next power of 2 >= points read]\n");
  fprintf(stderr," -h don't use hamming window\n");
  fprintf(stderr," -p for a spectrum to the screen\n");
  fprintf(stderr," -x for an output that will go into program excit\n");
  fprintf(stderr,"   (a 512 point fft will produce a 256 point spectrum\n");
  fprintf(stderr,"    which is the maximum program excit will accept)\n\n");
  fprintf(stderr," If -x is not used, -p will be assumed\n");

  exit(1);
}

/*---- calculate hamming window and rms of windowed wave -------------*/
void hamming(wave,fftpts,rms) /* It's as well to window data before fft ing it */
float wave[],*rms;
int fftpts;
{
int i;
float temp;
  *rms = 0.0;
  for (i = 0 ; i < fftpts ; i++){
    temp = wave[i] * (0.54 - 0.46 * cos(TWOPI * i/(fftpts-1)));
    wave[i] = temp;
    *rms = *rms + temp * temp;
  }
  *rms = sqrt((double)(*rms/fftpts));
}
/*------------ no hamming window ------------------*/
void no_hamming(wave,fftpts,rms)/* still does rms */
float wave[],*rms;
int fftpts;
{
int i;
float temp;
  *rms = 0.0;
  for (i = 0 ; i < fftpts ; i++){
	temp = wave[i];
    *rms = *rms + temp * temp;
  }
  *rms = sqrt((double)(*rms/fftpts));
}
/*------------- main ---------------------*/
main(argc,argv)
int argc;
char *argv[];
{
int i,fftpts, rfftpts, pts_to_read,skip,power2,offset,options,fftsize;
short buffer[MAXFFTPTS],*iwave_ptr,spec_flag = 0,ex_flag = 0,hamming_flag = 1;
long longi;
float clk,maxamp,total_power,Hz_per_pt,top_freq,dBout,maxdB;
float wave[MAXFFTPTS],*wavep,temp,rms,*pow_ptr,*amp_ptr;
char *input_file;
FILE *fp;
extern void realft();
/* buffer is used to hold 16 bit input and float output (both amplitude
  then power - this all saves a bit of space but needs pointers */
iwave_ptr = (short *)buffer; amp_ptr = pow_ptr = (float *)buffer;
/* defaults */
  pts_to_read = 128;
  skip = 0;
  clk = 44100.0;
  maxdB = 100.0;
  fftsize = 0;
/* Parse command line */
  options = 1;
  for(i=1; i < argc ; i++){
    switch (*argv[i]){
      case '-':
      switch (*(argv[i] + 1)) {
	case 'i': input_file = argv[++i]; options--; break;
	case 'n': pts_to_read = atoi(argv[++i]); break;
	case 's': skip = atoi(argv[++i]); break;
	case 'f': clk = atof(argv[++i]); break;
	case 'm': maxdB = atof(argv[++i]); break;
	case 'F': fftsize = atoi(argv[++i]); break;
	case 'h': hamming_flag = 0;break; /* don't do hamming window */
	case 'p': spec_flag = 1;break;
	case 'x': ex_flag = 1;break;
      }
    }
  }
//printf("argv[0] is %s\n",argv[0]);

  if(options) info(argv[0]);
  if(ex_flag == 0) spec_flag = 1;
		  /* see Numerical Recipes in C page 15 */
  wavep = wave-1; /* points wavep to 1 slot before wave[0] so passing */
		  /* wavep to realft() allows realft to index wave from[1..]*/
  maxamp = 32767.0; /* this could be asked for */
  for(i = 0; i < MAXFFTPTS ; i++) wave[i] = 0.0; /* initialise to zero */

  fprintf(stderr,"data input file : %s\n",input_file);
  if((fp = fopen(input_file,"rb")) == 0) {
    fprintf(stderr,"%s:  Can't open file %s\n",argv[0],input_file);exit(1);
  }

  fprintf(stderr,"Data sampling freq = %8.1f\n",clk);
  if(hamming_flag)fprintf(stderr,"Uses Hamming window\n");
  else fprintf(stderr,"No Hamming window\n");
  if(pts_to_read > MAXFFTPTS)
	{fprintf(stderr,"Max of %d points allowed\n",MAXFFTPTS);exit(1);}
  fprintf(stderr,"Points to read = %d\n",pts_to_read);
  fprintf(stderr,"%d points to skip in data file %s\n",skip,input_file);
  fprintf(stderr,"maxdB = %6.1f\n",maxdB);
  if(fseek(fp,(long)skip,0) != 0) { fprintf(stderr,"fseek error\n"); exit(1); }
	longi = ftell(fp); if(longi != skip) { fprintf(stderr,"ftell error\n"); exit(1); }
  i = fread(iwave_ptr,SHORTS,pts_to_read,fp);
  fprintf(stderr,"%d points (i) read\n",i);
  if(fclose(fp) != 0)  fprintf(stderr,"fclose error\n");
  if(i < pts_to_read) {
fprintf(stderr,"Not enough points left in data file %s\n",input_file);exit(1);
  }
  fftpts = pts_to_read;
  if(fftsize > fftpts) fftpts = fftsize;
  power2 = 32;
  while(fftpts > power2)  power2 *= 2;
  fftpts = power2;
  fprintf(stderr,"fftpts = %d\n",power2);
  offset = (fftpts - pts_to_read)/2;
  rfftpts = fftpts/2;

  for(i = 0; i < fftpts ; i++)  wave[i+offset] = *(iwave_ptr+i)/maxamp;
  if(hamming_flag == 1) hamming(wave,fftpts,&rms);
  if(hamming_flag == 0) no_hamming(wave,fftpts,&rms);
  dBout = 20.0 * log10(rms * sqrt(2.0)) + maxdB;
  realft(wavep,rfftpts,1);

/* unpack fft (see Numerical Recipes) into buffer pointed at by amp_ptr */
  *amp_ptr = fabs(wave[0]);
  maxamp = *amp_ptr;
  *(amp_ptr+rfftpts-1) = fabs(wave[1]);
  if(*(amp_ptr+rfftpts-1) > maxamp) maxamp = *(amp_ptr+rfftpts-1);
  for(i = 2 ; i < fftpts ; i += 2){
    temp = sqrt((double)(wave[i]*wave[i]+wave[i+1]*wave[i+1]));
    *(amp_ptr + i/2) = temp;
    if(temp > maxamp) maxamp = temp;
  }
/* normalise *amp_ptr (just for computational accuracy) and calc total power*/
  total_power = 0.0;
  for(i = 0 ; i < rfftpts ; i++){
    *(pow_ptr + i) = *(amp_ptr+i)* *(amp_ptr+i)/(2.0 * maxamp);
    total_power += *(pow_ptr + i);
  }
	for(i = 0 ; i < rfftpts ; i++){
		*(pow_ptr + i) = 10. * log10(*(pow_ptr + i)/total_power) + dBout;
		if(hamming_flag) *(pow_ptr + i) += HAM_CORR;
	}
  Hz_per_pt = clk/fftpts;
  if(spec_flag) {
    for(i = 0 ; i < rfftpts ; i++)
      printf(" %7.1f,%7.2f\n",i*Hz_per_pt,*(pow_ptr + i));
  }

  if(ex_flag){
    printf("exfft.exp\n");  // output file for excitation pattern numbers
    printf("none\n");       // no plot file
    printf("lin\n");
    printf("50,8000,10\n");   //ADDED LINE FOR RANGE OF FREQUENCIES PRINTED OUT (50,60,70.......7990,8000)
    printf("d\n");          // diffuse field
    printf("t\n");          /* tone */
    printf("h\n");          /* harmonic */
    i = 50.0/Hz_per_pt+1; /* index of lowest harmonic greater than 50.0 Hz */
    top_freq = clk/2. - Hz_per_pt; if(top_freq > 15000.) top_freq = 15000.0;
/* index[0]=dc, [1]=Hz_per_pt etc */
    printf("%.3f,%.3f,%.5f\n",Hz_per_pt*i,top_freq,Hz_per_pt);
    printf("0\n0\n");

    while((i < rfftpts)&&(i*Hz_per_pt < 15000.0 + Hz_per_pt/2.0)){
      printf("%6.1f\n",*(pow_ptr + i));
      i++;
    }
  }/* end of ex_flag */
}
