package typetodo.sync;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;

/**
 * This class is used to gain access(via OAuth2) to the user's Google Calendar. Upon completion
 * of OAuth2, it generates a Calendar client which can be used to access/manipulate protected data
 * in the user's Google Calendar.
 * The client can be retrieved with the getClient() method.
 * @author Phan Shi Yu
 *
 */
public class GCalAuthenticator {
	/**
	 * Be sure to specify the name of your application. If the application name is {@code null} or
	 * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
	 */
	private final String APPLICATION_NAME;

	/** Directory to store user credentials. */
	private final java.io.File DATA_STORE_DIR;
			
	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	 * globally shared instance across your application.
	 */
	private FileDataStoreFactory dataStoreFactory;

	/** Global instance of the JSON factory. */
	private final JsonFactory JSON_FACTORY;

	/** Global instance of the HTTP transport. */
	private HttpTransport httpTransport;

	@SuppressWarnings("unused")
	private Calendar calendarClient;
	private Tasks tasksClient;
	
	/**
	 * Constructor for GCalAuthenticator
	 * @param applicationName
	 */
	public GCalAuthenticator (String applicationName) {
		this.APPLICATION_NAME = applicationName;
		this.DATA_STORE_DIR =
				new java.io.File(System.getProperty("user.home"), ".store/typetodo.credentials");
		this.JSON_FACTORY = JacksonFactory.getDefaultInstance();
		
		try {
			// initialize the transport
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			// initialize the data store factory
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// authorization
			Credential credential = authorize();

			// set up global Calendar instance
			calendarClient = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
			.setApplicationName(APPLICATION_NAME).build();
			
			// Tasks client
		    setTasksClient(new com.google.api.services.tasks.Tasks.Builder(httpTransport, JSON_FACTORY, credential)
			    .setApplicationName(APPLICATION_NAME).build());

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Returns Google's Calendar client to access user's calendars and events
	 * @return client of type Calendar
	 */
	public Calendar getClient() {
		return calendarClient;
	}

	/** Authorizes the installed application to access user's protected data. */
	private Credential authorize() throws Exception {
		// load client secrets
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(this.getClass().getResourceAsStream("/client_secrets.json")));
		if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
				clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println(
					"Overwrite the src/main/resources/client_secrets.json file with the client secrets file "
							+ "you downloaded from the Quickstart tool or manually enter your Client ID and Secret "
							+ "from https://code.google.com/apis/console/?api=calendar#project:1017848623557 "
							+ "into src/main/resources/client_secrets.json");
			System.exit(1);
		}

		// Set up authorization code flow.
		// Ask for only the permissions you need. Asking for more permissions will
		// reduce the number of users who finish the process for giving you access
		// to their accounts. It will also increase the amount of effort you will
		// have to spend explaining to users what you are doing with their data.
		// Here we are listing all of the available scopes. You should remove scopes
		// that you are not actually using.
		Set<String> scopes = new HashSet<String>();
		scopes.add(CalendarScopes.CALENDAR);
		scopes.add(CalendarScopes.CALENDAR_READONLY);
		scopes.add(TasksScopes.TASKS);
		scopes.add(TasksScopes.TASKS_READONLY);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, scopes)
		.setDataStoreFactory(dataStoreFactory)
		.build();
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	/**
	 * @return the tasksClient
	 */
	public Tasks getTasksClient() {
		return tasksClient;
	}

	/**
	 * @param tasksClient the tasksClient to set
	 */
	public void setTasksClient(Tasks tasksClient) {
		this.tasksClient = tasksClient;
	}
}
