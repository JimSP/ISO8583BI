package br.com.cafebinario.dispacher;

import java.io.IOException;

public interface IServer extends Runnable{

	public void init() throws IOException;
	public void finish() throws IOException;

}
