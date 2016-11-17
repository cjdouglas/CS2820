package warehouseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.awt.Point;

public class Belt implements Tickable {
	Floor F;
	ArrayList<Point> beltArea;
	private Point pickLocation;
	private Point packLocation;
	private LinkedList<List<Item>> binList;
	private int beltCapacity;
	private ArrayList<Item> listOfItemOrder;
	private ArrayList<Item> itemOrder;
	/**
	 * @author Leon Grund
	 * @param Floor Object
	 * Floor is needed to find location of
	 * belt area, picker station, packer station
	 */
	public Belt(Floor F){
		this.F = F;
		beltArea = F.getBeltArea();
		Point pickLocation = F.getPickLocation();
		Point packLocation = F.getPackLocation();
		binList = new LinkedList<List<Item>>();
		listOfItemOrder = new ArrayList<Item>();
		itemOrder = new ArrayList<Item>();
		beltCapacity = beltArea.size();
	}

	/**
	   * private method to advance a Bin/order on belt
	   * @void List<Item>, Point - set location of each Item in List to Point
	   */
	private void moveBin(List<Item> B, Point Loc){
		for(Item i: B)i.setLocation(Loc);
	}
	
	/**
	   * private method to check if a Bin/order arrived at a Point
	   * @return boolean: List<Item>, Point - set location of each Item in List to Point
	   */
	private boolean checkBin(List<Item> B, Point loc){
		for(Item i: B)if(i.getLocation() != loc)return false;	
		return true;
	}
	
	/**
	   * private method returns the start location of the belt
	   * @return Point: at index zero in belt area ArrayList
	   */
	private Point startBeltLoc(){
		return beltArea.get(0);
	}
	
	/**
	   * private method returns the end location of the belt
	   * @return Point: at index ArrayList size - 1 in area belt ArrayList
	   * 	   */
	private Point endBeltLoc(){
		return beltArea.get(beltCapacity-1);
	}
	
	/**
	 * called by MASTER
	   * public method that adds Items to a Bin
	   * @void ArrayList<Item> - for each item check if item is on current order list
	   * 		yes -> add to bin
	   * 		no -> error: invalid item or needs order list first
	   */		 
	public void addItemsToBin(ArrayList<Item> pickedItems){
		for(Item i: pickedItems){
			if(listOfItemOrder.contains(i)){
				System.out.println("Valid Picked item");
				itemOrder.add(i);
				listOfItemOrder.remove(i);
				continue;
			}
			System.out.println("Invalid Item Error: wrong picked item OR need an order first");
		}
	}
	
	/**
	 * called by MASTER
	   * public method that places a new order
	   * @void ArrayList<Item> - of needed items to complete order
	   * 		yes -> if old order complete -> start new order
	   * 		no -> error: current order not complete
	   */
	public void newOrder(ArrayList<Item> orderItems){
		if(isOrderComplete()){
			System.out.println("Started a new Order");
			listOfItemOrder = orderItems;
			return;
		}
		System.out.println("Order not complete Error: Can't start new order before finishing current order");
	}
	
	/**
	   * public method to see the number of Bins on the belt
	   * @return int: number of Bins on belt
	   */
	public int numOfBins(){return binList.size();}
	
	
	/**
	 * private method simulating a packer that removes bin from the belt
	 * void removes bin at packer station -> order shipped
	 * @void remove first bin of binList when it arrived at packer station
	 */
	private void doPacker(){
		if(binList.isEmpty()){System.out.println("Belt is empty");return;}
		List<Item> toRemoveBin = binList.getFirst();
		if(checkBin(toRemoveBin, packLocation)){
			System.out.println("Bin at Packer Station: Removes Bin/Order: ");
			binList.removeFirst();
			return;
		}
		System.out.println("Packer waiting for bin to arrive");
	}
	
	/**
	 * method simulating a picker that puts items in bin and on the belt
	 * void places bin/order on belt
	 * @void adds a bin/order to the end of binList and advances to start of belt
	 */
	public void doPicker(){
		if(!binAvailable()){
			System.out.println("doPicker(I) Error: Belt full");
			return;
		}
		// Bin/Order is complete -> picker places bin/order on belt
		if(isOrderComplete() && (getOrder().size()>0)){
			System.out.println("Picker places complete bin/order on belt");
			moveBin(itemOrder, startBeltLoc());
			binList.addLast(new ArrayList<Item>(itemOrder));
			itemOrder.clear();
			System.out.println("Picker ready for newOrder(AryList<Item>) and AraddItemsToBin()");
		}
	}
	
	/**
	   * public method to see all items needed/left to complete current order
	   * @return ArrayList: List of Items for an order
	   */
	public ArrayList<Item> getOrder(){return itemOrder;}
	
	/**
	   * public method to see if the next order can start to be fulfilled
	   * @return boolean: true if current order is completed 
	   */
	public boolean isOrderComplete(){
		if(listOfItemOrder.isEmpty())return true;
		else return false;
		}

	/**
	   * public method to see if there is a bin spot available on belt
	   * @return boolean: true if belt capacity has not been reached yet
	   */
	  public boolean binAvailable() {
		  if(numOfBins() < beltCapacity) return true;
		  else return false;
	    }
	  
	 /**
	  	* public method to clear the belt
	  	* @void removes all bin/orders from the belt
	   	*/
	  public void clearBelt(){binList.clear();}
	
	/**
	   * the tick() method is where belt moving gets done;
	   * it will have to move any Bin or Parcel withing the Cell
	   * of a Belt area to the next Cell, and this has to be done
	   * on all Points of the beltarea in parallel (not coded yet here)
	   * 
	   * after moving the belt, tick() should check to see whether
	   * or not a Bin has arrived at the Packer - then doPacker() 
	   * should be called, which removes the Bin, creates a Parcel 
	   * and puts that Parcel on the belt (in more advanced versions,
	   * one Bin might make more than one Parcel, if Items are too 
	   * big to fit entirely into one Parcel). After the Parcel is
	   * in a Cell at the Packer, the belt will be stopped until some
	   * later tick when the Packer finishes the Parcel.
	   * 
	   * even fancier ideas are to give the Packer a queue of Bins
	   * and remove each Bin that arrives, taking some non-trivial
	   * number of ticks to make Parcels, returning them to the 
	   * beltarea when they are completed
	   * 
	   * and a really thorough Belt would simulate the shipping dock,
	   * collecting a lot of parcels and grouping them into a truck
	   *
	   */
	/**
	   * public tick belt, moves belt by one point,
	   *  places an complete order/bin on belt (picker station)
	   *  removes an order/ bin at packer station
	   * @return tick belt
	   */
	  public void tick() {
		  System.out.println("\n"+"----------Start: T I C K belt----------");
		  System.out.println("Order Complete?: " + isOrderComplete()); 
		  doPicker(); //places bin on belt (at picker station)
		  //------Advances all bins on belt by one point------
		  boolean flag;
			  for(List<Item> I: binList){
				  System.out.println("Order/Bin move one point" + Arrays.toString(I.toArray()));
				  flag = false;
				  if(checkBin(I,endBeltLoc())){
					  moveBin(I,packLocation);
					  continue;}
				  for(Point P: beltArea){
					 if(flag){moveBin(I,P); flag= false; break;}
					 if(checkBin(I,P)){flag = true;}
				  }
		  }
		//-------Belt done moving-----	 	 
		doPacker();	//removes bin from belt (at packer station)
		System.out.println("----------End: T I C K belt----------" + "\n"); 
	    }
	  }
