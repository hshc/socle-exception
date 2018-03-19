package fr.hsh.socle.exception.core;

import java.io.Serializable;

public final class LogId implements Serializable {
	private final String	mId;

	LogId() {
		super();
		this.mId = String.valueOf(System.currentTimeMillis());
	}

	@Override
	public String toString() {
		return this.mId;
	}

}
