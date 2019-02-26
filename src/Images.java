import java.io.*;
import java.util.ArrayList;
import structure5.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.List;

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
		image = new Color[rows][cols];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				int color = imageSource.getRGB(i, j);
				int red = (color >> 16) & 0xff;
				int green = (color >> 8) & 0xff;
				int blue = (color) & 0xff;
				image[j][i] = new Color(red, green, blue);
			}
		}

		Pair[][] array = sumArray();
		carve(array, array.length - 1, getMin(array[array.length - 1]));

		Color[][] newImage = new Color[rows][cols - 1];
		boolean skip = false;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				// if we already skipped an element in the array, we
				if (skip) {

					if (j < cols - 1) {
						newImage[i][j] = image[i][j + 1];
					}

				} else {
					if (array[i][j] == null) {

						if(j < cols - 1) {
							newImage[i][j] = image[i][j + 1];
							skip = true;
						}
					} else {
						newImage[i][j] = image[i][j];
					}
				}
			}

			skip = false;
		}

		/* Save as new image where g values set to 0 */
		BufferedImage imageNew = new BufferedImage(rows, cols - 1, BufferedImage.TYPE_INT_RGB);
		File fileNew = new File("./leaves_out_javas.jpg");
		for (int i = 0; i < cols - 1; i++) {
			for (int j = 0; j < rows; j++) {
				int r = newImage[j][i].getRed();
				int g = 0;
				int b = newImage[j][i].getBlue();
				int col = (r << 16) | (g << 8) | b;
				imageNew.setRGB(j, i, col);
			}
		}

		ImageIO.write(imageNew, "JPEG", fileNew);
	}

	// column and row of lowest energy pixel
	public static void carve(Pair[][] imageArray, int row, int col) {

		if (row == 0) {
			imageArray[row][col] = null;
		} else {
			carve(imageArray, row - 1, imageArray[row][col].getNext());
			imageArray[row][col] = null;
		}
	}

	// summed up energies array
	public static Pair[][] sumArray() {

		Pair array[][] = new Pair[rows][cols];

		// create array of energies
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				array[i][j] = new Pair(energy(image, i, j), 0);
			}
		}

		// sum up energies
		for (int j = 1; j < rows; j++) {
			for (int i = 0; i < cols; i++) {

				// if you're at the leftmost column, you only consider the values directly above
				// and to the top right
				if (i == 0) {

					// find the minimum energy and set the initial position of the smallest value
					// to the same column
					int minEnergy = Math.min(array[j - 1][i].getEnergy(), array[j - 1][i + 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == array[j - 1][i].getEnergy()) {
						pos = i;
					} else {
						pos = i + 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					array[j][i] = new Pair(array[j][i].getEnergy() + minEnergy, pos);

					// if you're at the rightmost column, you only consider the values directly
					// above and to the top left
				} else if (i == cols - 1) {

					// find the minimum energy and set the initial position of the smallest value
					// to the same column
					int minEnergy = Math.min(array[j - 1][i].getEnergy(), array[j - 1][i - 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == array[j - 1][i].getEnergy()) {
						pos = i;
					} else {
						pos = i - 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					array[j][i] = new Pair(array[j][i].getEnergy() + minEnergy, pos);

				} else {

					// find the minimum energy and set the initial position of the smallest value
					// to the same column
					int minEnergy = Math.min(Math.min(array[j - 1][i].getEnergy(), array[j - 1][i - 1].getEnergy()),
							array[j - 1][i + 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == array[j - 1][i].getEnergy()) {
						pos = i;
					} else if (minEnergy == array[j - 1][i - 1].getEnergy()) {
						pos = i - 1;
					} else {
						pos = i + 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					array[j][i] = new Pair(array[j][i].getEnergy() + minEnergy, pos);
				}
			}
		}

		return array;
	}

	// get minimum value of array
	public static int getMin(Pair[] array) {
		int theMin = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i].getEnergy() > theMin) {
				theMin = array[i].getEnergy();
			}
		}
		return theMin;
	}

	// find the energy of a pixel
	private static int energy(Color[][] arr, int row, int col) {

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

		if (row == arr.length - 1) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() + arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() + arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() + arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() + arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() + arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() + arr[row][0].getBlue());

			}

			// find RGB value across Y axis
			// if we are in last row, the bottom pixel is on same column, top row
			redY = Math.abs(arr[row - 1][col].getRed() + arr[0][col].getRed());
			greenY = Math.abs(arr[row - 1][col].getGreen() + arr[0][col].getGreen());
			blueY = Math.abs(arr[row - 1][col].getBlue() + arr[0][col].getBlue());

		} else if (row == 0) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() + arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() + arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() + arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() + arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() + arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() + arr[row][0].getBlue());

			}

			// find RGB value across Y axis
			// if we are in first row, the top pixel is on same column, bottom row
			redY = Math.abs(arr[arr.length - 1][col].getRed() + arr[row + 1][col].getRed());
			greenY = Math.abs(arr[arr.length - 1][col].getGreen() + arr[row + 1][col].getGreen());
			blueY = Math.abs(arr[arr.length - 1][col].getBlue() + arr[row + 1][col].getBlue());

		} else {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() + arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() + arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() + arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() + arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() + arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() + arr[row][0].getBlue());

			}

			// find RGB value across Y axis
			redY = Math.abs(arr[row - 1][col].getRed() + arr[row + 1][col].getRed());
			greenY = Math.abs(arr[row - 1][col].getGreen() + arr[row + 1][col].getGreen());
			blueY = Math.abs(arr[row - 1][col].getBlue() + arr[row + 1][col].getBlue());
		}

		// find the change in x
		changeX = redX * redX + greenX * greenX + blueX * blueX;

		// find the change in y
		changeY = redY * redY + greenY * greenY + blueY * blueY;

		// returns energy of input
		return changeX + changeY;
	}

//	// column and row of lowest energy pixel
//		public void nextMin(Color[][] colorArray, int[][] imageArray, int col, int row) {
//
//			// until you reach the top of the image
//			if (row == 0) {
//
//				// imageArray[col][row] = null; delete pixel here
//				/// shift array over
//
//			} else {
//
//				// check left and right boundaries
//				int aboveCol = imageArray[col][row - 1];
//				int leftCol;
//				int rightCol;
//				if (col != 0) {
//					leftCol = imageArray[col - 1][row - 1];
//				} else {
//					leftCol = imageArray[col][row - 1];
//				}
//				if (col != imageArray.length - 1) {
//					rightCol = imageArray[col + 1][row - 1];
//				} else {
//					rightCol = imageArray[col][row - 1];
//				}
//
//				// aboveCol is the smallest
//				if (aboveCol <= leftCol && aboveCol <= rightCol) {
//					nextMin(colorArray, imageArray, col, row - 1);
//					// imageArray[col][row] = null; delete pixel here
//					/// shift array over
//				}
//				// rightCol is the smallest
//				else if (rightCol <= leftCol && rightCol <= aboveCol) {
//					nextMin(colorArray, imageArray, col + 1, row - 1);
//					// imageArray[col][row] = null; delete pixel here
//					// delete from both arrays
//					/// shift array over
//				}
//				// leftCol is the smallest
//				else {
//					nextMin(colorArray, imageArray, col - 1, row - 1);
//					// imageArray[col][row] = null; delete pixel here
//					/// shift array over
//				}
//			}
//		}
}
