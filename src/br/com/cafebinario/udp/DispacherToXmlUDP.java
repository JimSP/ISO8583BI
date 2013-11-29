package br.com.cafebinario.udp;

import br.com.cafebinario.dispacher.DispacherBase;

public class DispacherToXmlUDP extends DispacherBase {

	public DispacherToXmlUDP(ChannelUDP channel) {
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
