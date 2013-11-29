package br.com.cafebinario.udp;

import br.com.cafebinario.dispacher.DispacherBase;

public class DispacherToISOUDP extends DispacherBase {
	public DispacherToISOUDP(ChannelUDP channel) {
		this.channel = channel;
	}
	
	public void run() {
		try {
			channel.send(toIso8583(channel.getData()));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
