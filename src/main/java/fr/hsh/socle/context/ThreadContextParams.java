package fr.hsh.socle.context;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.hsh.socle.exception.core.ISocleException;

/**
 * <h3>Description:</h3>
 * Cette classe permet de referencer un contexte technique propre a chaque thread.<br>
 * Elle est utilisee dans le cadre de la remontee des exceptions et permet de recuperer les informations contextuelles pertinentes lors de la construction de l'exception.<br>
 * Ces informations contextuelles sont les suivantes:<br>
 * <li>Utilisateur du service</li>
 * <li>Nom du service</li>
 * <li>Version du service</li>
 * <li>Adresse du serveur du service</li>
 * <li>Duree de la session du thread</li>
 * <li>...</li>
 * <h3>Utilisation:</h3>
 * <tt>
 * initializeContext(..)<br>
 * ...<br>
 * getContext()<br>
 * ...<br>
 * cleanContext()<br>
 * </tt>
 *  @version 
 *  @author
 */
public class ThreadContextParams {

	/**
	 * Variable systeme (<tt>System.getProperty(sSERVICE_USER_SYSTEM_PROP)</tt>) utilisee pour passer le compte technique du service.<br>
	 * Ex: <tt>-DServiceUser=YXZ021</tt>
	 * 
	 */
	public static String								sSERVICE_USER_SYSTEM_PROP	= "ServiceUser";
	private static ThreadLocal<ThreadContextParams>	sTHREAD_LOCAL				= new ThreadLocal<ThreadContextParams>();

	private static Manifest							sMANIFEST					= null;
	private static boolean							sINITIALIZED				= false;

	private static String								sAPP_NAME					= "UNKNOWN_APP_NAME";
	private static String								sAPP_VERSION				= "UNKNOWN_APP_VERSION";
	private static String								sSERVER_IP					= "UNKNOWN_SERVER";

	private String										mUser						= "UNKNOWN_USER";
	private final long								mBeginSession;
	private final List<ISocleException> 				mListException 				= new ArrayList<ISocleException>();
	private final Map<String, String>					mProperties					= new HashMap<String, String>();

	private ThreadContextParams(final String pUser, final long pBeginSession) {
		super();
		String lServiceUser = System.getProperty(sSERVICE_USER_SYSTEM_PROP);
		this.mUser = (pUser != null ? pUser : (lServiceUser != null ? lServiceUser : "ANONYMOUS" ));
		this.mBeginSession = pBeginSession;
	}

	public Manifest getManifest() {
		return sMANIFEST;
	}

	public String getAppName() {
		return sAPP_NAME;
	}

	public String getAppVersion() {
		return sAPP_VERSION;
	}

	public String getServerIp() {
		return sSERVER_IP;
	}

	public String getUser() {
		return this.mUser;
	}

	public long getSessionDuration() {
		return System.currentTimeMillis() - this.mBeginSession;
	}

	public List<ISocleException> getListException() {
		return this.mListException;
	}

	public void addExceptionToList(ISocleException pSocleException) {
		this.mListException.add(pSocleException);
	}

	public String getProperty(final String pKey) {
		return this.mProperties.get(pKey);
	}

	public void addProperty(final String pKey, final String pValue) {
		this.mProperties.put(pKey, pValue);
	}

	public String getThreadName() {
		return Thread.currentThread().getName();
	}

	public long getThreadId() {
		return Thread.currentThread().getId();
	}

	/**
	 * Pour recuperer les donnees contextuelle au thread courant.<br>
	 * Si le contexte n'a pas ete precedement initialise, une alerte est donnee, et un contexte '<tt>ANONYMOUS</tt>' est associe au thread pour etre enfin retourne par la methode.
	 * 
	 * @return
	 */
	public static ThreadContextParams getContext() {
		ThreadContextParams lContext = sTHREAD_LOCAL.get();

		if (!sINITIALIZED) {
			System.out.println("Le contexte n'a pas été initialisé, le contexte associé sera un contexte \"ANONYME\"");
		}

		if (lContext == null) {
			lContext = new ThreadContextParams("ANONYMOUS", System.currentTimeMillis());
			sTHREAD_LOCAL.set(lContext);
		}
		return lContext;
	}

