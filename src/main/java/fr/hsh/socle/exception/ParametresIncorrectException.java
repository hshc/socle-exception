/**
 * 
 */
package fr.hsh.socle.exception;

import fr.hsh.socle.exception.core.SocleFonctionnalException;

/**
 * @author idei065
 *
 */
public class ParametresIncorrectException extends SocleFonctionnalException {

	//	/**
	//	 * 
	//	 */
	//	public ParametresIncorrectException() {
	//		super();
	//	}
	//
	//	/**
	//	 * @param message
	//	 */
	//	public ParametresIncorrectException(final String message) {
	//		super(message);
	//		// TODO Auto-generated constructor stub
	//	}

	/**
	 * @param message
	 */
	public ParametresIncorrectException(final String code, final String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	//	/**
	//	 * @param cause
	//	 */
	//	public ParametresIncorrectException(final Throwable cause) {
	//		super(cause);
	//		// TODO Auto-generated constructor stub
	//	}
	//
	//	/**
	//	 * @param message
	//	 * @param cause
	//	 */
	//	public ParametresIncorrectException(final String message, final Throwable cause) {
	//		super(message, cause);
	//		// TODO Auto-generated constructor stub
	//	}

}
