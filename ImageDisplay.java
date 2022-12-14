
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;



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






	//CODE FOR MATCHING MACROBLOCKS AND CALCULATING MOTION VECTORS----------------------------------------------------------------------------------------------------------------------------------------


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




	//function to find the best matching macroblock for the current frame's macroblock in the previous frame, return top left coordinate of found macroblock match in previous frame, 
	//returns array of length 2 that stores the top left coordinate of the found macroblock match in the reference frame
	/*public static int[] find_Matching_Macroblock(double[][][] reference_frame, double[][][] current_frame_macroblock, int search_k_val, int height, int width, int current_x, int current_y){

		//set current min MAD to highest value
		double current_min_MAD = Double.MAX_VALUE;

		//track coordinates for min MAD block
		int current_min_MAD_x = 0;
		int current_min_MAD_y = 0;
		
		//lets try brute force search the whole frame
		//how to iterate through current frame 16x16 macroblocks
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
			
				double[][][] reference_macroblock = new double[16][16][3];

			
				//how to handle image dimensions that don't divide nicely into 16x16 blocks at the end? 490x270 does't split into 16x16 completely
				if(x + 16 > width || y + 16 > height){
					//System.out.println("X GOING TO GO OUT OF BOUNDS X = " + String.valueOf(x));
					break;
				}
				

				//loop to fill out macroblock row at current height->current height + 16
				for(int i = 0; i < 16; i++){
					reference_macroblock[i] = Arrays.copyOfRange(reference_frame[y+i], x, x+16);
				}

				//calculate MAD for the generated reference macroblock and input current macroblock, hardcoded blocksize 16
				double current_MAD = block_MAD(current_frame_macroblock, reference_macroblock, 16);

				//update min MAD seen so far, update the coordinates for this macroblock that produces the min MAD
				if(current_MAD  < current_min_MAD){
					current_min_MAD = current_MAD;

					current_min_MAD_x = x;
					current_min_MAD_y = y;
				}
			
			}

		}

		//System.out.println("MAD FOUND------");
		//System.out.println(current_min_MAD);

		int[] matching_macroblock_coord = {current_min_MAD_x, current_min_MAD_y};

		return matching_macroblock_coord;

		//return current_min_MAD, current_min_MAD_x, current_min_MAD_y

	}*/


	public static int[] find_Matching_Macroblock(double[][][] reference_frame, double[][][] current_frame_macroblock, int search_k_val, int height, int width, int current_x, int current_y){

		//set current min MAD to highest value
		double current_min_MAD = Double.MAX_VALUE;

		//track coordinates for min MAD block
		int current_min_MAD_x = 0;
		int current_min_MAD_y = 0;

		//define search area coordinates using the input k value, if area to check goes out of bounds, clamp it down
		int start_x = (current_x - search_k_val < 0) ? 0 : (current_x - search_k_val);
		int end_x = (current_x + search_k_val > width) ? width : (current_x + search_k_val);

		int start_y = (current_y - search_k_val < 0) ? 0 : (current_y - search_k_val);
		int end_y = (current_y + search_k_val > height) ? height : (current_y + search_k_val);

		
		
		//search neighbors in the area using the coordinates from the current macroblock
		//how to iterate through current frame 16x16 macroblocks
		for(int y = start_y; y < end_y; y++){
			for(int x = start_x; x < end_x; x++){
			
				double[][][] reference_macroblock = new double[16][16][3];
				

				if(x + 16 > width || y + 16 > height){
					break;
				}

				//loop to fill out macroblock row at current height->current height + 16
				for(int i = 0; i < 16; i++){
					reference_macroblock[i] = Arrays.copyOfRange(reference_frame[y+i], x, x+16);
				}

				//calculate MAD for the generated reference macroblock and input current macroblock, hardcoded blocksize 16
				double current_MAD = block_MAD(current_frame_macroblock, reference_macroblock, 16);

				//update min MAD seen so far, update the coordinates for this macroblock that produces the min MAD
				if(current_MAD  < current_min_MAD){
					current_min_MAD = current_MAD;

					current_min_MAD_x = x;
					current_min_MAD_y = y;
				}
			
			}

		}

		//System.out.println("MAD FOUND------");
		//System.out.println(current_min_MAD);

		int[] matching_macroblock_coord = {current_min_MAD_x, current_min_MAD_y};

		return matching_macroblock_coord;

		//return current_min_MAD, current_min_MAD_x, current_min_MAD_y

	}



	//function to calculate all motion vectors given 2 YUV frames
	public static ArrayList<int[]> get_MotionVectors(double[][][] reference_frame, double[][][] current_frame, int height, int width, int search_k_val){

		//store all the motion vectors for every macroblock in the current frame
		ArrayList<int[]> all_motion_vectors = new ArrayList<>();

		//how to iterate through current frame 16x16 macroblocks
		for(int y = 0; y < height; y+=16){
			for(int x = 0; x < width; x+=16){
			
				double[][][] curr_macroblock = new double[16][16][3];

			
				//how to handle image dimensions that don't divide nicely into 16x16 blocks at the end? 490x270 does't split into 16x16 completely 
				if(x + 16 > width || y + 16 > height){
					//System.out.println("X GOING TO GO OUT OF BOUNDS X = " + String.valueOf(x));
					System.out.println("X OR Y GOING TO GO OUT OF BOUNDS");
					break;
				}
				

				//loop to fill out macroblock row at current height->current height + 16
				for(int i = 0; i < 16; i++){
				
					curr_macroblock[i] = Arrays.copyOfRange(current_frame[y+i], x, x+16);
					//curr_macroblock[i] = Arrays.copyOfRange(test_frame_yuv[x+i], y, y+16 );

				}


				//send out the current x and y to use for checking the previous frame x,y location for a matching macroblock with minimal MAD
				//find_Matching_Macroblock(reference_frame, curr_macroblock, search_k_val, height, width, x, y);


				// tuple matching_block_info = find_Matching_Macroblock(reference_frame, curr_macroblock, search_k_val, height, width, x, y);
				int[] matching_block_coord = find_Matching_Macroblock(reference_frame, curr_macroblock, search_k_val, height, width, x, y);

				// curr_motion_vector = calculate_MotionVector(matching_block_info['x'], matching_block_info[y], x, y)
				int[] curr_motion_vector = calculate_MotionVector(matching_block_coord[0], matching_block_coord[1], x, y);

				all_motion_vectors.add(curr_motion_vector);
			
			}

		}

		return all_motion_vectors;

	}




	//function to calculate motion vector given 2 points, returns as a int array of length 2, maybe use tuples instead?
	public static int[] calculate_MotionVector(int reference_x, int reference_y, int current_x, int current_y){
		//array to store the new calculated MotionVector
		int[] motion_vector = new int[2];

		//x movement 
		motion_vector[0] = current_x - reference_x;

		//y movement
		motion_vector[1] = current_y - reference_y;

		return motion_vector;
	}






	//function to determine what the most common motion vector is, if motion vector is within some threshold of the most common, count it as background block, otherwise it is foreground block
	public static List<int[]> most_common_motionvector_Count(ArrayList<int[]> all_motion_vectors){
		System.out.println("FINDING MOST COMMON MOTION VECTOR");

		//hashmap to find the most common motion vector int[], key is the motion vector int[x, y] and value is the # of times it was seen Ex: key = [-12,0], value = 200

		//using the most common motion vector, loop through all motion vectors again, if current motion vector is within some thereshold of the most common for both its x and y value, count it as a background block, try threshold = 3 

		//Map<int[], Integer> motion_vector_map = new HashMap<int[], Integer>();
		Map<List<Integer>, Integer> motion_vector_map = new HashMap<List<Integer>, Integer>();

		//loop through all motion vectors and add it to the HashMap, increment count value if seen before
		for(int i = 0; i < all_motion_vectors.size(); i++){
			//retrieve the current motion vector
			int[] current_key_array = all_motion_vectors.get(i);
			
			List<Integer> current_key = new ArrayList<Integer>();
			current_key.add(current_key_array[0]);
			current_key.add(current_key_array[1]);

			//update motion vector hashmap
			if(motion_vector_map.containsKey(current_key)){
				//retrieve value for this key AKA the count for this given motion vector
				int current_count = motion_vector_map.get(current_key);

				//increment the count and update hashmap
				current_count++;
				motion_vector_map.put(current_key, current_count);
			}
			else{
				motion_vector_map.put(current_key, 1);
			}
		}

		
		//find the most common motion vector key
		int max_count = 0;
		int second_max_count = 0;
		int third_max_count = 0;

		List<Integer> most_common_motion_vector = new ArrayList<Integer>();
		List<Integer> second_most_common_motion_vector = new ArrayList<Integer>();
		List<Integer> third_most_common_motion_vector = new ArrayList<Integer>();

		for(Entry<List<Integer>, Integer> item : motion_vector_map.entrySet()){
			//compare current motion vector cound to the max seen so far, update if current motion vector count is higher
			if(item.getValue() > max_count){
				
				int temp_count = max_count;
				List<Integer> temp_motion_vector = most_common_motion_vector;				 

				max_count = item.getValue();
				most_common_motion_vector = item.getKey();

				if(temp_count > second_max_count){

					int temp_count2 = second_max_count;
					List<Integer> temp_motion_vector2 = second_most_common_motion_vector;

					second_max_count = temp_count;
					second_most_common_motion_vector = temp_motion_vector;

					if(temp_count2 > third_max_count){
						third_max_count = temp_count2;
						third_most_common_motion_vector = temp_motion_vector2;
					}
				}
			}


		}

		//convert back to array since other functions expect array as input
		int [] most_common_motion_vector_array = {most_common_motion_vector.get(0), most_common_motion_vector.get(1), max_count};
		//int [] second_most_common_motion_vector_array = {second_most_common_motion_vector.get(0), second_most_common_motion_vector.get(1), second_max_count};
		//int [] third_most_common_motion_vector_array = {third_most_common_motion_vector.get(0), third_most_common_motion_vector.get(1), third_max_count};


		List<int[]> three_most_common = new ArrayList<int[]>();

		three_most_common.add(most_common_motion_vector_array);
		//three_most_common.add(second_most_common_motion_vector_array);
		//three_most_common.add(third_most_common_motion_vector_array);

		return three_most_common;

		//return most_common_motion_vector_array ;

		//return the found most common motion vector
		//return most_common_motion_vector;

	}



	//function to calculate background and foregound blocks given the most common vector and threshold value
	public static ArrayList<Boolean> group_Blocks(ArrayList<int[]> all_motion_vectors, int[] most_common_motion_vector, int threshold){
		System.out.println("DETERMINING BACKGROUND AND FOREGROUND BLOCKS");

		//arraylist to store booleans that mark whether the current index motion vector is a foregound block or not
		ArrayList<Boolean> is_foreground_block = new ArrayList<>();

		int most_common_x = most_common_motion_vector[0];
		int most_common_y = most_common_motion_vector[1];

		//if current motion vector x and y movement is within some threshold of the most common motion vector, count it as background, otherwise count it as a foreground block
		for(int i = 0; i < all_motion_vectors.size(); i++){
			int [] current_motion_vector = all_motion_vectors.get(i);

			//if either current motion vector differences are more than threshold, mark as foregound block
			if((Math.abs(current_motion_vector[0] - most_common_x) > threshold) || (Math.abs(current_motion_vector[1] - most_common_y) > threshold)){
				is_foreground_block.add(true);
			}
			//otherwise mark it as a background block
			else{
				is_foreground_block.add(false);
			}
		}

		return is_foreground_block;
	}




	//function to extract foreground elements by whiting out background macroblocks, can inverse this process for extracting background 
	//will return int[][][3] of RGB values, must convert from YUB back to RGB for this
	public static int[][][] extract_Foreground(ArrayList<Boolean> is_foreground_block, double[][][] current_frame_yuv, int height, int width){
		System.out.println("GETTING FOREGROUND MACROBLOCKS");

		//set up array for storing the final RGB values for the extracted frame, this means most of it will be whited out since background is most blocks
		int[][][] extracted_rgb = new int[current_frame_yuv.length][current_frame_yuv[0].length][3];
		//to track reference the foreground boolean arraylist, since loop increments in intervals of 16
		int list_iterator = 0;

		//loop through the provided frame YUV values in macroblock intervals
		for(int y = 0; y < height; y+=16){
			for(int x = 0; x < width; x+=16){
			
				//how to handle image dimensions that don't divide nicely into 16x16 blocks at the end? 490x270 does't split into 16x16 completely 
				if(x + 16 > width || y + 16 > height){
					System.out.println("X OR Y GOING TO GO OUT OF BOUNDS");

					//if frame dimensions are not perfectly divisible into 16x16, fill it leftover pixel coordinates as background white pixels
					for(int macro_y = y; macro_y < height; macro_y++){
						for(int macro_x = x; macro_x < width; macro_x++){
							//set this pixel RGB to white
							int[] white_pixel = {255,255,255};

							extracted_rgb[macro_y][macro_x] = white_pixel;
						}
					}

					break;
				}

				//case where this macroblock is a foreground block, convert first to RGB then copy over into final result array
				if(is_foreground_block.get(list_iterator)){
					//loop within this macroblock and copy over
					for(int macro_y = y; macro_y < y+16; macro_y++){
						for(int macro_x = x; macro_x < x+16; macro_x++){
							
							//convert current pixel in macroblock YUV to RGB
							int[] pixel_rgb = convert_YUV_to_RGB(current_frame_yuv[macro_y][macro_x][0], current_frame_yuv[macro_y][macro_x][1], current_frame_yuv[macro_y][macro_x][2]);

							//set the pixel RGB values back to the final image since this is foreground pixel
							extracted_rgb[macro_y][macro_x] = pixel_rgb;
						}
					}
				}
				//case where macroblock is background, set all pixel values in block area to white 
				else{
					//since each macroblock is referenced at top left coordinate, loop till the end of this current macroblock area
					for(int macro_y = y; macro_y < y+16; macro_y++){
						for(int macro_x = x; macro_x < x+16; macro_x++){
							//set this pixel RGB to white
							int[] white_pixel = {255,255,255};

							extracted_rgb[macro_y][macro_x] = white_pixel;
						}
					}
				}
				

				list_iterator++;

			
			}
		}

		return extracted_rgb;
	}



	//THIS FUNCTION IS THE PRIMARY CALL THAT USES ALL THE OTHER HELPER FUNCTIONS

	//main function that calls the others for background and foreground extraction
	public static void extract_frames(ArrayList<double[][][]> all_img_yuv, int height, int width, int frame_difference, int search_k_val, int threshold){

		System.out.println("EXTRACTING FOREGROUND AND BACKGROUND");

		//store all the extracted foreground frame RGB values
		ArrayList<int[][][]> foreground_rgb = new ArrayList<>();

		//loop through all frames and separate foreground and background starting from frame 5
		for(int i = frame_difference; i < all_img_yuv.size(); i++){
			double[][][] current_frame_yuv = all_img_yuv.get(i);
			double[][][] reference_frame_yuv = all_img_yuv.get(i-frame_difference);

			//get all the motion vectors for this frame pair
			ArrayList<int[]> frame_motion_vectors = get_MotionVectors(reference_frame_yuv, current_frame_yuv, height, width, search_k_val);

			//find the 3 most common motion vectors
			List<int[]> three_most_common = most_common_motionvector_Count(frame_motion_vectors);

			//mark each motion vector as denoting a foregound block or not
			ArrayList<Boolean> is_foreground_block = group_Blocks(frame_motion_vectors, three_most_common.get(0), threshold);

			//get the RGB representation of extracted forground
			int[][][] extracted_foreground_rgb = extract_Foreground(is_foreground_block, current_frame_yuv, height, width);

			//add current extracted frame RGB
			foreground_rgb.add(extracted_foreground_rgb);

		}

		JFrame frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		BufferedImage current_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		JLabel lbIm1 = new JLabel(new ImageIcon(current_img));


		//loop to show all the buffered images to play as a video
		for(int i = 0; i < foreground_rgb.size(); i++){


			readImageRGB_Array(width, height, foreground_rgb.get(i), current_img);
			//replace the previous frame with the newest one
			//lbIm1.setIcon(new ImageIcon(background_img));
			lbIm1.setIcon(new ImageIcon(current_img));

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

	//modified version that reads in image using frame RGB pixel array
	public static void readImageRGB_Array(int width, int height, int[][][] frame_rgb, BufferedImage img)
	{
		
		//int ind = 0;
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				int pix = 0xff000000 | (frame_rgb[y][x][0] << 16) | (frame_rgb[y][x][1] << 8) | (frame_rgb[y][x][2]);

				img.setRGB(x,y,pix);
				//ind++;
			}
		}
			
	}




	//MAIN FUNCTION
	public void showIms(String[] args){

		//open folder with input rgb files and put them into a list
		String folder_path = "SAL_490_270_437/SAL_490_270_437";
		//String folder_path = "Stairs_490_270_346/Stairs_490_270_346";
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





		//main call, passes in all video frame YUV values, frame difference is difference in frames between current frame and reference frame
		extract_frames(all_img_yuv, height, width, 5, 16, 8); 





		/* 

		//TESTING FUNCTIONS AND STUFF HERE

		//testing storing RGB values from one frame
		//String test_filepath = "SAL_490_270_437/SAL_490_270_437/SAL_490_270_437.200.rgb";
		String test_filepath = "Stairs_490_270_346/Stairs_490_270_346/Stairs_490_270_346.200.rgb";
		int[][][] test_frame_rgb = getImageRGB(width, height, test_filepath);
		
		//yuv values for last frame
		double[][][] test_frame_yuv = getImageYUV(width, height, test_filepath);

		//yuv values for second to last frame, the test reference frame
		//String test_filepath_reference = "SAL_490_270_437/SAL_490_270_437/SAL_490_270_437.436.rgb";
		//String test_filepath_reference = "SAL_490_270_437/SAL_490_270_437/SAL_490_270_437.194.rgb";
		String test_filepath_reference = "Stairs_490_270_346/Stairs_490_270_346/Stairs_490_270_346.195.rgb";
		double[][][] test_frame_yuv_reference = getImageYUV(width, height, test_filepath_reference);



		 
		//get motion vectors for 2 test frames
		ArrayList<int[]> test_motion_vectors = get_MotionVectors(test_frame_yuv_reference, test_frame_yuv, height, width, 16);

		//get the most common motion vector for the 2 test frames
		//int[] most_common_motion_vector = most_common_motionvector_Count(test_motion_vectors);
		//System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(most_common_motion_vector[0]) + ", y = " + String.valueOf(most_common_motion_vector[1]) + ")");

		List<int[]> three_most_common = most_common_motionvector_Count(test_motion_vectors);
		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(0)[0]) + ", y = " + String.valueOf(three_most_common.get(0)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(0)[2]));
		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(1)[0]) + ", y = " + String.valueOf(three_most_common.get(1)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(1)[2]));
		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(2)[0]) + ", y = " + String.valueOf(three_most_common.get(2)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(2)[2]));





		//mark each motion vector as denoting a foregound block or not
		ArrayList<Boolean> test_is_foreground_block = group_Blocks(test_motion_vectors, three_most_common.get(0), 8);

		
		int foreground_count = 0;
		int background_count = 0;

		//print out all the motion vectors and if they are foregound blocks or not
		for(int i = 0; i < test_motion_vectors.size(); i++){
			System.out.println("Motion Vector " + String.valueOf(i) + "----------------------------------------------------");

			//print out the motion vector for the ith macroblock
			System.out.println("(x = " + String.valueOf(test_motion_vectors.get(i)[0]) + ", y = " + String.valueOf(test_motion_vectors.get(i)[1]) + ")");

			//if current motion vector denotes foreground block
			if(test_is_foreground_block.get(i)){
				System.out.println("FOREGROUND");
				foreground_count++;
			}
			else{
				System.out.println("BACKGROUND");
				background_count++;
			}
		}

		System.out.println("----------------------------------------------------------------------------");
		System.out.println("BACKGROUND COUNT = " + String.valueOf(background_count));
		System.out.println("FOREGROUND COUNT = " + String.valueOf(foreground_count)); 



		//get the extracted foreground image from the test current frame(not the reference frame since we extract foreground/background for current frame) 
		int[][][] extracted_foreground_rgb = extract_Foreground(test_is_foreground_block, test_frame_yuv, height, width);
		


		//test out reading image from pixel RGB array
		BufferedImage test_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB_Array(width, height, extracted_foreground_rgb, test_img);

		//readImageRGB_Array(width, height, test_frame_rgb, test_img);


		lbIm1.setIcon(new ImageIcon(test_img));

		//show the current test frame
		frame.setContentPane(lbIm1);
		frame.pack();
		frame.setVisible(true);

		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(0)[0]) + ", y = " + String.valueOf(three_most_common.get(0)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(0)[2]));
		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(1)[0]) + ", y = " + String.valueOf(three_most_common.get(1)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(1)[2]));
		System.out.println("MOST COMMON MOTION VECTOR: (x = " + String.valueOf(three_most_common.get(2)[0]) + ", y = " + String.valueOf(three_most_common.get(2)[1]) + ") WITH COUNT = " + String.valueOf(three_most_common.get(2)[2]));
		
		
		*/



		//maybe try to use the 3 most common motion vectors instead of just the most common

		//also maybe use more than just Y in the MAD calculations with YUV?

		//maybe set a limit of how high the difference can be to count as foreground, this is to account for new blocks popping into frame that didnt exist in previous frames

		//using the previous frame as reference might be too close, since all the motion vectors will be super small like (-1, 0), which makes it really hard to distinguish background and foreground using motion vector difference thresholds

		


		//camera moving right should have a lot of negative x values in the motion vector, because macroblock in current frame is further left compared to the previous frame  

		



		



		//frame dimensions not perfectly divisible by 16x16 blocks

		//maybe for motion vector similarity, calculate the running average/median motion vector for all the found macroblock pairs for 2 frames. If a motion vector is too far off from the average/median motion vector, count it as a foregound block

		//given average motion vector(u,v) if current motion vector (x,y). if |u-x| and/or |v-y| is too big, count it as a foregound block



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
