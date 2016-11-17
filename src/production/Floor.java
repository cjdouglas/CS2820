/**
 * @author Chris Douglas
 * Floor.java
 */


/*
 * 
 */

/*
 * Key:
 * P = Picking Station
 * K = Packer
 * B = Belt 
 * H = Shipping dock
 * R = Receiving dock 
 * S = Shelf
 * C = Charging Station
 * Open Space = Open road Space

	 0 1 2 3 4 5 6 7
  0 | | |P|B|B|B|K|H|
  1 | | | | | | | | |
  2 | | |S| | |S| | |
  3 | | |S| | |S| | |
  4 | | |S| | |S| | |
  5 | | |S| | |S| | |
  6 | | | | | | | | |
  7 |C| | | | | | |R|
	
 */

package production;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Floor {
    
	private final int width = 8;
	private final int height = 8;
	private final int numShelves = 8;
	
	// private HashMap<Integer, Point> locations;
	private Shelf[] shelves;
	private Point[] locations;
	
	private List<Point> beltArea;
	private Point chargeLocation;
	private Point pickLocation;
	private Point packLocation;
	
	private Point shippingDock;
	private Point receivingDock;
	
	/**
	 * Initializer for the Floor class
	 * @param numShelves The number of shelves being passed into the Floor
	 */
	public Floor(List<Shelf> shelfList) {
		shelves = new Shelf[8];
		locations = new Point[8];
		beltArea = new ArrayList<>();

		initFloor(shelfList);
	}	
	
	/**
	 * Initializes the locations for the floor
	 */
	private void initFloor(List<Shelf> shelfList) {
		
		for (int i = 0; i < numShelves; i++) {
			shelves[i] = shelfList.get(i);
		}
		
		int counter = 0;
		for (int i = 2; i < 6; i++) {
			locations[counter++] = new Point(2, i);
			locations[counter++] = new Point(5, i);
		}
		
		chargeLocation = new Point(0, height - 1);
		//--------BELT AREA-----------
		pickLocation = new Point(0, 2);
		packLocation = new Point(0, 6);
		beltArea.add(pickLocation);
		beltArea.add(new Point(0, 3));
		beltArea.add(new Point(0, 4));
		beltArea.add(new Point(0, 5));
		beltArea.add(packLocation);
		//----------------------------
		shippingDock = new Point(0, width - 1);
		receivingDock = new Point(width - 1, height - 1);
	}
	
	/**
	 * Returns the location of a given shelf, returns null if the shelf cannot be found
	 * @param shelf The shelf to return the location for
	 * @return The Location of a given shelf
	 */
	public Point getShelfLocation(int shelfId) {
		for (int i = 0; i < numShelves; i++) {
			if (shelves[i].getShelfNumber() == shelfId) {
				return locations[i];
			}
		}
		
		return null;
	}
	
	public Shelf getShelfAt(Point loc) {
		for (int i = 0; i < numShelves; i++) {
			if (locations[i].equals(loc)) {
				Shelf shelf = shelves[i];
				shelves[i] = null;
				return shelf;
			}
		}
		
		return null;
	}
	
	/**
	 * Places the given Shelf into the first open location on the Floor
	 * @param shelf The shelf to place back onto the Floor
	 */
	public boolean placeShelf(Shelf shelf) {
		for (int i = 0; i < numShelves; i++) {
			if (shelves[i] == null) {
				shelves[i] = shelf;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the Point representing the charge location
	 * @return The Point representing the charge location
	 */
	public Point getChargeLocation() {
		return chargeLocation;
	}
	
	/**
	 * Returns the Point representing the pick location
	 * @return The Point representing the pick location
	 */
	public Point getPickLocation() {
		return pickLocation;
	}
	
	/**
	 * Returns the Point representing the pack location
	 * @return The Point representing the pack location
	 */
	public Point getPackLocation() {
		return packLocation;
	}
	
	/**
	 * Returns the Point representing the shipping dock location
	 * @return The Point representing the shipping dock location
	 */
	public Point getShippingDockLocation() {
		return shippingDock;
	}
	
	/**
	 * Returns the Point representing the receiving dock location
	 * @return The Point representing the receiving dock location
	 */
	public Point getReceivingDockLocation() {
		return receivingDock;
	}
	
	/**
	 * Returns the width of the floor
	 * @return The width of the floor
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Returns the height of the floor
	 * @return The height of the floor
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns a list of points related to the Belt Area
	 * @return A list of points related to the Belt Area
	 */
	public List<Point> getBeltArea() {
		return beltArea;
	}
	
	/**
	 * Returns the array of all the shelves currently placed on the floor
	 * @return Array of all the shelves currently placed on the floor
	 */
	public Shelf[] getShelves() {
		return shelves;
	}
	
	public Point[] getLocations() {
		return locations;
	}
}