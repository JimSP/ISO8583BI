package br.com.cafebinario.tcp;

import br.com.cafebinario.dispacher.Channel;
import br.com.cafebinario.dispacher.DispacherBase;

public class DispacherToXmlTCP extends DispacherBase {

	public DispacherToXmlTCP(Channel channel){
		this.channel = channel;
	}
	
	public void run() {
		try {
			channel.send(toXml(channel.getData()));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
