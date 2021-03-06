package fr.hsh.socle.exception.core;

import java.io.Serializable;

public final class ContextDescriptor implements Serializable {

	final private String	mUser;
	final private long	mTimestamp;
	final private String	mApplicationID;
	final private String	mServerIP;
	final private String	mNomClasse;
	final private String	mNomMethode;
	final private int		mNumLigne;

	ContextDescriptor(final String pUser, final String pApplicationID, final String pServerIP, final String pNomClasse, final String pNomMethode, final int pNumLigne) {
		super();
		this.mUser = pUser;
		this.mTimestamp = System.currentTimeMillis();
		this.mApplicationID = pApplicationID;
		this.mServerIP = pServerIP;
		this.mNomClasse = pNomClasse;
		this.mNomMethode = pNomMethode;
		this.mNumLigne = pNumLigne;
	}

	public String getUser() {
		return this.mUser;
	}

	public long getTimestamp() {
		return this.mTimestamp;
	}

	public String getApplicationID() {
		return this.mApplicationID;
	}

	public String getServerIP() {
		return this.mServerIP;
	}

	public String getNomClasse() {
		return this.mNomClasse;
	}

	public String getNomMethode() {
		return this.mNomMethode;
	}

	public int getNumLigne() {
		return this.mNumLigne;
	}
}
