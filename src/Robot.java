package production;

import java.awt.Point;

/**
 * @author Ben East
 *
 *         Group A2 - Robot class
 */
public class Robot {
	// Instance variables
	private Point currentPosition;
	private Point targetPosition;
	private Shelf currentShelf;
	private boolean charged;
	private boolean busy;

	/**
	 * @author Ben East
	 * 
	 *         Constructs a new robot object.
	 * 
	 * @param position
	 *            The current position of the new robot.
	 */
	public Robot(Point position) {
		this.currentPosition = position;
		this.targetPosition = null;
		this.charged = true;
		this.busy = false;
	}

	/**
	 * @author Ben East
	 * @return Returns the currentPosition (x, y) coordinate pair for the robot
	 */
	protected Point getCurrentPosition() {
		return this.currentPosition;
	}

	/**
	 * @author Ben East
	 * @return Returns the shelf that the robot is currently holding, or null if
	 *         the robot isn't holding a shelf.
	 */
	protected Shelf getCurrentShelf() {
		return this.currentShelf;
	}

	/**
	 * @author Ben East
	 * 
	 *         Makes the robot "grab" a shelf from the warehouse.
	 * 
	 * @param s
	 *            The shelf the Robot needs to grab.
	 */
	protected void grabShelf(Shelf s) {
		this.currentShelf = s;
	}

	/**
	 * @author Ben East
	 * 
	 *         Sets the robot's busy value to the given parameter val.
	 * 
	 * @param val
	 *            The busy status of the robot.
	 */
	protected void setBusy(boolean val) {
		this.busy = val;
	}

	/**
	 * @author Ben East
	 * @return Returns true if the robot is busy, and false otherwise.
	 */
	protected boolean isBusy() {
		return this.busy;
	}

	/**
	 * @author Ben East
	 * 
	 *         Sets the robot's charged value to true.
	 */
	protected void recharge() {
		this.charged = true;
	}

	/**
	 * @author Ben East
	 * @return Returns true if the robot is charged, and false otherwise.
	 */
	protected boolean isCharged() {
		return this.charged;
	}

	/**
	 * @author Ben East
	 * 
	 *         Makes the robot "drop" it's shelf at it's current position.
	 * 
	 * @return Returns the shelf that the robot was previously carrying.
	 */
	protected Shelf dropShelf() {
		// If we're holding a shelf, put it down and give the scheduler the
		// shelf info the scheduler will inform the floor of the new shelf at
		// this.currentPosition.
		if (this.currentShelf != null) {
			Shelf tempShelf = this.currentShelf;
			this.currentShelf = null;
			this.charged = false;

			return tempShelf;
			// If not, don't give it a shelf.
		} else {
			return null;
		}
	}

	/**
	 * @author Ben East
	 * 
	 *         Sets the robot's target position to the given target coordinates.
	 * 
	 * @param target
	 *            The coordinates the robot should move toward.
	 */
	protected void setTarget(Point target) {
		this.targetPosition = target;
	}

	/**
	 * @author Ben East
	 * @return Returns the current target coordinates of the robot.
	 */
	protected Point getTarget() {
		return this.targetPosition;
	}

	/**
	 * @author Ben East
	 * 
	 *         Decrements the robot's x coordinate value to "move" it one step
	 *         to the left.
	 */
	protected void moveLeft() {
		this.currentPosition.setLocation(this.currentPosition.getX() - 1, this.currentPosition.getY());
	}

	/**
	 * @author Ben East
	 * 
	 *         Increments the robot's x coordinate value to "move" it one step
	 *         to the right.
	 */
	protected void moveRight() {
		this.currentPosition.setLocation(this.currentPosition.getX() + 1, this.currentPosition.getY());
	}

	/**
	 * @author Ben East
	 * 
	 *         Increments the robot's y coordinate value to "move" it one step
	 *         up.
	 */
	protected void moveUp() {
		this.currentPosition.setLocation(this.currentPosition.getX(), this.currentPosition.getY() + 1);
	}

	/**
	 * @author Ben East
	 * 
	 *         Decrements the robot's y coordinate value to "move" it one step
	 *         down.
	 */
	protected void moveDown() {
		this.currentPosition.setLocation(this.currentPosition.getX(), this.currentPosition.getY() - 1);
	}
}