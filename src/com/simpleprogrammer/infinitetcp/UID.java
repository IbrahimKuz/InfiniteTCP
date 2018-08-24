package com.simpleprogrammer.infinitetcp;

import java.util.ArrayList;
import java.util.Collections;

//Not in use | creates a unique identifier each time and can create 100000 before looping through them again
public class UID {
	private static ArrayList<Integer> ids = new ArrayList<Integer>();
	private static int index = 0;

	static {
		for (int i = 39193; i < 139193; i++) {
			ids.add(i);
		}

		Collections.shuffle(ids);
	}

	protected static int getID() {
		if (index > 100000) {
			index = 0;
		}
		return ids.get(index++);
	}

}
