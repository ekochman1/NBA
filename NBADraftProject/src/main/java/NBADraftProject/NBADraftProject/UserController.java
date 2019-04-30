package NBADraftProject.NBADraftProject;

import java.security.MessageDigest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.*;

import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;


@RestController
public class UserController {
    private static ArrayList<JSONArray> MasterJSON;


    //same logic behind databases, imagine the wallet system, but you only have 5 dollars and you get charged 3 dollars at the same time, in theory you are capable to handling each one individually
    //but not both


    //create a transaction 
    /* this will be to finish the draft
    @RequestMapping(value = "/FinishDraft", method = RequestMethod.GET)
    public ResponseEntity<String> FinishDraft(@RequestBody String payload, HttpServletRequest request){
        JSONObject payloadObj = new JSONObject(payload);
        String playerName = payloadObj.getString("playerName");
        String leagueID = payloadObj.getString("leagueID"); //Grabbing name and age parameters from URL
        String playerID = payloadObj.getString("playerID"); //just a fancier name for their username 

		//Creating http headers object to place into response entity the server will return.
		//This is what allows us to set the content-type to application/json or any other content-type
		//we would want to return 
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        
        try {
            String nameToPull = request.getParameter("username");
            Connection conn = DriverManager.getConnection("nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com" , "root", "Ethaneddie123");
            String SearchQuery = "SELECT owners  FROM Users WHERE owners = ?";
            PreparedStatement state = null;
            state = conn.prepareStatement("START TRANSACTION");
            state.execute();
            state = conn.prepareStatement(SearchQuery);
            state.setString(1, nameToPull);
            ResultSet rs = state.executeQuery();
            while (rs.next()) {
                if (rs.getString("owner").equals(playerID)) { //finds the user and gets their database
                	  //insert new connection to the table that holds the league IDs, userIDS and the player they wanted to draft
                	  // connect to change 
                	
                }
            }
        }

        catch(SQLException e){

                  e.printStackTrace();

                  return new ResponseEntity("Check status of server for stack trace.", responseHeaders, HttpStatus.BAD_REQUEST);

        }

    }   */

