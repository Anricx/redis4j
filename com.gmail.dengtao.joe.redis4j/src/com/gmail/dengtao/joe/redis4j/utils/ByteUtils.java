package com.gmail.dengtao.joe.redis4j.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Byte operation</p>
 * @author <a href="mailto:joe.dengtao@gmail.com">DengTao</a>
 * @version 1.0
 * @since 1.0
 */
public class ByteUtils {

	/** Empty byte array: byte[0] */
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	/** Represents a failed index search. */
    public static final int INDEX_NOT_FOUND = -1;

    /** The number of byte used to represent a byte value. */
	public static final int BYTE_LEN = 1;
	/** The number of byte used to represent a char value. */
	public static final int CHAR_BYTE_LEN = Character.SIZE / Byte.SIZE;
	/** The number of byte used to represent a short value. */
	public static final int SHORT_BYTE_LEN = Short.SIZE / Byte.SIZE;
	/** The number of byte used to represent a int value. */
	public static final int INTEGER_BYTE_LEN = Integer.SIZE / Byte.SIZE;
	/** The number of byte used to represent a long value. */
	public static final int LONG_BYTE_LEN = Long.SIZE / Byte.SIZE;
	/** The number of byte used to represent a float value. */
	public static final int FLOAT_BYTE_LEN = Float.SIZE / Byte.SIZE;
	/** The number of byte used to represent a double value. */
	public static final int DOUBLE_BYTE_LEN = Double.SIZE / Byte.SIZE;
	
	/**
	 * Merge <code>first</code> and <code>second</code> to a new byte array.
	 * 
	 * @param first first byte array
	 * @param second second byte array
	 * @return first + second
	 * @since 1.0
	 */
	public static byte[] merge(byte first, byte[] second) {
		if (isBlank(second)) {
			return new byte[] {first};
		}
		byte[] dts = new byte[1 + second.length];
		System.arraycopy(new byte[] {first}, 0, dts, 0, 1);
		System.arraycopy(second, 0, dts, 1, second.length);
		return dts;
	}
	
	/**
	 * Merge <code>first</code> and <code>second</code> to a new byte array.
	 * 
	 * @param first first byte array
	 * @param second second byte array
	 * @return first + second
	 * @since 1.0
	 */
	public static byte[] merge(byte[] first, byte second) {
		if (isBlank(first)) {
			return new byte[] { second };
		}
		byte[] dts = new byte[first.length + 1];
		System.arraycopy(first, 0, dts, 0, first.length);
		System.arraycopy(new byte[] { second }, 0, dts, first.length, 1);
		return dts;
	}
	
	/**
	 * Merge <code>first</code> and <code>second</code> to a new byte array.
	 * 
	 * @param first first byte array
	 * @param second second byte array
	 * @return first + second
	 * @since 1.0
	 */
	public static byte[] merge(byte[] first, byte[] second) {
		if (isBlank(first)) {
			if (isNotBlank(second)) {
				return second;
			} else {
				if (isBlank(first)) return EMPTY_BYTE_ARRAY;
				else return first;
			}
		}
		if (isBlank(second)) return first;
		byte[] dts = new byte[first.length + second.length];
		System.arraycopy(first, 0, dts, 0, first.length);
		System.arraycopy(second, 0, dts, first.length, second.length);
		return dts;
	}
	
