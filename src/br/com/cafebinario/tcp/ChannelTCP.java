package br.com.cafebinario.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import br.com.cafebinario.dispacher.Channel;
import br.com.cafebinario.dispacher.Notify;
import br.com.cafebinario.properties.AppProperties;

public class ChannelTCP implements Channel {

	private Socket socket;
	private InputStream is;
	private Notify notify;

	public ChannelTCP(Socket socket, InputStream is, Notify notify) {
		this.socket = socket;
		this.is = is;
		this.notify = notify;
	}

	@Override
	public void send(byte[] data) {
		try {
			notify.beginTransmition(data);
			socket.getOutputStream().write(data);
			notify.hasfinish(data);
		} catch (IOException e) {
			e.printStackTrace();
			notify.onError(-2);
		}

	}

	@Override
	public byte[] getAddress() {
		return socket.getInetAddress().getAddress();
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}

	@Override
	public byte[] getData() {
		byte[] data = new byte[Integer.parseInt(AppProperties.instanceOf().getProperty("BUFFER_SIZE"))];
		try {
			is.read(data);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			notify.onError(-3);
			return null;
		}
		
	}
}
