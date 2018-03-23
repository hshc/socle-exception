package fr.hsh.socle.exception.core;

import fr.hsh.socle.context.ThreadContextParams;

public abstract class SocleRuntimeException extends RuntimeException implements ISocleException {


	private final LogId				mLogId;
	private final ContextDescriptor	mContext;
	private final String				mCode;
	private final String				mLibelleCourt;

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt, final String pMessage, final Throwable pCause) {
		super(pMessage + "(" + pCause.getMessage() + ")", pCause);
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mLibelleCourt = pLibelleCourt;
	}

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt, final String pMessage, final ISocleException pCause) {
		super(pMessage + "(" + pCause.getMessage() + ")", (Throwable)((pCause instanceof Throwable)?pCause:null));
		final ISocleException lCause = pCause;
		this.mLogId = lCause.getLogId();
		this.mContext = lCause.getContext();
		this.mCode = lCause.getCode();
		this.mLibelleCourt = lCause.getShortLabel();
	}

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt, final Throwable pCause) {
		super(pCause.getMessage(), pCause);
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mLibelleCourt = pLibelleCourt;
	}

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt, final ISocleException pCause) {
		super(pCause.getMessage(), (Throwable)((pCause instanceof Throwable)?pCause:null));
		final ISocleException lCause = pCause;
		this.mLogId = lCause.getLogId();
		this.mContext = lCause.getContext();
		this.mCode = lCause.getCode();
		this.mLibelleCourt = lCause.getShortLabel();
	}

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt, final String pMessage) {
		super(pMessage);
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mLibelleCourt = pLibelleCourt;
	}

	protected SocleRuntimeException(final String pCode, final String pLibelleCourt) {
		super(pLibelleCourt);
		this.mLogId = new LogId();
		this.mContext = createContextDescriptor(this.getStackTrace()[0].getClassName(), this.getStackTrace()[0].getMethodName(), this.getStackTrace()[0].getLineNumber());
		this.mCode = pCode;
		this.mLibelleCourt = pLibelleCourt;
	}

	static final private ContextDescriptor createContextDescriptor(final String pNomClasse, final String pNomMethode, final int pNumLigne) {
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
		return this.mLibelleCourt;
	}

	@Override
	public String getMessage() {
		String lMessage = super.getMessage();
		if ((lMessage == null) || (lMessage.length() == 0)) {
			return this.mLibelleCourt;
		} else {
			return lMessage;
		}
	}

	public abstract NatureException getNature();

}
