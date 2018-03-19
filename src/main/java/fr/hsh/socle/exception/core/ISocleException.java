package fr.hsh.socle.exception.core;

public interface ISocleException {

	public LogId getLogId();

	public ContextDescriptor getContext();

	public String getCode();

	public String getLibelleCourt();

	public String getMessage();

	public NatureException getNature();
}
