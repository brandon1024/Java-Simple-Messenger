package webchatinterface.server.util;

import java.util.ArrayList;
import java.util.Iterator;

import webchatinterface.server.WebChatServerInstance;
import webchatinterface.util.Command;

/**@author Brandon Richardson
  *@version 1.4.3
  *@since 06/05/2016
  *<p>
  *The {@code ChatRoom} class represents a group of WebChatServerInstances who
  *are able to communicate privately among themselves. The ChatRoom class helps
  *facilitate private and public chatrooms, and aids in manageing these rooms
  *and members, both locally and globally.*/

public class ChatRoom
{
	/**A reference to the main public chatroom in which users are members upon
	  *connecting to the WebChatServer.*/
	public static ChatRoom publicRoom;
	
	/**An ArrayList representing a list of all client-server connection instances
	  *within all public and private chatrooms.*/
	private static ArrayList<WebChatServerInstance> membersGlobal;
	
	/**An ArrayList representing a list of all public and private chatrooms.*/
	private static ArrayList<ChatRoom> roomsGlobal;
	
	/**An ArrayList representing a list of local client-server connection instances
	  *within this chatroom.*/
	private ArrayList<WebChatServerInstance> members;
	
	/**Boolean variable representing whether the chatroom is public or private.*/
	private final boolean isPublic;
	
	/**Static initialization block for static class members.*/
	static
	{
		ChatRoom.membersGlobal = new ArrayList<WebChatServerInstance>();
		ChatRoom.roomsGlobal = new ArrayList<ChatRoom>();
		ChatRoom.publicRoom = new ChatRoom(true);
	}
	
	/**Constructs a new private ChatRoom object with a member size of zero. This object is then 
	  *added to the list of all public and private chatrooms.*/
	public ChatRoom()
	{
		this.members = new ArrayList<WebChatServerInstance>();
		this.isPublic = false;

		ChatRoom.roomsGlobal.add(this);
	}
	
	/**Constructs a new public ChatRoom object with a member size of zero. This object is then 
	  *added to the list of all public and private chatrooms. The private visibility of this
	  *constructor prevents outside classes from implementing public ChatRoom instances. Only
	  *a single public chatroom is permitted.
	  *@param isPublic If true, the chatroom is public. If false, the chatroom is private.*/
	private ChatRoom(boolean isPublic)
	{
		this.members = new ArrayList<WebChatServerInstance>();
		this.isPublic = isPublic;
		
		ChatRoom.roomsGlobal.add(this);
	}
	
	/**Adds a WebChatServerInstance to the chatroom represented by this ChatRoom object, and
	  *updates the list of global WebChatServerInstances.
	  *@param client The instance to add to the chatroom and the global list of users*/
	public void addMember(WebChatServerInstance client)
	{
		this.members.add(client);
		ChatRoom.membersGlobal.add(client);
	}
	
