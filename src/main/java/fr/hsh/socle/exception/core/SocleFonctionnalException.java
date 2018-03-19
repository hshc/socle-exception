package fr.hsh.socle.exception.core;


public class SocleFonctionnalException extends SocleException {

	private static final NatureException mNature = NatureException.Fonctionnelle;


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt, final ISocleException pCause) {
		super(pCode, pLibelleCourt, pCause);
		// TODO Auto-generated constructor stub
	}


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt, final String pMessage, final ISocleException pCause) {
		super(pCode, pLibelleCourt, pMessage, pCause);
		// TODO Auto-generated constructor stub
	}


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt, final String pMessage, final Throwable pCause) {
		super(pCode, pLibelleCourt, pMessage, pCause);
		// TODO Auto-generated constructor stub
	}


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt, final String pMessage) {
		super(pCode, pLibelleCourt, pMessage);
		// TODO Auto-generated constructor stub
	}


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt, final Throwable pCause) {
		super(pCode, pLibelleCourt, pCause);
		// TODO Auto-generated constructor stub
	}


	public SocleFonctionnalException(final String pCode, final String pLibelleCourt) {
		super(pCode, pLibelleCourt);
		// TODO Auto-generated constructor stub
	}


	@Override
	public NatureException getNature() {
		return SocleFonctionnalException.mNature;
	}

}
