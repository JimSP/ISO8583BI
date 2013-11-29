package br.com.cafebinario.udp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

import br.com.cafebinario.dispacher.IServer;
import br.com.cafebinario.dispacher.Notify;
import br.com.cafebinario.iso8583.config.LAYOUT_MASTER;
import br.com.cafebinario.iso8583.config.SUB_ELEMENTS;
import br.com.cafebinario.properties.AppProperties;

public class UDPToXmlServer implements IServer {
	private static volatile String exit = "";
	private static volatile long totalSize = 0;
	private static volatile int i = 0;

	private static Scanner sc;

	public static void main(String[] args) {
		AppProperties.instanceOf().load();
		try {
			LAYOUT_MASTER.instanceOf().load();
			SUB_ELEMENTS.instanceOf().load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		UDPToXmlServer frm = new UDPToXmlServer();
		new Thread(frm).start();
		sc = new Scanner(System.in);
		while (!"exit".equals(exit.toLowerCase())) {
			exit = sc.next();
		}

		//System.out.println("[RECEIVER] TOTAL DE [PACOTES ENVIDOS:TAMANHO]:" + i
				+ ":" + totalSize);
	}

	private DatagramSocket socket = null;

	public void init() throws SocketException, FileNotFoundException {

		socket = new DatagramSocket(Integer.parseInt(AppProperties.instanceOf()
				.getProperty("PORT")));

		//System.out.println("[RECEIVER]RECEIVE FILE DIR="
				+ AppProperties.instanceOf().getProperty("DIRECTORY"));
		//System.out.println("[RECEIVER]    LOCALADDRESS="
				+ socket.getLocalAddress());
		//System.out.println("[RECEIVER]       LOCALPORT="
				+ socket.getLocalPort());
		//System.out.println("[RECEIVER]      BUFFERSIZE="
				+ AppProperties.instanceOf().getProperty("BUFFER_SIZE"));
	}

	public void finish() throws IOException {

		if (socket != null)
			socket.close();

		if (sc != null)
			sc.close();
	}

	@Override
	public void run() {

		try {
			init();

			while (!"exit".equals(exit.toLowerCase())) {
				final byte[] inBuf = new byte[Integer.parseInt(AppProperties
						.instanceOf().getProperty("BUFFER_SIZE"))];
				final DatagramPacket inPacket = new DatagramPacket(inBuf,
						inBuf.length);

				socket.receive(inPacket);
				totalSize += inPacket.getLength();

				ChannelUDP channel = new ChannelUDP(socket, inPacket,
						new Notify() {

							@Override
							public void onError(int error) {
								//System.out.println(error);

							}

							@Override
							public void hasfinish(byte[] data) {
								//System.out.println("hasfinish-Length:"
										+ data.length);

							}

							@Override
							public void beginTransmition(byte[] data) {
								//System.out.println("beginTransmition-Length:"
										+ data.length);

							}
						});
				DispacherToXmlUDP dispacher = new DispacherToXmlUDP(channel);

				new Thread(dispacher).start();
				i++;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				finish();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
