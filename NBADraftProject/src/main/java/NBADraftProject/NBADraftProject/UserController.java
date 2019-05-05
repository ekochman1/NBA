package NBADraftProject.NBADraftProject;

import java.awt.RenderingHints.Key;
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
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;


@RestController
public class UserController {
    private static JSONArray MasterJSON = new JSONArray();
    private static HashMap<Integer, HashMap<Integer, Double>> MasterWallet = new HashMap<Integer, HashMap<Integer, Double>>();
    private static HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>> PlayerPicks = new HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>>();
    private static HashMap<Integer, Integer> DraftCount = new HashMap<Integer, Integer>();
    private static HashMap<Integer, Integer> UserCount = new HashMap<Integer, Integer>();
    private static HashMap<Integer, ArrayList> DraftOrder = new HashMap<>();

    //same logic behind databases, imagine the wallet system, but you only have 5 dollars and you get charged 3 dollars at the same time, in theory you are capable to handling each one individually
    //but not both

    
    
    @RequestMapping(value = "/finishDraft", method = RequestMethod.POST)
    public ResponseEntity<String> finishDraft(@RequestBody String payload){
    	 JSONObject payloadObj = new JSONObject(payload);
         int leagueID = payloadObj.getInt("leagueID");
         String query = null;
        //int userID = payloadObj.getInt("userID");
         int playerC = 0;
         int playerPF = 0;
         int playerPG = 0;
         int playerSG = 0;
         int playerSF = 0;
         
         boolean ctr = true;
         boolean pf = true;
         boolean pg = true;
         boolean sg = true;
         boolean sf = true;
         boolean pos = true;
         
         boolean sub1 = true;
         boolean sub2 = true;
         boolean sub3 = true;
         boolean sub4 = true;
         boolean sub5 = true;
         boolean sub6 = true;
         boolean sub7 = true;
         boolean sub8 = true;
         boolean sub9 = true;
         
         HttpHeaders responseHeaders = new HttpHeaders();
         responseHeaders.set("Content-Type", "application/json");
         
         try {
        	 Connection conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
			 PreparedStatement stmt = null;
        	 for(int userID: PlayerPicks.get(leagueID).keySet()) {//as it transverses the users, have it transverse for each loop, userID is equal to the current key it is in
        		 for(int playerID: PlayerPicks.get(leagueID).get(userID).keySet()) {
        			 stmt = conn.prepareStatement("START TRANSACTION");
        			 stmt.execute();
        		 	if(PlayerPicks.get(leagueID).get(userID).get(playerID).equals("C")) {
        				 //checks what the player's position is
        				 if(ctr) {
        					 ctr = false;
        					 playerC = playerID;
        				 }
        			 }
        			 else if(PlayerPicks.get(leagueID).get(userID).get(playerID).equals("PG")) {
        				 //checks what the player's position is
        				 if(pg) {
        					 pg = false;
        					 playerPG = playerID;
        				 }
        			 }
        			 else if(PlayerPicks.get(leagueID).get(userID).get(playerID).equals("PF")) {
        				 //checks what the player's position is
        				 if(pf) {
        					 pf = false;
        					 playerPF = playerID;
        				 }
        			 }
        			 else if(PlayerPicks.get(leagueID).get(userID).get(playerID).equals("SG")) {
        				 //checks what the player's position is
        				 if(sg) {
        					 sg = false;
        					 playerSG = playerID;
        				 }
        			 }else if(PlayerPicks.get(leagueID).get(userID).get(playerID).equals("SF")) {
        				 //checks what the player's position is
        				 if(sf) {
        					 sf = false;
        					 playerSF = playerID;
        				 }
        			 } else {
        				 if(sub1) {
        					 sub1 = false;
        					 query = "UPDATE Teams SET sub1=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub2) {
        					 sub2 = false;
        					 query = "UPDATE Teams SET sub2=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub3) {
        					 sub3 = false;
        					 query = "UPDATE Teams SET sub3=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub4) {
        					 sub4 = false;
        					 query = "UPDATE Teams SET sub4=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub5) {
        					 sub5 = false;
        					 query = "UPDATE Teams SET sub5=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub6) {
        					 sub6 = false;
        					 query = "UPDATE Teams SET sub6=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub7) {
        					 sub7 = false;
        					 query = "UPDATE Teams SET sub7=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub8) {
        					 sub8 = false;
        					 query = "UPDATE Teams SET sub8=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else if(sub9) {
        					 sub9 = false;
        					 query = "UPDATE Teams SET sub9=? WHERE userID=? AND leagueID=?";
        					 stmt = conn.prepareStatement(query);
        					 stmt.setInt( 1, playerID);
        					 stmt.setInt( 2, userID);
            				 stmt.setInt( 3, leagueID);
        				 }
        				 else {
        					 if(ctr) {
        						 ctr = false;
        						 query = "UPDATE Teams SET C=? WHERE userID=? AND leagueID=?";
            					 stmt = conn.prepareStatement(query);
            					 stmt.setInt( 1, playerID);
            					 stmt.setInt( 2, userID);
                				 stmt.setInt( 3, leagueID);
        					 }
        					 else if(pg) {
        						 pg = false;
        						 query = "UPDATE Teams SET PG=? WHERE userID=? AND leagueID=?";
            					 stmt = conn.prepareStatement(query);
            					 stmt.setInt( 1, playerID);
            					 stmt.setInt( 2, userID);
                				 stmt.setInt( 3, leagueID);
        					 }
        					 else if(pf) {
        						 pf = false;
        						 query = "UPDATE Teams SET PF=? WHERE userID=? AND leagueID=?";
            					 stmt = conn.prepareStatement(query);
            					 stmt.setInt( 1, playerID);
            					 stmt.setInt( 2, userID);
                				 stmt.setInt( 3, leagueID);
        					 }
        					 else if(sg) {
        						 sg = false;
        						 query = "UPDATE Teams SET SG=? WHERE userID=? AND leagueID=?";
            					 stmt = conn.prepareStatement(query);
            					 stmt.setInt( 1, playerID);
            					 stmt.setInt( 2, userID);
                				 stmt.setInt( 3, leagueID);
        					 }
        					 else if(sf) {
        						 sf = false;
        						 query = "UPDATE Teams SET SF=? WHERE userID=? AND leagueID=?";
            					 stmt = conn.prepareStatement(query);
            					 stmt.setInt( 1, playerID);
            					 stmt.setInt( 2, userID);
                				 stmt.setInt( 3, leagueID);
        					 }
        				 }
        			 }
					 if(!ctr && !pg && !pf && !sf && !sg && pos) {
						 query = "UPDATE Teams SET C = ?, PG = ?, PF = ?, SG = ?, SF = ? WHERE userID=? AND leagueID=?";
						 stmt = conn.prepareStatement(query);
						 stmt.setInt( 1, playerC);
						 stmt.setInt( 2, playerPG);
						 stmt.setInt( 3, playerPF);
						 stmt.setInt( 4, playerSG);
						 stmt.setInt( 5, playerSF);
						 stmt.setInt( 6, userID);
						 stmt.setInt( 7, leagueID);
						 pos = false;
					 }
        			 
        			 try {
						 stmt.executeUpdate();
					 } catch (NullPointerException e){
						 stmt = conn.prepareStatement("COMMIT");
						 stmt.execute();
        			 	continue;
					 }
					 stmt = conn.prepareStatement("COMMIT");
					 stmt.execute();
                     
                    // stmt = conn.prepareStatement(query);
                    // stmt.setInt();
                    // stmt.setInt();
                    // stmt.setInt();
                    // stmt.setInt();
                	 
        		 }
        		 playerC = 0;
                 playerPF = 0;
                 playerPG = 0;
                 playerSG = 0;
                 playerSF = 0;
                 
                 ctr = true;
                 pf = true;
                 pg = true;
                 sg = true;
                 sf = true;
                 pos = true;
                 
                 sub1 = true;
                 sub2 = true;
                 sub3 = true;
                 sub4 = true;
                 sub5 = true;
                 sub6 = true;
                 sub7 = true;
                 sub8 = true;
                 sub9 = true;
                 stmt = conn.prepareStatement("START TRANSACTION");
                 stmt.execute();
                 stmt = conn.prepareStatement("UPDATE Teams SET wallet = ? WHERE userID = ?");
                 stmt.setDouble(1, MasterWallet.get(leagueID).get(userID));
                 stmt.setInt(2, userID);
                 stmt.executeUpdate();
                 stmt = conn.prepareStatement("COMMIT");
                 stmt.execute();
        	 }
        	 return new ResponseEntity<>("{\"message\":\"Finished The Draft\"}", responseHeaders, HttpStatus.OK);
        	 }
         catch(SQLException e){
         	e.printStackTrace();
        	 return new ResponseEntity<>("{\"message\":\"issue with pushing to MQSQL\"}", responseHeaders, HttpStatus.BAD_REQUEST);
         }
    }

