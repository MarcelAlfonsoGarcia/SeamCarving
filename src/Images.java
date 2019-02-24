import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

/*
 * Sample code that reads in a jpg file, filters out the green
 * component, and saves new image 
 */

public class Images {

	// public int imageArray[][];
	public static int cols;
	public static int rows;
	public static Color image[][];

	public static void main(String args[]) throws IOException {
		File file = new File("./DSCF1643.JPG");
		BufferedImage imageSource = ImageIO.read(file);
		rows = imageSource.getHeight();
		cols = imageSource.getWidth();

		System.out.printf("%d by %d pixels\n", rows, cols);

		/* Read into an array of rgb values */
		image = new Color[cols][rows];
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

	// summed up energies array
	public int[][] sumArray() {

		int array[][] = new int[cols][rows];

		// create array of energies
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				array[i][j] = energy(image, j, i);
			}
		}

		// sum up energies
		for (int j = 1; j < rows; j++) {
			for (int i = 0; j < cols; i++) {

				if (i == 0) {
					array[i][j] = array[i][j] + Math.min(array[i][j - 1], array[i + 1][j - 1]);
				} else if (i == cols - 1) {
					array[i][j] = array[i][j] + Math.min(array[i][j - 1], array[i - 1][j - 1]);
				} else {
					array[i][j] = array[i][j]
							+ Math.min(Math.min(array[i][j - 1], array[i - 1][j - 1]), array[i + 1][j - 1]);
				}
			}
		}

		return array;
	}

	// get minimum value of array
	public int getMin(int[] array) {
		int theMin = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > theMin) {
				theMin = array[i];
			}
		}
		return theMin;
	}

	// column and row of lowest energy pixel
	public void nextMin(Color[][] colorArray, int[][] imageArray, int col, int row) {

		// until you reach the top of the image
		if (row == 0) {

			// imageArray[col][row] = null; delete pixel here
			/// shift array over

		} else {

			// check left and right boundaries
			int aboveCol = imageArray[col][row - 1];
			int leftCol;
			int rightCol;
			if (col != 0) {
				leftCol = imageArray[col - 1][row - 1];
			} else {
				leftCol = imageArray[col][row - 1];
			}
			if (col != imageArray.length - 1) {
				rightCol = imageArray[col + 1][row - 1];
			} else {
				rightCol = imageArray[col][row - 1];
			}

			// aboveCol is the smallest
			if (aboveCol <= leftCol && aboveCol <= rightCol) {
				nextMin(colorArray, imageArray, col, row - 1);
				// imageArray[col][row] = null; delete pixel here
				/// shift array over
			}
			// rightCol is the smallest
			else if (rightCol <= leftCol && rightCol <= aboveCol) {
				nextMin(colorArray, imageArray, col + 1, row - 1);
				// imageArray[col][row] = null; delete pixel here
				//delete from both arrays
				/// shift array over
			}
			// leftCol is the smallest
			else {
				nextMin(colorArray, imageArray, col - 1, row - 1);
				// imageArray[col][row] = null; delete pixel here
				/// shift array over
			}
		}
	}

	private Integer energy(Color[][] arr, int row, int col) {

		// values of RGB across x axis
		int redX = 0;
		int blueX = 0;
		int greenX = 0;

		// values of RGB across y axis
		int redY = 0;
		int blueY = 0;
		int greenY = 0;

		// change of RGB values across the axis
		int changeX = 0;
		int changeY = 0;

		if (row == arr[0].length - 1) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[arr.length - 1][row].getRed() + arr[col + 1][row].getRed());
				greenX = Math.abs(arr[arr.length - 1][row].getGreen() + arr[col + 1][row].getGreen());
				blueX = Math.abs(arr[arr.length - 1][row].getBlue() + arr[col + 1][row].getBlue());

			} else if (col == arr.length - 1) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[col - 1][row].getRed() + arr[0][row].getRed());
				greenX = Math.abs(arr[col - 1][row].getGreen() + arr[0][row].getGreen());
				blueX = Math.abs(arr[col - 1][row].getBlue() + arr[0][row].getBlue());

			}

			// find RGB value across Y axis
			// if we are in last row, the bottom pixel is on same column, top row
			redY = Math.abs(arr[col][row - 1].getRed() + arr[col][0].getRed());
			greenY = Math.abs(arr[col][row - 1].getGreen() + arr[col][0].getGreen());
			blueY = Math.abs(arr[col][row - 1].getBlue() + arr[col][0].getBlue());

		} else if (row == 0) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[arr.length - 1][row].getRed() + arr[col + 1][row].getRed());
				greenX = Math.abs(arr[arr.length - 1][row].getGreen() + arr[col + 1][row].getGreen());
				blueX = Math.abs(arr[arr.length - 1][row].getBlue() + arr[col + 1][row].getBlue());

			} else if (col == arr.length - 1) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[col - 1][row].getRed() + arr[0][row].getRed());
				greenX = Math.abs(arr[col - 1][row].getGreen() + arr[0][row].getGreen());
				blueX = Math.abs(arr[col - 1][row].getBlue() + arr[0][row].getBlue());

			}

			// find RGB value across Y axis
			// if we are in first row, the top pixel is on same column, bottom row
			redY = Math.abs(arr[col][arr[0].length - 1].getRed() + arr[col][row + 1].getRed());
			greenY = Math.abs(arr[col][arr[0].length - 1].getGreen() + arr[col][row + 1].getGreen());
			blueY = Math.abs(arr[col][arr[0].length - 1].getBlue() + arr[col][row + 1].getBlue());

		} else {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[arr.length - 1][row].getRed() + arr[col + 1][row].getRed());
				greenX = Math.abs(arr[arr.length - 1][row].getGreen() + arr[col + 1][row].getGreen());
				blueX = Math.abs(arr[arr.length - 1][row].getBlue() + arr[col + 1][row].getBlue());

			} else if (col == arr.length - 1) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[col - 1][row].getRed() + arr[0][row].getRed());
				greenX = Math.abs(arr[col - 1][row].getGreen() + arr[0][row].getGreen());
				blueX = Math.abs(arr[col - 1][row].getBlue() + arr[0][row].getBlue());

			}

			// find RGB value across Y axis
			redY = Math.abs(arr[col][row - 1].getRed() + arr[col][row + 1].getRed());
			greenY = Math.abs(arr[col][row - 1].getGreen() + arr[col][row + 1].getGreen());
			blueY = Math.abs(arr[col][row - 1].getBlue() + arr[col][row + 1].getBlue());
		}

		// find the change in x
		changeX = redX * redX + greenX * greenX + blueX * blueX;

		// find the change in y
		changeY = redY * redY + greenY * greenY + blueY * blueY;

		// returns energy of input
		return changeX + changeY;
	}

}
