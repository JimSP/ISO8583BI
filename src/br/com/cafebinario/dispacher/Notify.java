package br.com.cafebinario.dispacher;

public interface Notify {
	public void beginTransmition(byte[] data);
	public void hasfinish(byte[] data);
	public void onError(int error);
}
