package com.charter.provisioning.hss.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.SecureRandom;

@Slf4j
@Component
public class ServiceUtil {

	private static final int DEFAULT_LENGTH = 29;

	private static final int HEX_MASK = 0x000000ff;
	private static final String SECURE_ALGO = "SHA1PRNG";
	
	private static String IP_ADDR;
	private static SecureRandom prng;
	
	static {
		try {
			IP_ADDR = getIPAsHex();
			prng = SecureRandom.getInstance(SECURE_ALGO);
		} catch (Exception ex) {
			log.error("Unknown Error occurred while initializing static block");
		}
	}
	
	public ServiceUtil() {
		prng.setSeed(generateSeed());
	}
	
	/**
	 * <p>
	 * Returns a <code>DIGIT</code>-digit random number with leading zeros.
	 * </p>
	 * <i>NOTE: DIGIT should be less than or equal to 8</i>
	 * 
	 * @return a <code>DIGIT</code>-digit random number with leading zeros
	 */
	public String appendRandom(final int MAX_RANDOM, boolean useHex) {
		
		if (MAX_RANDOM <= 0)
			return null;

		StringBuilder builder = new StringBuilder(MAX_RANDOM);
		StringBuilder random = new StringBuilder();

		while (random.length() < MAX_RANDOM) {
			if (useHex)
				random.append(Integer.toHexString(prng.nextInt()));
			else
				random.append(Math.abs(prng.nextInt()));
		}

		if (random.length() > MAX_RANDOM)
			random.delete(MAX_RANDOM, random.length());

		builder.append(random);
		return builder.toString().toUpperCase();
	}

	/**
	 * Appends the current time in milliseconds to <code>buffer</code>
	 * 
	 * @return appends the current time in milliseconds to <code>buffer</code>
	 */
	public String appendTimestamp(final int MAX_RANDOM, boolean useHex) {
		if (MAX_RANDOM <= 0)
			return null;

		StringBuilder builder = new StringBuilder(MAX_RANDOM);

		if (useHex)
			builder.append(Long.toHexString(System.currentTimeMillis()));
		else
			builder.append(System.currentTimeMillis());

		padLeft(builder);
		builder.append(builder);
		return builder.toString().toUpperCase();
	}
	
	private static byte[] generateSeed() {
		byte[] ipBytes = IP_ADDR.getBytes();
		byte[] sysBytes = currentTimeMillisBytes();
		byte[] ranBytes = SecureRandom.getSeed(DEFAULT_LENGTH);
		byte[] seed = new byte[ipBytes.length + sysBytes.length
				+ ranBytes.length];

		System.arraycopy(ipBytes, 0, seed, 0, ipBytes.length);
		System.arraycopy(sysBytes, 0, seed, ipBytes.length, sysBytes.length);
		System.arraycopy(ranBytes, 0, seed, sysBytes.length, ranBytes.length);

		return seed;
	}
	
	private static String getIPAsHex() throws UnknownHostException {
		StringBuilder ipBuffer = new StringBuilder();

		byte[] ipaddr = InetAddress.getLocalHost().getAddress();
		for (byte anIpaddr : ipaddr) {
			ipBuffer.append(Integer.toHexString((int) anIpaddr & HEX_MASK));
			while (ipBuffer.length() < 2) {
				ipBuffer.insert(0, '0');
			}
		}

		return ipBuffer.toString();
	}
	
	private static byte[] currentTimeMillisBytes() {
		byte[] bArray = new byte[8];
		ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
		LongBuffer lBuffer = bBuffer.asLongBuffer();
		lBuffer.put(0, System.currentTimeMillis());
		return bArray;
	}

	private void padLeft(final StringBuilder builder) {
		while (builder.length() < 12) {
			builder.insert(0, '0');
		}
	}
}
