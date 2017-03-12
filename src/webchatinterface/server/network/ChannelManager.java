package webchatinterface.server.network;

import java.util.ArrayList;

public class ChannelManager
{
	private ArrayList<Channel> globalChannels;
	public Channel publicChannel;

	private ChannelManager()
	{
		this.globalChannels = new ArrayList<Channel>();
		this.publicChannel = this.newChannel("Public Channel", true, true);
	}

	public Channel newChannel(String channelTitle, boolean isMaster, boolean isPublic)
	{
		Channel channel = new Channel(channelTitle, isMaster, isPublic);
		this.globalChannels.add(channel);
		return channel;
	}

	public boolean closeChannel(Channel channel)
	{
		if(channel.isMaster())
			return false;
		if(!this.globalChannels.remove(channel))
			return false;

		channel.closeChannel();
		return true;
	}

	public Channel[] getGlobalChannels()
	{
		return this.globalChannels.toArray(new Channel[0]);
	}

	public int getGlobalChannelSize()
	{
		int size = 0;
		for(Channel channel : this.globalChannels)
			size += channel.getChannelSize();

		return size;
	}

	public static ChannelManager getInstance()
	{
		return InstanceHolder.INSTANCE;
	}

	private static class InstanceHolder
	{
		private static final ChannelManager INSTANCE = new ChannelManager();
	}
}
