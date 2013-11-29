package br.com.cafebinario.tcp;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import br.com.cafebinario.dispacher.Notify;
import br.com.cafebinario.properties.AppProperties;

public class TCPGetXmlClient  implements Runnable {

	public static void main(String[] args){
		try {
			AppProperties.instanceOf().load();
			new TCPGetXmlClient(new Notify() {
				
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket socket;
	private InetAddress address;
	private FileInputStream fis;
	private Notify udpNotify;

	public TCPGetXmlClient(Notify udpNotify) {
		this.udpNotify = udpNotify;
	}

	public void finish() throws IOException {
		if (socket != null)
			socket.close();

		if (fis != null)
			fis.close();
	}

	public Thread init(String fileName) throws IOException {
		address = InetAddress.getByName(AppProperties.instanceOf().getProperty("ADDRESS"));
		socket = new Socket(address, Integer.parseInt(AppProperties.instanceOf().getProperty("PORT")));

		fis = new FileInputStream(AppProperties.instanceOf().getProperty("DIRECTORY") + fileName);

		//System.out.println("[SENDER]           SENDFILE="
				+ AppProperties.instanceOf().getProperty("DIRECTORY") + fileName);
		//System.out.println("[SENDER]       LOCALADDRESS="
				+ socket.getLocalAddress());
		//System.out.println("[SENDER]          LOCALPORT="
				+ socket.getLocalPort());
		//System.out.println("[SENDER]      REMOTEADDRESS="
				+ socket.getInetAddress());

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

				byte[] dataReceive = new byte[Integer.parseInt(AppProperties.instanceOf().getProperty("BUFFER_SIZE"))];

				udpNotify.beginTransmition(outBuf);
				socket.getOutputStream().write(outBuf);
				socket.getInputStream().read(dataReceive);

				//System.out.println(new String(dataReceive));

				totalSize += outBuf.length;
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