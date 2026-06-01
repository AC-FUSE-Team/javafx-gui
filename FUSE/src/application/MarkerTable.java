package application;

import org.opencv.core.Point;
import org.opencv.core.Scalar;


/* The class contains a table of found markers;
 * The first marker [0] is treated as "pointer" and is set manually;
 * the class will calculate distances from a pointer to markers found in an image
 */
public class MarkerTable {

	/* After the object has been created, initMarkers() should be invoked
	 * before adding markers found in an image.
	 * Also, initMarkers() must be invoked before processing a new image.
	 */
	Marker[] markers;
	final int MAX_MARKERS = 21;
	final int ALLOWANCE = 5;
	
	int nextMarker;
	boolean debugMode;
	
	
	public MarkerTable(boolean isDebugMode) {

		markers = new Marker[MAX_MARKERS];
		
		for(int i = 0; i <  MAX_MARKERS; i++)
			markers[i] = new Marker();
		
		debugMode = isDebugMode;
	}
	
	/* Initializes marker statuses; assigns pointer coordinates
	 * provided via parameters */
	public void initMarkers(int inX, int inY) {
		
		for (int i = 0; i < MAX_MARKERS; i++) {
			markers[i].setStatus(0);
		}
		
		markers[0].setCenter(new Point(inX, inY));
		markers[0].setStatus(1);
		
		// Start filling in the table from the next marker after pointer [0]
		nextMarker = 1;
	}
	
	/* Adds a marker to the list */
	public void addMarker(int inX, int inY, Scalar mainColor) {

		Scalar[] colors = new Scalar[3];
		
		colors[0] = mainColor;
		colors[1] = new Scalar(mainColor.val[0] - ALLOWANCE,
								mainColor.val[1] - ALLOWANCE,
								mainColor.val[2] - ALLOWANCE);
		
		colors[2] = new Scalar(mainColor.val[0] + ALLOWANCE,
								mainColor.val[1] + ALLOWANCE,
								mainColor.val[2] + ALLOWANCE);
		
		markers[nextMarker].setCenter(new Point(inX, inY));
		markers[nextMarker].setStatus(1);
		markers[nextMarker].setColor(colors);

		nextMarker++;
	}
	
	/* Calculates distances between every found marker and a pointer (markers[0]);
	 * returns index of the closest one (selected marker) or -1 if pointer was not found in the scene */
	public int findSelectedMarker() {
		
		int minDistance = 0;
		int minDistanceIndex = 0;
		Point pointerCenter = markers[0].getCenter();
		
		if(debugMode)
			System.out.printf("%nCenter Point: (%d, %d)%n%n", (int)pointerCenter.x, (int)pointerCenter.y);
		
		/* Loop through other markers: calculate distance, find a marker with minimum distance  */
		for (int i = 1; i < MAX_MARKERS; i++) {
			
			if(markers[i].getStatus() == 1) {
				Point markerCenter = markers[i].getCenter();
				int distance = (int)(Math.pow(markerCenter.x - pointerCenter.x, 2) +
						Math.pow(markerCenter.y - pointerCenter.y, 2));
				
				markers[i].setDistance(distance);
				
				/* Set initial minDistance to the distance of the first [1] element
				 * (the first marker, not the pointer) */
				if(i == 1) {
					minDistance = distance;
					minDistanceIndex = 1;
				}
				else {
					if(minDistance > distance) {
						minDistance = distance;
						minDistanceIndex = i;
					}
				}
				
				if(debugMode) {
					System.out.printf("Distance for ellipse %d: %d / MinDistance %d %d%n",
						i, distance, minDistanceIndex, minDistance);
					System.out.printf("Color saved: (%d, %d, %d)%n", (int)(markers[i].getColor()[0].val[2]),
											(int)(markers[i].getColor()[0].val[1]),
											(int)(markers[i].getColor()[0].val[0]) );
					System.out.printf("Color left bounadry: (%d, %d, %d)%n", (int)(markers[i].getColor()[1].val[2]),
							(int)(markers[i].getColor()[1].val[1]),
							(int)(markers[i].getColor()[1].val[0]) );
					System.out.printf("Color right bounadry: (%d, %d, %d)%n%n", (int)(markers[i].getColor()[2].val[2]),
							(int)(markers[i].getColor()[2].val[1]),
							(int)(markers[i].getColor()[2].val[0]) );
				}
			}
			
		}
		return minDistanceIndex;
	}
	
	public int getMarkerPlaceNumber() {
		return MAX_MARKERS - 1;
		
	}
	
	public Scalar[] getColor(int colorIndex) {
		
		return markers[colorIndex].color;
	}
}