    @RequestMapping(value = "/checkIfReady", method = RequestMethod.POST)
    public ResponseEntity<String> checkIfReady(@RequestBody String payload, HttpServletRequest request) {
    	JSONObject payloadObj = new JSONObject(payload);
    	int leagueID = payloadObj.getInt("leagueID");
        String userName = payloadObj.getString("username");
        JSONObject obj = new JSONObject();

        if (DraftOrder.containsKey(leagueID)){
        	DraftOrder.get(leagueID).add(userName);
		} else {
        	DraftOrder.put(leagueID, new ArrayList(Arrays.asList(userName)));
		}
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        int newUserCount = -1;
        try {
			newUserCount = UserCount.get(leagueID) - 1;
		} catch (NullPointerException e){
        	e.printStackTrace();
        	for (int id : UserCount.keySet()){
        		System.out.println(id + " : " + UserCount.get(id));
        		System.out.println("the id for this league is " + leagueID);
			}
		}
        UserCount.put(leagueID, newUserCount);
        if(UserCount.get(leagueID)<=0) {
        	Collections.shuffle(DraftOrder.get(leagueID));
        	obj.put("message", "Draft starting soon!");
        	obj.put("order", DraftOrder.get(leagueID));
        	return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("{\"message\":\"Still waiting for: "+newUserCount+" players\"}", responseHeaders, HttpStatus.OK);
        }
    	
    	
        
    }
    
    
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
            String query = "SELECT userID, leagueAllocation, numTeams, maxTeam FROM Teams, League WHERE Teams.leagueID = League.leagueID AND League.leagueID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
           // stmt.setInt(1, userID);
           // stmt.setInt(2, leagueID);
			stmt.setInt(1, leagueID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
				if(rs.getInt("numTeams")>=rs.getInt("maxTeam")) {
					responseObj.put("message", "This league is already full");
					return new ResponseEntity<>(responseObj.toString(), responseHeaders, HttpStatus.OK);
				}else if (rs.getInt("userID") == userID){
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
              String query = "INSERT INTO League(leagueName, maxTeam, leagueAllocation, leagueCom) VALUES (?, ?, ?, ?)";
              stmt = conn.prepareStatement(query);
              stmt.setString(1, leagueName);
              stmt.setInt(2, maxTeam);
              stmt.setDouble(3, leagueAllocation);
              stmt.setInt(4, userID);
              stmt.executeUpdate();
              stmt = conn.prepareStatement("COMMIT");
              stmt.execute();
              
              int leagueID = 0;

              query = "SELECT leagueID FROM League WHERE leagueName = ?";
              stmt = conn.prepareStatement(query);
              stmt.setString(1, leagueName);
              ResultSet rs = stmt.executeQuery();
              while(rs.next()) {
              	  leagueID = rs.getInt("leagueID");
              }
              System.out.println(leagueID);
              
              int count = maxTeam*14;
              
              DraftCount.put(leagueID, count);
              UserCount.put(leagueID, maxTeam);

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
        //JSONArray nameArray = new JSONArray();
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

                MasterJSON.put(obj);
                

                //puts the json array in the EC2 server
            }
            
            return new ResponseEntity<>(MasterJSON.toString(), responseHeaders, HttpStatus.OK);
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
		JSONObject obj = new JSONObject();
        try {
        	obj.put("wallet", MasterWallet.get(leagueID).get(userID));
        	return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
		} catch (NullPointerException e){
        	e.printStackTrace();
			obj.put("message", "Wallet not Found");
			return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
		}
    }
    

