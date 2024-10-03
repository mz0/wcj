package com.exactpro.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Paths {
    public static Path cwd() {
        return FileSystems.getDefault().getPath("").toAbsolutePath();
    }
}
