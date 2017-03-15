package util;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code DynamicQueue} class represents an abstract data type to store
  *objects. The data type is a singly linked list representing a queue. 
  *Objects enqueued are stored in nodes, linked to the next node as enqueued.
  *The DynamicQueue follows a first-in first-out sequence, FIFO. 
  *The class provides methods to enqueue, dequeue, and
  *remove all nodes, along with methods for determining the size of the queue.
  */

public class DynamicQueue<E1>
{
	/**The first node in the queue. If the queue is empty, the field is null.*/
	private Node<E1> first;
	
	/**The last node in the queue. If the queue is empty, the field is null.*/
	private Node<E1> last;
	
	/**The length of the queue.*/
	private int length;
	
	/**Builds a {@code ConsoleQueue} object. Constructs a queue of objects
	  *with initial size of zero.*/
	public DynamicQueue()
	{
		this.first = null;
		this.last = null;
		this.length = 0;
	}
	
	/**Method that returns true if the queue is empty, and false otherwise.
	  *@return true if the queue is empty, and returns false if the queue
	  *is not empty.*/
	public boolean isEmpty()
	{
		return this.length == 0;
	}
	
	/**Accessor method for the size of the queue.
	  *@return the size of the queue.*/
	public int size()
	{
		return this.length;
	}
	
	/**The dequeue() method returns the Object at the head of the queue, and
	  *removes the reference to the node from the queue.
	  *@return the Object at the head of the queue, or null if the queue is empty*/
	public synchronized E1 dequeue()
	{
		if(this.length == 0)
			return null;
		else if(this.length == 1)
		{
			this.length--;
			E1 obj = this.first.obj;
			this.first = null;
			this.last = null;
			
			return obj;
		}
		else
		{
			this.length--;
			E1 obj = this.first.obj;
			this.first = this.first.nextNode;
			return obj;
		}
	}
	
	/**The enqueue() method stores a new Object in the queue.
	  *@param obj The object to be enqueued*/
	public synchronized void enqueue(E1 obj)
	{
		Node<E1> newNode = new Node<E1>(obj);
		
		if(this.length == 0)
		{
			this.first = newNode;
			this.last = newNode;
			newNode.nextNode = null;
		}
		else
		{
			newNode.nextNode = this.last;
			this.last.nextNode = newNode;
			this.last = newNode;
		}
		
		this.length++;
	}
	
	/**Removes all Objects from the queue.*/
	public synchronized void removeAll()
	{
		this.first = null;
		this.last = null;

		this.length = 0;
	}
	
	/**The {@code Node} class represents an Object node used in a queue. Each node has a reference 
	  *to the next node in the queue and a reference to the Object in which this node represents.*/
	private class Node<E2>
	{
		/**The Object that this node represents in the queue.*/
		private E2 obj;
		
		/**The succeeding node in the queue.*/
		private Node<E2> nextNode;
		
		/**Builds a {@code Node} object. Constructs an Object node with a given
		  *Object and assigns the succeeding node to null.
		  *@param obj The object to be stored in this node
		  */
		public Node(E2 obj)
		{
			this.obj = obj;
			this.nextNode = null;
		}
	}
}