    @RequestMapping(value = "/createTeam", method = RequestMethod.POST)
    public ResponseEntity<String> createTeam(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        int leagueID = payloadObj.getInt("leagueID");
        int playerID = payloadObj.getInt("playerID");
        String teamName = payloadObj.getString("teamName");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        JSONObject responseObj = new JSONObject();
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds." +
                    "amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "SELECT teamID FROM Teams WHERE userID = ? AND leagueID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playerID);
            stmt.setInt(2, leagueID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("teamID") == 0){
                    break;
                }
                responseObj.put("message", "You already have a team in this league.");
                return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
            }
            query = "START TRANSACTION";
            stmt = conn.prepareStatement(query);
            stmt.execute();
            query = "INSERT INTO Teams (userID, leagueID, teamName) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, playerID);
            stmt.setInt(2, leagueID);
            stmt.setString(3, teamName);
            stmt.executeUpdate();
            query = "COMMIT";
            stmt = conn.prepareStatement(query);
            stmt.execute();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
            responseObj.put("message", "error - contact customer support and have them review the server log.");
            return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        responseObj.put("message", "Team created!");
        return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RedirectView landing() {
        return new RedirectView("/LoginWorkspace.html");
    }
    
    @RequestMapping(value = "/createLeague", method = RequestMethod.POST)
    public ResponseEntity<String> createLeague(@RequestBody String payload, HttpServletRequest request){
    	  JSONObject payloadObj = new JSONObject(payload);
          String leagueName = payloadObj.getString("leagueName");
          int userID = payloadObj.getInt("userID");//Grabbing name and age parameters from URL
          int maxTeam = payloadObj.getInt("maxTeam");
          Double leagueAllocation = payloadObj.getDouble("leagueAllocation");
          
          
          HttpHeaders responseHeaders = new HttpHeaders();
          responseHeaders.set("Content-Type", "application/json");


          try {
              Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
              String transactionQuery = "START TRANSACTION";
              PreparedStatement stmt = null;
              stmt = conn.prepareStatement(transactionQuery);
              stmt.execute();
              String query = "INSERT INTO League(leaugeName, maxTeam, leagueAllocation) VALUES (?, ?, ?, ?, ?)";
              stmt = conn.prepareStatement(query);
              stmt.setString(1, leagueName);
              stmt.setInt(4, maxTeam);
              stmt.setDouble(5, leagueAllocation);
              stmt.executeUpdate();
              stmt = conn.prepareStatement("COMMIT");
              stmt.execute();
              
              int leagueID = 0;
              
              PreparedStatement stmt1 = null;
              String query0 = "SELECT leagueName FROM League WHERE leagueName = ?";
              stmt1 = conn.prepareStatement(query);
              stmt1.setString(1, leagueName);
              ResultSet rs = stmt1.executeQuery();
              while(rs.next()) {
            	  if(leagueName == rs.getString("leagueName")) {
            		  leagueID = rs.getInt("leagueID");
            	  }
            	  else {
            		  return new ResponseEntity<>("{\"message\":\"issue with pushing to MQSQL\"}", responseHeaders, HttpStatus.BAD_REQUEST);
            	  }
              }
              PreparedStatement state = null;
              state = conn.prepareStatement(transactionQuery);
              state.execute();
              String query1 = "INSERT INTO Teams(userID, wallet, leagueID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
              state = conn.prepareStatement(query);
              state.setInt(2, userID);
              state.setDouble(3, leagueAllocation);
              state.setInt(4, leagueID);
              state.executeUpdate();
              state = conn.prepareStatement("COMMIT");
              state.execute();
              
			  JSONObject responseObj = new JSONObject();
			  responseObj.put("leagueID", leagueID);
              return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
          }
          catch(SQLException e) {
        	  return new ResponseEntity<>("{\"message\":\"No Connection to MySQL\"}", responseHeaders, HttpStatus.NOT_FOUND);
        	  }
          }

    @RequestMapping(value = "/createDraft", method = RequestMethod.GET)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> database(HttpServletRequest leagueID) {
        //String nameToPull = request.getParameter("firstname");

        String JSONID = leagueID.getParameter("leagueID");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        Connection conn = null;
        ArrayList<String> listdata = new ArrayList<String>();
        JSONArray nameArray = new JSONArray();
        int playerId = 0;
        String position = "";
        int Player_rankPos = 0;
        int Player_rankOverall = 0;
        String name = "";
        String team = "";
        boolean taken = false;
        // double Player_standDev = 0; never used?

        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "SELECT playerID, position, playerRankPos, playerRankOverall, name, teamCode FROM Player_Ranking";
            PreparedStatement stmt = null;    //important for safety reasons
            /*String tester = "tester";
             return tester;*/

            stmt = conn.prepareStatement(query);
            // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {    //while there's something else next in the result set
                playerId = rs.getInt("playerID");
                position = rs.getString("position");
                Player_rankPos = rs.getInt("playerRankPos");
                Player_rankOverall = rs.getInt("playerRankOverall");
                name = rs.getString("name");
                team = rs.getString("teamCode");

                JSONObject obj = new JSONObject();
                obj.put("leagueid", JSONID); //this way i can identify the master JSON file
                obj.put("playerId", playerId);
                obj.put("position", position);
                obj.put("Player_rankPos", Player_rankPos);
                obj.put("Player_rankOverall", Player_rankOverall);
                obj.put("name", name);
                obj.put("team", team);
                obj.put("taken", taken);

                nameArray.put(obj);

                for (int i = 0; i < nameArray.length(); i++) {
                    listdata.add(nameArray.getString(i));
                }

                //puts the json array in the EC2 server
            }
            MasterJSON.add(nameArray);
        } catch (SQLException e) {
            return new ResponseEntity<>(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK);

    }

    @RequestMapping(value = "/joinDraft", method = RequestMethod.GET)
    public ResponseEntity<String> joinDraft(@RequestParam String leagueID){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        JSONObject responseObj = new JSONObject();
        try{
            Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds." +
                    "amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "SELECT leagueID, leagueName, leagueAllocation FROM League WHERE leagueID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(leagueID));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                responseObj.put("leagueID", rs.getInt("leagueID"));
                responseObj.put("leagueName", rs.getString("leagueName"));
                responseObj.put("leagueAllocation", rs.getFloat("leagueAllocation"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/draft", method = RequestMethod.POST)  //THIS SHOULD BE ACTIVATED ON CLICK
    public ResponseEntity<String> draft(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        int leagueID = payloadObj.getInt("leagueID");
        int PlayerID = payloadObj.getInt("playerID");
        //String position = payloadObj.getString("position");
        //int Player_rankPos = payloadObj.getInt("Player_rankPos");
        //int Player_rankOverall = payloadObj.getInt("Plater_rankOverall");
        //String name = payloadObj.getString("name");
        //String team = payloadObj.getString("team");
        JSONArray newArray = new JSONArray();
        JSONArray nameArray = new JSONArray();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");


        for (int i = 0; i < MasterJSON.size(); i++) {
            if (MasterJSON.get(i).getJSONObject(0).getInt("leagueid") == leagueID) {
                for (int j = 0; j < MasterJSON.get(i).length(); i++) {
                    if (MasterJSON.get(i).getJSONObject(j).getInt("playerid") == PlayerID) {
                        JSONObject obj = new JSONObject();
                        obj.put("leagueid", MasterJSON.get(i).getJSONObject(j).getInt("leagueid")); //this way i can identify the master JSON file
                        obj.put("playerId", MasterJSON.get(i).getJSONObject(j).getInt("playerID"));
                        obj.put("position", MasterJSON.get(i).getJSONObject(j).getString("position"));
                        obj.put("Player_rankPos", MasterJSON.get(i).getJSONObject(j).getInt("Player_rankPos"));
                        obj.put("Player_rankOverall", MasterJSON.get(i).getJSONObject(j).getInt("Player_rankOverall"));
                        obj.put("name", MasterJSON.get(i).getJSONObject(j).getString("name"));
                        obj.put("team", MasterJSON.get(i).getJSONObject(j).getString("team"));
                        obj.put("taken", true);
                        newArray.put(obj);
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("leagueid", MasterJSON.get(i).getJSONObject(j).getInt("leagueid")); //this way i can identify the master JSON file
                        obj.put("playerId", MasterJSON.get(i).getJSONObject(j).getInt("playerID"));
                        obj.put("position", MasterJSON.get(i).getJSONObject(j).getString("position"));
                        obj.put("Player_rankPos", MasterJSON.get(i).getJSONObject(j).getInt("Player_rankPos"));
                        obj.put("Player_rankOverall", MasterJSON.get(i).getJSONObject(j).getInt("Player_rankOverall"));
                        obj.put("name", MasterJSON.get(i).getJSONObject(j).getString("name"));
                        obj.put("team", MasterJSON.get(i).getJSONObject(j).getString("team"));
                        obj.put("taken", MasterJSON.get(i).getJSONObject(j).getBoolean("taken"));
                        newArray.put(obj);
                    }
                }

            }
            MasterJSON.set(i, newArray);

            for (int k = 0; k < newArray.length(); k++) {
                if (!newArray.getJSONObject(k).getBoolean("taken")) {
                    nameArray.put(newArray.getJSONObject(k));
                }
            }
        }
        return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK); // MAKE SURE THAT WHEN THIS RETURNS THE OK STATUS, IT BROADCASTS THE NEW JSON OBJECT
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> register(@RequestBody String payload, HttpServletRequest request) { //springboot has a thread pool that handles the server with multiple users
        JSONObject payloadObj = new JSONObject(payload);
        String username = payloadObj.getString("username"); //Grabbing name and age parameters from URL
        String password = payloadObj.getString("password");

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        //MessageDigest digest = null;
        //String hashedKey = null;

        //hashedKey = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            String nameToPull = request.getParameter("username");
            Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String SearchQuery = "SELECT username FROM Users WHERE username = ?";
            PreparedStatement state = null;
            state = conn.prepareStatement(SearchQuery);
            state.setString(1, nameToPull);
            ResultSet rs = state.executeQuery();
            boolean isNew = true;
            while (rs.next()) {
                if (rs.getString("username").equals(username)) {
                    isNew = false;
                }
            }

            JSONObject responseObj = new JSONObject();
            int userID = 0;
            if (isNew) {
                //put the SQL connection in here

                String transactionQuery = "START TRANSACTION";
                PreparedStatement stmt = null;
                stmt = conn.prepareStatement(transactionQuery);
                stmt.execute();
                String query = "INSERT INTO Users(username, hashedPassword) VALUES (?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                stmt = conn.prepareStatement("COMMIT");
                stmt.execute();

                query = "SELECT userID FROM Users WHERE username = ? AND hashedPassword = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                rs = stmt.executeQuery();
                while(rs.next()){
                    userID = rs.getInt("userID");
                }
                responseObj.put("userID", String.valueOf(userID));

            } else {
                responseObj.put("message", "username taken");
                return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.FORBIDDEN);
            }
            //Returns the response with a String, headers, and HTTP status
            responseObj.put("username", username);
            responseObj.put("message", "user registered");
            return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Check status of server for stack trace.", responseHeaders, HttpStatus.BAD_REQUEST);
        }
		/*finally {
    	try {
    		if (conn != null) {
    			conn.close(); }
    	}catch(SQLException se) {
    		
    	} */
    }
  

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> login(HttpServletRequest request) {


        String username = request.getParameter("username"); //Grabbing name and age parameters from URL
        String password = request.getParameter("password");

		/*Creating http headers object to place into response entity the server will return.
		This is what allows us to set the content-type to application/json or any other content-type
		we would want to return */
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        MessageDigest digest = null;
        String hashedKey = null;

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String searchQuery = "SELECT userID, username, hashedPassword FROM Users WHERE username = ? AND hashedPassword = ?";
            PreparedStatement state = null;
            state = conn.prepareStatement(searchQuery);
            state.setString(1, username);
            state.setString(2, password);
            ResultSet rs = state.executeQuery();
            boolean notNew = false;
            boolean rightPassword = false;
            int userID = 0;
            while (rs.next()) {
                if (rs.getString("username").equals(username)) {
                    notNew = true;
                }
                if (rs.getString("hashedPassword").equals(password)) {
                    rightPassword = true;
                }
                userID = rs.getInt("userID");
            }

            if (!notNew) {
                return new ResponseEntity<>("{\"message\":\"username not registered\"}", responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //String storedHashedKey = MyServer.users.get(username);

                if (rightPassword) { //BCrypt.checkpw(password, storedHashedKey) when we get to it.
                    JSONObject responseObj = new JSONObject();
                    responseObj.put("message", "user logged in.");
                    responseObj.put("userID", String.valueOf(userID));
                    return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("{\"message\":\"username/password combination is incorrect\"}", responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SQLException e) {
            return new ResponseEntity<>("{\"message\":\"No Connection to MySQL\"}", responseHeaders, HttpStatus.NOT_FOUND);
        }
    }
}
