package webchatinterface.server.network;

import util.KeyGenerator;
import webchatinterface.server.communication.WebChatServerInstance;

import java.util.ArrayList;

public class Channel
{
	private boolean ismaster;
	private String channelTitle;
	private String channelID;
	private boolean isPublic;
	private ArrayList<WebChatServerInstance> channelMembers;

	protected Channel(String title, boolean isMaster, boolean isPublic)
	{
		this.channelTitle = title;
		this.ismaster = isMaster;
		this.isPublic = isPublic;
		this.channelID = KeyGenerator.generateKey64(KeyGenerator.ALPHANUMERIC_MIXED_CASE);
		this.channelMembers = new ArrayList<WebChatServerInstance>();
	}

	public boolean addChannelMember(WebChatServerInstance newMember)
	{
		if(newMember == null)
			return false;
		if(this.channelMembers.contains(newMember))
			return false;

		return this.channelMembers.add(newMember);
	}

	public boolean removeChannelMember(WebChatServerInstance newMember)
	{
		return this.channelMembers.remove(newMember);
	}

	public WebChatServerInstance[] getChannelMembers()
	{
		return this.channelMembers.toArray(new WebChatServerInstance[0]);
	}

	protected void closeChannel()
	{
		this.channelMembers.clear();
	}

	public String getChannelTitle()
	{
		return this.channelTitle;
	}

	public String getChannelID()
	{
		return this.channelID;
	}

	public boolean isPublic()
	{
		return isPublic;
	}

	public boolean isMaster()
	{
		return this.ismaster;
	}

	public int getChannelSize()
	{
		return this.channelMembers.size();
	}

	public String toString()
	{
		return this.getChannelTitle() + "[" + this.getChannelID() + "] : " + (this.isPublic() ? "Public" : "Private") + " : " + this.channelMembers.size() + " Members";
	}
}