	public static void cleanContext() {
		sTHREAD_LOCAL.remove();
	}

	/**
	 * <h3>Description:</h3>
	 * Initialise le contexte avec l'utilisateur '<tt>pUser</tt>' et les donnees du <tt>MANIFEST</tt>
	 * 
	 * @param pUser
	 * @param pMainProgramClass
	 */
	public static void initializeContext(final String pUser) {
		initializeContext(pUser, null);
	}

	/**
	 * <h3>Description:</h3>
	 * Initialise le contexte avec l'utilisateur '<tt>pUser</tt>' et le <tt>MANIFEST</tt> du module contenant la classe '<tt>pMainProgramClass</tt>'
	 * 
	 * @param pUser
	 * @param pMainProgramClass
	 */
	public static void initializeContext(final String pUser, final Class pMainProgramClass) {

		if (!sINITIALIZED) {
			try {
				sMANIFEST = getManifestFromClass(pMainProgramClass);
				String appName = sMANIFEST.getMainAttributes().getValue("Implementation-Title");
				String appVersion = sMANIFEST.getMainAttributes().getValue("Implementation-Version");
				if (appName != null) {
					sAPP_NAME = appName;
				}
				if (appVersion != null) {
					sAPP_VERSION = appVersion;
				}
			} catch (final URISyntaxException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}

			try {
				sSERVER_IP = new String(InetAddress.getLocalHost().getHostAddress());
			} catch (final UnknownHostException e) {
				e.printStackTrace();
			}

			sINITIALIZED = true;
		}

		final ThreadContextParams lContext = new ThreadContextParams(pUser, System.currentTimeMillis());
		sTHREAD_LOCAL.set(lContext);
	}

	/**
	 * Savoir si le contexte du thread courant a deja ete initialise ou pas.
	 * 
	 * @return
	 */
	public static boolean isInitialized() {
		return sINITIALIZED;
	}

	private static Manifest getManifestFromClass(final Class pMainProgramClass) throws URISyntaxException, IOException {
		URI lRoot = null;
		String lRootPath = null;
		URL lManifestUrl = null;
		Manifest lManifest = null;
		if (pMainProgramClass == null) {
			lManifestUrl = Thread.currentThread().getContextClassLoader().getResource(JarFile.MANIFEST_NAME);
			lManifest = new Manifest(lManifestUrl.openStream());
		} else {
			lRoot = pMainProgramClass.getProtectionDomain().getCodeSource().getLocation().toURI();
			lRootPath = lRoot.toString();
			final int end = lRootPath.lastIndexOf("WEB-INF");
			// cas d'une application WEB
			if (end != -1) {
				final String endPath = lRootPath.substring(end);
				lRootPath = lRootPath.substring(0, end);
				for (final Matcher recherche = Pattern.compile("!").matcher(endPath); recherche.find();) {
					lRootPath = lRootPath.substring(lRootPath.indexOf(":") + 1);
				}
				lManifestUrl = new URL(lRootPath + JarFile.MANIFEST_NAME);
				lManifest = new Manifest(lManifestUrl.openStream());
			} else {
				// cas d'un batch
				lManifestUrl = new URL(lRootPath + JarFile.MANIFEST_NAME);
				lManifest = new Manifest(lManifestUrl.openStream());
			}
		}
		return lManifest;
	}

