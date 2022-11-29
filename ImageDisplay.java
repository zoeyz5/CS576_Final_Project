
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.*;



public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 490; // default image width and height
	int height = 270;
	BufferedImage current_img;




	//helper function that gets stores all RGB values for a given image.rgb file in a nested array
	public int[][][] getImageRGB(int width, int height, String imgPath)
	{

		//System.out.println("GOT INSIDE RGB READ FUNCTION");

		int[][][] all_pixel_rgb = new int[height][width][3];

		try
		{
			int frameLength = width*height*3;

			//int[][][] all_pixel_rgb = new int[width][height][3];

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					/*byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];*/


					//not sure why there is multiplcation by 3 now to read RGB values but the provided videos from the Prof dont work with the original offsets commented out above
					byte a = 0;
					byte r = bytes[3*ind];
					byte g = bytes[3*ind+1];
					byte b = bytes[3*ind+2];

					//make sure rgb spans 0 to 255
					int unsigned_r = (int)(r & 0xff);
					int unsigned_g = (int)(g & 0xff);
					int unsigned_b = (int)(b & 0xff);

					//fill in rgb value at this index
					all_pixel_rgb[y][x][0] = unsigned_r;
					all_pixel_rgb[y][x][1] = unsigned_g;
					all_pixel_rgb[y][x][2] = unsigned_b;

					//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					//img.setRGB(x,y,pix);
					ind++;
				}
			}
			return all_pixel_rgb;
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return all_pixel_rgb;
	}


	//helper function that gets stores all YUV values for a given image.rgb file in a nested array
	public double[][][] getImageYUV(int width, int height, String imgPath)
	{

		double[][][] all_pixel_yuv = new double[height][width][3];

		try
		{
			int frameLength = width*height*3;

			//int[][][] all_pixel_rgb = new int[width][height][3];

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					/*byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2];*/


					//not sure why there is multiplcation by 3 now to read RGB values but the provided videos from the Prof dont work with the original offsets commented out above
					byte a = 0;
					byte r = bytes[3*ind];
					byte g = bytes[3*ind+1];
					byte b = bytes[3*ind+2];

					//make sure rgb spans 0 to 255
					int unsigned_r = (int)(r & 0xff);
					int unsigned_g = (int)(g & 0xff);
					int unsigned_b = (int)(b & 0xff);

					//convert extracted RGB values to YUV
					all_pixel_yuv[y][x] = convert_RGB_to_YUV(unsigned_r, unsigned_g, unsigned_b);


					//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					//img.setRGB(x,y,pix);
					ind++;
				}
			}
			return all_pixel_yuv;
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return all_pixel_yuv;
	}


	//helper function to convert RGB values to YUV
	public static double[] convert_RGB_to_YUV(int R_value, int G_value, int B_value){

		//use matrix multiplication with the given conversion matrix to generate the YUV values from RGB
		double Y_value = (R_value * 0.299) + (G_value * 0.587) + (B_value * 0.114);
		double U_value = (R_value * 0.596) + (G_value * -0.274) + (B_value * -0.322);
		double V_value = (R_value * 0.211) + (G_value * -0.523) + (B_value * 0.312);

		double[] YUV_array = {Y_value, U_value, V_value};

		return YUV_array;
	}

	//helper function to convert YUV values back to RGB
	public static int[] convert_YUV_to_RGB(double Y_value, double U_value, double V_value){

		//use the inverse matrix multiplication matrix to convert back to RGB values
		int R_value = (int)Math.round(Y_value + (U_value * 0.956) + (V_value * 0.621));
		int G_value = (int)Math.round(Y_value + (U_value * -0.272) + (V_value * -0.647));
		int B_value = (int)Math.round(Y_value + (U_value * -1.106) + (V_value * 1.703));

		//make sure the reconverted RGB values dont exceed 255, have to clamp it to between 0 and 255
		if(R_value < 0){
			R_value = 0;
		}
		else{
			R_value = (R_value > 255) ? 255 : R_value;
		}

		if(G_value < 0){
			G_value = 0;
		}
		else{
			G_value = (G_value > 255) ? 255 : G_value;
		}

		if(B_value < 0){
			B_value = 0;
		}
		else{
			B_value = (B_value > 255) ? 255 : B_value;
		}



		/*int R_value = (converted_R_value > 255) ? 255 : converted_R_value;
		int G_value = (converted_G_value > 255) ? 255 : converted_G_value;
		int B_value = (converted_B_value > 255) ? 255 : converted_B_value;*/ 

		int[] RGB_array = {R_value, G_value, B_value};

		return RGB_array;
	}


	//helper function to calculate block based MAD for two 16x16 blocks, one from the current frame and one frome the previous/reference frame, assumes YUV double values, block_size should be 16
	public static double block_MAD(double[][][] current_block, double[][][] reference_block, int block_size){
		//current block comes from current frame, reference block is a canidate match for current block from the prev frame

		//calculate total amount of pixels in a block for average computation
		double total_pixels = block_size * block_size;

		double running_total = 0;

		for(int y = 0; y < block_size; y++){
			for(int x = 0; x < block_size; x++){
				//depending on if we use YUV or RGB can delete or add some lines here for each channel: y,u,v or r,g,b
				
				//current approach here is to only use Y luminence value in YUV
				double difference = Math.abs(current_block[y][x][0] - reference_block[y][x][0]);
				running_total += difference;
			}
		}

		double MAD = running_total/total_pixels;

		return MAD;
	}






	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					//byte a = 0;
					//byte r = bytes[ind];
					//byte g = bytes[ind+height*width];
					//byte b = bytes[ind+height*width*2]; 

					
					byte a = 0;
					byte r = bytes[3*ind];
					byte g = bytes[3*ind+1];
					byte b = bytes[3*ind+2]; 


					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void showIms(String[] args){

		//open folder with input rgb files and put them into a list
		String folder_path = "SAL_490_270_437/SAL_490_270_437";
		//String folder_path = args[0];
		File input_folder = new File(folder_path);
		//put all the files in the given folder into a list
		String[] file_list = input_folder.list();


		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		//readImageRGB(width, height, args[0], imgOne);


		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));





		//list to store all buffed images for display
		ArrayList<BufferedImage> all_img = new ArrayList<>();

		//list to store all buffed image RGB values for every pixel
		ArrayList<int[][][]> all_img_rgb = new ArrayList<>();

		//list to store all buffed image YUV values for every pixel
		ArrayList<double[][][]> all_img_yuv = new ArrayList<>();

		//loop through each rgb file in the folder to show it as a video, exact RGB values for each image, store in big list to convert to YUV later
		for(String file : file_list){

			//generate location for every rgb file to pass to readImageRGB()
			String file_location = folder_path + "/" + file;
			System.out.println(file_location);

			//extract RGB values from this image
			int[][][] frame_rgb = getImageRGB(width, height, file_location);
			all_img_rgb.add(frame_rgb);

			//extract image as YUV values
			double[][][] frame_yuv = getImageYUV(width, height, file_location);
			all_img_yuv.add(frame_yuv);


			//read in current image file to the BufferedImage
			current_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			readImageRGB(width, height, file_location, current_img);

			all_img.add(current_img);

		}



		//loop to show all the buffered images to play as a video
		for(int i = 0; i < all_img.size(); i++){

			//replace the previous frame with the newest one
			//lbIm1.setIcon(new ImageIcon(background_img));
			lbIm1.setIcon(new ImageIcon(all_img.get(i)));

			//show the current frame
			frame.setContentPane(lbIm1);
			frame.pack();
			frame.setVisible(true);

			//sleep for a period of time before showing the next frame
			try {
				Thread.sleep((long)41.66);          
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

		}


		//TESTING FUNCTIONS AND STUFF HERE

		//testing storing RGB values from one frame
		String test_filepath = "SAL_490_270_437/SAL_490_270_437/SAL_490_270_437.437.rgb";
		int[][][] test_frame_rgb = getImageRGB(width, height, test_filepath);

		//print out all RGB pixel values for this frame
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				System.out.println(String.valueOf(test_frame_rgb[y][x][0]) + ", " + String.valueOf(test_frame_rgb[y][x][1]) + ", " + String.valueOf(test_frame_rgb[y][x][2]));
			}
		}

	

		//check if all size of all the stored data is right, should be equal to the amount of frames
		System.out.println(all_img.size());
		System.out.println(all_img_rgb.size());



		System.out.println("CHECKING SUBARRAY COPYING OF NESTED ARRAY(GETTING MACROBLOCKS OUT)");

		//use test_frame_rgb to see if we can get the first macroblock out

		int[][][] macroblock = new int[16][16][3];

		for(int i = 0; i < 16; i++){
			macroblock[i] = Arrays.copyOf(test_frame_rgb[i], 16);
		}

		for(int y = 0; y < 16; y++){
			for(int x = 0; x < 16; x++){

				System.out.println("ORIGINAL:");
				System.out.println(String.valueOf(test_frame_rgb[y][x][0]) + ", " + String.valueOf(test_frame_rgb[y][x][1]) + ", " + String.valueOf(test_frame_rgb[y][x][2]));

				System.out.println("MACROBLOCK:");
				System.out.println(String.valueOf(macroblock[y][x][0]) + ", " + String.valueOf(macroblock[y][x][1]) + ", " + String.valueOf(macroblock[y][x][2]));

				System.out.println("----------------------------------------------------------------------------------------------------");
			}
		}
		
		System.out.println("MACROBLOCK DIMENSIONS");
		System.out.println(macroblock.length);
		System.out.println(macroblock[0].length);
		System.out.println(macroblock[0][0].length);

		System.out.println("FULL FRAME DIMENSIONS");
		System.out.println(test_frame_rgb.length);
		System.out.println(test_frame_rgb[0].length);
		System.out.println(test_frame_rgb[0][0].length);


		//MACROBLOCK LOOPING
		
		//yuv values for last frame
		double[][][] test_frame_yuv = getImageYUV(width, height, test_filepath);

		//how to iterate through current frame macroblocks
		for(int y = 0; y < height; y+=16){
			for(int x = 0; x < width; x+=16){
				//macroblock[y] = Arrays.copyOfRange(test_frame_rgb[y], x, x+16);
				//System.out.println("Coordinates-------------------------");
				//System.out.println(String.valueOf(x) + ", " + String.valueOf(y));

				double[][][] curr_macroblock = new double[16][16][3];

				
				//how to handle image dimensions that don't divide nicely into 16x16 blocks at the end? 490x270 does't split into 16x16 completely
				if(x + 16 > width){
					System.out.println("X GOING TO GO OUT OF BOUNDS X = " + String.valueOf(x));
					break;
				}
				else if(y + 16 > height){
					System.out.println("Y GOING TO GO OUT OF BOUNDS Y = " + String.valueOf(y));
					break;
				}

				//loop to fill out macroblock row at current height->current height + 16
				for(int i = 0; i < 16; i++){
					curr_macroblock[i] = Arrays.copyOfRange(test_frame_yuv[y+i], x, x+16);
					//curr_macroblock[i] = Arrays.copyOfRange(test_frame_yuv[x+i], y, y+16 );
				}

				System.out.println("MACROBLOCK DIMENSIONS");
				System.out.println(curr_macroblock.length);
				System.out.println(curr_macroblock[0].length);
				System.out.println(curr_macroblock[0][0].length);
			}

		}

		



		//frame dimensions not perfectly divisible by 16x16 blocks



		//for the current frame, iterate i+16, for reference frame iterate i++ use offset to get the 16x16 matrix from there

		//one motion vector per matching macroblock pair, which coordinate to use for motion vector calculation, probably just the top left point?

		//motion vector = current frame point - prev frame point
		//AKA motion vector d = (dx, dy) = ((current_x - prev_x), (current_y - prev_y))

		//what MAD difference threshold value to consider no motion?

		//given correctly computed motion vectors for all macroblocks, how to identify which blocks are background and foregound using just the motion vectors?
		//have to do some statistical analysis on what motion vector seems to be the most common. 
		//In a non moving camera video, all motion vectors for background would be 0, but if camera is moving have to find motion vectors which are most similar, those blocks with in line with the most similar motion vector will be considered background

	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
