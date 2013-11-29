package br.com.cafebinario.dispacher;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;

import br.com.cafebinario.iso8583.ConfigInfo;
import br.com.cafebinario.iso8583.Iso8583ParseIPMFile;
import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.xml.XmlParse;

public abstract class DispacherBase implements Runnable {

	protected Channel channel;
	
	private String getFileName(){
		String fileName = AppProperties.instanceOf().getProperty("DIRECTORY")
				+ System.currentTimeMillis() + "_"
				+ new BigInteger(channel.getAddress()).toString(10) + "_"
				+ channel.getPort() + ".xml";
		return fileName;
	}
	
	private void createFile(String fileName, byte[] data) throws IOException{
		PrintStream w = new PrintStream(fileName);
		w.write(data);
		w.close();
	}
	
	private byte[] trin(byte[] data) throws IOException {
		return new String(data).trim().getBytes();
	}

	protected byte[] toIso8583(byte[] xml){
		String fileName = getFileName();
		
		try{
			createFile(fileName, xml);
			XmlParse parse = new XmlParse();
			return parse.processFile(fileName);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	protected byte[] toXml(byte[] data) {
		
		String fileName = getFileName();
		try {
			createFile(fileName, data);

			br.com.cafebinario.iso8583.Iso8583ParseIPMFile parse = new Iso8583ParseIPMFile();
			ConfigInfo info = new ConfigInfo();

			info.setPrintName(true);
			info.setPrintLength(true);
			info.setPrintType(true);
			info.setPrintMask(true);
			info.setPrintMetaDataLink(true);
			info.setPrintData(true);
			info.setPrintSubFieldName(true);
			info.setPrintSubFieldMetaDataLink(true);
			info.setPrintSubFieldTag(true);
			info.setPrintSubFieldLength(true);
			info.setPrintSubFieldData(true);
			info.setPrintMSG(false);
			info.setPrintSql(false);
			info.setPrintRelat(true);
			info.setPrintPagina(true);
			info.setTipoArquivo("XML");
			info.setFull_path(fileName);

			PrintStream psLog = new PrintStream(fileName + ".log");
			PrintStream psXml = new PrintStream(fileName + ".xml");

			parse.setOutRelat(psLog);// DEBUG
			parse.setOutPagina(psXml);// FORMAT
			parse.setIn(new RandomAccessFile(fileName, "rw"));// ISO

			parse.processFile(info);
			byte[] xmlFile = new byte[Integer.parseInt(AppProperties
					.instanceOf().getProperty("BUFFER_SIZE"))];
			FileInputStream fis = new FileInputStream(fileName + ".xml");

			while (fis.read(xmlFile) > 0);

			psLog.flush();
			psLog.close();

			psXml.flush();
			psXml.close();

			fis.close();
			return xmlFile;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

}