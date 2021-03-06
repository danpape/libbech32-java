package design.contract.bech32;

import org.junit.Test;

import java.util.Arrays;

import static design.contract.bech32.DecodedResult.Encoding.BECH32;
import static design.contract.bech32.DecodedResult.Encoding.BECH32M;
import static org.junit.Assert.*;

public class Bech32Test {

    @Test
    public void stripUnknownChars_withNullString_returnsNull() {
        assertNull(Bech32.stripUnknownChars(null));
    }

    @Test
    public void stripUnknownChars_withSimpleString_returnsSameString() {
        String expected = "ace";
        assertEquals(expected, Bech32.stripUnknownChars(expected));
    }

    @Test
    public void stripUnknownChars_withDashes_returnsStrippedString() {
        assertEquals("tx1rqqqqqqqqmhuqk", Bech32.stripUnknownChars("tx1-rqqq-qqqq-qmhu-qk"));
    }

    @Test
    public void stripUnknownChars_withLots_returnsStrippedString() {
        assertEquals("tx1rjk0u5ng4jsfmc", Bech32.stripUnknownChars("tx1!rjk0\\u5ng*4jsf^^mc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringTooShort_withShortString_throws() {
        Bech32.Impl.rejectBStringTooShort("ace");
    }

    @Test()
    public void rejectBStringTooShort_withLongString_wontThrow() {
        Bech32.Impl.rejectBStringTooShort("aceaceace");
        return;
    }

    @Test()
    public void rejectBStringTooLong_withShortString_wontThrow() {
        Bech32.Impl.rejectBStringTooLong("aceaceace");
    }

    @Test()
    public void rejectBStringTooLong_withLongerString_wontThrow() {
        char[] buffer = new char[Bech32.Limits.MAX_BECH32_LENGTH - 1];
        Arrays.fill(buffer, 'a');
        Bech32.Impl.rejectBStringTooLong(new String(buffer));
    }

    @Test()
    public void rejectBStringTooLong_withMaxLengthString_wontThrow() {
        char[] buffer = new char[Bech32.Limits.MAX_BECH32_LENGTH];
        Arrays.fill(buffer, 'a');
        Bech32.Impl.rejectBStringTooLong(new String(buffer));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringTooLong_withLongString_throws() {
        char[] buffer = new char[Bech32.Limits.MAX_BECH32_LENGTH + 1];
        Arrays.fill(buffer, 'a');
        Bech32.Impl.rejectBStringTooLong(new String(buffer));
    }

    @Test
    public void rejectBStringMixedCase_withSingleCaseString_wontThrow() {
        Bech32.Impl.rejectBStringMixedCase("abcdefg");
        Bech32.Impl.rejectBStringMixedCase("abc123def");
        Bech32.Impl.rejectBStringMixedCase("12AB34CD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringMixedCase_withMixedCases_throws() {
        Bech32.Impl.rejectBStringMixedCase("abcDefg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringMixedCase_withMixedCasesAndNumbers_throws() {
        Bech32.Impl.rejectBStringMixedCase("1abcDefg2");
    }

    @Test
    public void rejectBStringValuesOutOfRange_withInRangeStrings_wontThrow() {
        Bech32.Impl.rejectBStringValuesOutOfRange("abcde");
        Bech32.Impl.rejectBStringValuesOutOfRange("!!abcde}~");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringValuesOutOfRange_withSpaces_throws() {
        Bech32.Impl.rejectBStringValuesOutOfRange("ab cd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringValuesOutOfRange_withNonPrintable_throws() {
        Bech32.Impl.rejectBStringValuesOutOfRange("ab\ncd");
    }

    @Test
    public void rejectBStringWithNoSeparator_withSeparator_wontThrow() {
        Bech32.Impl.rejectBStringWithNoSeparator("ab1cd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectBStringWithNoSeparator_withNoSeparator_throws() {
        Bech32.Impl.rejectBStringWithNoSeparator("abcd");
    }

    @Test
    public void findSeparatorPosition_withSeparator() {
        int pos = Bech32.Impl.findSeparatorPosition("ab1cd");
        assertEquals(2, pos);

        pos = Bech32.Impl.findSeparatorPosition("abc1def1lalala");
        assertEquals(7, pos);
    }

    @Test
    public void findSeparatorPosition_withoutSeparator() {
        int pos = Bech32.Impl.findSeparatorPosition("");
        assertEquals(-1, pos);

        pos = Bech32.Impl.findSeparatorPosition("lalalala");
        assertEquals(-1, pos);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void extractHumanReadablePart_withEmptyString_throws() {
        Bech32.Impl.extractHumanReadablePart("");
    }

    @Test
    public void extractHumanReadablePart_withOnlySeparator_returnsEmptyString() {
        String hrp = Bech32.Impl.extractHumanReadablePart("1");
        assertEquals("", hrp);
    }

    @Test
    public void extractHumanReadablePart_withSeparatorAtEnd_returnsString() {
        String hrp = Bech32.Impl.extractHumanReadablePart("ab1");
        assertEquals("ab", hrp);
    }

    @Test
    public void extractHumanReadablePart_withSeparatorInMiddle_returnsString() {
        String hrp = Bech32.Impl.extractHumanReadablePart("ab1cd");
        assertEquals("ab", hrp);
    }

    public void extractDataPartwithEmptyString_returnsEmptyArray() {
        char[] dp = Bech32.Impl.extractDataPart("");
        assertEquals(0, dp.length);
    }

    @Test
    public void extractDataPart_withOnlySeparator_returnsEmptyArray() {
        char[] dp = Bech32.Impl.extractDataPart("1");
        assertEquals(0, dp.length);
    }

    @Test
    public void extractDataPart_withSeparatorAtStart_returnsArray() {
        char[] dp = Bech32.Impl.extractDataPart("1ab");
        assertEquals(2, dp.length);
        assertEquals('a', dp[0]);
        assertEquals('b', dp[1]);
    }

    @Test
    public void extractDataPart_withSeparatorAtEnd_returnsEmptyArray() {
        char[] dp = Bech32.Impl.extractDataPart("ab1");
        assertEquals(0, dp.length);
    }

    @Test
    public void extractDataPart_withSeparatorInMiddle_returnsArray() {
        char[] dp = Bech32.Impl.extractDataPart("ab1cd");
        assertEquals(2, dp.length);
        assertEquals('c', dp[0]);
        assertEquals('d', dp[1]);
    }

    @Test
    public void mapDP_withLowercaseData() {
        char[] dp = Bech32.Impl.extractDataPart("1acd");
        Bech32.Impl.mapDP(dp);
        assertEquals(0x001d, dp[0]);
        assertEquals(0x0018, dp[1]);
        assertEquals(0x000d, dp[2]);
    }

    @Test
    public void mapDP_withUppercaseData() {
        char[] dp = Bech32.Impl.extractDataPart("1ACD");
        Bech32.Impl.mapDP(dp);
        assertEquals(0x001d, dp[0]);
        assertEquals(0x0018, dp[1]);
        assertEquals(0x000d, dp[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapDP_withInvalidData_throws() {
        char[] dp = Bech32.Impl.extractDataPart("1abc"); // 'b' is invalid
        Bech32.Impl.mapDP(dp);
    }

    @Test
    public void expandHrp_withUppercaseHrp() {
        String e = Bech32.Impl.expandHrp("ABC");
        assertEquals(0x0002, e.charAt(0));
        assertEquals(0x0002, e.charAt(1));
        assertEquals(0x0002, e.charAt(2));
        assertEquals(0x0000, e.charAt(3));
        assertEquals(0x0001, e.charAt(4));
        assertEquals(0x0002, e.charAt(5));
        assertEquals(0x0003, e.charAt(6));
    }

    @Test
    public void expandHrp_withLowercaseHrp() {
        String e = Bech32.Impl.expandHrp("abc");
        assertEquals(0x0003, e.charAt(0));
        assertEquals(0x0003, e.charAt(1));
        assertEquals(0x0003, e.charAt(2));
        assertEquals(0x0000, e.charAt(3));
        assertEquals(0x0001, e.charAt(4));
        assertEquals(0x0002, e.charAt(5));
        assertEquals(0x0003, e.charAt(6));
    }

    @Test
    public void polymod_short() {
        String e = Bech32.Impl.expandHrp("A");
        long p = Bech32.Impl.polymod(e.toCharArray());
        assertEquals(34817, p);
    }

    @Test
    public void polymod_long() {
        String e = Bech32.Impl.expandHrp("qwerty");
        long p = Bech32.Impl.polymod(e.toCharArray());
        assertEquals(448484437, p);
    }

    @Test
    public void verifyChecksum_withShortHrp_noData_isGood() {
        String bstring = "a1lqfn3a";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertTrue(Bech32.Impl.verifyChecksum(hrp, dp));
    }

    @Test
    public void verifyChecksum_c1_withShortHrp_noData_isGood() {
        String bstring = "a12uel5l";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertTrue(Bech32.Impl.verifyChecksumUsingOriginalConstant(hrp, dp));
    }

    @Test
    public void verifyChecksum_withLongerHrp_longData_isGood() {
        String bstring = "abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertTrue(Bech32.Impl.verifyChecksum(hrp, dp));
    }

    @Test
    public void verifyChecksum_c1_withLongerHrp_longData_isGood() {
        String bstring = "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertTrue(Bech32.Impl.verifyChecksumUsingOriginalConstant(hrp, dp));
    }

    @Test
    public void verifyChecksum_withShortHrp_noData_isBad() {
        // this is "bad" because one character from above test is changed
        String bstring = "b12uel5l";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertFalse(Bech32.Impl.verifyChecksum(hrp, dp));
    }

    @Test
    public void verifyChecksum_withLongerHrp_longData_isBad() {
        // this is "bad" because one character from above test is changed
        String bstring = "abcdeg1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx";
        String hrp = Bech32.Impl.extractHumanReadablePart(bstring);
        char[] dp = Bech32.Impl.extractDataPart(bstring);
        Bech32.Impl.mapDP(dp);
        assertFalse(Bech32.Impl.verifyChecksum(hrp, dp));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void stripChecksum_inputTooSmall_throws() {
        Bech32.Impl.stripChecksum("abcde");
    }

    @Test
    public void stripChecksum_inputOnlyChecksum_returnsEmptyString() {
        assertEquals(0, Bech32.Impl.stripChecksum("abcdef").length());
    }

    @Test
    public void stripChecksum_inputIsLonger_returnsStringWithoutChecksum() {
        String shortened = Bech32.Impl.stripChecksum("helloabcdef");
        assertEquals("hello", shortened);
    }

    @Test
    public void createChecksum_simple() {
        String hrp = "a";
        char[] data = new char[0];
        char[] checksum = Bech32.Impl.createChecksum(hrp, data).toCharArray();
        assertEquals(0x001f, checksum[0]);
        assertEquals(0x0000, checksum[1]);
        assertEquals(0x0009, checksum[2]);
        assertEquals(0x0013, checksum[3]);
        assertEquals(0x0011, checksum[4]);
        assertEquals(0x001d, checksum[5]);
    }

    @Test
    public void createChecksum_c1_simple() {
        String hrp = "a";
        char[] data = new char[0];
        char[] checksum = Bech32.Impl.createChecksumUsingOriginalConstant(hrp, data).toCharArray();
        assertEquals(0x000a, checksum[0]);
        assertEquals(0x001c, checksum[1]);
        assertEquals(0x0019, checksum[2]);
        assertEquals(0x001f, checksum[3]);
        assertEquals(0x0014, checksum[4]);
        assertEquals(0x001f, checksum[5]);
    }


    @Test(expected = IllegalArgumentException.class)
    public void encode_emptyArgs_throws() {
        String hrp = "";
        char[] data = new char[0];
        Bech32.encode(hrp, data);
    }

    @Test
    public void encode_simple() {
        String hrp = "a";
        char[] data = new char[0];
        String b = Bech32.encode(hrp, data);
        assertEquals("a1lqfn3a", b);
    }

    @Test
    public void encode_c1_simple() {
        String hrp = "a";
        char[] data = new char[0];
        String b = Bech32.encodeUsingOriginalConstant(hrp, data);
        assertEquals("a12uel5l", b);
    }

    @Test(expected = NullPointerException.class)
    public void decode_nullString_throws() {
        Bech32.decode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decode_emptyString_throws() {
        Bech32.decode("");
    }

    @Test
    public void decode_stringTooShort_throws() {
        try {
            Bech32.decode("a");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string too short", e.getMessage());
        }
    }

    @Test
    public void decode_stringTooLong_throws() {
        try {
            Bech32.decode("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string too long", e.getMessage());
        }
    }

    @Test
    public void decode_stringMixedCase_throws() {
        try {
            Bech32.decode("aAaaaaaaaaaaaaaaaa");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string is mixed case", e.getMessage());
        }
    }

    @Test
    public void decode_stringValuesOutOfRange_throws() {
        try {
            Bech32.decode("a aaaaaaaaaaaaaaaa");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string has value out of range", e.getMessage());
        }
        try {
            String s = "aaaa" + '\u0127' + "aaaa";

            Bech32.decode(s);
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string has value out of range", e.getMessage());
        }
    }

    @Test
    public void decode_stringNoSeparator_throws() {
        try {
            Bech32.decode("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string is missing separator character", e.getMessage());
        }
    }

    @Test
    public void decode_stringHrpTooShort_throws() {
        try {
            Bech32.decode("1aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        } catch(IllegalArgumentException e) {
            assertEquals("HRP must be at least one character", e.getMessage());
        }
    }

    @Test
    public void decode_stringHrpTooLong_throws() {
        try {
            Bech32.decode("an84characterlonghumanreadablepartaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa1a");
        } catch(IllegalArgumentException e) {
            assertEquals("HRP must be less than 84 characters", e.getMessage());
        }
    }

    @Test
    public void decode_stringDataPartTooShort_throws() {
        try {
            Bech32.decode("a33characterlonghumanreadablepart1a");
        } catch(IllegalArgumentException e) {
            assertEquals("data part must be at least six characters", e.getMessage());
        }
    }

    @Test
    public void decode_badChecksum_throws() {
        try {
            Bech32.decode("a12uel5m");
        } catch(IllegalArgumentException e) {
            assertEquals("bech32 string has bad checksum", e.getMessage());
        }
    }

    @Test
    public void decode_simple() {
        DecodedResult decodedResult = Bech32.decode("a1lqfn3a");
        assertEquals("a", decodedResult.getHrp());
        assertEquals(0, decodedResult.getDp().length);
        assertEquals(BECH32M, decodedResult.getEncoding());
    }

    @Test
    public void decode_c1_simple() {
        DecodedResult decodedResult = Bech32.decode("a12uel5l");
        assertEquals("a", decodedResult.getHrp());
        assertEquals(0, decodedResult.getDp().length);
        assertEquals(BECH32, decodedResult.getEncoding());
    }

    @Test
    public void decode_longer() {
        DecodedResult decodedResult = Bech32.decode("abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx");
        assertEquals("abcdef", decodedResult.getHrp());
        assertEquals(32, decodedResult.getDp().length);
        assertEquals(0x001f, decodedResult.getDp()[0]); // first 'l' in above data part
        assertEquals(0x0000, decodedResult.getDp()[31]);// last 'q' in above data part
        assertEquals(BECH32M, decodedResult.getEncoding());
    }

    @Test
    public void decode_c1_longer() {
        DecodedResult decodedResult = Bech32.decode("abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw");
        assertEquals("abcdef", decodedResult.getHrp());
        assertEquals(32, decodedResult.getDp().length);
        assertEquals(0x0000, decodedResult.getDp()[0]); // first 'q' in above data part
        assertEquals(0x001f, decodedResult.getDp()[31]);// last 'l' in above data part
        assertEquals(BECH32, decodedResult.getEncoding());
    }

    @Test
    public void decodeThenEncode_givesInitialData_long() {
        String bstr = "abcdef1l7aum6echk45nj3s0wdvt2fg8x9yrzpqzd3ryx";

        DecodedResult decodedResult = Bech32.decode(bstr);
        assertEquals("abcdef", decodedResult.getHrp());

        String enc = Bech32.encode(decodedResult.getHrp(), decodedResult.getDp());
        assertEquals(bstr, enc);
    }

    @Test
    public void decodeThenEncode_c1_givesInitialData_long() {
        String bstr = "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw";

        DecodedResult decodedResult = Bech32.decode(bstr);
        assertEquals("abcdef", decodedResult.getHrp());

        String enc = Bech32.encodeUsingOriginalConstant(decodedResult.getHrp(), decodedResult.getDp());
        assertEquals(bstr, enc);
    }

    @Test
    public void decodeThenEncode_givesInitialData_longer() {
        String bstr = "split1checkupstagehandshakeupstreamerranterredcaperredlc445v";

        DecodedResult decodedResult = Bech32.decode(bstr);
        assertEquals("split", decodedResult.getHrp());

        String enc = Bech32.encode(decodedResult.getHrp(), decodedResult.getDp());
        assertEquals(bstr, enc);
    }
    @Test
    public void decodeThenEncode_c1_givesInitialData_longer() {
        String bstr = "split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w";

        DecodedResult decodedResult = Bech32.decode(bstr);
        assertEquals("split", decodedResult.getHrp());

        String enc = Bech32.encodeUsingOriginalConstant(decodedResult.getHrp(), decodedResult.getDp());
        assertEquals(bstr, enc);
    }

}
