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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public RedirectView landing() {
        return new RedirectView("/LoginWorkspace.html");
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
            String query = "SELECT playerId, position, Player_rankPos, Player_rankOverall, name, team FROM Player_Ranking";
            PreparedStatement stmt = null;    //important for safety reasons
            /**String tester = "tester";
             return tester;**/

            stmt = conn.prepareStatement(query);
            // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {    //while there's something else next in the result set
                playerId = rs.getInt("playerId");
                position = rs.getString("position");
                Player_rankPos = rs.getInt("Player_rankPos");
                Player_rankOverall = rs.getInt("Player_rankOverall");
                name = rs.getString("name");
                team = rs.getString("team");

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

                if (nameArray != null) {
                    for (int i = 0; i < nameArray.length(); i++) {
                        listdata.add(nameArray.getString(i));
                    }
                }

                //puts the json array in the EC2 server
            }
            MasterJSON.add(nameArray);
        } catch (SQLException e) {
            return new ResponseEntity(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }
        }
        return new ResponseEntity(nameArray.toString(), responseHeaders, HttpStatus.OK);

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

            return new ResponseEntity(nameArray.toString(), responseHeaders, HttpStatus.OK); // MAKE SURE THAT WHEN THIS RETURNS THE OK STATUS, IT BROADCASTS THE NEW JSON OBJECT
        }
        return new ResponseEntity("Something Went Wrong", responseHeaders, HttpStatus.OK);


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
            String SearchQuery = "SELECT owner FROM Users WHERE owner = ?";
            PreparedStatement state = null;
            state = conn.prepareStatement(SearchQuery);
            state.setString(1, nameToPull);
            ResultSet rs = state.executeQuery();
            boolean isNew = true;
            while (rs.next()) {
                if (rs.getString("owner").equals(username)) {
                    isNew = false;
                }
            }


            if (isNew) {
                //put the SQL connection in here

                String transactionQuery = "START TRANSACTION";
                PreparedStatement stmt = null;
                stmt = conn.prepareStatement(transactionQuery);
                stmt.execute();
                String query = "INSERT INTO Users(owner, hashedPassword, league, team, venmoID, email) VALUES (?, ?, NULL, NULL, NULL, NULL)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                stmt = conn.prepareStatement("COMMIT");
                stmt.execute();

            } else {
                JSONObject responseObj = new JSONObject();
                responseObj.put("message", "username taken");
                return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.FORBIDDEN);
            }
            //Returns the response with a String, headers, and HTTP status
            JSONObject responseObj = new JSONObject();
            responseObj.put("username", username);
            responseObj.put("message", "user registered");
            return new ResponseEntity(responseObj.toString(), responseHeaders, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();

            return new ResponseEntity("Check status of server for stack trace.", responseHeaders, HttpStatus.BAD_REQUEST);
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
            String searchQuery = "SELECT owner, hashedPassword FROM Users WHERE owner = ? AND hashedPassword = ?";
            PreparedStatement state = null;
            state = conn.prepareStatement(searchQuery);
            state.setString(1, username);
            state.setString(2, password);
            ResultSet rs = state.executeQuery();
            boolean notNew = false;
            boolean rightPassword = false;
            while (rs.next()) {
                if (rs.getString("owner").equals(username)) {
                    notNew = true;
                }
                if (rs.getString("hashedPassword").equals(password)) {
                    rightPassword = true;
                }
            }

            if (!notNew) {
                return new ResponseEntity("{\"message\":\"username not registered\"}", responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //String storedHashedKey = MyServer.users.get(username);

                if (rightPassword) { //BCrypt.checkpw(password, storedHashedKey) when we get to it.
                    return new ResponseEntity("{\"message\":\"user logged in\"}", responseHeaders, HttpStatus.OK);
                } else {
                    return new ResponseEntity("{\"message\":\"username/password combination is incorrect\"}", responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SQLException e) {
            return new ResponseEntity("{\"message\":\"No Connection to MySQL\"}", responseHeaders, HttpStatus.NOT_FOUND);
        }
    }
}
