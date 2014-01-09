import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.thoughtworks.xstream.XStream;


public class ManualUploader {
	public static void main(String[] args) throws Exception {
		int max = 610;
		
		for (int i = 0; i <= max; i++) {
			String file = "C:/Users/rkhan/Desktop/temp/Quran/my-project/quran-pages/quran-page-"
					+ i + ".jpg";
			
			String notifyAdmin = (i == max) ? "true" :  "false";
			
			postToJBrown(file, notifyAdmin);
		}
	}

	public static void postToJBrown(String fileName, String notifyAdmin)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://happy.javabrown.com/user/services/ftp/jpush.php");

		MultipartEntity entity = new MultipartEntity();
		File file = new File(fileName);
		entity.addPart("notify", new StringBody(notifyAdmin));
		entity.addPart("auth_code", new StringBody("rkonlineauth"));
		entity.addPart("ufile", new FileBody(file));
		post.setEntity(entity);

		HttpResponse response = client.execute(post);
		System.out.println(response.toString());
	}
}



class Base64Encoder {

	public static void start(String args[]) throws IOException {
		List<image> list = new ArrayList<image>();

		for (int i = 0; i <= 610; i++) {
			File imageFile = new File(
					"C:/Users/rkhan/Desktop/temp/Quran/my-project/quran-pages/quran-page-"
							+ i + ".jpg");
			String encodedData = encodeImage(imageFile);
			list.add(new image(imageFile.getName(), encodedData));
		}

		FileWriter writer = new FileWriter(
				"C:/Users/rkhan/Desktop/temp/Quran/my-project/encoded-quran.xml");
		writer.write(new XStream().toXML(list));
		writer.close();
		System.out.println("done");
	}

	private static String encodeImage(File imageFile) throws IOException {
		BufferedInputStream in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(imageFile));

			String imageType = "jpg";
			String dataCode = "data:image/" + imageType + ";base64,";
			
			//InputStream compressed = in;
			InputStream compressed = compress(imageFile, -1);
			String encodedData = dataCode + getEncodedData(compressed);

			return encodedData;

			// out.flush();
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	private static InputStream compress(File file,
			float quality) throws IOException {
		BufferedImage image = ImageIO.read(file); 
		quality = (quality <= 0) ? 0.5f : quality;
		
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
		if (!writers.hasNext())
			throw new IllegalStateException("No writers found");
		ImageWriter writer = (ImageWriter) writers.next();
		// Create the ImageWriteParam to compress the image.
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		// The output will be a ByteArrayOutputStream (in memory)
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush(); // otherwise the buffer size will be zero!
		// From the ByteArrayOutputStream create a RenderedImage.
		ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
		// RenderedImage out = ImageIO.read(in);
		int size = bos.toByteArray().length;
		System.out.println("Compressed to " + quality + ": " + size + " bytes");
		return in;
	}

	private static String getEncodedData(InputStream in) throws IOException {
		int lineLength = 72;
		byte[] buf = new byte[lineLength / 4 * 3];
		StringBuffer buffer = new StringBuffer();
		while (true) {
			int len = in.read(buf);
			if (len <= 0)
				break;
			buffer.append(Base64Coder.encode(buf, 0, len));
			// out.newLine();
		}
		return buffer.toString();
	}

	private static void encodeStream(InputStream in, BufferedWriter out)
			throws IOException {
		int lineLength = 72;
		byte[] buf = new byte[lineLength / 4 * 3];
		while (true) {
			int len = in.read(buf);
			if (len <= 0)
				break;
			out.write(Base64Coder.encode(buf, 0, len));
			out.newLine();
		}
	}

	static String encodeArray(byte[] in) throws IOException {
		StringBuffer out = new StringBuffer();
		out.append(Base64Coder.encode(in, 0, in.length));
		return out.toString();
	}

	static byte[] decodeArray(String in) throws IOException {
		byte[] buf = Base64Coder.decodeLines(in);
		return buf;
	}

	private static void decodeFile(File inputFile, File outputFile)
			throws IOException {
		BufferedReader in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedReader(new FileReader(inputFile));
			out = new BufferedOutputStream(new FileOutputStream(outputFile));
			decodeStream(in, out);
			out.flush();
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	private static void decodeStream(BufferedReader in, OutputStream out)
			throws IOException {
		while (true) {
			String s = in.readLine();
			if (s == null)
				break;
			byte[] buf = Base64Coder.decodeLines(s);
			out.write(buf);
		}
	}
}

class Base64Coder {

	// The line separator string of the operating system.
	private static final String systemLineSeparator = System
			.getProperty("line.separator");

