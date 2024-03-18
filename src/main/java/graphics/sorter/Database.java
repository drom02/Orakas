package graphics.sorter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import graphics.sorter.Structs.*;
import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class Database {
    public static  String databaseName = "jdbc:sqlite:"+ JsonManip.loadRedirect()+ "mainSorter.db";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static void prepareTables(){

        String clientTable = "CREATE TABLE IF NOT EXISTS clientTable (\n"
                + " clientID text PRIMARY KEY,\n"
                + " status integer NOT NULL,\n"
                + " name text NOT NULL,\n"
                + " surname text NOT NULL,\n"
                + " homeLocation text NOT NULL,\n"
                + " comment text NOT NULL, \n"
                + " isDeleted Integer NOT NULL, \n"
                + " FOREIGN KEY (homeLocation) REFERENCES locationTable(locationID) \n"
                + ");";
        String assistantTable = "CREATE TABLE IF NOT EXISTS assistantTable (\n"
                + " assistantID text PRIMARY KEY,\n"
                + " status integer NOT NULL,\n"
                + " name text NOT NULL,\n"
                + " surname text NOT NULL,\n"
                + " contractType text NOT NULL,\n"
                + " contractTime real NOT NULL,\n"
                + " likesOvertime integer NOT NULL,\n"
                + " comment text NOT NULL, \n"
                + " workDays text NOT NULL,\n"
                + " isDeleted integer NOT NULL\n"
                + ");";
        String locationTable = "CREATE TABLE IF NOT EXISTS locationTable (\n"
                + " locationID text PRIMARY KEY,\n"
                + " address text NOT NULL,\n"
                + " casualName text NOT NULL,\n"
                + " isDeleted integer NOT NULL\n"
                + ");";
        String clientMonthTable = "CREATE TABLE IF NOT EXISTS clientMonthTable (\n"
                + " monthID text PRIMARY KEY,\n"
                + " clientID text NOT NULL,\n"
                + " year text NOT NULL,\n"
                + " month integer NOT NULL,\n"
             //   + " locationID text NOT NULL,\n"
                + " FOREIGN KEY (clientID) REFERENCES clientTable(clientID) ON DELETE CASCADE \n"
             //   + " FOREIGN KEY (locationID) REFERENCES locationTable(locationID )\n"
                + ");";
        String clientDayTable = "CREATE TABLE IF NOT EXISTS clientDayTable (\n"
                + " clientDayID text PRIMARY KEY,\n"
                + " clientID text NOT NULL,\n"
                + " monthID text NOT NULL,\n"
                + " isMerged integer NOT NULL,\n"
                + " isDay integer NOT NULL,\n"
                + " day integer NOT NULL,\n"
                + " month integer NOT NULL,\n"
                + " year integer NOT NULL,\n"
                + " locationID text,\n"
                + " defStartTime text ,\n"
                + " defEndTime text,\n"
                + " FOREIGN KEY (clientID) REFERENCES clientTable(clientID), \n"
                + " FOREIGN KEY (monthID) REFERENCES  clientMonthTable(monthID) ON DELETE CASCADE, \n"
                + " FOREIGN KEY (locationID) REFERENCES locationTable(locationID) \n"

                + ");";
        String serviceIntervalTable = "CREATE TABLE IF NOT EXISTS serviceIntervalTable (\n"
                + " serviceIntervalID text PRIMARY KEY,\n"
                + " clientDayID text NOT NULL,\n"
                + " overseeingAssistantID,\n"
                + " start text NOT NULL,\n"
                + " end text NOT NULL,\n"
                + " location text,\n"
                + " isNotRequired integer NOT NULL,\n"
                + " comment text, \n"
                + " FOREIGN KEY (clientDayID) REFERENCES clientDayTable(clientDayID) ON DELETE CASCADE, \n"
                + " FOREIGN KEY (overseeingAssistantID) REFERENCES assistantTable(assistantID) , \n"
                + " FOREIGN KEY (location) REFERENCES locationTable(locationID) \n"
                + ");";
        String compatibilityTable = "CREATE TABLE IF NOT EXISTS compatibilityTable (\n"
                + " compatibilityID text PRIMARY KEY,\n"
                + " clientID text NOT NULL,\n"
                + " assistantID text NOT NULL,\n"
                + " compatibility integer NOT NULL, \n"
                + " FOREIGN KEY (clientID) REFERENCES clientTable(clientID) ON DELETE CASCADE, \n"
                + " FOREIGN KEY (assistantID) REFERENCES assistantTable(assistantID) ON DELETE CASCADE \n"
                + ");";
        String assistantAvailabilityTable = "CREATE TABLE IF NOT EXISTS assistantAvailabilityTable (\n"
                + " availabilityID text PRIMARY KEY,\n"
                + " year integer NOT NULL,\n"
                + " month integer NOT NULL,\n"
                + " jsonContent text NOT NULL \n"
                + ");";
        String settingsTable = "CREATE TABLE IF NOT EXISTS settingsTable (\n"
                + " settingsID text PRIMARY KEY,\n"
                + " filePath text NOT NULL,\n"
                + " selectedYear integer NOT NULL,\n"
                + " selectedMonth integer NOT NULL,\n"
                + " defStart1 integer NOT NULL, \n"
                + " defStart2 integer NOT NULL, \n"
                + " defEnd1 integer NOT NULL, \n"
                + " defEnd2 integer NOT NULL, \n"
                + " maxShiftLength integer NOT NULL \n"
                + ");";
        String[] tables = new String[]{clientTable,assistantTable,locationTable,clientMonthTable,compatibilityTable,clientDayTable,serviceIntervalTable,assistantAvailabilityTable,settingsTable };
        try (Connection conn = DriverManager.getConnection(databaseName);
        Statement stmt = conn.createStatement()) {
            for(String st : tables){
                //stmt.execute(st);
                stmt.execute(st);
            }
            stmt.execute("PRAGMA journal_mode = WAL");
            stmt.execute("PRAGMA synchronous = NORMAL");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void dataTest() {

        String name = "jdbc:sqlite:"+ Settings.getSettings().getFilePath()+ "mainSorter.db";
        String sql = "CREATE TABLE IF NOT EXISTS clientTable (\n"
                + " ID text PRIMARY KEY,\n"
                + " status integer NOT NULL,\n"
                + " name text NOT NULL,\n"
                + " surname text NOT NULL,\n"
                + " homeLocation text NOT NULL,\n"
                + " comment text \n"
                + ");";

        String sqlAlt = "CREATE TABLE IF NOT EXISTS clientTableAlt (\n"
                + " ID text PRIMARY KEY,\n"
                + " content text NOT NULL\n"
                + ");";
        String sqlLocation = "CREATE TABLE IF NOT EXISTS locationTable (\n"
                + " ID text PRIMARY KEY,\n"
                + " address text NOT NULL,\n"
                + " casualName text NOT NULL\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(name);
             Statement stmt = conn.createStatement()) {

            // Create a new table
            stmt.execute(sql);
            stmt.execute(sqlAlt);
            stmt.execute(sqlLocation);
            System.out.println("A new table has been created.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void testUser(){

        String query = "INSERT OR REPLACE INTO clientTable (ID, status,name,surname,homeLocation,comment) VALUES (?, ?, ?, ?, ?, ?)";
        String queryAlt = "INSERT OR REPLACE INTO clientTableAlt (ID, content) VALUES (?, ?)";
        String name = "jdbc:sqlite:"+ Settings.getSettings().getFilePath()+ "test.db";

        try (Connection conn = DriverManager.getConnection(name);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ClientProfile testP = JsonManip.getJsonManip().loadClientProfileInfo().getClientList().getFirst();
            stmt.setString(1, String.valueOf(testP.getID()));
            stmt.setBoolean(2, testP.getActivityStatus());
            stmt.setString(3,testP.getName());
            stmt.setString(4,testP.getSurname());
            stmt.setString(5, String.valueOf(testP.getHomeLocation()));
            stmt.setString(6,testP.getComment());
            stmt.executeUpdate();

            PreparedStatement stmtAlt = conn.prepareStatement(queryAlt);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String st =   objectMapper.writeValueAsString(testP);
            stmtAlt.setString(1, String.valueOf(testP.getID()));
            stmtAlt.setString(2,st);
            stmtAlt.executeUpdate();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveLocation(Location loc){

        String query = "INSERT OR REPLACE INTO locationTable (ID, address,casualName) VALUES (?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, loc.getID().toString());
            stmt.setString(2, loc.getAddress());
            stmt.setString(3,loc.getCasualName());
            stmt.setInt(4,0);
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void saveLocation(Connection conn, Location loc){

        String query = "INSERT OR REPLACE INTO locationTable (locationID, address,casualName,isDeleted) VALUES (?, ?, ?, ?)";
        try(
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, loc.getID().toString());
            stmt.setString(2, loc.getAddress());
            stmt.setString(3,loc.getCasualName());
            stmt.setInt(4,0);
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void saveLocation(Connection conn, Location loc, Integer state){

        String query = "INSERT OR REPLACE INTO locationTable (ID, address,casualName) VALUES (?, ?, ?)";
        try(
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, loc.getID().toString());
            stmt.setString(2, loc.getAddress());
            stmt.setString(3,loc.getCasualName());
            stmt.setInt(4,state);
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static Location loadLocation(UUID locID){
        String query = "SELECT * FROM locationTable WHERE locationID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,locID.toString());
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Location outputLocation = new Location(UUID.fromString(rs.getString("locationID")),rs.getString("address"),rs.getString("casualName"));
                    return  outputLocation;
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static ListOfLocations loadLocations(){
        ListOfLocations loc = new ListOfLocations();
        String query = "SELECT * FROM locationTable";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)){
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Location outputLocation = new Location(UUID.fromString(rs.getString("locationID")),rs.getString("address"),rs.getString("casualName"));
                    loc.getListOfLocations().add(outputLocation);
                }
                return  loc;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static Location loadLocation(String locID, Connection conn){
        String query = "SELECT * FROM locationTable WHERE locationID = ?";
        try(conn;
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,locID);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Location outputLocation = new Location(UUID.fromString(rs.getString("locationID")),rs.getString("address"),rs.getString("casualName"));
                    return  outputLocation;
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static ClientProfile loadClientProfile(UUID clientID){
        String query = "SELECT * FROM clientTable WHERE clientID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,String.valueOf(clientID));
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    //String out = rs.getString(2);
                    String ID = rs.getString("clientID");
                    String name = rs.getString("name");
                    Boolean status = rs.getBoolean("status");
                    String surname = rs.getString("surname");
                    String homeLocation = rs.getString("homeLocation");
                    String comment = rs.getString("comment");
                    ClientProfile outputProfile = new ClientProfile(UUID.fromString(ID),status, name,surname,loadLocation(homeLocation,conn),comment);
                    return  outputProfile;
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static ListOfClientsProfiles loadClientProfiles(){
        String query = "SELECT * FROM clientTable WHERE isDeleted = ?";
        ListOfClientsProfiles lis = new ListOfClientsProfiles(new ArrayList<ClientProfile>());
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setBoolean(1,false);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    //String out = rs.getString(2);
                    String ID = rs.getString("clientID");
                    String name = rs.getString("name");
                    Boolean status = rs.getBoolean("status");
                    String surname = rs.getString("surname");
                    String homeLocation = rs.getString("homeLocation");
                    String comment = rs.getString("comment");
                    //TODO redesign to avoid nested database calls or creating new connection
                    ClientProfile outputProfile = new ClientProfile(UUID.fromString(ID),status, name,surname,loadLocation(UUID.fromString(homeLocation)),comment);
                    lis.getFullClientList().add(outputProfile);
                }
                return  lis;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ListOfClientsProfiles(new ArrayList<ClientProfile>());
    }
    public static void saveClientProfile(ClientProfile clip){
        String query = "INSERT OR REPLACE INTO clientTable (clientID, status, name,surname, homeLocation, comment,isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, clip.getID().toString());
            stmt.setBoolean(2, clip.getActivityStatus());
            stmt.setString(3,clip.getName());
            stmt.setString(4,clip.getSurname());
            stmt.setString(5, String.valueOf(clip.getHomeLocation().getID()));
            stmt.setString(6,clip.getComment());
            stmt.setBoolean(7,false);
            stmt.execute();
            saveLocation(conn,clip.getHomeLocation());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static Assistant loadAssistant(UUID assistantID){
        String query = "SELECT * FROM assistantTable WHERE assistantID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,String.valueOf(assistantID));
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    //String out = rs.getString(2);
                    String ID = rs.getString("assistantID");
                    String name = rs.getString("name");
                    Boolean status = rs.getBoolean("status");
                    String surname = rs.getString("surname");
                    String contractType = rs.getString("contractType");
                    Integer contractTime = rs.getInt("contractTime");
                    Boolean likesOvertime = rs.getBoolean("likesOvertime");
                    int[] workDays = DatabaseUtils.stringToIntArray(rs.getString("workDays"));
                    String comment = rs.getString("comment");
                    ArrayList<ArrayList<UUID>> compatibility = loadCompatibility(assistantID);
                    Assistant outputAssistant = new Assistant(UUID.fromString(ID),status, name,surname,contractType,contractTime,likesOvertime,comment,workDays,compatibility);
                    return  outputAssistant;
                }
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void saveCompatibility(ArrayList<ArrayList<UUID>> inputList, UUID assistantID){
        int totalSize = 0;
        for(ArrayList<UUID> secondOrder : inputList ){
            totalSize += secondOrder.size();
        }
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(prepareCompatibilityString(totalSize))) {
            int type = 0;
            int order = 0;
            for(ArrayList<UUID> subList : inputList){
                for(UUID id : subList){
                    stmt.setString(++order, assistantID.toString() + id.toString());
                    stmt.setString(++order, id.toString());
                    stmt.setString(++order, assistantID.toString());
                    stmt.setInt(++order, type);

                }
                type++;
            }
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    private static String prepareCompatibilityString(int input ){
        StringBuilder query = new StringBuilder("INSERT OR REPLACE INTO compatibilityTable (compatibilityID,clientID, assistantID, compatibility) VALUES");
        int i = 0;
        while(i <input){
            query.append("(?, ?, ?, ?)");
            if (i < input - 1) {
                query.append(", ");
            }
            i++;
        }
        return query.toString();
    }
    public static ArrayList<ArrayList<UUID>> loadCompatibility(UUID assistantID){
        /*
        0 =
        1 =
        2 =
         */
        String query = "SELECT * FROM compatibilityTable WHERE assistantID =  ?";
        ArrayList<ArrayList<UUID>> output = new ArrayList<>(Arrays.asList(new ArrayList<UUID>(),new ArrayList<UUID>(),new ArrayList<UUID>()));
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1,String.valueOf(assistantID));
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    UUID clientID = UUID.fromString(rs.getString("clientID"));
                    int inte = rs.getInt("compatibility");
                    output.get(inte).add(clientID);
                }}
        return output;
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void saveAssistant(Assistant assistant){
        String query = "INSERT OR REPLACE INTO assistantTable (assistantID, status, name,surname, contractType, contractTime,likesOvertime,comment,workDays,isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, assistant.getID().toString());
            stmt.setBoolean(2, assistant.getActivityStatus());
            stmt.setString(3,assistant.getName());
            stmt.setString(4,assistant.getSurname());
            stmt.setString(5,assistant.getContractType());
            stmt.setDouble(6,assistant.getContractTime());
            stmt.setBoolean(7,assistant.getLikesOvertime());
            stmt.setString(8,assistant.getComment());
            stmt.setString(9,DatabaseUtils.IntArrayToString(assistant.getWorkDays()));
            stmt.setBoolean(10,false);
            stmt.execute();
            saveCompatibility(assistant.getClientPreference(), assistant.getID());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void softDeleteAssistant(Assistant assistant){
        String query = "UPDATE assistantTable SET isDeleted = ? WHERE assistantID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1,true);
            stmt.setString(2, assistant.getID().toString());
            stmt.execute();
            //TODO add delete compatibility on SoftDelete
            //saveCompatibility(assistant.getClientPreference(), assistant.getID());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void softDeleteClient(ClientProfile client){
        String query = "UPDATE clientTable SET isDeleted = ? WHERE clientID = ?";
        String deleteS = "DELETE FROM clientMonthTable WHERE clientID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName)){
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(query);
                PreparedStatement delete = conn.prepareStatement(deleteS)){
                stmt.setBoolean(1, true);
                stmt.setString(2, client.getID().toString());
                stmt.executeUpdate();
                delete.setString(1, client.getID().toString());
                delete.executeUpdate();
                conn.commit();
                //TODO add delete compatibility on SoftDelete
               //saveCompatibility(assistant.getClientPreference(), assistant.getID());
        }catch (SQLException e) {
                // Attempt to rollback transaction if SQLException occurs in the try block
                conn.rollback();
                throw new RuntimeException("Transaction rolled back due to an exception", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
    public static ListOfAssistants loadAssistants(){
        ListOfAssistants output = new ListOfAssistants(new ArrayList<Assistant>());
        String query = "SELECT * FROM assistantTable WHERE isDeleted = ?";
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setBoolean(1,false);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    //String out = rs.getString(2);
                    String ID = rs.getString("assistantID");
                    String name = rs.getString("name");
                    Boolean status = rs.getBoolean("status");
                    String surname = rs.getString("surname");
                    String contractType = rs.getString("contractType");
                    Integer contractTime = rs.getInt("contractTime");
                    Boolean likesOvertime = rs.getBoolean("likesOvertime");
                    int[] workDays = DatabaseUtils.stringToIntArray(rs.getString("workDays"));
                    String comment = rs.getString("comment");
                    ArrayList<ArrayList<UUID>> compatibility = loadCompatibility(UUID.fromString(ID));
                    Assistant outputAssistant = new Assistant(UUID.fromString(ID),status, name,surname,contractType,contractTime,likesOvertime,comment,workDays,compatibility);
                    output.getFullAssistantList().add(outputAssistant);
                }
                return  output;
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            //
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void saveClientDay(ArrayList<ClientDay> clDA, String monthID,Connection conn){
        String query = "INSERT OR REPLACE INTO clientDayTable (clientDayID, clientID, monthID,isMerged, isDay, " +
                "day,month,year,locationID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            for(ClientDay clD : clDA){
            String clDID = DatabaseUtils.prepCDID(clD);
            stmt.setString(1,clDID );
            stmt.setString(2, clD.getClient().toString());
            stmt.setString(3,monthID);
            stmt.setBoolean(4, clD.isMerged());
            stmt.setBoolean(5,clD.getDayStatus());
            stmt.setInt(6, clD.getDay());
            stmt.setInt(7, clD.getMonth().getValue());
            stmt.setInt(8,clD.getYear());
            if(clD.getLocation()==null){
                stmt.setString(9,null);
            }else{
                stmt.setString(9,clD.getLocation().getID().toString());
            }
                pruneServiceIntervals(clDID,conn);
                saveServiceIntervals(clD,clDID,conn);
            stmt.addBatch();
            }
            stmt.executeBatch();

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void saveIndividualClientDay(ClientDay clD, String monthID){
        String query = "INSERT OR REPLACE INTO clientDayTable (clientDayID, clientID, monthID,isMerged, isDay, " +
                "day,month,year,locationID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(databaseName);
                PreparedStatement stmt = conn.prepareStatement(query)) {
                String clDID = DatabaseUtils.prepCDID(clD);
                stmt.setString(1,clDID );
                stmt.setString(2, clD.getClient().toString());
                stmt.setString(3,monthID);
                stmt.setBoolean(4, clD.isMerged());
                stmt.setBoolean(5,clD.getDayStatus());
                stmt.setInt(6, clD.getDay());
                stmt.setInt(7, clD.getMonth().getValue());
                stmt.setInt(8,clD.getYear());
                if(clD.getLocation()==null){
                    stmt.setString(9,null);
                }else{
                    stmt.setString(9,clD.getLocation().getID().toString());
                }
                pruneServiceIntervals(clDID,conn);
                saveServiceIntervals(clD,clDID,conn);
                stmt.addBatch();

            stmt.executeBatch();

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static ArrayList<ClientDay> loadClientDay(UUID monthID){
        String query = "SELECT * FROM clientDayTable WHERE monthID = ?";
        ArrayList<ClientDay> lis = new ArrayList<ClientDay>();
        try(Connection conn = DriverManager.getConnection(databaseName);
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, String.valueOf(monthID));
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    String clientID = rs.getString("clientID");
                    Boolean isMerged = rs.getBoolean("isMerged");
                    Boolean isDay = rs.getBoolean(" isDay");
                    int day = rs.getInt("day");
                    int month = rs.getInt("month");
                    int year = rs.getInt("year");
                    String location = rs.getString("locationID");
                    ClientDay cl = new ClientDay(UUID.fromString(clientID), day, Month.of(month), year,null,null,loadLocation(UUID.fromString(location)),isMerged,isDay);
                    lis.add(cl);
                }
                return  lis;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static ArrayList<ClientDay> loadClientDays(String monthID, Boolean timeStatus, Connection conn){
        String query = "SELECT * FROM clientDayTable WHERE monthID = ? AND isDay = ?";
        ArrayList<ClientDay> lis = new ArrayList<ClientDay>();
        try(PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, monthID);
            stmt.setBoolean(2, timeStatus);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    String clientID = rs.getString("clientID");
                    Boolean isMerged = rs.getBoolean("isMerged");
                    Boolean isDay = rs.getBoolean("isDay");
                    int day = rs.getInt("day");
                    int month = rs.getInt("month");
                    int year = rs.getInt("year");
                    String location = rs.getString("locationID");
                    ClientDay cl = new ClientDay(UUID.fromString(clientID), day, Month.of(month), year,null,null,loadLocation(UUID.fromString(location)),isMerged,isDay);
                    cl.setDayIntervalList(loadServiceInterval(cl));
                    lis.add(cl);
                }
                return  lis;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void pruneServiceIntervals(String dayID,Connection conn){
        String sql = "DELETE FROM serviceIntervalTable WHERE clientDayID = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dayID);
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void saveServiceIntervals(ClientDay cl, String dayID, Connection conn){
        String query = "INSERT OR REPLACE INTO serviceIntervalTable (serviceIntervalID, clientDayID, overseeingAssistantID,start, end, location,isNotRequired,comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            int inDex = 0;
            int servCount = 0;
            for(ServiceInterval serv : cl.getDayIntervalList()){
                stmt.setString(++inDex, dayID+","+servCount);
                stmt.setString(++inDex, dayID);
                stmt.setString(++inDex, (serv.getOverseeingAssistant() == null) ? null :String.valueOf(serv.getOverseeingAssistant().getID()));
                stmt.setString(++inDex,serv.getStart().toString());
                stmt.setString(++inDex,serv.getEnd().toString());
                if(serv.getLocation()==null){
                    stmt.setString(++inDex,String.valueOf(serv.getLocation()));
                }else{
                    stmt.setString(++inDex,String.valueOf(serv.getLocation()));
                }
                stmt.setBoolean(++inDex,serv.getIsNotRequired());
                stmt.setString(++inDex,serv.getComment());
                servCount++;
                if(servCount>1){
                   System.out.println();
                }
                stmt.addBatch();
                inDex = 0;
            }
            stmt.executeBatch();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static ServiceIntervalArrayList loadServiceInterval(ClientDay cld){
         String query = "SELECT * FROM serviceIntervalTable WHERE clientDayID = ?";
         ServiceIntervalArrayList lis = new ServiceIntervalArrayList();
         try(Connection conn = DriverManager.getConnection(databaseName);
             PreparedStatement stmt = conn.prepareStatement(query)){
             stmt.setString(1,DatabaseUtils.prepCDID(cld));
             try(ResultSet rs = stmt.executeQuery()){
                 while(rs.next()){
                     String serviceIntervalID = rs.getString("serviceIntervalID");
                     String clientDayID = rs.getString("clientDayID");
                     String overseeingAssistantID = rs.getString("overseeingAssistantID");
                     String start = rs.getString("start");
                     String end = rs.getString("end");
                     String location = rs.getString("location");
                     Boolean isNotRequired = rs.getBoolean("isNotRequired");
                     String comment = rs.getString("comment");
                     Assistant out = null;
                     if((overseeingAssistantID != null)){
                         out = loadAssistant(UUID.fromString(overseeingAssistantID));
                     }
                     ServiceInterval outputInterval = new ServiceInterval(LocalDateTime.parse(start,formatter),LocalDateTime.parse(end,formatter),out,comment,isNotRequired);
                     if(!(location != null)){
                         outputInterval.setLocation(UUID.fromString(location));
                     }
                     lis.add(outputInterval);
                 }
                 return  lis;
             }
         }catch (Exception e) {
             System.out.println(e.getMessage());
         }
         return null;
    }
    public static void saveClientMonth(ClientMonth clm){
        String query = "INSERT OR REPLACE INTO clientMonthTable (monthID, clientID, year,month) VALUES (?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName )){
            try(PreparedStatement stmt = conn.prepareStatement(query)){
            String id = DatabaseUtils.prepMID(clm);
            conn.setAutoCommit(false);
            stmt.setString(1, id);
            stmt.setString(2, String.valueOf(clm.getClientId()));
            stmt.setInt(3,clm.getYear());
            stmt.setInt(4,clm.getMon().getValue());
            saveClientDay(clm.getClientDaysInMonth(),id,conn);
            saveClientDay(clm.getClientNightsInMonth(),id,conn);
            stmt.execute();
            conn.commit();
        }catch (SQLException e) {
            // Attempt to rollback transaction if SQLException occurs in the try block
            conn.rollback();
            conn.setAutoCommit(true);
            throw new RuntimeException("Transaction rolled back due to an exception", e);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Database error", e);
    }
    }
    public static ClientMonth loadClientMonth(int yearI, int monthI, UUID clientIDI){
        String monthID = DatabaseUtils.prepMID(yearI, monthI,clientIDI);
         String query = "SELECT * FROM clientMonthTable WHERE monthID = ?";
         try(Connection conn = DriverManager.getConnection(databaseName)){
             try( PreparedStatement stmt = conn.prepareStatement(query)){
             conn.setAutoCommit(false);
             stmt.setString(1,monthID);
             try(ResultSet rs = stmt.executeQuery()){
                 if(rs.next()){
                     String clientID = rs.getString("clientID");
                     int year = rs.getInt("year");
                     int month = rs.getInt("month");
                     //String location = rs.getString("locationID");
                     ArrayList<ClientDay> dayList = loadClientDays(monthID,true,conn);
                     ArrayList<ClientDay> nightList = loadClientDays(monthID,false,conn);
                     ClientMonth output = new ClientMonth(Month.of(month),year,UUID.fromString(clientID),dayList,nightList);
                     conn.commit();
                     return  output;
                 }else{
                     ClientMonth clm = new ClientMonth(Month.of(monthI),yearI,clientIDI,
                             Database.loadLocation(Database.loadClientProfile(clientIDI).getHomeLocation().getID()));
                     saveClientMonth(clm );
                     conn.commit();
                     return clm;
                 }
             }
         }catch (SQLException e) {
                     // Attempt to rollback transaction if SQLException occurs in the try block
                     conn.rollback();
                     conn.setAutoCommit(true);
                     throw new RuntimeException("Transaction rolled back due to an exception", e);
                 }
             } catch (SQLException e) {
                 throw new RuntimeException("Database error", e);
             }
    }
    public static ListOfClients loadFullClients(int yearI, int monthI){
        ListOfClientsProfiles lop = loadClientProfiles();
        ListOfClients loc = new ListOfClients();
        for(ClientProfile clp : lop.getFullClientList()){
            Client newCl =clp.convertToClient(loadClientMonth(yearI,monthI,clp.getID()));
            loc.getClientList().add(newCl);
        }
        return loc;
    }

    public static void saveAllClientMonths(ListOfClientMonths licmo){
            for(ClientMonth clm : licmo.getListOfClientMonths()){
                saveClientMonth(clm);
            }
    }
    public static void saveAssistantAvailability(int year,int month,AvailableAssistants av ){
        av.setYear(year);
        av.setMonth(month);
        String query = "INSERT OR REPLACE INTO AssistantAvailabilityTable (availabilityID, month, year,jsonContent) VALUES (?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            ObjectMapper objectMapper = new ObjectMapper();
            stmt.setString(1, year + "." +month);
            stmt.setInt(2, year);
            stmt.setInt(3,month);
            stmt.setString(4,objectMapper.writeValueAsString(av));
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static AvailableAssistants loadAssistantAvailability(int year,int month ){
        String query = "SELECT * FROM AssistantAvailabilityTable WHERE availabilityID = ?";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1,year + "." +month);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    ObjectMapper objectMapper = new ObjectMapper();
                    AvailableAssistants avOut = objectMapper.readValue(rs.getString("jsonContent"),AvailableAssistants.class);
                    avOut.setYear(year);
                    avOut.setMonth(month);
                    return avOut;
                }else{
                    JsonManip.getJsonManip().generateNewMonthsAssistants(year,month);
                    return loadAssistantAvailability(year,month);
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static Settings loadSettings(){
        String query = "SELECT * FROM settingsTable WHERE settingsID = ? ";

        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, "settings1");
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Settings st = Settings.createNewSettingsFile();
                    st.setCurrentYear(rs.getInt("selectedYear"));
                    st.setCurrentMonth(rs.getInt("selectedMonth"));
                    st.setDeftStart(new int[]{rs.getInt("defStart1"),rs.getInt("defStart2")});
                    st.setDefEnd(new int[]{rs.getInt("defEnd1"),rs.getInt("defEnd2")});
                    st.setFilePath(rs.getString("filePath"));
                    st.setMaxShiftLength(rs.getInt("maxShiftLength"));
                    return  st;
                }else{
                    System.out.println("Error");
                    return  Settings.createNewSettingsFile();
                }
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static void saveSettings(Settings set){
        String query = "INSERT OR REPLACE INTO settingsTable (settingsID, filePath, selectedYear,selectedMonth," +
                "defStart1,defStart2,defEnd1,defEnd2,maxShiftLength) VALUES (?, ?, ?, ?,?, ?, ?, ?, ?)";
        try(Connection conn = DriverManager.getConnection(databaseName );
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "settings1");
            stmt.setString(2, set.getFilePath());
            JsonManip.saveRedirect(set.getFilePath());
            stmt.setInt(3,set.getCurrentYear());
            stmt.setInt(4,set.getCurrentMonth());
            stmt.setInt(5,set.getDeftStart()[0]);
            stmt.setInt(6,set.getDeftStart()[1]);
            stmt.setInt(7,set.getDefEnd()[0]);
            stmt.setInt(8,set.getDefEnd()[1]);
            stmt.setInt(9,set.getMaxShiftLength());
            stmt.execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
