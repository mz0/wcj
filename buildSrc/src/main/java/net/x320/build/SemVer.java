package net.x320.build;

public class SemVer {
    public final static int maxDigitsMajor = 2; // 99 < 214; see ordinal() comment
    public final static int maxDigitsMinor = 4; // Dec. 31 => 1231 < 7483
    public final static int maxDigitsPatch = 3;
    public final static String regEx = String.format("^v(\\d{1,%d})\\.(\\d{1,%d})\\.(\\d{1,%d})$",
            maxDigitsMajor, maxDigitsMinor, maxDigitsPatch);
}
