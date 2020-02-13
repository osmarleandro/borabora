package com.noctarius.borabora.spi;

public class ExtractedSuperClass {

	/**
	 * Returns if <tt>first</tt> and <tt>second</tt> are equal.
	 *
	 * @param first  first object
	 * @param second second object
	 * @return true if both objects are equal, otherwise false
	 */
	public static boolean equals(Object first, Object second) {
	    String name = first.getClass().getName();
	    String otherName = second.getClass().getName();
	
	    if (name.contains("$$Lambda$") && !otherName.contains("$$Lambda$") //
	            || !name.contains("$$Lambda$") && otherName.contains("$$Lambda$")) {
	
	        return false;
	    }
	
	    if (name.contains("$$Lambda$") && otherName.contains("$$Lambda$")) {
	        int nameIndex = name.indexOf("$$Lambda$");
	        int otherNameIndex = otherName.indexOf("$$Lambda$");
	
	        int nameEndIndex = name.indexOf('/', nameIndex);
	        int otherNameEndIndex = name.indexOf('/', otherNameIndex);
	
	        return name.substring(nameIndex, nameEndIndex).equals(otherName.substring(otherNameIndex, otherNameEndIndex));
	    }
	
	    return first.equals(second);
	}

	public ExtractedSuperClass() {
		super();
	}

}