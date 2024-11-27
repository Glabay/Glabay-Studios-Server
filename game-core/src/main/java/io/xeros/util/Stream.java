package io.xeros.util;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.xeros.GameThread;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class Stream {

	private static final Logger logger = LoggerFactory.getLogger(Stream.class);

	final Charset CHARSET = Charsets.UTF_8;

	public Stream() {
		buffer = new byte[1];
		currentOffset = 0;
	}

	public Stream(byte[] abyte0) {
		buffer = abyte0;
		currentOffset = 0;
	}

	public void resize() {
		buffer = Arrays.copyOfRange(buffer, 0, currentOffset);
	}

	public byte readSignedByteA() {
		return (byte) (buffer[currentOffset++] - 128);
	}

	public byte readSignedByteC() {
		return (byte) (-buffer[currentOffset++]);
	}

	public long readQWord2() {
		final long l = readInteger() & 0xffffffffL;
		final long l1 = readInteger() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public byte readSignedByteS() {
		return (byte) (128 - buffer[currentOffset++]);
	}

	public int readUnsignedByteA() {
		return buffer[currentOffset++] - 128 & 0xff;
	}

	public int readUnsignedByteC() {
		return -buffer[currentOffset++] & 0xff;
	}

	public int readUnsignedByteS() {
		return 128 - buffer[currentOffset++] & 0xff;
	}

	public void writeByteA(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (i + 128);
	}

	public void writeByteS(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (128 - i);
	}

	public void writeByteC(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (-i);
	}

	public int readSignedWordBigEndian() {
		try {
			currentOffset += 2;
			int i = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
			if (i > 32767)
				i -= 0x10000;
			return i;
		} catch (Throwable t) {
			t.printStackTrace();
			return -1;
		}
	}

	public int readSignedShortLittleEndianA() {
		currentOffset += 2;
		int i = (buffer[currentOffset - 1] & 0xff) + ((buffer[currentOffset - 2] - 128 & 0xff) << 8); //
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordA() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readSignedWordBigEndianA() {
		try {
			currentOffset += 2;
			int i = ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
			if (i > 32767)
				i -= 0x10000;
			return i;
		} catch (Throwable t) {
			t.printStackTrace();
			return -1;
		}
	}

	public int readUnsignedWordBigEndian() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] & 0xff);
	}

	public int readUnsignedWordA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] - 128 & 0xff);
	}

	public int readUnsignedWordBigEndianA() {
		currentOffset += 2;
		return ((buffer[currentOffset - 1] & 0xff) << 8) + (buffer[currentOffset - 2] - 128 & 0xff);
	}

	public void writeWordBigEndianA(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i + 128);
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void writeWordA(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) (i + 128);
	}

	public void writeWordBigEndian_dup(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public int readDWord_v1() {
		currentOffset += 4;
		return ((buffer[currentOffset - 2] & 0xff) << 24) + ((buffer[currentOffset - 1] & 0xff) << 16) + ((buffer[currentOffset - 4] & 0xff) << 8)
				+ (buffer[currentOffset - 3] & 0xff);
	}

	public int readDWord_v2() {
		currentOffset += 4;
		return ((buffer[currentOffset - 3] & 0xff) << 24) + ((buffer[currentOffset - 4] & 0xff) << 16) + ((buffer[currentOffset - 1] & 0xff) << 8)
				+ (buffer[currentOffset - 2] & 0xff);
	}

	public void writeDWord_v1(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) (i >> 16);
	}

	public void writeDWord_v2(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void readBytes_reverse(byte[] abyte0, int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[currentOffset++];

	}

	public void writeBytes_reverse(byte[] abyte0, int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			buffer[currentOffset++] = abyte0[k];

	}

	public void readBytes_reverseA(byte[] abyte0, int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = (byte) (buffer[currentOffset++] - 128);
	}

	public void writeBytes_reverseA(byte[] abyte0, int i, int j) {
		ensureCapacity(i);
		for (int k = (j + i) - 1; k >= j; k--)
			buffer[currentOffset++] = (byte) (abyte0[k] + 128);

	}

	public void createFrame(int id) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextValue());
	}

	private static final int frameStackSize = 10;
	private int frameStackPtr = -1;
	private final int[] frameStack = new int[frameStackSize];

	public void createFrameVarSize(int id) {
		ensureCapacity(3);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextValue());
		buffer[currentOffset++] = 0;
		if (frameStackPtr >= frameStackSize - 1) {
			throw new RuntimeException("Stack overflow");
		} else
			frameStack[++frameStackPtr] = currentOffset;

	}

	public void createFrameVarSizeWord(int id) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (id + packetEncryption.getNextValue());
		writeUShort(0);
		if (frameStackPtr >= frameStackSize - 1) {
			throw new RuntimeException("Stack overflow");
		} else
			frameStack[++frameStackPtr] = currentOffset;

	}

	public void endFrameVarSize() {
		if (frameStackPtr < 0)
			throw new RuntimeException("Stack empty");
		else
			writeFrameSize(currentOffset - frameStack[frameStackPtr--]);
	}

	public void endFrameVarSizeWord() {
		if (frameStackPtr < 0)
			throw new RuntimeException("Stack empty");
		else
			writeFrameSizeWord(currentOffset - frameStack[frameStackPtr--]);
	}

	public void writeByte(int i) {
		ensureCapacity(1);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeUShort(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void putShort(int value) {
		putShort(value, BIG_ENDIAN);
	}

	public void putShort(int value, ByteOrder order) {
        if (order.equals(BIG_ENDIAN)) {
            ensureCapacity(2);
            writeByte(value >> 8);
        } else if (order.equals(LITTLE_ENDIAN)) {
            ensureCapacity(1);
            writeByte(value >> 8);
        }
	}

	public void writeWordBigEndian(int i) {
		ensureCapacity(2);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
	}

	public void write3Byte(int i) {
		ensureCapacity(3);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWord(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) (i >> 24);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) i;
	}

	public void writeDWordBigEndian(int i) {
		ensureCapacity(4);
		buffer[currentOffset++] = (byte) i;
		buffer[currentOffset++] = (byte) (i >> 8);
		buffer[currentOffset++] = (byte) (i >> 16);
		buffer[currentOffset++] = (byte) (i >> 24);
	}

	public void writeQWord(long l) {
		ensureCapacity(8);
		buffer[currentOffset++] = (byte) (int) (l >> 56);
		buffer[currentOffset++] = (byte) (int) (l >> 48);
		buffer[currentOffset++] = (byte) (int) (l >> 40);
		buffer[currentOffset++] = (byte) (int) (l >> 32);
		buffer[currentOffset++] = (byte) (int) (l >> 24);
		buffer[currentOffset++] = (byte) (int) (l >> 16);
		buffer[currentOffset++] = (byte) (int) (l >> 8);
		buffer[currentOffset++] = (byte) (int) l;
	}

	public String readSizedString() {
		int length = readInteger();
		byte[] bytes = new byte[length];
		readBytes(bytes, 0, length);
		return new String(bytes);
	}

	public void writeString(String s) {
		ensureCapacity(s.length());
		System.arraycopy(s.getBytes(), 0, buffer, currentOffset, s.length());
		currentOffset += s.length();
		buffer[currentOffset++] = 10;
	}

	public void writeNullTerminatedString(String s) {
		ensureCapacity(s.length());
		System.arraycopy(s.getBytes(), 0, buffer, currentOffset, s.length());
		currentOffset += s.length();
		buffer[currentOffset++] = 0;
	}

	public void writeBytes(byte[] data, int offset, int start) {
		ensureCapacity(offset);
		for (int k = start; k < start + offset; k++)
			buffer[currentOffset++] = data[k];
	}

	public void writeFrameSize(int i) {
		buffer[currentOffset - i - 1] = (byte) i;
	}

	public void writeFrameSizeWord(int i) {
		buffer[currentOffset - i - 2] = (byte) (i >> 8);
		buffer[currentOffset - i - 1] = (byte) i;
	}

	public int readUnsignedByte() {
		return buffer[currentOffset++] & 0xff;
	}

	public byte readSignedByte() {
		return buffer[currentOffset++];
	}

	public int readUnsignedWord() {
		currentOffset += 2;
		return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
	}

	public int readSignedWord() {
		currentOffset += 2;
		int i = ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int readInteger() {
		currentOffset += 4;
		return ((buffer[currentOffset - 4] & 0xff) << 24) + ((buffer[currentOffset - 3] & 0xff) << 16) + ((buffer[currentOffset - 2] & 0xff) << 8)
				+ (buffer[currentOffset - 1] & 0xff);
	}

	public long readLong() {
		try {
			long l = readInteger() & 0xffffffffL;
			long l1 = readInteger() & 0xffffffffL;
			return (l << 32) + l1;
		} catch (Throwable t) {
			t.printStackTrace();
			return 0L;
		}
	}

	public String readString() {
		int i = currentOffset;
		while (buffer[currentOffset++] != 10)
			;
		return new String(buffer, i, currentOffset - i - 1);
	}

	public void readBytes(byte[] abyte0, int i, int j) {
		for (int k = j; k < j + i; k++)
			abyte0[k] = buffer[currentOffset++];

	}

	public void initBitAccess() {
		bitPosition = currentOffset * 8;
	}

	public void writeBits(int numBits, int value) {
		int bytes = numBits * 8 + 3;
		ensureCapacity(bytes * 2);
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;

		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset)) & bitMaskOut[bitOffset];

			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~bitMaskOut[bitOffset];
			buffer[bytePos] |= value & bitMaskOut[bitOffset];
		} else {
			buffer[bytePos] &= ~(bitMaskOut[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & bitMaskOut[numBits]) << (bitOffset - numBits);
		}
	}

	public void finishBitAccess() {
		currentOffset = (bitPosition + 7) / 8;
	}

	public byte[] buffer;
	public int currentOffset;
	public int bitPosition;

	public static int[] bitMaskOut = new int[32];

	static {
		for (int i = 0; i < 32; i++)
			bitMaskOut[i] = (1 << i) - 1;
	}

	public void ensureCapacity(int len) {
		String currentThreadName = Thread.currentThread().getName();
		if (!currentThreadName.equals(GameThread.THREAD_NAME)) {
			logger.error("Non-game thread is writing packets thread={} {}", currentThreadName, new Exception());
		}

		if ((currentOffset + len) >= buffer.length) {
			byte[] oldBuffer = buffer;
			int newLength = (buffer.length * 2);
			buffer = new byte[newLength];
			System.arraycopy(oldBuffer, 0, buffer, 0, oldBuffer.length);
			ensureCapacity(len);
		}
	}

	public void ensureNecessaryCapacity(int size) {
		if (currentOffset + size >= buffer.length) {
			int offset = (currentOffset + size) - (buffer.length - 1);
			buffer = Arrays.copyOf(buffer, buffer.length + offset);
		}
	}

	/**
	 * The byte array that contains any and all information
	 * 
	 * @return the buffer
	 */
	public final byte[] getBuffer() {
		return buffer;
	}

	public ISAACCipher packetEncryption;

}
