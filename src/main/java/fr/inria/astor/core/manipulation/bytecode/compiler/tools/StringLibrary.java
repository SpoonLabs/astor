package fr.inria.astor.core.manipulation.bytecode.compiler.tools;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringLibrary {

	public static String unique(String string) {
		return string.intern();
	}
	
	public static List<String> split(String chainedStrings, Character character) {
		return split(chainedStrings, format("[%c]", character));
	}
	
	public static List<String> split(String chainedStrings, String splittingRegex) {
		return asList(chainedStrings.split(splittingRegex));
	}
	
	public static String join(Collection<String> subStrings, Character connector) {
		return join(subStrings, "" + connector);
	}
	
	public static String join(Collection<String> subStrings, String connector) {
		StringBuilder joined = new StringBuilder();
		if (! subStrings.isEmpty()) {
			Iterator<String> iterator = subStrings.iterator();
			joined.append(iterator.next());
			while (iterator.hasNext()) {
				joined.append(connector + iterator.next());
			}
		}
		return joined.toString();
	}
	
	public static String stripEnd(String string, String suffix) {
		if (string.endsWith(suffix)) {
			return string.substring(0, string.length() - suffix.length());
		}
		return string;
	}
	
	public static String firstAfterSplit(String string, Character character) {
		return firstAfterSplit(string, format("[%c]", character));
	}
	
	public static String firstAfterSplit(String string, String splittingRegex) {
		List<String> splitted = split(string, splittingRegex);
		if (! splitted.isEmpty()) {
			return splitted.get(0);
		}
		return string;
	}
	
	public static String lastAfterSplit(String string, Character character) {
		return lastAfterSplit(string, format("[%c]", character));
	}
	
	public static String lastAfterSplit(String string, String splittingRegex) {
		List<String> splitted = split(string, splittingRegex);
		if (! splitted.isEmpty()) {
			return splitted.get(splitted.size() - 1);
		}
		return string;
	}
	
	public static String reversed(String string) {
		int length = string.length();
		StringBuilder builder = new StringBuilder(length);
		for (int index = length - 1; index >= 0; index -= 1) {
			builder.append(string.charAt(index));
		}
		return builder.toString();
	}
	
	public static List<String> toStringList(Collection<? extends Object> objects) {
		return toStringList(objects, "null");
	}
	
	public static List<String> toStringList(Collection<? extends Object> objects, String nullString) {
		List<String> toStringList = new LinkedList<String>();
		for (Object object : objects) {
			if (object == null) {
				toStringList.add(nullString);
			} else {
				toStringList.add(object.toString());
			}
		}
		return toStringList;
	}
	
	public static <K, V> Map<String, V> toStringMap(Map<K, V> sourceMap) {
		Map<String, V> toStringMap = new HashMap<String, V>();
		for (K key : sourceMap.keySet()) {
			toStringMap.put(key.toString(), sourceMap.get(key));
		}
		return toStringMap;
	}
	
	public static int maximumToStringLength(Collection<? extends Object> objects, int lengthOfNullToString) {
		int length = 0;
		for (Object object : objects) {
			int objectLength = lengthOfNullToString;
			if (object != null) {
				objectLength = object.toString().length();
			}
			length = Math.max(length, objectLength);
		}
		return length;
	}
	
	public static String rightFilled(String string, int targetLength, Character filler) {
		int difference = 0;
		int length = string.length();
		if (length < targetLength) {
			difference = targetLength - length;
		}
		return string + repeated(filler, difference);
	}
	
	public static Collection<String> rightFilled(Collection<String> strings, int targetLength, Character filler) {
		Collection<String> filled = new ArrayList<String>(strings.size());
		for (String string : strings) {
			filled.add(rightFilled(string, targetLength, filler));
		}
		return filled;
	}
	
	public static String leftFilled(String string, int targetLength, Character filler) {
		int difference = 0;
		int length = string.length();
		if (length < targetLength) {
			difference = targetLength - length;
		}
		return repeated(filler, difference) + string;
	}
	
	public static Collection<String> leftFilled(Collection<String> strings, int targetLength, Character filler) {
		Collection<String> filled = new ArrayList<String>(strings.size());
		for (String string : strings) {
			filled.add(leftFilled(string, targetLength, filler));
		}
		return filled;
	}
	
	public static String repeated(Character character, int times) {
		return repeated("" + character, times);
	}
	
	public static String repeated(String text, int times) {
		StringBuilder builder = new StringBuilder();
		if (times > 0) {
			for (int i = 0; i < times; i += 1) {
				builder.append(text);
			}
		}
		return builder.toString();
	}

	public static String plainDecimalRepresentation(Number number) {
		double doubleValue = number.doubleValue();
		BigDecimal decimal = BigDecimal.valueOf(doubleValue);
		if (doubleValue >  1.0 || doubleValue < -1.0) {
			DecimalFormat decimalFormat = new DecimalFormat("#0.0");
			return decimalFormat.format(decimal);
		} else {
			return decimal.toPlainString();
		}
	}
}