	/**
	 * Checks if <code>bts</code> is empty byte[0] or null.
	 * <pre>
     * ByteUtils.isBlank(null)      	= true
     * ByteUtils.isBlank(new byte[0])   = true
     * ByteUtils.isBlank(new byte[10])  = false
     * </pre>
	 * @param bts byte[] array of binary data to be tested.
	 * @return true if the byte array is empty or null
	 * @since 1.0
	 */
	public static boolean isBlank(byte[] bts) {
		if (bts == null || bts.length == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if <code>bts</code> is not empty byte[0] and not null only.
	 * <pre>
     * ByteUtils.isBlank(null)      	= false
     * ByteUtils.isBlank(new byte[0])   = false
     * ByteUtils.isBlank(new byte[10])  = true
     * </pre>
	 * @param bts byte[] array of binary data to be tested.
	 * @return true if the byte array is not empty and not null
	 * @since 1.0
	 */
	public static boolean isNotBlank(byte[] bts) {
		return !isBlank(bts);
	}
	
	/**
	 * Tests if <code>bts</code> starts with the specified prefix.
	 * @param bts byte[] array of binary data to be tested.
	 * @param prefix the prefix.
	 * @return <code>true</code> if the bytes's first byte and the follow bytes are same as the specified prefix;<br> 
	 * 		   <code>false</code> otherwise.
	 * @since 1.0
	 */
	public static boolean startWith(byte[] bts, byte[] prefix) {
		if (bts.length < prefix.length) {
			return false;
		} else {
			for (int i = 0, l = prefix.length; i < l; i++) {
				if (bts[i] != prefix[i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Tests if <code>bts</code> starts with the specified prefix.
	 * @param bts byte[] array of binary data to be tested.
	 * @param prefix the prefix.
	 * @return <code>true</code> if the bytes's first byte is same as the specified prefix;<br> 
	 * 		   <code>false</code> otherwise.
	 * @since 1.0
	 */
	public static boolean startWith(byte[] bts, byte prefix) {
		if (bts[0] == prefix) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Tests if <code>bts</code> ends with the specified suffix.
     *
     * @param bts byte[] array of binary data to be tested.
     * @param suffix the suffix.
     * @return  <code>true</code> if the bytes's last byte and the previous bytes are same as the specified prefix;<br>
     *          <code>false</code> otherwise.
	 * @since 1.0
	 */
	public static boolean endWith(byte[] bts, byte[] suffix) {
		int lastIndex = lastIndexOf(bts, suffix);
		if (lastIndex != INDEX_NOT_FOUND) {
			if (lastIndex + suffix.length == bts.length) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Tests if <code>bts</code> ends with the specified suffix.
     *
     * @param bts byte[] array of binary data to be tested.
     * @param suffix the suffix.
     * @return  <code>true</code> if the bytes's last byte is same as the specified prefix;<br>
     *          <code>false</code> otherwise.
	 * @since 1.0
	 */
	public static boolean endWith(byte[] bts, byte suffix) {
		if (bts[bts.length - 1] == suffix) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the index within the <code>bts</code> of the first occurrence of
     * the specified byte.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param  target traget byte.
     * @return  the index of the first occurrence of the byte in the
     *          byte array, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the byte does not occur.
	 * @since 1.0
	 */
	public static int indexOf(byte[] bts, byte target) {
		return indexOf(bts, target, 0);
	}
	
	/**
	 * Returns the index within the <code>bts</code> of the first occurrence of
     * the specified byte, starting at pos.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param target traget byte.
     * @param pos Position to start find <code>target</code> from.
     * @return  the index of the first occurrence of the byte in <code>bts</code>, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the byte does not occur.
	 * @since 1.0
	 */
	public static int indexOf(byte[] bts, byte target, int pos) {
		if (pos < 0) {
			throw new IllegalArgumentException("offset cant be a negative number!");
		}
		for (int i = pos, l = bts.length; i < l; i++) {
			if (bts[i] == target) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}
	
	/**
	 * Returns the index within the <code>bts</code> of the first occurrence of
     * the specified bytes.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param target traget bytes.
     * @return  the index of the first occurrence of the bytes in the
     *          byte array, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the bytes does not occur.
	 * @since 1.0
	 */
	public static int indexOf(byte[] bts, byte[] target) {
		return indexOf(bts, target, 0);
	}
	
	/**
	 * Returns the index within the <code>bts</code> of the first occurrence of
     * the specified bytes, start find <code>target</code> in <code>bts</code> from offset.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param target traget bytes.
     * @param pos Position to start find data from.
     * @return  the index of the first occurrence of the bytes in the
     *          byte array, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the bytes does not occur.
	 * @since 1.0
	 */
	public static int indexOf(byte[] bts, byte[] target, int pos) {
		if (pos < 0) {
			throw new IllegalArgumentException("offset cant be a negative number!");
		}
		if (bts.length - pos < target.length) {
			return INDEX_NOT_FOUND;
		} else {
			int si = INDEX_NOT_FOUND;
			int i = 0;
			boolean find = true;
			int l = target.length;
			for (;;) {
				if ((si = indexOf(bts, target[i], pos)) == INDEX_NOT_FOUND) {
					return INDEX_NOT_FOUND;
				} else {
					find = true;
					if ((si + l) <= bts.length) {
						for (int j = 1; j < l; j++) {
							if (bts[si + j] == target[j]) {
								continue;
							} else {
								find = false;
								pos = si + 1;
								break;
							}
						}
						if (find) {
							return si;
						} else {
							continue;
						}
					} else {
						return INDEX_NOT_FOUND;
					}
				}
			}
		}
	}
	
	/**
	 * Returns the last index within the <code>bts</code> of the last occurrence of
     * the specified byte.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param  target traget byte.
     * @return  the index of the last occurrence of the byte in the
     *          byte array, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the byte does not occur.
	 * @since 1.0
	 */
	public static int lastIndexOf(byte[] bts, byte target) {
		int index = indexOf(bts, target);
		if (index != INDEX_NOT_FOUND) {
			int offset = index + 1;
			int temp = INDEX_NOT_FOUND;
			for(;;) {
				temp = indexOf(bts, target, offset);
				if (temp != INDEX_NOT_FOUND) {
					index = temp;
					offset = index + 1;
				} else {
					return index;
				}
			}
		} else {
			return INDEX_NOT_FOUND;
		}
	}
	
	/**
	 * Returns the last index within the <code>bts</code> of the last occurrence of
     * the specified bytes.
     * 
     * @param bts byte[] array of binary data to find byte.
     * @param target traget bytes.
     * @return  the index of the last occurrence of the bytes in the
     *          byte array, or
     *          <code>{@link ByteUtils#INDEX_NOT_FOUND}</code> if the bytes does not occur.
	 * @since 1.0
	 */
	public static int lastIndexOf(byte[] bts, byte[] target) {
		int index = indexOf(bts, target);
		if (index != INDEX_NOT_FOUND) {
			int pos = index + 1;
			int temp = INDEX_NOT_FOUND;
			for(;;) {
				temp = indexOf(bts, target, pos);
				if (temp != INDEX_NOT_FOUND) {
					index = temp;
					pos = index + 1;
				} else {
					return index;
				}
			}
		} else {
			return INDEX_NOT_FOUND;
		}
	}
	
	/**
	 * Replaces each bytes of the srcBts that matches the literal target
     * sequence with the specified literal replacement bytes. The
     * replacement proceeds from the beginning of the srcBts to the end.
     *
     * @param src byte[] array of binary data to be replaced.
     * @param  target The sequence of char values to be replaced.
     * @param  replacement The replacement sequence of char values.
     * @return  The resulting new bytes.
     * @since 1.0
	 */
	public static byte[] replace(byte[] src, byte[] target, byte[] replacement) {
		int pos = 0;
		for(;;) {
			int leftIndex = indexOf(src, target, pos);
			if (leftIndex == INDEX_NOT_FOUND) {
				return src;
			} else {
				pos = leftIndex + replacement.length;
				byte[] leftBts = cut(src, 0, leftIndex);
				byte[] rightBts = cut(leftIndex + target.length, src);
				leftBts = merge(leftBts, replacement);
				src = merge(leftBts, rightBts);
			}
		}
	}
	
	/**
	 * Get a new byte array from <code>src</code>; start index:pos, length: len or src.length - pos.
	 * @param src byte[] array of binary data to be cut.
	 * @param pos Position to start cut data from.
	 * @param len cut length.
	 * @return new bytes of src, start index:pos, length: len or src.length - pos.
	 * @since 1.0
	 */
	public static byte[] cut(byte[] src, int pos, int len) {
		if (isBlank(src)) {
			return EMPTY_BYTE_ARRAY;
		}
		if (len <= 0 || pos < 0) {
			return EMPTY_BYTE_ARRAY;
		}
		len = Math.min((src.length - pos), len);
		byte[] dts = new byte[len];
		System.arraycopy(src, pos, dts, 0, len);
		return dts;
	}

	/**
	 * Get a new byte array from <code>src</code>; start index:pos, length: src.length - pos.
	 * @param pos Position to start cut data from.
	 * @param src byte[] array of binary data to be cut.
	 * @return new bytes of src, start index:pos, length: src.length - pos.
	 * @since 1.0
	 */
	public static byte[] cut(int pos, byte[] src) {
		return cut(src, pos, src.length - pos);
	}

	/**
	 * Get a new byte array from <code>src</code>; start index:0, length: len or src.length - pos.
	 * @param pos start cut index.
	 * @param src byte array to be cut.
	 * @return new bytes of src, start index:0, length: len or src.length - pos.
	 * @since 1.0
	 */
	public static byte[] cut(byte[] src, int len) {
		return cut(src, 0, len);
	}

	/**
	 * Clone the src byte array.
	 * 
	 * @param src byte[] array of binary data to be cloned.
	 * @return a clone of the src byte array.
	 * @since 1.0
	 */
	public static byte[] clone(byte[] src) {
		byte[] dts = new byte[src.length];
		System.arraycopy(src, 0, dts, 0, src.length);
		return dts;
	}
	
	/**
	 * Splits the byte array <code>bts</code> around matches of the given delimiter
	 * @param bts byte[] array of binary data to be split.
	 * @param delimiter 
	 * @return the array of byte[] computed by splitting the <code>bts</code>
     *          around matches of the given delimiter.
	 * @since 1.0
	 */
	public static byte[][] split(byte[] bts, byte delimiter) {
		List<byte[]> byteList = new ArrayList<byte[]>();
		int pos = 0;
		for(;;) {
			int index = indexOf(bts, delimiter, pos);
			if (index != INDEX_NOT_FOUND) {
				byte[] tempBts = cut(bts, pos, index - pos);
				byteList.add(tempBts);
				pos = index + 1;
			} else {
				if (pos == bts.length) {
					break;
				} else {
					byte[] tempBts = cut(bts, pos, bts.length - pos);
					byteList.add(tempBts);
					break;
				}
			}
		}
		return byteList.toArray(new byte[byteList.size()][]);
	}
	
	/**
	 * Splits the byte array <code>bts</code> around matches of the given delimiter
	 * @param bts byte[] array of binary data to be split.
	 * @param delimiter 
	 * @return the array of byte[] computed by splitting the <code>bts</code>
     *          around matches of the given delimiter.
	 * @since 1.0
	 */
	public static byte[][] split(byte[] bts, byte[] delimiter) {
		List<byte[]> byteList = new ArrayList<byte[]>();
		int pos = 0;
		for(;;) {
			int index = indexOf(bts, delimiter, pos);
			if (index != INDEX_NOT_FOUND) {
				byte[] tempBts = cut(bts, pos, index - pos);
				byteList.add(tempBts);
				pos = index + delimiter.length;
			} else {
				if (pos == bts.length) {
					break;
				} else {
					byte[] tempBts = cut(bts, pos, bts.length - pos);
					byteList.add(tempBts);
					break;
				}
			}
		}
		return byteList.toArray(new byte[byteList.size()][]);
	}
	
	/**
	 * Splits the byte array <code>bts</code> around matches of the given delimiter
	 * @param bts byte[] array of binary data to be split.
	 * @param delimiter 
	 * @param limit the result threshold, as described above
	 * @return the array of byte[] computed by splitting the <code>bts</code>
     *          around matches of the given delimiter.
	 * @since 1.0
	 */
	public static byte[][] split(byte[] bts, byte[] delimiter, int limit) {
		List<byte[]> byteList = new ArrayList<byte[]>();
		int pos = 0;
		boolean matchLimit = limit > 0;
		for(;;) {
			int index = indexOf(bts, delimiter, pos);
			if (index != INDEX_NOT_FOUND) {
				if (matchLimit && byteList.size() == limit - 1) {
					byteList.add(cut(bts, pos, bts.length - pos));
					break;
				} else {
					byte[] tempBts = cut(bts, pos, index - pos);
					byteList.add(tempBts);
					pos = index + delimiter.length;
				}
			} else {
				if (pos == bts.length) {
					break;
				} else {
					byte[] tempBts = cut(bts, pos, bts.length - pos);
					byteList.add(tempBts);
					break;
				}
			}
		}
		return byteList.toArray(new byte[byteList.size()][]);
	}

	/**
	 * Convent short to byte array.
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(short in) {
		byte[] bts = new byte[SHORT_BYTE_LEN];
		for (int i = 0; i < SHORT_BYTE_LEN; i++) {
			bts[i] = (byte) (in >>> (Byte.SIZE - i * Byte.SIZE));
		}
		return bts;
	}

	/**
	 * Convent int to byte array.
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(int in) {
		byte[] bts = new byte[INTEGER_BYTE_LEN];
		for (int i = 0; i < INTEGER_BYTE_LEN; i++) {
			bts[i] = (byte) (in >>> (24 - i * Byte.SIZE));
		}
		return bts;
	}

	/**
	 * Convent long to byte array.
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(long in) {
		byte[] bts = new byte[LONG_BYTE_LEN];
		for (int i = 0; i < LONG_BYTE_LEN; i++) {
			bts[i] = (byte) (in >>> (56 - i * Byte.SIZE));
		}
		return bts;
	}

	/**
	 * Convent float to byte array
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(float in) {
		byte[] bts = new byte[FLOAT_BYTE_LEN];
		int data = Float.floatToIntBits(in);
		for (int i = 0; i < FLOAT_BYTE_LEN; i++) {
			bts[i] = (byte) (data >>> (24 - i * Byte.SIZE));
		}
		return bts;
	}

	/**
	 * Convent double to byte array
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(double in) {
		byte[] bts = new byte[DOUBLE_BYTE_LEN];
		long data = Double.doubleToLongBits(in);
		for (int i = 0; i < DOUBLE_BYTE_LEN; i++) {
			bts[i] = (byte) (data >>> (56 - i * Byte.SIZE));
		}
		return bts;
	}
	
	/**
	 * Convent char to byte array
	 * @param in
	 * @return
	 * @since 1.0
	 */
	public static byte[] toBytes(char in) {
		return new byte[] {(byte) in};
	}
	
	/**
	 * Absolute <i>get</i> method for reading a short value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * short value according to the current byte order.  </p>
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The short value at the given offset
	 * @since 1.0
	 */
	public static byte getByte(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0 || offset > b.length - 1) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		return b[offset];
	}
	
	/**
	 * Absolute <i>get</i> method for reading a short value.
     *
     * <p> Reads two bytes at the given index, composing them into a
     * short value according to the current byte order.  </p>
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The short value at the given offset
	 * @since 1.0
	 */
	public static short getShort(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		short out = (short) (b[0] < 0 ? -1 : 0);
		for (int l = b.length - offset, i = l < SHORT_BYTE_LEN ?  SHORT_BYTE_LEN - l : 0, off = offset - i; i < SHORT_BYTE_LEN; i++) {
			out <<= Byte.SIZE;  
			out |= (b[off + i] & 0xff); 
		}
		return out;
	}
	

	/**
	 * Absolute <i>get</i> method for reading an int value.
     *
     * <p> Reads four bytes at the given index, composing them into a
     * int value according to the current byte order.  </p>
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The int value at the given index
	 * @since 1.0
	 */
	public static int getInt(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		int out = b[0] < 0 ? -1 : 0;
		for (int l = b.length - offset, i = l < INTEGER_BYTE_LEN ?  INTEGER_BYTE_LEN - l : 0, off = offset - i; i < INTEGER_BYTE_LEN; i++) {
			out <<= Byte.SIZE;  
			out |= (b[off + i] & 0xff); 
		}
		return out;
	}
	
	/**
	 * Absolute <i>get</i> method for reading a long value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * long value according to the current byte order.  </p>
     *
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The long value at the given index
	 * @since 1.0
	 */
	public static long getLong(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		long out = b[0] < 0 ? -1L : 0L;
		for (int l = b.length - offset, i = l < LONG_BYTE_LEN ?  LONG_BYTE_LEN - l : 0, off = offset - i; i < LONG_BYTE_LEN; i++) {
			out <<= Byte.SIZE;  
			out |= (b[off + i] & 0xff); 
		}
		return out;
	}
	
	/**
	 * Absolute <i>get</i> method for reading a float value.
     *
     * <p> Reads four bytes at the given index, composing them into a
     * float value according to the current byte order.  </p>
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The float value at the given index
	 * @since 1.0
	 */
	public static float getFloat(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		int out = b[0] < 0 ? -1 : 0;
		for (int l = b.length - offset, i = l < FLOAT_BYTE_LEN ?  FLOAT_BYTE_LEN - l : 0, off = offset - i; i < FLOAT_BYTE_LEN; i++) {
			out <<= Byte.SIZE;  
			out |= (b[off + i] & 0xff); 
		}
		return Float.intBitsToFloat(out);
	}
	
	/**
	 * Absolute <i>get</i> method for reading a double value.
     *
     * <p> Reads eight bytes at the given index, composing them into a
     * double value according to the current byte order.  </p>
	 * @param b
	 * @param offset The index from which the bytes will be read
	 * @return The double value at the given index
	 * @since 1.0
	 */
	public static double getDouble(byte[] b, int offset) {
		if (isBlank(b)) {
			throw new IllegalArgumentException("blank byte array.");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("invalid array offset:" + offset + ".");
		}
		long out = b[0] < 0 ? -1L : 0L;
		for (int l = b.length - offset, i = l < DOUBLE_BYTE_LEN ?  DOUBLE_BYTE_LEN - l : 0, off = offset - i; i < DOUBLE_BYTE_LEN; i++) {
			out <<= Byte.SIZE;
			out |= (b[off + i] & 0xff); 
		}
		return Double.longBitsToDouble(out);
	}
	
	/**
	 * Caculate the number of byte of all the args.<br>
	 * 
	 * Only Support:<br>
	 * byte([]), short([]), int([]), long([]), float([]), double([]), char([]), <br>
	 * java.lang.Byte([]), java.lang.Short([]), java.lang.Integer([]), java.lang.Long([]), <br>
	 * java.lang.Float([]), java.lang.Double([]) and java.lang.Character([]).
	 * @param args
	 * @return 
	 * @since 1.0
	 */
	public static int sizeOf(Object... args) {
		int len = 0;
		if (args != null && args.length > 0) {
			for (Object o : args) {
				if (o == null) {
					continue;
				} else if (o instanceof Byte) {					// byte|Byte
					len += BYTE_LEN;
				} else if (o instanceof Short) {				// short|Short
					len += SHORT_BYTE_LEN;
				} else if (o instanceof Integer) {				// int|Integer
					len += INTEGER_BYTE_LEN;
				} else if (o instanceof Long) { 				// long|Long
					len += LONG_BYTE_LEN;
				} else if (o instanceof Float) {				// float|Float
					len += FLOAT_BYTE_LEN;
				} else if (o instanceof Double) {				// double|Double
					len += DOUBLE_BYTE_LEN;
				} else if (o instanceof Character) {			// char|Character
					len += CHAR_BYTE_LEN;
				} else if (o instanceof byte[]) {				// byte[]
					len += BYTE_LEN * ((byte[]) o).length;
				} else if (o instanceof short[]) {				// short[];
					len += SHORT_BYTE_LEN * ((short[]) o).length;
				} else if (o instanceof int[]) {				// int[];
					len += INTEGER_BYTE_LEN * ((int[]) o).length;
				} else if (o instanceof long[]) {				// long[];
					len += LONG_BYTE_LEN * ((long[]) o).length;
				} else if (o instanceof float[]) {				// float[];
					len += FLOAT_BYTE_LEN * ((float[]) o).length;
				} else if (o instanceof double[]) {				// double[];
					len += DOUBLE_BYTE_LEN * ((double[]) o).length;
				} else if (o instanceof char[]) {				// char[];
					len += CHAR_BYTE_LEN * ((char[]) o).length;
				} else if (o instanceof Byte[]) {				// Byte[]
					for (Byte b : ((Byte[]) o)) {
						if (b == null) continue;
						else len += BYTE_LEN;
					}
				} else if (o instanceof Short[]) {				// Short[]
					for (Short b : ((Short[]) o)) {
						if (b == null) continue;
						else len += SHORT_BYTE_LEN;
					}
				} else if (o instanceof Integer[]) {			// Integer[]
					for (Integer b : ((Integer[]) o)) {
						if (b == null) continue;
						else len += INTEGER_BYTE_LEN;
					}
				} else if (o instanceof Long[]) {				// Long[]
					for (Long b : ((Long[]) o)) {
						if (b == null) continue;
						else len += LONG_BYTE_LEN;
					}
				} else if (o instanceof Float[]) {				// Float[]
					for (Float b : ((Float[]) o)) {
						if (b == null) continue;
						else len += FLOAT_BYTE_LEN;
					}
				} else if (o instanceof Double[]) {				// Double[]
					for (Double b : ((Double[]) o)) {
						if (b == null) continue;
						else len += DOUBLE_BYTE_LEN;
					}
				} else if (o instanceof Character[]) {			// Character[];
					for (Character b : ((Character[]) o)) {
						if (b == null) continue;
						else len += CHAR_BYTE_LEN;
					}
				} else {
					throw new IllegalArgumentException("invalid argument type:" + o.getClass() + "! only support:byte([]), short([]), int([]), long([]), float([]), double([]), char([]), java.lang.Byte([]), java.lang.Short([]), java.lang.Integer([]), java.lang.Long([]), java.lang.Float([]), java.lang.Double([]) and java.lang.Character([]).");
				}
			}
		}
		return len;
	}
}