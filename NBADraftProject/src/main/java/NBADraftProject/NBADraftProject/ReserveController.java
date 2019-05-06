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
    @RequestMapping(value="/showTeam", method = RequestMethod.GET)
    public ResponseEntity<String> showTeam(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        Connection conn = null;
        int leagueID = Integer.parseInt(request.getParameter("leagueID"));
        int userID = Integer.parseInt(request.getParameter("userID"));
        String teamName = "";
        int C = 0, PG = 0, PF = 0, SG = 0, SF = 0, sub1 = 0, sub2 = 0, sub3 = 0, sub4 = 0, sub5 = 0, sub6 = 0, sub7 = 0, sub8 = 0, sub9 = 0;
        JSONArray fullTeam = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "SELECT teamName FROM Teams WHERE userID = ? AND leagueID = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, leagueID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
            	teamName = rs.getString("teamName");
			}
            query = "select name, position From Teams, Players where Players.playerID = Teams.C and teamName=? "+
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.PG and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.PF and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.SG and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.SF and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub1 and teamName=?" + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub2 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub3 and teamName=? " + 
            		"union " +
            		"select name, position From Teams, Players where Players.playerID = Teams.sub4 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub5 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub6 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub7 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub8 and teamName=? " + 
            		"union " + 
            		"select name, position From Teams, Players where Players.playerID = Teams.sub9 and teamName=?";
            		
            	//important for safety reasons
            stmt = conn.prepareStatement(query);
            for (int i = 1; i < 15; i++){
            	stmt.setString(i, teamName);
			}
            rs = stmt.executeQuery();
            while(rs.next()) {
            	obj.put("name",rs.getString("name"));
            	obj.put("position",rs.getString("position"));
            	fullTeam.put(obj);
            }
            }
         catch (SQLException e ) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred. Check server for details.", responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(obj.toString(), responseHeaders, HttpStatus.OK);
    }

}

