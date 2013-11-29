package br.com.cafebinario.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import br.com.cafebinario.dispacher.Channel;
import br.com.cafebinario.dispacher.IServer;
import br.com.cafebinario.dispacher.Notify;
import br.com.cafebinario.iso8583.config.LAYOUT_MASTER;
import br.com.cafebinario.iso8583.config.SUB_ELEMENTS;
import br.com.cafebinario.properties.AppProperties;

public class TCPToXmlServer implements IServer {
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

		TCPToXmlServer frm = new TCPToXmlServer();
		new Thread(frm).start();
		sc = new Scanner(System.in);
		while (!"exit".equals(exit.toLowerCase())) {
			exit = sc.next();
		}

		//System.out.println("[RECEIVER] TOTAL DE [PACOTES ENVIDOS:TAMANHO]:" + i
				+ ":" + totalSize);
	}

	private ServerSocket socket = null;

	public void init() throws IOException {

		socket = new ServerSocket(Integer.parseInt(AppProperties.instanceOf().getProperty("PORT")), 
								  Integer.parseInt(AppProperties.instanceOf().getProperty("BACKLOG")));

		//System.out.println("[RECEIVER]RECEIVE FILE DIR="
				+ AppProperties.instanceOf().getProperty("DIRECTORY"));
		//System.out.println("[RECEIVER]    LOCALADDRESS="
				+ socket.getLocalSocketAddress());
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

				Socket clientSocket = socket.accept();
				InputStream is = clientSocket.getInputStream();
				Channel channel = new ChannelTCP(clientSocket, is,
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
				DispacherToXmlTCP dispacher = new DispacherToXmlTCP(channel);

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