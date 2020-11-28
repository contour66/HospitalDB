import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DatabaseMetaData;
import java.io.BufferedReader;
import java.io.Reader;

gi
public class Scratch {

   private Connection connect() {
      // SQLite connection string
      String url = "jdbc:sqlite:Hospital.sl3";
      Connection conn = null;
      try {
         conn = DriverManager.getConnection(url);
         // This will turn on foreign keys
         // by default SQLite turns them off
         conn.createStatement().executeUpdate("PRAGMA foreign_keys = ON;");
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      return conn;
   }

   // public checkRooms(){
   //    PrintStart("CHECK ROOMS");
   //    int count = 1;
   //    while(count <= 20 ){
   //       rooms.put(count, false);
   //       count++;
   //    }
   //    Set<Integer> keys = rooms.keySet();
   //    for(Integer v: keys){
   //       System.out.println("ROOM: "  + v + " OCCUPIED? " + rooms.get(v));
   //    }
   //    PrintStart("END INTIATE ROOMS");
   //    return rooms;
   // }


   // public boolean RoomMaxOut() {
   //    String sql = "SELECT MAX(room_id) FROM room;";
   //    int room = 0;
   //    try (Connection conn = this.connect();
   //          Statement stmt = conn.createStatement();
   //          ResultSet rs = stmt.executeQuery(sql)) {
   //       while (rs.next()) {
   //          System.out.println(rs.getString("room_id") + "\t" + rs.getString("last_name") + "\t" + " id: "
   //                + rs.getString("person_id") + "\t");
   //                room = Integer.parseInt(rs.getString("room_id"));
                
   //       }
   //    } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   //    }
   //    if(room >= 20){
   //       return true;
   //    }
   //    return false;
   // }


   // public void CreateTables() {

   //    String sql =
   //    "CREATE TABLE person(person_id INTEGER PRIMARY KEY, first_name TEXT NOT NULL, last_name TEXT NOT NULL UNIQUE);"
   //    + "CREATE TABLE patient(person_id INTEGER PRIMARY KEY REFERENCES people(personid), ins_policy INTEGER, ins_name TEXT);";

   //    try (Connection conn = this.connect();) {
   //       PreparedStatement ps = conn.prepareStatement(sql);
   //       ps.executeUpdate();
   //       ps.close();
   //    } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   //    }
   //    System.out.println("Create Tables");
   // }




public void CreateTables(){
    // Delimiter
    String delimiter = ";";

    // Create scanner
    Scanner scanner;
    File file = new File("create_tables.txt");
    try {
        scanner = new Scanner(file).useDelimiter(delimiter);
    } catch (FileNotFoundException e1) {
        e1.printStackTrace();
        return;
    }

    // Loop through the SQL file statements 
    Statement currentStatement = null;
    while(scanner.hasNext()) {

        // Get statement 
        String rawStatement = scanner.next() + delimiter;
        try (Connection conn = this.connect();) {
            // Execute statement
            currentStatement = conn.createStatement();
            currentStatement.execute(rawStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Release resources
            if (currentStatement != null) {
                try {
                    currentStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            currentStatement = null;
        }
    }
scanner.close();
}



public void DropAllTables(){
   // Delimiter
   String delimiter = ";";

   // Create scanner
   Scanner scanner;
   File file = new File("drop_tables.txt");
   try {
       scanner = new Scanner(file).useDelimiter(delimiter);
   } catch (FileNotFoundException e1) {
       e1.printStackTrace();
       return;
   }

   // Loop through the SQL file statements 
   Statement currentStatement = null;
   while(scanner.hasNext()) {

       // Get statement 
       String rawStatement = scanner.next() + delimiter;
       try (Connection conn = this.connect();) {
           // Execute statement
           currentStatement = conn.createStatement();
           currentStatement.execute(rawStatement);
       } catch (SQLException e) {
           e.printStackTrace();
       } finally {
           // Release resources
           if (currentStatement != null) {
               try {
                   currentStatement.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
           }
           currentStatement = null;
       }
   }
scanner.close();
}



   // public void DropAllTables() {
   //    String drop = "DROP TABLE person;" + "DROP TABLE _patient_old;" + "DROP TABLE adminstrator;"
   //          + "DROP TABLE admission_record;" + "DROP TABLE doctor;" + "DROP TABLE emergency_contact;"
   //          + "DROP TABLE nurse;" + "DROP TABLE patient;" + "DROP TABLE technician;" + "DROP TABLE treatment;"
   //          + "DROP TABLE volunteer;" + "DROP TABLE room;";
   //    try (Connection conn = this.connect();) {
   //       PreparedStatement ps = conn.prepareStatement(drop);
   //       ps.executeUpdate();
   //       ps.close();
   //    } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   //    }
   // }

   public void checkLastName(String lastname) {
      String sql = "SELECT " + lastname + ", FROM person;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // loop through the result set
         while (rs.next()) {
            System.out.println(rs.getString("first_name") + "\t" + rs.getString("first_name") + "\t");
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   public void selectSample() {
      String sql = "SELECT person_id, first_name FROM person;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // loop through the result set
         while (rs.next()) {
            System.out.println(rs.getString("first_name") + "\t" + rs.getString("first_name") + "\t");
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   public void insertPerson(String person_id, String first_name, String last_name) {

      // This is BAD! Do NOT use!
      // This allows SQL injection attacks!
      // String BADsql = "INSERT INTO Suppliers(snum, sname, status, city) VALUES (" +
      // snum + ","+ sname + ","+ status + "," + city + ");";

      // This is much better!

      // String sql ="CREATE TABLE person(person_id INTEGER PRIMARY KEY, first_name
      // TEXT NOT NULL, last_name TEXT NOT NULL UNIQUE);";
      String sql = "INSERT INTO person(person_id, first_name, last_name) VALUES (?,?,?);";

      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, person_id); // First ? in sql
         ps.setString(2, first_name); // Second ? in sql
         ps.setString(3, last_name); // Fourth ? in sql
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("All tables dropped");
   }

   public void ListTables() {
      try (Connection conn = this.connect();) {
         DatabaseMetaData metaData = conn.getMetaData();
         String[] types = { "TABLE" }; // Retrieving the columns in the database
         ResultSet tables = metaData.getTables(null, null, "%", types);
         while (tables.next()) {
            System.out.println(tables.getString("TABLE_NAME"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
   }

   // public static void NewTables() throws FileNotFoundException {
//       Connect app = new Connect();
//       try {
//          app.CreateTables();
//       } catch (FileNotFoundException e) {
//          System.out.println(e.getMessage());
//       }
// 
//    }

   public static void main(String[] args) throws FileNotFoundException {
      Connect app = new Connect();
      app.DropAllTables();
      // app.insertPerson("123", "Michael", "Auburn");
      // System.out.println("After Insert");
      // app.selectSample();
         app.CreateTables();
    
       app.ListTables();
   }



   // public boolean CheckMaxRooms() {
   //    if (room_num >= 20) {
   //       System.out.println("CANNOT ADD PATIENT TO ROOM \nMAX NUMBER OF ROOMS REACHED");
   //       return true;
   //    }
   //    return false;
   // }

   // public boolean RoomMaxOut() {
   //    String sql = "SELECT MAX(room_id) FROM room;";
   //    int room = 0;
   //    try (Connection conn = this.connect();
   //          Statement stmt = conn.createStatement();
   //          ResultSet rs = stmt.executeQuery(sql)) {
   //       while (rs.next()) {
   //          System.out.println(rs.getString("room_id") + "\t" + rs.getString("last_name") + "\t" + " id: "
   //                + rs.getString("person_id") + "\t");
   //                room = Integer.parseInt(rs.getString("room_id"));
                
   //       }
   //    } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   //    }
   //    if(room >= 20){
   //       return true;
   //    }
   //    return false;
   // }

   // private Hashtable intiateRooms(){
   //    PrintStart("INTIATE ROOMS");
   //    int count = 1;
   //    while(count <= 20 ){
   //       rooms.put(count, false);
   //       count++;
   //    }
   //    Set<Integer> keys = rooms.keySet();
   //    for(Integer v: keys){
   //       System.out.println("ROOM: "  + v + " OCCUPIED? " + rooms.get(v));
   //    }
   //    PrintStart("END INTIATE ROOMS");
   //    return rooms;
   // }


   // private void updateRoomTableTrue(Integer room){
   //    rooms.replace(room, false, true);
   // }

   // private void updateRoomTableFalse(Integer room){
   //    rooms.replace(room, false, true);
   // }

}