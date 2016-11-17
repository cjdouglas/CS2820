package production;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.awt.Point;

/**
 * @author Ben East
 *
 *         Group A2 - RobotScheduler class
 */
public class RobotScheduler implements Tickable {
	// Instance Variables
	private ArrayList<Robot> robotList;
	private HashSet<Integer> shelvesForOrder;
	private PriorityQueue<Integer> shelvesForRestock;
	private PriorityQueue<HashSet<Integer>> pendingOrders;
	private Floor floor;

	/**
	 * @author Ben East
	 * @param f
	 *            The floor of the warehouse to be navigated.
	 * @param numBots
	 *            The number of robots to be created.
	 * 
	 * 			  Constructs a new robotScheduler object.
	 */
	public RobotScheduler(Floor f, int numBots) {
		floor = f;
		this.robotList = new ArrayList<Robot>();
		this.shelvesForOrder = new HashSet<Integer>();
		this.shelvesForRestock = new PriorityQueue<Integer>();
		this.pendingOrders = new PriorityQueue<HashSet<Integer>>();
		createRobots(numBots);
	}

	/**
	 * @author Ben East
	 * @return Returns the list of robots in the warehouse.
	 */
	public ArrayList<Robot> getRobotList() {
		return this.robotList;
	}

	/**
	 * @author Ben East
	 * @param shelvesNeeded
	 * 		   A hashset of shelf numbers needed for the order.
	 * 
	 *         Takes a new assignment of shelves, saves the list, and sends
	 *         robots to the pick location if they're already holding it.	
	 */
	public void assignOrder(HashSet<Integer> shelvesNeeded) {
		// if a robot already has the shelf, reroute to the pick station.
		if (this.shelvesForOrder.isEmpty()) {
			for (Robot r : robotList) {
				Integer currentShelfNumber = -1;
				if (r.getCurrentShelf() != null) {
					currentShelfNumber = r.getCurrentShelf().getShelfNumber();
				}
				
				if (shelvesNeeded.contains(currentShelfNumber)) {
					r.setTarget(floor.getPickLocation());
					r.setBusy(true);
					shelvesNeeded.remove(currentShelfNumber);
					break; // Move on to the next shelf...
				}
			}
			// Save the remaining shelves for future robots
			this.shelvesForOrder = shelvesNeeded;
		} else {
			this.pendingOrders.add(shelvesNeeded);
		}
	}
	
	/**
	 * @author Ben East
	 * @param shelfToRestock
	 *            The shelf to be restocked.
	 * 
	 *            This method allows the master to give the scheduler a shelf
	 *            that needs to be restocked. The scheduler will assign the
	 *            shelf on the next tick.
	 */
	public void restock(Integer shelfToRestock) {
		for (Robot r : this.robotList) {
			Integer currentShelfNumber = -1;
			
			if(r.getCurrentShelf() != null) {
				currentShelfNumber = r.getCurrentShelf().getShelfNumber();
			}
			
			if (currentShelfNumber.equals(shelfToRestock)) { // Order the robot to restock
				r.setTarget(floor.getReceivingDockLocation());
				r.setBusy(true);
				return; // We're done here...
			}
		}
		// Add to list of shelves for restock
		this.shelvesForRestock.add(shelfToRestock);
	}

	/**
	 * @author Ben East
	 * 
	 *         Assigns a shelf to an idle robot if there are shelves to be
	 *         assigned.
	 */
	protected void assignShelf(Robot r) {
		// Prioritize restock over the current order.
		if (!this.shelvesForRestock.isEmpty()) { // Assign from shelvesForRestock
			r.setTarget(floor.getShelfLocation(this.shelvesForRestock.remove()));
			r.setBusy(true);
		} else if (!this.shelvesForOrder.isEmpty()) { // Assign from shelvesForOrder
			Integer nextShelf = this.shelvesForOrder.iterator().next();
			r.setTarget(floor.getShelfLocation(nextShelf));
			r.setBusy(true);
			this.shelvesForOrder.remove(nextShelf);
		} else if (this.shelvesForOrder.isEmpty() && !this.pendingOrders.isEmpty()) { // Update shelvesForOrder
			this.shelvesForOrder = this.pendingOrders.remove();
			assignShelf(r);
		}
	}

	/**
	 * @author Ben East
	 * 
	 *         Advances each robot 1 step toward their target, and makes them
	 *         perform an action if they are at their target.
	 */
	public void advanceRobots() {
		for (Robot r : robotList) {
			Integer currentShelfNumber = -1;	// Arbitrary shelf number for null case
			Point currentPos = r.getCurrentPosition(), targetPos = r.getTarget(),
					currentShelfLocation = new Point(-1, -1); // Arbitrary shelf location for null case
			
			if(r.getCurrentShelf() != null) { // Update if holding a shelf
				currentShelfNumber = r.getCurrentShelf().getShelfNumber();
				currentShelfLocation = floor.getShelfLocation(currentShelfNumber);
			}
			
			if (!currentPos.equals(targetPos)) { // keep moving
				moveTowardTarget(r);
			} else {
				if (targetPos.equals(floor.getChargeLocation())) { // Recharge
					r.recharge();
					r.setBusy(false);
					//r.setTarget() //set to arbitrary point away from charge station.
				} else if (targetPos.equals(floor.getPickLocation()) && currentShelfNumber != -1) { // Handle pick area
					r.setTarget(currentShelfLocation);
				} else if (targetPos.equals(currentShelfLocation) && currentShelfNumber != -1) { // Handle drop shelf
					floor.placeShelf(r.dropShelf());
					r.setTarget(floor.getChargeLocation());
					r.setBusy(true);
				} else if (r.isBusy() && currentShelfNumber == -1) { // Handle grab shelf
					// Since shelves have a home position, if this robot was
					// assigned the shelf, it must be on the ground.
					r.grabShelf(floor.getShelfAt(currentPos));
				} else if (currentShelfNumber != -1 && targetPos.equals(floor.getReceivingDockLocation())) { // Handle restock
					r.setTarget(currentShelfLocation);
				}
			}
		}
	}

