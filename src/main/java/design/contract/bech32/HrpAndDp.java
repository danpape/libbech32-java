package design.contract.bech32;

import java.util.Arrays;
import java.util.Objects;

public class HrpAndDp {
    private String hrp;
    private char[] dp;
    private Encoding encoding;

    public HrpAndDp() {
        this.encoding = Encoding.UNKNOWN;
    }

    public HrpAndDp(String hrp, char[] dp) {
        this.hrp = hrp;
        this.dp = dp;
        this.encoding = Encoding.UNKNOWN;
    }

    public String getHrp() {
        return hrp;
    }

    public void setHrp(String hrp) {
        this.hrp = hrp;
    }

    public char[] getDp() {
        return dp;
    }

    public void setDp(char[] dp) {
        this.dp = dp;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HrpAndDp hrpAndDp = (HrpAndDp) o;
        return hrp.equals(hrpAndDp.hrp) &&
                Arrays.equals(dp, hrpAndDp.dp) &&
                encoding == hrpAndDp.encoding;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hrp, encoding);
        result = 31 * result + Arrays.hashCode(dp);
        return result;
    }

    public enum Encoding {
        NONE, UNKNOWN, BECH32, BECH32M;
    }
}
