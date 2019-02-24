import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/*
 * Sample code that reads in a jpg file, filters out the green
 * component, and saves new image 
 */




public class Images {

	public int imageArray[][];
	
    public static void main(String args[]) throws IOException {
        File file = new File("./DSCF1643.JPG");
        BufferedImage imageSource = ImageIO.read(file);
	int rows = imageSource.getHeight();
	int cols = imageSource.getWidth();
	
	System.out.printf("%d by %d pixels\n", rows, cols);

	/* Read into an array of rgb values */
	Color image[][] = new Color[cols][rows];
	for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int color = imageSource.getRGB(i, j);
		int red = (color >> 16) & 0xff;
		int green = (color >> 8) & 0xff;
		int blue = (color) & 0xff;
		image[i][j] = new Color(red, green, blue);
	    }
	}

	/* Save as new image where g values set to 0 */
	BufferedImage imageNew = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
	File fileNew = new File("./leaves_out_java.jpg");
	for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
		int r = image[i][j].getRed();
		int g = 0;
		int b = image[i][j].getBlue();
		int col = (r << 16) | (g << 8) | b;
		imageNew.setRGB(i, j, col);
	    }
	}
	
	
	ImageIO.write(imageNew, "JPEG", fileNew);
    }
    
   
    public int getMin(int[] array) {
    	int theMin = 0;
		for (int i=0; i<=array.length; i++) {
			if(array[i]> theMin) {
				theMin = array[i];
			}
		}
		return theMin;
    }
    
    //column and row of lowest energy pixel
    public void nextMin(int col, int row) {
    	int aboveCol = imageArray[col][row-1];
    	int leftCol = imageArray[col][row-1];
    	int rightCol = imageArray[col][row-1];
    	
    	image[col][rows]
    }
    
}