	// TODO Update movement to make use of highways and shelving areas.
	/**
	 * @author Ben East
	 * @param r
	 *            The robot to be moved.
	 *            
	 *            Determines which direction the robot should move, and
	 *            moves there if not occupied.
	 */
	protected void moveTowardTarget(Robot r) {
		double currentX = r.getCurrentPosition().getX(), currentY = r.getCurrentPosition().getY(),
				targetX = r.getTarget().getX(), targetY = r.getTarget().getY();
		
		// Move right
		if (currentX < targetX) {
			Point potentialLocation = new Point((int) currentX + 1, (int) currentY);
			if (notOccupied(potentialLocation) && currentX != floor.getWidth()) {
				r.moveRight();
				return; // Return to limit to one movement per method call.
			}
		}

		// Move left
		if (currentX > targetX) {
			Point potentialLocation = new Point((int) currentX - 1, (int) currentY);
			if (notOccupied(potentialLocation) && currentX != 0) {
				r.moveLeft();
				return; // Return to limit to one movement per method call.
			}
		}

		// Move up
		if (currentY < targetY) {
			Point potentialLocation = new Point((int) currentX, (int) currentY + 1);
			if (notOccupied(potentialLocation) && currentY != floor.getHeight()) {
				r.moveUp();
				return; // Return to limit to one movement per method call.
			}
		}

		// Move down
		if (currentY > targetY) {
			Point potentialLocation = new Point((int) currentX, (int) currentY - 1);
			if (notOccupied(potentialLocation) && currentY != 0) {
				r.moveDown();
				return; // Return to limit to one movement per method call.
			}
		}
	}

	/**
	 * @author Ben East
	 * @param location
	 *            The location to be checked.
	 * @return Returns true if no robot is at the location, and false otherwise.
	 */
	protected boolean notOccupied(Point location) {
		// Prevent robots from landing on pack, belt, or shipping dock locations.
		if (location.equals(floor.getPackLocation())) {
			return false;
		} else if (floor.getBeltArea().contains(location)) {
			return false;
		} else if (location.equals(floor.getShippingDockLocation())) {
			return false;
		}
		
		for (Robot r : robotList) {
			if (r.getCurrentPosition().equals(location)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * @author Ben East
	 * @param numBots
	 *            The number of robots to be created.
	 *            
	 *            Creates all of the robots for the warehouse. For use in the
	 *            constructor only.
	 */
	protected void createRobots(int numBots) {
		for (int i = 0; i < numBots; ++i) {
			robotList.add(new Robot(new Point(i, 0)));
		}
	}

	/**
	 * @author Ben East
	 * @param shelfNum
	 *            The Shelf to be checked
	 * @return Return true if the robotScheduler is working on restocking a
	 *         shelf, and return false otherwise.
	 */
	public boolean restockingNow(Integer shelfNum) {
		if (this.shelvesForRestock.contains(shelfNum)) {
			return true;
		}
		
		for (Robot r : robotList) {
			if (r.getCurrentShelf().equals(shelfNum) && r.getTarget().equals(floor.getReceivingDockLocation())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @author Ben East
	 * The method to allow the robot scheduler to take action during the simulation.
	 */
	public void tick() {
		for (Robot r : this.robotList) {
			if (!r.isBusy() && r.isCharged()) {
				// If the robot is ready to work, attempt to assign it a shelf.
				assignShelf(r);
			}
		}
		// Move each robot 1 step
		advanceRobots();
	}
	
	/**
	 * @author Ben East
	 * @author Liam Crawford
	 * @return Return true if there are robots at pick locations, and false otherwise.
	 */
	public boolean robotsAtPicker() {
		for(Robot r : robotList) {
			if(r.getCurrentPosition().equals(floor.getPickLocation())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @author Ben East
	 * @author Liam Crawford
	 * @return Returns true if a robot is at the receiving dock, and false otherwise.
	 */
	public boolean robotsAtRestock() {
		for(Robot r : robotList) {
			if(r.getCurrentPosition().equals(floor.getReceivingDockLocation())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @author Ben East
	 * @author Liam Crawford
	 * @return Returns an arrayList of the shelves that are at pick locations.
	 */
	public ArrayList<Integer> shelvesAtPicker() {
		ArrayList<Integer> shelves = new ArrayList<Integer>();
		
		for(Robot r : robotList) {
			if(r.getCurrentPosition().equals(floor.getPickLocation())) {
				shelves.add(r.getCurrentShelf().getShelfNumber());
			}
		}
		
		return shelves;
	}
	
	/**
	 * @author Ben East
	 * @author Liam Crawford
	 * @return Returns an arrayList of the shelves that are at receiving docks.
	 */
	public ArrayList<Integer> shelvesAtRestock() {
		ArrayList<Integer> shelves = new ArrayList<Integer>();
		
		for(Robot r : robotList) {
			if(r.getCurrentPosition().equals(floor.getReceivingDockLocation())) {
				shelves.add(r.getCurrentShelf().getShelfNumber());
			}
		}
		
		return shelves;
	}
}