    @RequestMapping(value = "/draft", method = RequestMethod.POST)  //THIS SHOULD BE ACTIVATED ON CLICK
    public ResponseEntity<String> draft(@RequestBody String payload, HttpServletRequest request) {
        JSONObject payloadObj = new JSONObject(payload);
        int userID = payloadObj.getInt("userID");
        int leagueID = payloadObj.getInt("leagueID");
        int PlayerID = payloadObj.getInt("playerID");
		Double wallet = MasterWallet.get(leagueID).get(userID);
        
		
		
        JSONObject obj = new JSONObject();
        //JSONObject draftedPlayer = new JSONObject();
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");


        for (int i = 0; i < MasterJSON.length(); i++) {
            if (MasterJSON.getJSONObject(i).getInt("playerID") == PlayerID) {
                    	if (MasterJSON.getJSONObject(i).getDouble("salary") > wallet) {
                    		return new ResponseEntity<>("{\"message\":\"You cannot afford to draft this player\"}", responseHeaders, HttpStatus.OK);
                    	}
                    	wallet = wallet - MasterJSON.getJSONObject(i).getDouble("salary");
						MasterWallet.get(leagueID).put(userID, wallet);
						
						
						String position = MasterJSON.getJSONObject(i).getString("position");
						
						HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
						tempMap.put(PlayerID, position);
						
						HashMap<Integer, HashMap<Integer, String>> tempMap2 = new HashMap<Integer, HashMap<Integer, String>>();
						tempMap2.put(userID, tempMap);
						
						PlayerPicks.put(leagueID, tempMap2);
						
						
						
                    	obj.put("playerRankOverall", MasterJSON.getJSONObject(i).getInt("playerRankOverall"));
                    	obj.put("name", MasterJSON.getJSONObject(i).getString("name"));
                    	break;
                    }
        }
        DraftCount.put(leagueID, DraftCount.get(leagueID)-1);
        if(DraftCount.get(leagueID)<=0) {
        	System.out.println("draft completed");
        	obj.put("finish_trigger", leagueID);
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
