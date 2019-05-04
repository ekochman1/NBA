package NBADraftProject.NBADraftProject;

import java.security.MessageDigest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.boot.SpringApplication;
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
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONArray;


@RestController
public class UserController {
    private static ArrayList<JSONArray> MasterJSON = new ArrayList<>();
    private static HashMap<Integer, HashMap<Integer, Double>> MasterWallet = new HashMap<Integer, HashMap<Integer, Double>>();

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
        int userID = payloadObj.getInt("userID");
        double wallet = 0.0;
        String teamName = payloadObj.getString("teamName");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        
        JSONObject responseObj = new JSONObject();
        
       

        try{
        	Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "SELECT userID, Teams.leagueID, leagueAllocation FROM Teams, League WHERE Teams.leagueID = League.leagueID";
            PreparedStatement stmt = conn.prepareStatement(query);
           // stmt.setInt(1, userID);
           // stmt.setInt(2, leagueID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("userID") == userID && rs.getInt("leagueID")== leagueID){
                responseObj.put("message", "You already have a team in this league.");
                return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
                }
                 wallet = rs.getDouble("leagueAllocation");
            }
            
            HashMap<Integer, Double> tempMap = MasterWallet.get(leagueID);
            tempMap.put(userID, wallet);
            MasterWallet.put(leagueID, tempMap);
            
            query = "START TRANSACTION";
            stmt = conn.prepareStatement(query);
            stmt.execute();
            query = "INSERT INTO Teams (userID, leagueID, teamName) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, leagueID);
            stmt.setString(3, teamName);
            stmt.executeUpdate();
            query = "COMMIT";
            stmt = conn.prepareStatement(query);
            stmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
            responseObj.put("message", "error - contact customer support and have them review the server log.");
            return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.FORBIDDEN);
        }
        responseObj.put("message", "Team created!");
        return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RedirectView landing() {
        return new RedirectView("/LoginWorkspace.html?#");
    }
    
    @RequestMapping(value = "/createLeague", method = RequestMethod.POST)
    public ResponseEntity<String> createLeague(@RequestBody String payload, HttpServletRequest request){
    	  JSONObject payloadObj = new JSONObject(payload);
          String leagueName = payloadObj.getString("leagueName");
          int userID = payloadObj.getInt("userID");//Grabbing name and age parameters from URL
          int maxTeam = payloadObj.getInt("maxTeam");
          Double leagueAllocation = payloadObj.getDouble("leagueAllocation");
          String teamName = payloadObj.getString("teamName");
          
          HttpHeaders responseHeaders = new HttpHeaders();
          responseHeaders.set("Content-Type", "application/json");


          try {
              Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
              String transactionQuery = "START TRANSACTION";
              PreparedStatement stmt = null;
              stmt = conn.prepareStatement(transactionQuery);
              stmt.execute();
              String query = "INSERT INTO League(leagueName, maxTeam, leagueAllocation) VALUES (?, ?, ?)";
              stmt = conn.prepareStatement(query);
              stmt.setString(1, leagueName);
              stmt.setInt(2, maxTeam);
              stmt.setDouble(3, leagueAllocation);
              stmt.executeUpdate();
              stmt = conn.prepareStatement("COMMIT");
              stmt.execute();
              
              int leagueID = 0;

              query = "SELECT leagueName, leagueID FROM League WHERE leagueName = ?";
              stmt = conn.prepareStatement(query);
              stmt.setString(1, leagueName);
              ResultSet rs = stmt.executeQuery();
              while(rs.next()) {
            	  if(leagueName.equals(rs.getString("leagueName"))) {
            		  leagueID = rs.getInt("leagueID");
            	  }
            	  else {
            		  return new ResponseEntity<>("{\"message\":\"issue with pushing to MQSQL\"}", responseHeaders, HttpStatus.BAD_REQUEST);
            	  }
              }

              stmt = conn.prepareStatement(transactionQuery);
              stmt.execute();
              query = "INSERT INTO Teams(userID, leagueID, teamName, wallet) VALUES (?, ?, ?, ?)";
              stmt = conn.prepareStatement(query);
              stmt.setInt(1, userID);
              stmt.setInt(2, leagueID);
              stmt.setString(3, teamName);
              stmt.setDouble(4, leagueAllocation);
              stmt.executeUpdate();
              stmt = conn.prepareStatement("COMMIT");
              stmt.execute();
              
			  JSONObject responseObj = new JSONObject();
			  
			  JSONObject obj = new JSONObject();
			  obj.put("userID", userID);
			  obj.put("leagueID", leagueID);
			  obj.put("wallet", leagueAllocation);
			  
			  responseObj.put("leagueID", leagueID);
			  responseObj.put("wallet", leagueAllocation);

			  HashMap<Integer, Double> tempMap = new HashMap<Integer, Double>();
			  tempMap.put(userID, leagueAllocation);
			  MasterWallet.put(leagueID, tempMap);

              return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
          }
          catch(SQLException e) {
              e.printStackTrace();
        	  return new ResponseEntity<>("An error occurred.", responseHeaders, HttpStatus.NOT_FOUND);
        	  }
          }

    @RequestMapping(value = "/createDraft", method = RequestMethod.GET)
    // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> database(HttpServletRequest leagueID) {
        //String nameToPull = request.getParameter("firstname");

        int JSONID = Integer.parseInt(leagueID.getParameter("leagueID"));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        Connection conn = null;
        //ArrayList<String> listdata = new ArrayList<String>();
        JSONArray nameArray = new JSONArray();
        int playerID = 0;
        String position = "";
        int Player_rankPos = 0;
        int Player_rankOverall = 0;
        String name = "";
        String team = "";
        Double salary = 0.0;
        boolean taken = false;
        // double Player_standDev = 0; never used?

        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "select playerRankOverall, Player_Ranking.playerID, Player_Ranking.position, playerRankPos, Player_Ranking.name, Player_Ranking.teamCode, salary From Players, "
            		+ "Player_Ranking where Players.playerID = Player_Ranking.playerID Order by Player_Ranking.playerRankOverall;";
            PreparedStatement stmt = null;    //important for safety reasons
            /*String tester = "tester";
             return tester;*/

            stmt = conn.prepareStatement(query);
            // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {    //while there's something else next in the result set
                playerID = rs.getInt("playerID");
                position = rs.getString("position");
                Player_rankPos = rs.getInt("playerRankPos");
                Player_rankOverall = rs.getInt("playerRankOverall");
                name = rs.getString("name");
                team = rs.getString("teamCode");
                salary = rs.getDouble("salary");

                JSONObject obj = new JSONObject();
                obj.put("leagueID", JSONID); //this way i can identify the master JSON file
                obj.put("playerID", playerID);
                obj.put("position", position);
                obj.put("playerRankPos", Player_rankPos);
                obj.put("playerRankOverall", Player_rankOverall);
                obj.put("name", name);
                obj.put("teamCode", team);
                obj.put("salary", salary);
                obj.put("taken", taken);
                obj.put("userID", 0);

                nameArray.put(obj);

                //puts the json array in the EC2 server
            }
            MasterJSON.add(nameArray);
            return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK);
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
    
    @RequestMapping(value = "/displayWallet", method = RequestMethod.GET)  //THIS SHOULD BE ACTIVATED ON CLICK
    public ResponseEntity<String> displayWallet(HttpServletRequest request) {
    	int leagueID = Integer.parseInt(request.getParameter("leagueID"));
    	int userID = Integer.parseInt(request.getParameter("userID"));
    	HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        
        for(int i = 0; i < MasterWallet.size(); i++) {
        	if(MasterWallet.get(i).getInt("userID")==userID && MasterWallet.get(i).getInt("userID")==userID) {
        		JSONObject obj = new JSONObject();
        		return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
        	}
        }
        JSONObject obj = new JSONObject();
        obj.put("message", "Wallet not Found");
        return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
    }
    

    @RequestMapping(value = "/draft", method = RequestMethod.POST)  //THIS SHOULD BE ACTIVATED ON CLICK
    public ResponseEntity<String> draft(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        int userID = payloadObj.getInt("userID");
        int leagueID = payloadObj.getInt("leagueID");
        int PlayerID = payloadObj.getInt("playerID");
        //double wallet = payloadObj.getDouble("wallet");
        //String position = payloadObj.getString("position");
        //int Player_rankPos = payloadObj.getInt("Player_rankPos");
        //int Player_rankOverall = payloadObj.getInt("Plater_rankOverall");
        //String name = payloadObj.getString("name");
        //String team = payloadObj.getString("team");
        JSONArray newArray = new JSONArray();
        //JSONArray nameArray = new JSONArray();
        JSONObject wallet = new JSONObject();
        
        JSONObject obj = new JSONObject();
        wallet.put("userID", 0);
		wallet.put("leagueID", 0);
		wallet.put("wallet", 0);
        
        
        for(int l = 0; l < MasterWallet.size(); l++) {
        	if(MasterWallet.get(l).getInt("userID") == userID && MasterWallet.get(l).getInt("leagueID") == leagueID) {
        		wallet.put("userID", MasterWallet.get(l).getInt("userID"));
        		wallet.put("leagueID", MasterWallet.get(l).getInt("leagueID"));
        		wallet.put("wallet", MasterWallet.get(l).getDouble("wallet"));
        		break;
        	}
        }
        
        if(wallet.getInt("userID")==0 || wallet.getInt("leagueID")==0) {
        	
        }
        

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");


        for (int i = 0; i < MasterJSON.size(); i++) {
            if (MasterJSON.get(i).getJSONObject(0).getInt("leagueID") == leagueID) {
                for (int j = 0; j < MasterJSON.get(i).length(); j++) {
                    if (MasterJSON.get(i).getJSONObject(j).getInt("playerID") == PlayerID) {
                    	if (MasterJSON.get(i).getJSONObject(j).getDouble("salary") > wallet.getDouble("wallet")) {
                    		return new ResponseEntity<>("{\"message\":\"You cannot afford to draft this player\"}", responseHeaders, HttpStatus.OK);
                    	}
                    	for(int p = 0; p < MasterWallet.size(); p++) {
                    		if(MasterWallet.get(p).getInt("leagueID") == leagueID && MasterWallet.get(p).getInt("userID") == userID) {
                    			wallet.put("wallet", wallet.getDouble("wallet") - MasterJSON.get(i).getJSONObject(j).getDouble("salary"));
                    			MasterWallet.get(p).put("wallet" , wallet);
                    			break;
                    		}
                    		
                    	}
                    	
                    	MasterJSON.get(i).getJSONObject(j).put("userID",userID);
                    	MasterJSON.get(i).getJSONObject(j).put("taken",true);
                    	obj.put("playerRankOverall", MasterJSON.get(i).getJSONObject(j).getInt("playerRankOverall"));
                    	obj.put("name", MasterJSON.get(i).getJSONObject(j).getString("name"));
                    	
                    	/*
                        JSONObject obj = new JSONObject();
                        
                        obj.put("leagueID", MasterJSON.get(i).getJSONObject(j).getInt("leagueID")); //this way i can identify the master JSON file
                        obj.put("playerID", MasterJSON.get(i).getJSONObject(j).getInt("playerID"));
                        obj.put("position", MasterJSON.get(i).getJSONObject(j).getString("position"));
                        obj.put("playerRankPos", MasterJSON.get(i).getJSONObject(j).getInt("playerRankPos"));
                        obj.put("playerRankOverall", MasterJSON.get(i).getJSONObject(j).getInt("playerRankOverall"));
                        
                        obj1.put("playerRankOverall", MasterJSON.get(i).getJSONObject(j).getInt("playerRankOverall"));
                        
                        
                        obj.put("name", MasterJSON.get(i).getJSONObject(j).getString("name"));
                        obj.put("teamCode", MasterJSON.get(i).getJSONObject(j).getString("teamCode"));
                        obj.put("taken", true);
                        obj.put("userID", userID);
                        newArray.put(obj);  
                        */
                        
                        
                    }   /*else {
                        JSONObject obj = new JSONObject();
                        obj.put("leagueID", MasterJSON.get(i).getJSONObject(j).getInt("leagueID")); //this way i can identify the master JSON file
                        obj.put("playerID", MasterJSON.get(i).getJSONObject(j).getInt("playerID"));
                        obj.put("position", MasterJSON.get(i).getJSONObject(j).getString("position"));
                        obj.put("playerRankPos", MasterJSON.get(i).getJSONObject(j).getInt("playerRankPos"));
                        obj.put("playerRankOverall", MasterJSON.get(i).getJSONObject(j).getInt("playerRankOverall"));
                        obj.put("name", MasterJSON.get(i).getJSONObject(j).getString("name"));
                        obj.put("teamCode", MasterJSON.get(i).getJSONObject(j).getString("teamCode"));
                        obj.put("taken", MasterJSON.get(i).getJSONObject(j).getBoolean("taken"));
                        obj.put("userID", MasterJSON.get(i).getJSONObject(j).getInt("userID"));
                        newArray.put(obj);
                    } */
                }
                MasterJSON.set(i, newArray);
                break;
            }
        }
        return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK); // MAKE SURE THAT WHEN THIS RETURNS THE OK STATUS, IT BROADCASTS THE NEW JSON OBJECT
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

        //MessageDigest digest = null;
        //String hashedKey = null;

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
                    responseObj.put("username", username);
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
