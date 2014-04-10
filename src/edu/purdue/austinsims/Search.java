package edu.purdue.austinsims;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Search
 */
@WebServlet("/")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306";
	private static final String JDBC_DB = "crawler";
	private static final String JDBC_USER = "root";
	private static final String JDBC_PASS = "";
       
	
	private static final String searchFormFmt = 
			"<form action='#' method='get'>" +
			"<input name='query' type='text' value='%s'/>" + 
			"<input type='submit' value='Search' />" + 
			"</form>";
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("<h1>Web Search</h1>");
		
		String query = request.getParameter("query");
		if (query == null) {
			// Display form
			out.write(String.format(searchFormFmt,""));
		} else {
			// Display form again
			out.write(String.format(searchFormFmt, query));
			out.write("<hr>");
			// Get results
			Database db;
			try {
				db = new Database(JDBC_URL, JDBC_USER, JDBC_PASS, JDBC_DB);
				out.printf("<p>Search results for '%s':</p>", query);
				Map<URL, String> results = db.search(query);
				for (URL url : results.keySet()) {
					out.print(
							"<p>" +
							"<a href='" + url + "'>" + url + "</a> <br />" +
							results.get(url) +
							"</p>"
							);
				}
			} catch (SQLException e) {
				out.println("<p>Sorry, an internal database error has occurred.</p>");
				out.println("<hr>");
				e.printStackTrace(out);
			}
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
