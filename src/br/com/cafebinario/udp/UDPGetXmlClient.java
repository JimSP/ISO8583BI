package br.com.cafebinario.udp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import br.com.cafebinario.dispacher.Notify;
import br.com.cafebinario.properties.AppProperties;

public class UDPGetXmlClient implements Runnable {
	
	
	public static void main(String[] args){
		try {
			AppProperties.instanceOf().load();
			new UDPGetXmlClient(new Notify() {
				
				@Override
				public void beginTransmition(byte[] data) {
					//System.out.println("beginTransmition-Length:" + data.length);
					
				}

				@Override
				public void hasfinish(byte[] data) {
					//System.out.println("hasfinish-Length:" + data.length);
					
				}

				@Override
				public void onError(int error) {
					//System.out.println("onError:" + error);
					
				}
			}).init("MSG_ISO.IPM");
		} catch (UnknownHostException | SocketException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private DatagramSocket socket;
	private InetAddress address;
	private FileInputStream fis;
	private Notify udpNotify;

	public UDPGetXmlClient(Notify udpNotify) {
		this.udpNotify = udpNotify;
	}

	public void finish() throws IOException {
		if (socket != null)
			socket.close();

		if (fis != null)
			fis.close();
	}

	public Thread init(String fileName) throws UnknownHostException,
			SocketException, FileNotFoundException {
		address = InetAddress.getByName("189.100.111.33");
				//AppProperties.instanceOf().getProperty("ADDRESS"));
		socket = new DatagramSocket();

		fis = new FileInputStream(AppProperties.instanceOf().getProperty("DIRECTORY") + fileName);

		//System.out.println("[SENDER]           SENDFILE="
				+ AppProperties.instanceOf().getProperty("DIRECTORY") + fileName);
		//System.out.println("[SENDER]       LOCALADDRESS="
				+ socket.getLocalAddress());
		//System.out.println("[SENDER]          LOCALPORT="
				+ socket.getLocalPort());
		//System.out.println("[SENDER]      REMOTEADDRESS="
				+ address);

		Thread th = new Thread(this);
		th.start();
		return th;

	}

	@Override
	public void run() {
		
		int error = 0;
		byte[] outBuf = null;
		int i = 0;
		long totalSize = 0;

		try {
			outBuf = new byte[Integer.parseInt(AppProperties.instanceOf().getProperty("BUFFER_SIZE"))];
			
			while (fis.read(outBuf) > 0) {

				DatagramPacket outPacket = new DatagramPacket(outBuf,
						outBuf.length, address, 6666);
						//Integer.parseInt(AppProperties.instanceOf().getProperty("PORT")));

				byte[] dataReceive = new byte[Integer.parseInt(AppProperties.instanceOf().getProperty("BUFFER_SIZE"))];

				DatagramPacket receivePacket = new DatagramPacket(dataReceive,
						dataReceive.length, address, Integer.parseInt(AppProperties.instanceOf().getProperty("SERVER_PORT")));

				udpNotify.beginTransmition(outBuf);
				socket.send(outPacket);
				socket.receive(receivePacket);

				//System.out.println(new String(dataReceive));

				totalSize += outPacket.getLength();
				i++;

				try {
					Thread.sleep(Integer.parseInt(AppProperties.instanceOf().getProperty("SLEEP")));
				} catch (InterruptedException e) {
					error = 2;
					e.printStackTrace();
					udpNotify.onError(error);
				}
			}

		} catch (IOException e) {
			error = 3;
			e.printStackTrace();
			udpNotify.onError(error);
		} finally {
			if (error == 0) {
				//System.out.println("[SENDER]             CODIGO=" + error);
				System.out
						.println("[SENDER]             STATUS=TRANSMICAO ENCERRADA COM SUCESSO.");
				//System.out.println("[SENDER]    PACOTES ENVIDOS=" + i);
				//System.out.println("[SENDER]     TAMANHO ENVIDO=" + totalSize);
			} else {
				System.out
						.println("[SENDER]            STATUS=TRANSMICAO ENCERRADA COM ERRO.");
				//System.out.println("[SENDER]    PACOTES ENVIDOS=" + i);
				//System.out.println("[SENDER]     TAMANHO ENVIDO=" + totalSize);
			}

			udpNotify.hasfinish(outBuf);
		}
	}
}