	public static void main(final String[] args) {
		System.out.println("ThreadContextParams.main() debut");

		ThreadContextParams.initializeContext(null, ThreadContextParams.class);
		final ThreadContextParams context = ThreadContextParams.getContext();
		System.out.println(context.getAppName());
		System.out.println(context.getAppVersion());
		System.out.println(context.getServerIp());
		System.out.println(context.getUser());
		System.out.println(context.getSessionDuration());

		final String str = "foo";
		System.out.println(str);

		Manifest mManifest = null;
		try {
			mManifest = ThreadContextParams.getManifestFromClass(context.getClass());

			ClassLoader cl = context.getClass().getClassLoader();
			URL a = null;
			System.err.println("context.getClass().getResource( -");
			a = context.getClass().getResource("ThreadContextParams.class"); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/fr/hsh/socle/context/ThreadContextParams.class"
			System.out.println("ThreadContextParams.class : " + a);
			a = context.getClass().getResource("/ThreadContextParams.class"); // KO
			System.out.println("/ThreadContextParams.class : " + a);
			a = context.getClass().getResource(JarFile.MANIFEST_NAME); // KO
			System.out.println(JarFile.MANIFEST_NAME + " : " + a);
			a = context.getClass().getResource("/" + JarFile.MANIFEST_NAME); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/META-INF/MANIFEST.MF"
			System.out.println("/" + JarFile.MANIFEST_NAME + " : " + a);
			a = context.getClass().getResource("/"); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/"
			System.out.println("/ : " + a);
			a = context.getClass().getResource(""); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/fr/hsh/socle/context/"
			System.out.println("'' : " + a);
			System.err.println("cl.getResource( -");
			a = cl.getResource("ThreadContextParams.class"); // KO
			System.out.println("ThreadContextParams.class : " + a);
			a = cl.getResource("/ThreadContextParams.class"); // KO
			System.out.println("/ThreadContextParams.class : " + a);
			a = cl.getResource(JarFile.MANIFEST_NAME); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/META-INF/MANIFEST.MF"
			System.out.println(JarFile.MANIFEST_NAME + " : " + a);
			a = cl.getResource("/" + JarFile.MANIFEST_NAME); // KO
			System.out.println("/" + JarFile.MANIFEST_NAME + " : " + a);
			a = cl.getResource("/"); // KO
			System.out.println("/ : " + a);
			a = cl.getResource(""); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/"
			System.out.println("'' : " + a);


			cl = Thread.currentThread().getContextClassLoader();
			a = context.getClass().getResource("ThreadContextParams.class"); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/fr/hsh/socle/context/ThreadContextParams.class"
			System.out.println("ThreadContextParams.class : " + a);
			a = context.getClass().getResource("/ThreadContextParams.class"); // KO
			System.out.println("/ThreadContextParams.class : " + a);
			a = context.getClass().getResource(JarFile.MANIFEST_NAME); // KO
			System.out.println(JarFile.MANIFEST_NAME + " : " + a);
			a = context.getClass().getResource("/" + JarFile.MANIFEST_NAME); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/META-INF/MANIFEST.MF"
			System.out.println("/" + JarFile.MANIFEST_NAME + " : " + a);
			a = context.getClass().getResource("/"); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/"
			System.out.println("/ : " + a);
			a = context.getClass().getResource(""); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/fr/hsh/socle/context/"
			System.out.println("'' : " + a);
			System.err.println("cl.getResource( -");
			a = cl.getResource("ThreadContextParams.class"); // KO
			System.out.println("ThreadContextParams.class : " + a);
			a = cl.getResource("/ThreadContextParams.class"); // KO
			System.out.println("/ThreadContextParams.class : " + a);
			a = cl.getResource(JarFile.MANIFEST_NAME); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/META-INF/MANIFEST.MF"
			System.out.println(JarFile.MANIFEST_NAME + " : " + a);
			a = cl.getResource("/" + JarFile.MANIFEST_NAME); // KO
			System.out.println("/" + JarFile.MANIFEST_NAME + " : " + a);
			a = cl.getResource("/"); // KO
			System.out.println("/ : " + a);
			a = cl.getResource(""); // OK
			// "file:/home/nta/workspaces/workspace_adinfos/socle-exception/target/classes/"
			System.out.println("'' : " + a);

			System.out.println("ThreadContextParams");
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ThreadContextParams.main() out");
	}
}
