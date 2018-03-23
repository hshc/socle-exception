package fr.hsh.socle.exception.core;

import fr.hsh.socle.context.ThreadContextParams;

public abstract class SocleException extends Exception implements ISocleException {

	private static final long serialVersionUID = 7614802352089716125L;
	
	private final LogId				mLogId;
	private final ContextDescriptor	mContext;
	private final String			mCode;
	private final String			mShortLabel;
	private String					mMessage;

	protected SocleException(final String pCode, final String pLibelleCourt, final Throwable pCause) {
		super(pCause.getMessage(), pCause);
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mShortLabel = pLibelleCourt;
	}
	protected SocleException(final String pCode, final String pLibelleCourt, final String pMessage, final Throwable pCause) {
		this(pCode, pLibelleCourt, pCause);		
		this.mMessage = pMessage + "( caused by '" + pCause.getMessage() + "')";
	}

	protected SocleException(final String pCode, final String pLibelleCourt, final ISocleException pCause) {
		super(pCause.getMessage(), (Throwable)((pCause instanceof Throwable)?pCause:null));
		final ISocleException lCause = pCause;
		this.mLogId = lCause.getLogId();
		this.mContext = lCause.getContext();
		this.mCode = pCode==null?lCause.getCode():pCode;
		this.mShortLabel = pLibelleCourt==null?lCause.getShortLabel():pLibelleCourt;
	}
	protected SocleException(final String pCode, final String pLibelleCourt, final String pMessage, final ISocleException pCause) {
		this(pCode, pLibelleCourt, pCause);
		this.mMessage = pMessage + "( caused by '" + pCause.getMessage() + "')";
	}

	protected SocleException(final String pCode, final String pLibelleCourt) {
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mShortLabel = pLibelleCourt;
	}

	protected SocleException(final String pCode, final String pLibelleCourt, final String pMessage) {
		this(pCode, pLibelleCourt);
		this.mMessage = pMessage;
	}

	private static final ContextDescriptor createContextDescriptor(final String pNomClasse, final String pNomMethode, final int pNumLigne) {
		final ThreadContextParams context = ThreadContextParams.getContext();
		final String lUser = context.getUser();
		final String lApplicationID = context.getAppName();
		final String lServerIP = context.getServerIp();
		return new ContextDescriptor(lUser, lApplicationID, lServerIP, pNomClasse, pNomMethode, pNumLigne);
	}

	public LogId getLogId() {
		return this.mLogId;
	}

	public ContextDescriptor getContext() {
		return this.mContext;
	}

	public String getCode() {
		return this.mCode;
	}

	public String getShortLabel() {
		return this.mShortLabel;
	}

	@Override
	public String getMessage() {
		if (this.mMessage == null || this.mMessage.isEmpty()) {
			String lMessage = super.getMessage();
			if (lMessage == null || lMessage.isEmpty()) {
				return this.mShortLabel;
			} else {
				this.mMessage = lMessage;
			}
		} 
		return this.mMessage;
	}
	
	public ISocleException prefixMessage(final String pMessage) {
		this.mMessage = pMessage + getMessage();
		return this;
	}
	
	public ISocleException postfixMessage(final String pMessage) {
		this.mMessage = getMessage() + pMessage;
		return this;
	}
	
	public abstract NatureException getNature();
}