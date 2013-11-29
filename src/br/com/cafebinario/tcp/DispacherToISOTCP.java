package br.com.cafebinario.tcp;

import br.com.cafebinario.dispacher.DispacherBase;

public class DispacherToISOTCP extends DispacherBase {

	public DispacherToISOTCP(ChannelTCP channel) {
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
