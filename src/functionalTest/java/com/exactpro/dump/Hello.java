package com.exactpro.dump;

import com.exactpro.build.GitStatus;

public class Hello {
	public static void main(String[] args) {
		System.out.printf("Hello branch '%s'", GitStatus.getBranch());
	}
}
