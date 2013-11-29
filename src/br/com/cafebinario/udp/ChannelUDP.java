package br.com.cafebinario.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import br.com.cafebinario.dispacher.Channel;
import br.com.cafebinario.dispacher.Notify;


public class ChannelUDP implements Channel {
	
	protected DatagramSocket socket;
	protected DatagramPacket packet;
	protected Notify udpNotify;
	
	public ChannelUDP(DatagramSocket socket, DatagramPacket packet, Notify udpNotify){		
		this.socket = socket;
		this.packet = packet;
		this.udpNotify = udpNotify;
	}

	@Override
	public void send(byte[] data) {
		try {
			udpNotify.beginTransmition(data);
			packet.setData(data);
			socket.send(packet);
			udpNotify.hasfinish(data);
		} catch (IOException e) {
			e.printStackTrace();
			udpNotify.onError(-1);
		}
	}

	@Override
	public byte[] getAddress() {
		return packet.getAddress().getAddress();
	}

	@Override
	public int getPort() {
		return packet.getPort();
	}
	
	@Override
	public byte[] getData(){
		return packet.getData();
	}
}