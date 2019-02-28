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
	public static Trio image[][];

	public static void main(String args[]) throws IOException {
		File file = new File("./tree.JPG");
		BufferedImage imageSource = ImageIO.read(file);
		rows = imageSource.getHeight();
		cols = imageSource.getWidth();

		System.out.printf("%d by %d pixels\n", rows, cols);

		/* Read into an array of rgb values */
		image = new Trio[rows][cols];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				int color = imageSource.getRGB(i, j);
				int red = (color >> 16) & 0xff;
				int green = (color >> 8) & 0xff;
				int blue = (color) & 0xff;
				image[j][i] = new Trio(new Color(red, green, blue), 0, 0);
			}
		}

		// create the array of sum of energies and carve it
		sumArray(image, rows, cols);
		for (int i = 0; i < 500; i++) {
			carve(image, rows - 1, getMinCol(image[rows - 1]));

			// recalculate energies and seams
			sumArray(image, rows, cols - i - 1);
		}

		System.out.println(rows);
		/* Save as new image where g values set to 0 */
		BufferedImage imageNew = new BufferedImage(image[rows - 1].length, rows, BufferedImage.TYPE_INT_RGB);
		File fileNew = new File("./treeCarved500.jpg");
		for (int i = 0; i < image[0].length; i++) {
			for (int j = 0; j < rows; j++) {
				int r = image[j][i].getColor().getRed();
				int g = image[j][i].getColor().getGreen();
				int b = image[j][i].getColor().getBlue();
				int col = (r << 16) | (g << 8) | b;
				imageNew.setRGB(i, j, col);
			}
		}
		System.out.println(imageNew.getHeight());
		ImageIO.write(imageNew, "JPEG", fileNew);
	}

	// column and row of lowest energy pixel
	public static void carve(Trio[][] imageArray, int row, int col) {

		// if you're at the top row stop the recursion
		if (row == 0) {

			// create the new row and add all the elements of the previous row,
			// except for the element at the column to be carved
			Trio newArr[] = new Trio[imageArray[0].length - 1];

			for (int i = 0; i < imageArray[0].length - 1; i++) {

				if (i < col) {
					newArr[i] = imageArray[row][i];
				} else {
					newArr[i] = imageArray[row][i + 1];
				}
			}

			imageArray[0] = newArr;

		} else {

			// recurse before you remove the element
			carve(imageArray, row - 1, imageArray[row][col].getNext());

			// create the new row and add all the elements of the previous row,
			// except for the element at the column to be carved
			Trio newArr[] = new Trio[imageArray[row].length - 1];
			
			for (int i = 0; i < imageArray[row].length - 1; i++) {

				if (i < col) {
					newArr[i] = imageArray[row][i];
				} else {
					newArr[i] = imageArray[row][i + 1];
				}
			}

			imageArray[row] = newArr;
		}
	}

	// summed up energies array
	public static void sumArray(Trio[][] image, int rows, int cols) {

		// create array of energies
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				image[i][j].setEnergy(energy(image, i, j));
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
					int minEnergy = Math.min(image[j - 1][i].getEnergy(), image[j - 1][i + 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == image[j - 1][i].getEnergy()) {
						pos = i;
					} else {
						pos = i + 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					// System.out.println(i + " cols and  " + j + " rows");
					image[j][i].setEnergy(image[j][i].getEnergy() + minEnergy);
					image[j][i].setNext(pos);

					// if you're at the rightmost column, you only consider the values directly
					// above and to the top left
				} else if (i == cols - 1) {

					// find the minimum energy and set the initial position of the smallest value
					// to the same column
					int minEnergy = Math.min(image[j - 1][i].getEnergy(), image[j - 1][i - 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == image[j - 1][i].getEnergy()) {
						pos = i;
					} else {
						pos = i - 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					image[j][i].setEnergy(image[j][i].getEnergy() + minEnergy);
					image[j][i].setNext(pos);

				} else {

					// find the minimum energy and set the initial position of the smallest value
					// to the same column
					int minEnergy = Math.min(Math.min(image[j - 1][i].getEnergy(), image[j - 1][i - 1].getEnergy()),
							image[j - 1][i + 1].getEnergy());
					int pos = i;

					// find the exact column of the minimum value
					if (minEnergy == image[j - 1][i].getEnergy()) {
						pos = i;
					} else if (minEnergy == image[j - 1][i - 1].getEnergy()) {
						pos = i - 1;
					} else {
						pos = i + 1;
					}

					// create the association between the energy of the pixel and the position of
					// the lowest
					// energy pixel that leads to it
					image[j][i].setEnergy(image[j][i].getEnergy() + minEnergy);
					image[j][i].setNext(pos);
				}
			}
		}
	}

	// get column of the minimum value of array
	public static int getMinCol(Trio[] array) {
		int theMin = array[0].getEnergy();
		int index = 0;
		for (int i = 0; i < array.length; i++) {

			if (array[i] != null) {
				if (array[i].getEnergy() < theMin) {
					theMin = array[i].getEnergy();
					index = i;
				}
			}
		}
		return index;
	}

	// find the energy of a pixel
	private static int energy(Trio[][] arr, int row, int col) {

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
				redX = Math.abs(arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col + 1].getColor().getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue());

			} else {

				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getColor().getRed() - arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen() - arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue() - arr[row][col + 1].getColor().getBlue());

			}

			// find RGB value across Y axis
			// if we are in last row, the bottom pixel is on same column, top row
			redY = Math.abs(arr[row - 1][col].getColor().getRed());
			greenY = Math.abs(arr[row - 1][col].getColor().getGreen());
			blueY = Math.abs(arr[row - 1][col].getColor().getBlue());

		} else if (row == 0) {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col + 1].getColor().getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue());

			} else {

				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getColor().getRed() - arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen() - arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue() - arr[row][col + 1].getColor().getBlue());

			}

			// find RGB value across Y axis
			// if we are in first row, the top pixel is on same column, bottom row
			redY = Math.abs(arr[row + 1][col].getColor().getRed());
			greenY = Math.abs(arr[row + 1][col].getColor().getGreen());
			blueY = Math.abs(arr[row + 1][col].getColor().getBlue());

		} else {

			if (col == 0) {

				// find RGB value across x axis
				// if the pixel is in the first column, the left pixel is in the same row, last
				// column
				redX = Math.abs(arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col + 1].getColor().getBlue());

			} else if (col == arr[row].length - 1) {

				// find RGB value across x axis
				// if the pixel is in the last column, the right pixel is in the same row, first
				// column
				redX = Math.abs(arr[row][col - 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue());

			} else {

				// find RGB value across x axis
				redX = Math.abs(arr[row][col - 1].getColor().getRed() - arr[row][col + 1].getColor().getRed());
				greenX = Math.abs(arr[row][col - 1].getColor().getGreen() - arr[row][col + 1].getColor().getGreen());
				blueX = Math.abs(arr[row][col - 1].getColor().getBlue() - arr[row][col + 1].getColor().getBlue());

			}

			// find RGB value across Y axis
			redY = Math.abs(arr[row - 1][col].getColor().getRed() - arr[row + 1][col].getColor().getRed());
			greenY = Math.abs(arr[row - 1][col].getColor().getGreen() - arr[row + 1][col].getColor().getGreen());
			blueY = Math.abs(arr[row - 1][col].getColor().getBlue() - arr[row + 1][col].getColor().getBlue());
		}

		// find the change in x
		changeX = (int) (Math.pow(redX, 2) + Math.pow(greenX, 2) + Math.pow(blueX, 2));

		// find the change in y
		changeY = (int) (Math.pow(redY, 2) + Math.pow(greenY, 2) + Math.pow(blueY, 2));

		// returns energy of input
		return changeX + changeY;
	}
}
