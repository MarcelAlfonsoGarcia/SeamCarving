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
		File file = new File("./cuba.JPG");
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

		// create the array of sum of energies and carve it 
		Pair[][] array = sumArray(image, rows, cols);
		for (int i = 0; i < 1000; i++) {
			carve(array, image, array.length - 1, getMinCol(array[array.length - 1]));
			
			// recalculate energies and seams
			array = sumArray(image, rows, array[0].length);
		}

		/* Save as new image where g values set to 0 */
		BufferedImage imageNew = new BufferedImage(array[0].length, rows, BufferedImage.TYPE_INT_RGB);
		File fileNew = new File("./kk4.jpg");
		for (int i = 0; i < array[0].length; i++) {
			for (int j = 0; j < rows; j++) {
				int r = image[j][i].getRed();
				int g = image[j][i].getGreen();
				int b = image[j][i].getBlue();
				int col = (r << 16) | (g << 8) | b;
				imageNew.setRGB(i, j, col);
			}
		}
		ImageIO.write(imageNew, "JPEG", fileNew);
	}

	// column and row of lowest energy pixel
	public static void carve(Pair[][] imageArray, Color[][] image, int row, int col) {

		// if you're at the top row stop the recursion
		if (row == 0) {

			// create the new row and add all the elements of the previous row,
			// except for the element at the column to be carved
			Pair newArr[] = new Pair[imageArray[0].length - 1];
			Color newImg[] = new Color[image[0].length - 1];
			
			for (int i = 0; i < imageArray[0].length - 1; i++) {

				if (i < col) {
					newArr[i] = imageArray[row][i];
					newImg[i] = image[row][i];
				} else {
					newArr[i] = imageArray[row][i + 1];
					newImg[i] = image[row][i+1];
				}
			}

			imageArray[0] = newArr;
			image[0] = newImg;

		} else {
			
			// recurse before you remove the element
			carve(imageArray, image, row - 1, imageArray[row][col].getNext());

			// create the new row and add all the elements of the previous row,
			// except for the element at the column to be carved
			Pair newArr[] = new Pair[imageArray[row].length - 1];
			Color newImg[] = new Color[image[row].length - 1];
			
			for (int i = 0; i < imageArray[row].length - 1; i++) {

				if (i < col) {
					newArr[i] = imageArray[row][i];
					newImg[i] = image[row][i];
				} else {
					newArr[i] = imageArray[row][i + 1];
					newImg[i] = image[row][i+1];
				}
			}

			imageArray[row] = newArr;
			image[row] = newImg;
		}
	}

	// summed up energies array
	public static Pair[][] sumArray(Color[][] image, int rows, int cols) {

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

	// get column of the minimum value of array
	public static int getMinCol(Pair[] array) {
		int theMin = array[0].getEnergy();
		for (int i = 0; i < array.length; i++) {

			if (array[i] != null) {
				if (array[i].getEnergy() < theMin) {
					theMin = i;
				}
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
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() - arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][0].getBlue());

			} else {
				
				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][col + 1].getBlue());
				
			}

			// find RGB value across Y axis
			// if we are in last row, the bottom pixel is on same column, top row
			redY = Math.abs(arr[row - 1][col].getRed() - arr[0][col].getRed());
			greenY = Math.abs(arr[row - 1][col].getGreen() - arr[0][col].getGreen());
			blueY = Math.abs(arr[row - 1][col].getBlue() - arr[0][col].getBlue());

		} else if (row == 0) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() - arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][0].getBlue());

			} else {
				
				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][col + 1].getBlue());
				
			}

			// find RGB value across Y axis
			// if we are in first row, the top pixel is on same column, bottom row
			redY = Math.abs(arr[arr.length - 1][col].getRed() - arr[row + 1][col].getRed());
			greenY = Math.abs(arr[arr.length - 1][col].getGreen() - arr[row + 1][col].getGreen());
			blueY = Math.abs(arr[arr.length - 1][col].getBlue() - arr[row + 1][col].getBlue());

		} else {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][arr[row].length - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][arr[row].length - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][arr[row].length - 1].getBlue() - arr[row][col + 1].getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][0].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][0].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][0].getBlue());

			} else {
				
				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getRed() - arr[row][col + 1].getRed());
				greenX = Math.abs(arr[row][col - 1].getGreen() - arr[row][col + 1].getGreen());
				blueX = Math.abs(arr[row][col - 1].getBlue() - arr[row][col + 1].getBlue());
				
			}

			// find RGB value across Y axis
			redY = Math.abs(arr[row - 1][col].getRed() - arr[row + 1][col].getRed());
			greenY = Math.abs(arr[row - 1][col].getGreen() - arr[row + 1][col].getGreen());
			blueY = Math.abs(arr[row - 1][col].getBlue() - arr[row + 1][col].getBlue());
		}

		// find the change in x
		changeX = redX * redX + greenX * greenX + blueX * blueX;

		// find the change in y
		changeY = redY * redY + greenY * greenY + blueY * blueY;

		// returns energy of input
		return changeX + changeY;
	}
}
