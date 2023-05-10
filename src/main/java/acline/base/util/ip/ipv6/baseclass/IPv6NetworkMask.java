package acline.base.util.ip.ipv6.baseclass;

import java.io.Serializable;
import java.util.BitSet;

import static acline.base.util.ip.ipv6.baseclass.BitSetHelpers.bitSetOf;

/**
 * @author Aaron Chen
 */
public final class IPv6NetworkMask   implements Serializable
{
    private final int prefixLength;

    /**
     * Construct an IPv6 network mask from a prefix length. The prefix length should be in the interval ]0, 128].
     *
     * @param prefixLength prefix length
     * @throws IllegalArgumentException if the prefix length is not in the interval ]0, 128]
     */
    IPv6NetworkMask(int prefixLength)
    {
        if (prefixLength < 0 || prefixLength > 128)
            throw new IllegalArgumentException("prefix length should be in interval [0, 128]");

        this.prefixLength = prefixLength;
    }


    /**
     * Construct an IPv6 network mask from an IPv6 address. The address should be a valid network mask.
     *
     * @param iPv6Address address to use as network mask
     * @throws IllegalArgumentException if the address is not a valid network mask
     * @return ipv6 network mask
     */
    public static IPv6NetworkMask fromAddress(final IPv6Address iPv6Address)
    {
        validateNetworkMask(iPv6Address);
        return new IPv6NetworkMask(iPv6Address.numberOfLeadingOnes());
    }

    /**
     * Construct an IPv6 network mask from a prefix length. The prefix length should be in the interval ]0, 128].
     *
     * @param prefixLength prefix length
     * @throws IllegalArgumentException if the prefix length is not in the interval ]0, 128]
     * @return ipv6 network mask
     */
    public static IPv6NetworkMask fromPrefixLength(int prefixLength)
    {
        return new IPv6NetworkMask(prefixLength);
    }

    private static void validateNetworkMask(IPv6Address addressToValidate)
    {
        final BitSet addressAsBitSet = bitSetOf(addressToValidate.getLowBits(), addressToValidate.getHighBits());
        boolean firstZeroFound = false;
        for (int i = 127; i >= 0 && !firstZeroFound; i--)
        {
            if (!addressAsBitSet.get(i))
            {
                firstZeroFound = true;

                // a zero -> all the others should also be zero
                for (int j = i - 1; j >= 0; j--)
                {
                    if (addressAsBitSet.get(j))
                    {
                        throw new IllegalArgumentException(addressToValidate + " is not a valid network mask");
                    }
                }
            }
        }
    }

    public int asPrefixLength()
    {
        return prefixLength;
    }

    public IPv6Address asAddress()
    {
        if (prefixLength == 128)
        {
            return new IPv6Address(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL);
        }
        else if (prefixLength == 64)
        {
            return new IPv6Address(0xFFFFFFFFFFFFFFFFL, 0L);
        }
        else if (prefixLength > 64)
        {
            final int remainingPrefixLength = prefixLength - 64;
            return new IPv6Address(0xFFFFFFFFFFFFFFFFL, (0xFFFFFFFFFFFFFFFFL << (64 - remainingPrefixLength)));
        }
        else if (prefixLength == 0)
        {
            return new IPv6Address(0, 0);
        }
        else
        {
            return new IPv6Address(0xFFFFFFFFFFFFFFFFL << (64 - prefixLength), 0);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv6NetworkMask that = (IPv6NetworkMask) o;

        if (prefixLength != that.prefixLength) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return prefixLength;
    }

    @Override
    public String toString()
    {
        return "" + prefixLength;
    }
}
