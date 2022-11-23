
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

		int[][][] all_pixel_rgb = new int[width][height][3];

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
					all_pixel_rgb[x][y][0] = unsigned_r;
					all_pixel_rgb[x][y][1] = unsigned_g;
					all_pixel_rgb[x][y][2] = unsigned_b;

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


	//helper function to convert RGB values to YUV
	public static double[] convert_RGB_to_YUV(int R_value, int G_value, int B_value){

		//use matrix multiplication with the given conversion matrix to generate the YUV values from RGB
		double Y_value = (R_value * 0.299) + (G_value * 0.587) + (B_value * 0.114);
		double U_value = (R_value * 0.596) + (G_value * -0.274) + (B_value * -0.322);
		double V_value = (R_value * 0.211) + (G_value * -0.523) + (B_value * 0.312);

		double[] YUV_array = {Y_value, U_value, V_value};

		return YUV_array;
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
				
				double difference = Math.abs(current_block[x][y][0] - reference_block[x][y][0]);
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

		//loop through each rgb file in the folder to show it as a video, exact RGB values for each image, store in big list to convert to YUV later
		for(String file : file_list){

			//generate location for every rgb file to pass to readImageRGB()
			String file_location = folder_path + "/" + file;
			System.out.println(file_location);

			//extract RGB values from this image
			int[][][] frame_rgb = getImageRGB(width, height, file_location);
			all_img_rgb.add(frame_rgb);


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




		//testing storing RGB values from one frame
		String test_filepath = "SAL_490_270_437/SAL_490_270_437/SAL_490_270_437.437.rgb";
		int[][][] test_frame_rgb = getImageRGB(width, height, test_filepath);

		//print out all RGB pixel values for this frame
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				System.out.println(String.valueOf(test_frame_rgb[x][y][0]) + ", " + String.valueOf(test_frame_rgb[x][y][1]) + ", " + String.valueOf(test_frame_rgb[x][y][2]));
			}
		}

		//check if all size of all the stored data is right, should be equal to the amount of frames
		System.out.println(all_img.size());
		System.out.println(all_img_rgb.size());


		//for the current frame, iterate i+16, for reference frame iterate i++ use offset to get the 16x16 matrix from there

		//one motion vector per matching macroblock pair, which coordinate to use for motion vector calculation, probably just the top left point?

		//motion vector = current frame point - prev frame point
		//AKA motion vector d = (dx, dy) = ((current_x - prev_x), (current_y - prev_y))

		//what MAD difference threshold value to consider no motion?

	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