	/**Tests whether a given WebChatServerInstance is a member of the chatroom represented
	  *by this ChatRoom instance.
	  *@param client The client-server instance to compare to the list of members in the
	  *chatroom.
	  *@return true if the client-server instance is in the chatroom represented by this
	  *ChatRoom object*/
	public boolean isMember(WebChatServerInstance client)
	{
		Iterator<WebChatServerInstance> iterator = this.members.iterator();
		while(iterator.hasNext())
		{
			WebChatServerInstance member = iterator.next();
			if(client.equals(member))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**Remove a member from the chatroom. If the size of the chatroom following the removal
	  *of the client-server instance is zero, the closeRoom() method is invoked.
	  *@param client The client-server instance to remove from the chatroom.*/
	public void removeMember(WebChatServerInstance client)
	{
		this.members.remove(client);
		
		ChatRoom.removeMemberGlobal(client);
		
		if(this.size() == 0 && !this.isPublic)
		{
			this.closeRoom();
		}
	}
	
	/**Closes the chatroom by removing the reference to it from the list of chatrooms.
	  *If the room is public, this call to the clsoeRoom() method is simply ignored.*/
	public void closeRoom()
	{
		if(!this.isPublic)
		{
			ChatRoom.roomsGlobal.remove(this);
		}
	}
	
	/**Removes and disconnects all client-server instances connected to this chatroom.
	  *Once all the members are disconnected, the chatroom is closed by invoking the
	  *closeRoom() method.*/
	public void disconnectClients()
	{
		Iterator<WebChatServerInstance> iterator = this.members.iterator();
		while(iterator.hasNext())
		{
			WebChatServerInstance member = iterator.next();
			this.removeMember(member);
			ChatRoom.removeMemberGlobal(member);
			member.disconnect(Command.REASON_ROOM_CLOSED);
		}
		
		this.closeRoom();
	}
	
	/**Disconnects a single member from the chatroom. The member is removed from the
	  *global list of connected clients and the local chatroom member list.
	  *@param member The client-server instance to disconnect from the server.*/
	public void disconnectMember(WebChatServerInstance member)
	{
		ChatRoom.removeMemberGlobal(member);
		member.disconnect(Command.REASON_KICKED);
		this.removeMember(member);
	}
	
	/**Accessor method for an array representing a list of users connected to the chatroom.
	  *The returned array will be "safe" in that no references to it are maintained by this list. 
	  *(In other words, this method must allocate a new array). The caller is thus free to modify the returned array.
	  *@return an array representing a list of users connected to the chatroom*/
	public WebChatServerInstance[] getConnectedClients()
	{
		return this.members.toArray(new WebChatServerInstance[this.members.size()]);
	}
	
	/**Accessor method for the current size of the ChatRoom (the number of members within the
	  *chatroom).
	  *@return the numeber of clients in the chatroom.*/
	public int size()
	{
		return this.members.size();
	}
	
	/**Accessor method for the ChatRoom signature; whether it is public or private.
	  *Private chatrooms typically have up to two members, while public chatrooms
	  *have an unlimited capacity.
	  *@return true of the chatroom is public, false if the chatroom is private.*/
	public boolean isPublic()
	{
		return this.isPublic;
	}
	
	/**Returns a string representation of this chatroom object. The string contains the
	  *privacy of the chatroom, and the number of members.
	  *@return a textual representation of this chatroom object*/
	@Override
	public String toString()
	{
		return this.getRoomTitle() + " : " + this.members.size() + " members";
	}
	
	/**Returns the title of the chatroom. Simply returns the privacy of the room.
	  *If the isPublic field is true, returns "Public Chatroom". Else, returns
	  *"Private Chatroom".
	  *@return the title of this chatroom.*/
	public String getRoomTitle()
	{
		if(this.isPublic)
		{
			return new String("Public Chatroom");
		}
		else
		{
			return new String("Private Chatroom");
		}
	}
	
	/**Accessor method for an array representing a global list of users connected to the server.
	  *The returned array will be "safe" in that no references to it are maintained by this list. 
	  *(In other words, this method must allocate a new array). The caller is thus free to modify the returned array.
	  *@return an array representing a global list of users connected to the server*/
	public static WebChatServerInstance[] getGlobalMembers()
	{
		return ChatRoom.membersGlobal.toArray(new WebChatServerInstance[ChatRoom.membersGlobal.size()]);
	}
	
	/**Accessor method for an array representing a list of all public and private chatrooms.
	  *The returned array will be "safe" in that no references to it are maintained by this list. 
	  *(In other words, this method must allocate a new array). The caller is thus free to modify the returned array.
	  *@return an array representing a list of all public and private chatrooms*/
	public static ChatRoom[] getGlobalRooms()
	{
		return ChatRoom.roomsGlobal.toArray(new ChatRoom[ChatRoom.roomsGlobal.size()]);
	}
	
	/**Accessor method for the numebr of members in all chatrooms.
	  *@return the numeber of clients connected to the server*/
	public static int getGlobalMembersSize()
	{
		return ChatRoom.membersGlobal.size();
	}
	
	/**Accessor method for the number of chatrooms currently open.
	  *@return the number of chatrooms open.*/
	public static int getGlobalRoomsSize()
	{
		return ChatRoom.roomsGlobal.size();
	}
	
	/**Remove a client-server isnatnce from the global list of users connected to the server/
	 *@param member The client-server instance to remove from the list of instances.*/
	private static void removeMemberGlobal(WebChatServerInstance member)
	{
		ChatRoom.membersGlobal.remove(member);
	}
}
