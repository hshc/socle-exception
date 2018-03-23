package fr.hsh.socle.exception.core;

public interface ISocleException {

	public LogId getLogId();

	public ContextDescriptor getContext();

	public String getCode();

	public String getShortLabel();

	public String getMessage();

	public NatureException getNature();
	
	public ISocleException prefixMessage(final String pMessage);
	
	public ISocleException postfixMessage(final String pMessage);
}
