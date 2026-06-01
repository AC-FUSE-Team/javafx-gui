package application;

import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.EdgeDrawing;
import org.opencv.ximgproc.Ximgproc;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Rect;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;


public class ComputerVisionDriver {
	
	Mat imageOriginal;
	Mat imageProcessedTmp;
	Mat imageProcessed;
	Mat imageFinal;
	Mat ellipses;
	
	MarkerTable markerTable;
	
	boolean debugMode;
	// Default size of color sampling rectangle;
	// should be even
	final int DEFAULT_SAMPLING_SIZE = 50;
	
	Scanner input;
	
	public ComputerVisionDriver(boolean isDebugMode) {
		
		debugMode = isDebugMode;
		
		imageProcessedTmp = new Mat();
		imageProcessed = new Mat();
		imageFinal = new Mat();
		ellipses = new Mat();
		markerTable = new MarkerTable(debugMode);
		
		input = new Scanner(System.in);

	}
	
	public void release() {
		input.close();
	}
	
	public void initColors() {
		
	}
	
	// Uses user's input to select and dump selected colors of markers into a file
	public void selectColors() {
		
		FileWriter file;
		//"/home/nataliya/AC-courses/FUSE/app/markers.txt"
		//"C:\\Users\\nakor\\FUSE\\markers.txt"
		String fileName = "C:\\Users\\nakor\\FUSE\\markers.txt";
		try {
			file = new FileWriter(fileName, false);

		int markerSeqNumber = 0;

		do {
			System.out.printf("Please select marker number to save its color (100 - to save and finish): ");
			
			while(!input.hasNextInt())
			{;}
			markerSeqNumber = input.nextInt();
			
			if (markerSeqNumber != 100) {
				
				Scalar[] color = markerTable.getColor(markerSeqNumber);
				String colorLine = String.format("%d %d %d  %d %d %d  %d %d %d%n",
						(int)color[0].val[0], (int)color[0].val[1], (int)color[0].val[2],
						(int)color[1].val[0], (int)color[1].val[1], (int)color[1].val[2],
						(int)color[2].val[0], (int)color[2].val[1], (int)color[2].val[2]);
				
				System.out.printf(colorLine);
				file.write( colorLine );
			}
			
		} while( markerSeqNumber != 100);
		
		System.out.printf("Finished%n");
		file.close();
		
		}
		catch(IOException e) {
			System.out.printf("File Exception:" + e);
		}
	}
	
	
	/* Detects markers on an original snapshot:
	 *  snapshotIn is an original image,
	 *  snapshotOut is used to output diagnostic information over an original image;
	 *  the both objects are created and provided by a caller */
	public int findSelectedMarker(Mat snapshotIn, Mat snapshotOut) {
		
		imageOriginal = snapshotIn;
		imageFinal = snapshotOut;
		
		/* Detect Ellipses in the original image */
		Imgproc.cvtColor(imageOriginal, imageProcessedTmp, Imgproc.COLOR_BGR2GRAY);
		Imgproc.medianBlur(imageProcessedTmp, imageProcessed, 3);
		//medianBlur​(imageProcessedTmp, imageProcessed, 3);

		EdgeDrawing ed = Ximgproc.createEdgeDrawing();
		ed.detectEdges(imageProcessed);
		ed.detectEllipses(ellipses);
		//detectEllipses​(ellipses);

		// The first [0] marker (a.k.a "pointer") is a center of the image
		markerTable.initMarkers(imageOriginal.width() / 2, imageOriginal.height() / 2);
		
		// Number of found ellipses
		int ellipsesNumber = ellipses.rows();
		// Number of markers that markerTable can keep
		int markerPlaces = markerTable.getMarkerPlaceNumber();
		
		int numberOfMarkers = (ellipsesNumber > markerPlaces) ? markerPlaces : ellipsesNumber;
		
		System.out.printf("Image size: %dx%d%n%n", imageOriginal.width(), imageOriginal.height());
		
		System.out.printf("%nEllipses found: %d%n", ellipsesNumber);
		System.out.printf("Ellipses to be processed %d%n", numberOfMarkers);
		
		// Index of legit markers ([0]-th is reference marker, start from 1)
		int legitMarkersNumber = 1;
		
		// Loop through detected ellipses
		for (int i = 0; i < numberOfMarkers; i++) {
		
			if(debugMode)
				System.out.printf("%nEllipse #%d:%n", i);
			
			// Extract a center and sizes of an ellipse
			
			// Ellipse center
			int centerX = (int)ellipses.get(i, 0)[0];
			int centerY = (int)ellipses.get(i, 0)[1];
			int ellipseSize1 = (int)(ellipses.get(i, 0)[2] + ellipses.get(i, 0)[3]);
			int ellipseSize2 = (int)(ellipses.get(i, 0)[2] + ellipses.get(i, 0)[4]);
			
			Point center = new Point(centerX, centerY);
			Size size = new Size(ellipseSize1, ellipseSize2);
			
			
			// 0. Prepare color sampling rectangle
			
			int sampleDefaultRectSize = DEFAULT_SAMPLING_SIZE;
			
			int sampleRectSize = (ellipseSize1 < ellipseSize2) ? ellipseSize1 : ellipseSize2;
			sampleRectSize = (sampleRectSize < sampleDefaultRectSize) ? sampleRectSize : sampleDefaultRectSize;
			
			if (sampleRectSize % 2 > 0) {
				sampleRectSize--; // make it even
			}

			// Drawing ellipse center
			Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
		    		2, new Scalar(255, 255, 255), 1);

			// Skip ellipses which sampling rectangle doesn't fit image
			if((centerX - sampleRectSize / 2) < 0 ||
					(centerX + sampleRectSize / 2) >= imageOriginal.width() ||
					(centerY - sampleRectSize / 2) < 0 ||
					(centerY + sampleRectSize / 2) >= imageOriginal.height()) {
				
				if(debugMode) {
					// drawing center of skipped ellipse 
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		5, new Scalar(255, 90, 255), 2);
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		15, new Scalar(255, 90, 255), 2);
					Imgproc.circle(imageFinal, new Point((int)ellipses.get(i, 0)[0], (int)ellipses.get(i, 0)[1]),
				    		25, new Scalar(255, 90, 255), 2);
					
					// print a seq. number of found ellipse at the bottom-right side off an ellipse center
					Imgproc.putText(imageFinal, String.format("%d", i), new Point(center.x - 15, center.y - 10),
							Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
					
					Imgproc.putText(imageFinal, String.format("%d", i), new Point(center.x + 5, center.y + 15),
							Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
					
					System.out.printf("%nThe ellipse skipped%n");
				}
				
				continue;
			}

			int sampleRectX = centerX - sampleRectSize / 2;
			int sampleRectY = centerY - sampleRectSize / 2;
			
			// 1. Create sampling rectangle
			Rect sampleRect = new Rect(sampleRectX, sampleRectY, sampleRectSize, sampleRectSize);
	
			// 2. Create a submat of an original snapshot image
			Mat areaToSample = imageOriginal.submat(sampleRect);
	
			// 3. Calculate the mean color values
			Scalar meanColor = Core.mean(areaToSample);

			// highlight the contour of an ellipse with calculated color
			Size sizeHalo = size;
			sizeHalo.height += 10;
			sizeHalo.width += 10;
			
			// add a new marker to a marker table
			markerTable.addMarker((int)center.x, (int)center.y, meanColor);
			
			
			/* Output block */
			
			/*** *** ***
			 *  Draw everything needed to the final image 
			 *** *** ***  */
			
			// Draw a Halo around selected ellipse
			Imgproc.ellipse(imageFinal, center, sizeHalo, ellipses.get(i, 0)[5], 0, 360,
					meanColor, 8, 1);

			// print a seq. number of legit marker at the top-left side off an ellipse center
			Imgproc.putText(imageFinal, String.format("%d", legitMarkersNumber),
					new Point(center.x - 15, center.y - 10), Imgproc.FONT_HERSHEY_DUPLEX,
					0.7, new Scalar (255, 255, 255));
			
			/*
			 * putText​(imageFinal,
			 
				String.format("%d", legitMarkersNumber),
				new Point(center.x - 15, center.y - 10), Imgproc.FONT_HERSHEY_DUPLEX,
				0.7, new Scalar (255, 255, 255));
			*/
			if(debugMode) {
				
				// print a seq. number of found ellipse at the bottom-right side off an ellipse center
				Imgproc.putText(imageFinal, String.format("%d", i),
						new Point(center.x + 5, center.y + 15), Imgproc.FONT_HERSHEY_DUPLEX, 0.7, new Scalar (255, 170, 255));
								
				// draw center of the image
				Imgproc.circle(imageFinal, new Point(imageOriginal.width() / 2, imageOriginal.height() / 2),
			    		5, new Scalar(0, 0, 255), 3);
				
				Imgproc.rectangle(imageFinal, sampleRect, new Scalar(255, 255, 255), 1);
			}
			
			
			/*** *** *** 
			 * Print out log to a terminal
			 *** *** *** */
			
			if(debugMode)
				System.out.printf("\t## Legit marker #%d%n", legitMarkersNumber);
			else
				System.out.printf("Legit marker #%d%n", legitMarkersNumber);
			System.out.printf("\tCenter: (%d, %d)%n", centerX, centerY);
			
			if(debugMode) {
				System.out.printf("\tRectangle: corner (%d, %d), size (%d, %d)%n", sampleRectX, sampleRectY, sampleRectSize, sampleRectSize);
			}
			
			// print out sampled color of an ellipse
			System.out.printf("\tColor RGB: (%d, %d, %d)%n",
					(int)meanColor.val[2], (int)meanColor.val[1], (int)meanColor.val[0]);
			
			
			
			legitMarkersNumber++;
			
		}

		ellipses.release();
		
		int selection = markerTable.findSelectedMarker();
		
		return selection;
	}

}
