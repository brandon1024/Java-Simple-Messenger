package webchatinterface.server.network;

import webchatinterface.server.communication.WebChatServerInstance;
import webchatinterface.util.Command;

import java.util.ArrayList;
import java.util.Iterator;

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
	public static ChatRoom publicRoom;
	private static ArrayList<WebChatServerInstance> membersGlobal;
	private static ArrayList<ChatRoom> roomsGlobal;
	private ArrayList<WebChatServerInstance> members;
	private final boolean isPublic;
	
	static
	{
		ChatRoom.membersGlobal = new ArrayList<WebChatServerInstance>();
		ChatRoom.roomsGlobal = new ArrayList<ChatRoom>();
		ChatRoom.publicRoom = new ChatRoom(true);
	}
	
	public ChatRoom()
	{
		this.members = new ArrayList<WebChatServerInstance>();
		this.isPublic = false;

		ChatRoom.roomsGlobal.add(this);
	}
	
	private ChatRoom(boolean isPublic)
	{
		this.members = new ArrayList<WebChatServerInstance>();
		this.isPublic = isPublic;
		
		ChatRoom.roomsGlobal.add(this);
	}
	
	public void addMember(WebChatServerInstance client)
	{
		this.members.add(client);
		ChatRoom.membersGlobal.add(client);
	}
	
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
	
	public void removeMember(WebChatServerInstance client)
	{
		this.members.remove(client);
		
		ChatRoom.removeMemberGlobal(client);
		
		if(this.size() == 0 && !this.isPublic)
		{
			this.closeRoom();
		}
	}
	
	public void closeRoom()
	{
		if(!this.isPublic)
		{
			ChatRoom.roomsGlobal.remove(this);
		}
	}
	
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
	
	public void disconnectMember(WebChatServerInstance member)
	{
		ChatRoom.removeMemberGlobal(member);
		member.disconnect(Command.REASON_KICKED);
		this.removeMember(member);
	}
	
	public WebChatServerInstance[] getConnectedClients()
	{
		return this.members.toArray(new WebChatServerInstance[this.members.size()]);
	}
	
	public int size()
	{
		return this.members.size();
	}
	
	public boolean isPublic()
	{
		return this.isPublic;
	}
	
	public String toString()
	{
		return this.getRoomTitle() + " : " + this.members.size() + " members";
	}
	
	private String getRoomTitle()
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
	
	public static WebChatServerInstance[] getGlobalMembers()
	{
		return ChatRoom.membersGlobal.toArray(new WebChatServerInstance[ChatRoom.membersGlobal.size()]);
	}
	
	public static ChatRoom[] getGlobalRooms()
	{
		return ChatRoom.roomsGlobal.toArray(new ChatRoom[ChatRoom.roomsGlobal.size()]);
	}
	
	public static int getGlobalMembersSize()
	{
		return ChatRoom.membersGlobal.size();
	}
	
	public static int getGlobalRoomsSize()
	{
		return ChatRoom.roomsGlobal.size();
	}
	
	private static void removeMemberGlobal(WebChatServerInstance member)
	{
		ChatRoom.membersGlobal.remove(member);
	}
}
