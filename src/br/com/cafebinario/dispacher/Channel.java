package br.com.cafebinario.dispacher;

public interface Channel {
	public void send(byte[] data);
	public byte[] getAddress();
	public int getPort();
	byte[] getData();
}
