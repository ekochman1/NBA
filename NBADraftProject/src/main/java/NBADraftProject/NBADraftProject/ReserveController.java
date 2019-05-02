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

        try {
            conn = DriverManager.getConnection("jdbc:mysql://nbafantasydb.cxa7g8pzkm2m.us-east-2.rds.amazonaws.com/NBAFantasy", "root", "Ethaneddie123");
            String query = "select leagueID, leagueName, maxTeam, numTeams from League where numTeams != maxTeam";
            PreparedStatement stmt = null;	//important for safety reasons
            
            stmt = conn.prepareStatement(query);
           // stmt.setString(1, nameToPull);
            ResultSet rs = stmt.executeQuery();	
            while (rs.next()) {	//while there's something else next in the resultset
                leagueName = rs.getString("leagueName");
                leagueID = rs.getInt("leagueID");
                maxTeam = rs.getInt("maxTeam");
                numTeams = rs.getInt("numTeams");
            	
            	JSONObject obj = new JSONObject();
            	obj.put("leagueName", leagueName);
            	obj.put("leagueID", leagueID);
                obj.put("maxTeam", maxTeam);
                obj.put("numTeams", numTeams);
      
            	
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
}