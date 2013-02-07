package org.designwizard.design.manager.util;

import java.util.StringTokenizer;

import org.designwizard.design.DesignIF;

public class TranslatorUtil {

	/**
	 * Receives a signature extracted from bytecode and translates it to java
	 * pattern.
	 * 
	 * @param parameter
	 * @return
	 */
	public String extractSignature(String parameter) {
		if (parameter.length() == 0) {
			return "";
		}
		return translate(parameter);
	}

	/**
	 * Translate a parameter declared on bytecode to java's pattern.
	 * 
	 * @param parameter
	 *            The parameter to be translated.
	 * @return The parameter translated to java pattern.
	 */
	private String translate(String parameter) {

		StringBuffer arrayPre = new StringBuffer();
		StringBuffer arrayPos = new StringBuffer();

		while (parameter.charAt(0) == '[') {
			arrayPre.append("[");
			arrayPos.append(";");
			parameter = parameter.substring(1);
		}

		if (parameter.length() == 1) {

			String translation = parameter;
			if (!arrayPre.toString().contains("["))
				translation = translateKey(parameter.charAt(0));
			return arrayPre + translation;
		} else if (parameter.charAt(0) == 'L' && parameter.contains(";")) {
			if (arrayPre.toString().contains("["))
				arrayPre.append("L");
			int separator = parameter.indexOf(';');
			if (separator == parameter.length() - 1) {
				return arrayPre + parameter.substring(1, separator) + arrayPos;
			} else {
				return arrayPre
						+ parameter.substring(1, separator)
						+ arrayPos
						+ ","
						+ extractSignature(parameter.substring(separator + 1,
								parameter.length()));
			}
		} else {
			String translation = parameter.substring(0, 1);
			if (!arrayPre.toString().contains("["))
				translation = translateKey(parameter.charAt(0));
			return arrayPre + translation
					+ "," + extractSignature(parameter.substring(1));
		}
	}

	/**
	 * Translate the specified char to the corresponding primitive type.
	 * 
	 * @param key
	 *            the char that identifies a primitive type on bytecode.
	 * @return a string representation of the primitive type. void is also seen
	 *         as a primitive type.
	 */
	private String translateKey(char key) {
		switch (key) {
		case 'C':
			return "char";
		case 'S':
			return "short";
		case 'I':
			return "int";
		case 'B':
			return "byte";
		case 'J':
			return "long";
		case 'F':
			return "float";
		case 'Z':
			return "boolean";
		case 'D':
			return "double";
		case 'V':
			return "void";
		default:
			return "";
		}
	}

	/**
	 * Receives an entity extracted from bytecode and translates it to java
	 * pattern.
	 */
	public String translateBytecodeToJavaPattern(String signature) {
		// debug
//		 System.out.println(signature);

		String callersSignature = "";
		String callerNameFormated = "";
		String staticMethod = "";
		String returnValue = "";

		if (signature.startsWith(DesignIF.METHOD_IDENTIFIER)
				|| signature.startsWith(DesignIF.STATIC_IDENTIFIER)) {

			StringTokenizer st = new StringTokenizer(signature);

			if (signature.startsWith(DesignIF.STATIC_IDENTIFIER)) {
				staticMethod = st.nextToken();
			}
			callerNameFormated = st.nextToken();
			callersSignature = st.nextToken();
			callersSignature = callersSignature.substring(1,
					callersSignature.indexOf(")"));

			returnValue = callerNameFormated + "("
					+ this.extractSignature(callersSignature) + ")";
			if (!(staticMethod.length() == 0)) {
				returnValue = staticMethod + callerNameFormated + "("
						+ this.extractSignature(callersSignature) + ")";
			}

			return returnValue;
		} else if (signature.contains(";") || signature.contains("[")
				|| (signature.length() == 1)) {
			return this.extractSignature(signature);
		}
		return signature;
	}
}