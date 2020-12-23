package com.heqifuhou.sortlistview;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<Object> {
	public int compare(Object _o1, Object _o2) {
		GroupMemberBean o1 = (GroupMemberBean)_o1;
		GroupMemberBean o2 = (GroupMemberBean)_o2;
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
