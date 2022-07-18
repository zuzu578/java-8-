package egovframework.api.admin.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	/**
	 * 암호화
	 *
	 * @param cleartext the cleartext
	 * @param seed the seed
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String encrypt(String cleartext, String seed) throws Exception {
		return Encrypt(cleartext, seed);
	}

	/**
	 * 복호화
	 *
	 * @param encrypted the encrypted
	 * @param seed the seed
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String decrypt(String encrypted, String seed) throws Exception {
		return Decrypt(encrypted, seed);
	}

	/**
	 * Gets the raw key.
	 *
	 * @param seed the seed
	 * @return the raw key
	 * @throws Exception the exception
	 */
	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}


	/**
	 * 암호화
	 *
	 * @param raw the raw
	 * @param clear the clear
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	/**
	 * 복호화
	 *
	 * @param raw the raw
	 * @param encrypted the encrypted
	 * @return the byte[]
	 * @throws Exception the exception
	 */
	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	/**
	 * Hex로 변환
	 *
	 * @param txt the txt
	 * @return the string
	 */
	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	/**
	 * Hex로 부터 새로운 String 변환
	 *
	 * @param hex the hex
	 * @return the string
	 */
	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	/**
	 * Hex 스트링을 바이트배열로 변환
	 *
	 * @param hexString the hex string
	 * @return the byte[]
	 */
	public static byte[] toByte(String hexString) {
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}

	/**
	 * 바이트버퍼로 부터 스트링 Hex 형태로 변환
	 *
	 * @param buf the buf
	 * @return the string
	 */
	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	/** The Constant HEX. */
	private final static String HEX = "0123456789ABCDEF";

	/**
	 * hex Append
	 *
	 * @param sb the sb
	 * @param b the b
	 */
	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}

	public static String Decrypt(String text, String key) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] keyBytes= new byte[16];
			byte[] b= key.getBytes("UTF-8");
			int len= b.length;
			if (len > keyBytes.length) len = keyBytes.length;
			System.arraycopy(b, 0, keyBytes, 0, len);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
			cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
			byte [] results = cipher.doFinal(toByte(text));
			return new String(results,"UTF-8");		
		} catch(NumberFormatException nfe) {
			return text;
		}
	}

	public static String Encrypt(String text, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes= new byte[16];
		byte[] b= key.getBytes("UTF-8");
		int len= b.length;
		if (len > keyBytes.length) len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
		return new String(toHex(results));
	}
}