	// Mapping table from 6-bit nibbles to Base64 characters.
	private static final char[] map1 = new char[64];
	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++)
			map1[i++] = c;
		for (char c = 'a'; c <= 'z'; c++)
			map1[i++] = c;
		for (char c = '0'; c <= '9'; c++)
			map1[i++] = c;
		map1[i++] = '+';
		map1[i++] = '/';
	}

	// Mapping table from Base64 characters to 6-bit nibbles.
	private static final byte[] map2 = new byte[128];
	static {
		for (int i = 0; i < map2.length; i++)
			map2[i] = -1;
		for (int i = 0; i < 64; i++)
			map2[map1[i]] = (byte) i;
	}

	/**
	 * Encodes a string into Base64 format. No blanks or line breaks are
	 * inserted.
	 * 
	 * @param s
	 *            A String to be encoded.
	 * @return A String containing the Base64 encoded data.
	 */
	public static String encodeString(String s) {
		return new String(encode(s.getBytes()));
	}

	/**
	 * Encodes a byte array into Base 64 format and breaks the output into lines
	 * of 76 characters. This method is compatible with
	 * <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
	 * 
	 * @param in
	 *            An array containing the data bytes to be encoded.
	 * @return A String containing the Base64 encoded data, broken into lines.
	 */
	public static String encodeLines(byte[] in) {
		return encodeLines(in, 0, in.length, 76, systemLineSeparator);
	}

	/**
	 * Encodes a byte array into Base 64 format and breaks the output into
	 * lines.
	 * 
	 * @param in
	 *            An array containing the data bytes to be encoded.
	 * @param iOff
	 *            Offset of the first byte in <code>in</code> to be processed.
	 * @param iLen
	 *            Number of bytes to be processed in <code>in</code>, starting
	 *            at <code>iOff</code>.
	 * @param lineLen
	 *            Line length for the output data. Should be a multiple of 4.
	 * @param lineSeparator
	 *            The line separator to be used to separate the output lines.
	 * @return A String containing the Base64 encoded data, broken into lines.
	 */
	public static String encodeLines(byte[] in, int iOff, int iLen,
			int lineLen, String lineSeparator) {
		int blockLen = (lineLen * 3) / 4;
		if (blockLen <= 0)
			throw new IllegalArgumentException();
		int lines = (iLen + blockLen - 1) / blockLen;
		int bufLen = ((iLen + 2) / 3) * 4 + lines * lineSeparator.length();
		StringBuilder buf = new StringBuilder(bufLen);
		int ip = 0;
		while (ip < iLen) {
			int l = Math.min(iLen - ip, blockLen);
			buf.append(encode(in, iOff + ip, l));
			buf.append(lineSeparator);
			ip += l;
		}
		return buf.toString();
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are
	 * inserted in the output.
	 * 
	 * @param in
	 *            An array containing the data bytes to be encoded.
	 * @return A character array containing the Base64 encoded data.
	 */
	public static char[] encode(byte[] in) {
		return encode(in, 0, in.length);
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are
	 * inserted in the output.
	 * 
	 * @param in
	 *            An array containing the data bytes to be encoded.
	 * @param iLen
	 *            Number of bytes to process in <code>in</code>.
	 * @return A character array containing the Base64 encoded data.
	 */
	public static char[] encode(byte[] in, int iLen) {
		return encode(in, 0, iLen);
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are
	 * inserted in the output.
	 * 
	 * @param in
	 *            An array containing the data bytes to be encoded.
	 * @param iOff
	 *            Offset of the first byte in <code>in</code> to be processed.
	 * @param iLen
	 *            Number of bytes to process in <code>in</code>, starting at
	 *            <code>iOff</code>.
	 * @return A character array containing the Base64 encoded data.
	 */
	public static char[] encode(byte[] in, int iOff, int iLen) {
		int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
		int oLen = ((iLen + 2) / 3) * 4; // output length including padding
		char[] out = new char[oLen];
		int ip = iOff;
		int iEnd = iOff + iLen;
		int op = 0;
		while (ip < iEnd) {
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
			int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
		}
		return out;
	}

	/**
	 * Decodes a string from Base64 format. No blanks or line breaks are allowed
	 * within the Base64 encoded input data.
	 * 
	 * @param s
	 *            A Base64 String to be decoded.
	 * @return A String containing the decoded data.
	 * @throws IllegalArgumentException
	 *             If the input is not valid Base64 encoded data.
	 */
	public static String decodeString(String s) {
		return new String(decode(s));
	}

	/**
	 * Decodes a byte array from Base64 format and ignores line separators, tabs
	 * and blanks. CR, LF, Tab and Space characters are ignored in the input
	 * data. This method is compatible with
	 * <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
	 * 
	 * @param s
	 *            A Base64 String to be decoded.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException
	 *             If the input is not valid Base64 encoded data.
	 */
	public static byte[] decodeLines(String s) {
		char[] buf = new char[s.length()];
		int p = 0;
		for (int ip = 0; ip < s.length(); ip++) {
			char c = s.charAt(ip);
			if (c != ' ' && c != '\r' && c != '\n' && c != '\t')
				buf[p++] = c;
		}
		return decode(buf, 0, p);
	}

	/**
	 * Decodes a byte array from Base64 format. No blanks or line breaks are
	 * allowed within the Base64 encoded input data.
	 * 
	 * @param s
	 *            A Base64 String to be decoded.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException
	 *             If the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(String s) {
		return decode(s.toCharArray());
	}

	/**
	 * Decodes a byte array from Base64 format. No blanks or line breaks are
	 * allowed within the Base64 encoded input data.
	 * 
	 * @param in
	 *            A character array containing the Base64 encoded data.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException
	 *             If the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(char[] in) {
		return decode(in, 0, in.length);
	}

	/**
	 * Decodes a byte array from Base64 format. No blanks or line breaks are
	 * allowed within the Base64 encoded input data.
	 * 
	 * @param in
	 *            A character array containing the Base64 encoded data.
	 * @param iOff
	 *            Offset of the first character in <code>in</code> to be
	 *            processed.
	 * @param iLen
	 *            Number of characters to process in <code>in</code>, starting
	 *            at <code>iOff</code>.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException
	 *             If the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(char[] in, int iOff, int iLen) {
		if (iLen % 4 != 0)
			throw new IllegalArgumentException(
					"Length of Base64 encoded input string is not a multiple of 4.");
		while (iLen > 0 && in[iOff + iLen - 1] == '=')
			iLen--;
		int oLen = (iLen * 3) / 4;
		byte[] out = new byte[oLen];
		int ip = iOff;
		int iEnd = iOff + iLen;
		int op = 0;
		while (ip < iEnd) {
			int i0 = in[ip++];
			int i1 = in[ip++];
			int i2 = ip < iEnd ? in[ip++] : 'A';
			int i3 = ip < iEnd ? in[ip++] : 'A';
			if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
				throw new IllegalArgumentException(
						"Illegal character in Base64 encoded data.");
			int b0 = map2[i0];
			int b1 = map2[i1];
			int b2 = map2[i2];
			int b3 = map2[i3];
			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
				throw new IllegalArgumentException(
						"Illegal character in Base64 encoded data.");
			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;
			out[op++] = (byte) o0;
			if (op < oLen)
				out[op++] = (byte) o1;
			if (op < oLen)
				out[op++] = (byte) o2;
		}
		return out;
	}

	// Dummy constructor.
	private Base64Coder() {
	}

} // end class Base64Coder

class image {
	String name;
	String data;

	public image(String name, String data) {
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}
}



class ImageFetcher 
{	
    public static void fetch() throws InterruptedException   
    {
    	
    	int newFileIndex = 0;
    	
    	for(int para =1; para < 50; para++){
    	 for(int page=1 ; page <50; page++){
    		String fileIndex = page+".jpg";
    		if(page < 10){
    			fileIndex="0"+page+".jpg";
    		}
    		
    		String paraIndex = "para"+para;
    		if(para < 10){
    			paraIndex ="para0"+para;
    		}
    		//http://www.equranacademy.com/images/stories/paraIndex+"/"+fileIndex
    		String urlStr = "http://www.equranacademy.com/images/stories/"+paraIndex+"/"+fileIndex;
    		
	    	BufferedImage image = null;
	        try {
	        	System.out.print("Receiving - " + urlStr);
	            URL url = new URL(urlStr);
	            image = ImageIO.read(url);
	 
	            ImageIO.write(image, "jpg",new File("C:\\tmp\\ii\\quran-page-"+newFileIndex+".jpg"));
	            newFileIndex++;
	            //ImageIO.write(image, "gif",new File("C:\\out.gif"));
	            //ImageIO.write(image, "png",new File("C:\\out.png"));
	            System.out.println(".\t\t**Received success**");
	        } catch (IOException e) {
	        	System.err.println("Failed - "+urlStr);
	        	//e.printStackTrace();
	        }
	        
	        //Thread.sleep(2000);
    	}
       }
        System.out.println("Done");
    }
    
	private static InputStream compress(BufferedImage image) throws IOException {
		float quality =  0.5f;
		
		Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpeg");
		if (!writers.hasNext())
			throw new IllegalStateException("No writers found");
		ImageWriter writer = (ImageWriter) writers.next();
		// Create the ImageWriteParam to compress the image.
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(quality);
		// The output will be a ByteArrayOutputStream (in memory)
		ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
		ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), param);
		ios.flush(); // otherwise the buffer size will be zero!
		// From the ByteArrayOutputStream create a RenderedImage.
		ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
		// RenderedImage out = ImageIO.read(in);
		int size = bos.toByteArray().length;
		System.out.println("Compressed to " + quality + ": " + size + " bytes");
		return in;
	}    
}
