package acline.base.util.ip.ipv6.baseclass;

import java.util.BitSet;

/**
 * @author Aaron Chen
 */
class BitSetHelpers
{

    static BitSet bitSetOf(long lowerBits, long upperBits) {
        final BitSet bitSet = new BitSet();
        convert(lowerBits, 0, bitSet);
        convert(upperBits, Long.SIZE, bitSet);
        return bitSet;
    }

    static void convert(long value, int bitSetOffset, BitSet bits) {
        int index = 0;
        while (value != 0L) {
            if (value % 2L != 0) {
                bits.set(bitSetOffset + index);
            }
            ++index;
            value = value >>> 1;
        }
    }

}
