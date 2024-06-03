package com.exactpro.build;

import java.util.regex.Pattern;

public class SemVer implements Comparable<SemVer> {
    public final static Pattern TAG_PATTERN = Pattern.compile("^v(\\d{1,9})\\.(\\d{1,9})\\.(\\d{1,9})$");

    private final int major;
    private final int minor;
    private final int patch;
    private final String suffix;

    @Override
    public int compareTo(SemVer o) {
        int delta = major - o.major;
        if (delta != 0) {
            return delta;
        }
        delta = minor - o.minor;
        if (delta != 0) {
            return delta;
        }
        delta = patch - o.patch;
        if (delta != 0) {
            return delta;
        }
        return nullAwareCompare(suffix, o.suffix);
    }

    private int nullAwareCompare(String suffix1, String suffix2) {
        if (suffix1 == suffix2) { return 0; } // both are null or same String
        if (suffix1 == null) { return 1; }
        if (suffix2 == null) { return -1; }
        return suffix1.compareTo(suffix2);
    }

    public static SemVer fromTagName(String baseTag) {
        java.util.regex.Matcher matcher = TAG_PATTERN.matcher(baseTag);
        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            return new SemVer(major, minor, patch, null);
        }
        throw new IllegalArgumentException(String.format("SemVer tag %s is not matching %s", baseTag, TAG_PATTERN));
    }

    public SemVer withSuffix(String suffix) {
        return new SemVer(major, minor, patch, suffix);
    }

    public SemVer withPatchIncrementedBy(int patchIncrement) {
        if (patchIncrement < 0) {
            throw new IllegalArgumentException("negative patchIncrement " + patchIncrement);
        } else if (patch + patchIncrement < 0) {
            throw new IllegalArgumentException(String.format(
                "patchIncrement %d + patchVersion from %s exceeds Integer.MAX_VALUE", patchIncrement, this));
        }
        return new SemVer(major, minor, patch + patchIncrement, suffix);
    }

    private SemVer(int major, int minor, int patch, String suffix) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d%s", major, minor, patch, suffix == null ? "" : "-" + suffix);
    }
}
