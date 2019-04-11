package NBADraftProject.NBADraftProject;

import java.security.AccessControlContext;
import java.security.MessageDigest;
import org.springframework.web.bind.annotation.*; 
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.*;
import javax.xml.ws.spi.http.HttpContext;

import java.security.NoSuchAlgorithmException;

import java.sql.*;

import org.json.JSONObject;
import org.json.JSONArray;

@RestController
public class UserController {
	static int count = 0; // this is bad cause this int is not thread safe, so it would not be consistence across multiple users
	//same logic behind databases, imagine the wallet system, but you only have 5 dollars and you get charged 3 dollars at the same time, in theory you are capable to handling each one individually
	//but not both
	
	//create a transaction
	@RequestMapping(value = "/register", method = RequestMethod.POST) // <-- setup the endpoint URL at /hello with the HTTP POST method
	public ResponseEntity<String> register(@RequestBody String payload, HttpServletRequest request) {//springboot has a thread pool that handles the server with multiple users
		JSONObject payloadObj = new JSONObject(payload);
		String username = payloadObj.getString("username"); //Grabbing name and age parameters from URL
		String password = payloadObj.getString("password");

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
		HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.set("Content-Type", "application/json");
    	responseHeaders.set("Access-Control-Allow-Origin", "*");
		
		MessageDigest digest = null;
		String hashedKey = null;
		
		//hashedKey = BCrypt.hashpw(password, BCrypt.gensalt());
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String nameToPull = request.getParameter("username");
			Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com:3306/NBAFantasy?user=root&password=Ethaneddie123");
			String SearchQuery = "SELECT owner FROM Users WHERE owner = ?";
			PreparedStatement state = conn.prepareStatement(SearchQuery);
	    	state.setString(1, nameToPull);
	    	ResultSet rs = state.executeQuery();
	    	boolean isNew = true;
	        	while (rs.next()) {
	        		if(rs.getString("owner").equals(username)) {
	        			isNew = false;
	        		}
	        	}
		
		
    		if (isNew) {
				MyServer.users.put(username, password); //we would use hashedkey once I understand it a bit better
				//put the SQL connection in here
			
				String transactionQuery = "START TRANSACTION";
	    		PreparedStatement stmt;
	        	stmt = conn.prepareStatement(transactionQuery);
	        	stmt.execute();
	        	String query = "INSERT INTO Users (owner, hashedPassword, league, team, venmoID, email) VALUES (?, ?, ?, ?, ?, ?);";
	        	stmt = conn.prepareStatement(query);
	        	stmt.setString(1, username);
	        	stmt.setString(2, password);
	        	stmt.setNull(3, Types.VARCHAR);
	        	stmt.setNull(4, Types.VARCHAR);
	        	stmt.setNull(5, Types.VARCHAR);
	        	stmt.setNull(6, Types.VARCHAR);
	        	stmt.execute();
				conn.close();
			}else {
				JSONObject responseObj = new JSONObject();
				responseObj.put("message", "username taken");
				conn.close();
				return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			}
			//Returns the response with a String, headers, and HTTP status
			JSONObject responseObj = new JSONObject();
			responseObj.put("username", username);
			responseObj.put("message", "user registered");
			return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
		} catch (SQLException|ClassNotFoundException e ) {
			e.printStackTrace();
			return new ResponseEntity("Check console", responseHeaders , HttpStatus.BAD_REQUEST);
    	}
		/*finally {
    	try {
    		if (conn != null) {
    			conn.close(); }
    	}catch(SQLException se) {
    		
    	} */
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET) // <-- setup the endpoint URL at /hello with the HTTP POST method
	public ResponseEntity<String> login(HttpServletRequest request) {
		String username = request.getParameter("username"); //Grabbing name and age parameters from URL
		String password = request.getParameter("password");

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
		HttpHeaders responseHeaders = new HttpHeaders(); 
    	responseHeaders.set("Content-Type", "application/json");
    	responseHeaders.set("Access-Control-Allow-Origin", "*");
		
		MessageDigest digest = null;
		String hashedKey = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		String nameToPull = request.getParameter("username");
		String passwordToPull = request.getParameter("password");
		Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com:3306/NBAFantasy?user=root&password=Ethaneddie123");
		String searchQuery = "SELECT owner, password FROM Users WHERE owner = ? AND password = ?"; //fix query
		PreparedStatement state = null;
	    state = conn.prepareStatement(searchQuery);
	    state.setString(1, nameToPull);
	    state.setString(2, passwordToPull);
	    ResultSet rs = state.executeQuery();
	    boolean notNew = false;
	    boolean rightPassword = false;
	        while (rs.next()) {
	        	if(rs.getString("owner") == username) {
	        		notNew = true;
	        	}
	        	if(rs.getString("password")== password) {
	        		rightPassword = true;
	        	}
	        }
	        conn.close();
    	if (notNew) {
			return new ResponseEntity("{\"message\":\"username not registered\"}", responseHeaders, HttpStatus.BAD_REQUEST);
		}else {
			//String storedHashedKey = MyServer.users.get(username);

			if (rightPassword) { //BCrypt.checkpw(password, storedHashedKey) when we get to it.
				return new ResponseEntity("{\"message\":\"user logged in\"}", responseHeaders, HttpStatus.OK);
			}else {
				return new ResponseEntity("{\"message\":\"username/password combination is incorrect\"}", responseHeaders, HttpStatus.BAD_REQUEST);
			}
		}
	} catch (SQLException|ClassNotFoundException e ) {
			e.printStackTrace();
	}
	/* finally {
    	try {
    		if (conn != null) { conn.close(); }
    	}catch(SQLException se) {
    	}
    	
	}} */
	return null;
	}
}
	

	 /*public static String bytesToHex(byte[] in) {
		StringBuilder builder = new StringBuilder();
		for(byte b: in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}  */