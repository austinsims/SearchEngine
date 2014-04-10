package edu.purdue.austinsims;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Database {
	Connection conn;
	String dbName;

	/**
	 * Create a Database object from a properties file containing the info to
	 * connect to the database, namely jdbc.url, jdbc.username and
	 * jdbc.password. If the server does not already contain a database, create
	 * it and the needed tables.
	 * 
	 * @param propFile contains server info
	 * @throws IOException
	 */
	
	/**
	 * Does the database with the name exist?
	 * @param conn
	 * @param db Which database?
	 */
	public boolean doesDatabaseExist(String db) {
		try {
			Statement s = conn.createStatement();
			String SQL_CHECK_DB_EXIST = String.format("SELECT schema_name FROM information_schema.schemata WHERE schema_name = '%s'", db);
			ResultSet rs =  s.executeQuery(SQL_CHECK_DB_EXIST);
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	Database(Properties props) throws IOException, SQLException {
		String url = props.getProperty("jdbc.url");
		String user = props.getProperty("jdbc.username");
		String password = props.getProperty("jdbc.password");
		dbName = props.getProperty("jdbc.db");

		conn = DriverManager.getConnection(url, user, password);
	}
	
	Database(String url, String user, String password, String dbName) throws SQLException {
		conn = DriverManager.getConnection(url, user, password);
		conn.createStatement().execute(String.format("use %s;",dbName));
	}
	
	
	public boolean hasURL(String url) throws SQLException {
		try {
			Statement s = conn.createStatement();
			ResultSet rs =  s.executeQuery(String.format("SELECT * FROM url WHERE url LIKE '%s'", url));
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Return a map of URLs to descriptions
	 * @param keywords search criteria
	 * @return
	 * @throws SQLException
	 */
	public Map<URL, String> search(String keywords) throws SQLException {
		Set<URL> results = null;
		Map<URL, String> allDescs = new HashMap<URL, String>();
		
		boolean firstWord = true;
		for (String word : keywords.split(" ")) {
			Statement st = conn.createStatement();
			String query = String.format(
					"SELECT url, description " + 
					"FROM url JOIN word ON (url.urlid = word.urlid) " +
					"WHERE word LIKE '%s';",
					word);
			ResultSet rs = st.executeQuery(query);
			Set<URL> s = new HashSet<URL>();
			while (rs.next()) {
				try {
					URL url = new URL(rs.getString(1));
					String d = rs.getString(2); 
					s.add(url);
					allDescs.put(url, d);
				} catch (MalformedURLException e) {
					continue;
				}
			}
			if (firstWord) {
				results = s;
				firstWord = false;
			} else {
				results.retainAll(s);
			}
		}
		
		
		Map<URL, String> selectedDesc = new HashMap<URL, String>();
		for (URL url : allDescs.keySet()) {
			if (results.contains(url))
				selectedDesc.put(url, allDescs.get(url));
		}
		
		return selectedDesc;
	}
	
}