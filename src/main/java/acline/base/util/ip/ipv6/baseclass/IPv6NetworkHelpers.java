package acline.base.util.ip.ipv6.baseclass;

import java.util.BitSet;

import static acline.base.util.ip.ipv6.baseclass.BitSetHelpers.bitSetOf;

/**
 * @author Aaron Chen
 */
public class IPv6NetworkHelpers
{
    static int longestPrefixLength(IPv6Address first, IPv6Address last)
    {
        final BitSet firstBits = bitSetOf(first.getLowBits(), first.getHighBits());
        final BitSet lastBits = bitSetOf(last.getLowBits(), last.getHighBits());

        return countLeadingSimilarBits(firstBits, lastBits);
    }

    private static int countLeadingSimilarBits(BitSet a, BitSet b)
    {
        int result = 0;
        for (int i = 127; i >= 0 && (a.get(i) == b.get(i)); i--)
        {
            result++;
        }
        return result;
    }
}
