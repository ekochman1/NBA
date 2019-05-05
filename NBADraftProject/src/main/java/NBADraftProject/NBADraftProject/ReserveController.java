package NBADraftProject.NBADraftProject;

import org.springframework.web.bind.annotation.*; 
import org.springframework.http.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ReserveController {
	
	
    @RequestMapping(value = "/loadleague", method = RequestMethod.GET) // <-- setup the endpoint URL at /hello with the HTTP POST method
    public ResponseEntity<String> database() {
        //String nameToPull = request.getParameter("firstname");
        HttpHeaders responseHeaders = new HttpHeaders(); 
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        JSONArray nameArray = new JSONArray();
        int leagueID = 0;
        String leagueName = "";
        int maxTeam = 0;
        int numTeams = 0;
        double wallet = 0.0;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "select leagueID, leagueName, maxTeam, numTeams, leagueAllocation from League where numTeams != maxTeam";
            PreparedStatement stmt = null;	//important for safety reasons
            
            stmt = conn.prepareStatement(query);
           // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();	
            while (rs.next()) {	//while there's something else next in the resultset
                leagueName = rs.getString("leagueName");
                leagueID = rs.getInt("leagueID");
                maxTeam = rs.getInt("maxTeam");
                numTeams = rs.getInt("numTeams");
                wallet = rs.getDouble("leagueAllocation");
            	
            	JSONObject obj = new JSONObject();
            	obj.put("leagueName", leagueName);
            	obj.put("leagueID", leagueID);
                obj.put("maxTeam", maxTeam);
                obj.put("numTeams", numTeams);
                obj.put("wallet", wallet);
      
            	
            	nameArray.put(obj);
                    }
        } catch (SQLException e ) {
            return new ResponseEntity<>(e.toString(), responseHeaders, HttpStatus.OK);
        } finally {
            try {
                if (conn != null) { conn.close(); }
            }catch(SQLException se) {
                se.printStackTrace();
            }  
        }
        return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK);
      
    }

    @RequestMapping(value="/getTeams", method = RequestMethod.GET)
    public ResponseEntity<String> getTeams(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        JSONArray nameArray = new JSONArray();
        String userID = request.getParameter("userID");
        int leagueID = 0;
        String leagueName = "";
        String teamName = "";
        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "select Teams.leagueID, leagueName, teamName from Teams, League where Teams.leagueID = League.leagueID AND userID = ?";
            PreparedStatement stmt = null;	//important for safety reasons

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(userID));
            // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {	//while there's something else next in the resultset
                leagueName = rs.getString("leagueName");
                leagueID = rs.getInt("leagueID");
                teamName = rs.getString("teamName");

                JSONObject obj = new JSONObject();
                obj.put("leagueName", leagueName);
                obj.put("leagueID", leagueID);
                obj.put("teamName", teamName);

                nameArray.put(obj);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred. Check server for details.", responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value="/getInjuries", method = RequestMethod.GET)
    public ResponseEntity<String> getInjuries(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        JSONArray nameArray = new JSONArray();
        String injury= "";
        String name = "";
        String notes = "";

        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "select name, injury, notes from Injuries";
            PreparedStatement stmt = null;	//important for safety reasons

            stmt = conn.prepareStatement(query);
            //stmt.setSt(1, Integer.parseInt(leagueName));
            // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {	//while there's something else next in the resultset
                name = rs.getString("name");
                injury = rs.getString("injury");
                notes  = rs.getString("notes");

                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("injury", injury);
                obj.put("notes", notes);

                nameArray.put(obj);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred. Check server for details.", responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(nameArray.toString(), responseHeaders, HttpStatus.OK);
    }

}

