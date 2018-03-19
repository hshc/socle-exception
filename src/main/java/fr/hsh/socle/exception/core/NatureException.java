package fr.hsh.socle.exception.core;

public enum NatureException {
	Technique("T"), 
	Fonctionnelle("F"),
	Applicative("A");

	private String mCode;
	NatureException(final String pCode) {
		this.mCode = pCode;
	}

	public String getCode() {
		return this.mCode;
	}
}