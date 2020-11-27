import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.util.Set;

import javax.crypto.interfaces.PBEKey;

public class Connect {
   private int pid = 0;
   private int contact_id = 100;
   private int room_num = 0;
   private int admission_id = 0;
   private int doctor_id = 0;
   private int treatment_id = 0;
   private Hashtable<Integer, Boolean> rooms = new Hashtable<Integer, Boolean>();

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** INTIATE DB **********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   
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

   private void PrintStart(String statement) {
      System.out.println("\n+++++++++++++++++++++++++\n*************************\n");
      System.out.println(statement);
      System.out.println("-------------------------\n");
   }

   private void PrintEnd(String statement) {

      System.out.println("\n-------------------------");
      System.out.println(statement);
      System.out.println("\n*************************\n+++++++++++++++++++++++++\n\n~~~~~~~~~~~~~~~~~~~~~~~~~");
   }

   public void CreateTables() {
      PrintStart("BEGIN TABLE CREATION");
      String delimiter = ";";
      Scanner scanner;
      File file = new File("create_tables.txt");
      try {
         scanner = new Scanner(file).useDelimiter(delimiter);
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
         return;
      }
      while (scanner.hasNext()) {
         String sql = scanner.next() + delimiter;
         String[] sp = sql.split(" ");
         try (Connection conn = this.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
            // System.out.println("TABLE " + sp[2] + " CREATED");
         } catch (SQLException e) {
            System.out.println(e.getMessage());
         }
      }
      scanner.close();
      PrintEnd("END TABLE CREATION");
      //intiateRooms();
   }

   public void DropAllTables() throws FileNotFoundException {
      String delimiter = ";";
      Scanner scanner;
      File file = new File("drop_tables.txt");
      try {
         scanner = new Scanner(file).useDelimiter(delimiter);
      } catch (FileNotFoundException e1) {
         e1.printStackTrace();
         return;
      }
      while (scanner.hasNext()) {
         // Get statement
         String sql = scanner.next() + delimiter;
         System.out.println(sql);
         try (Connection conn = this.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            ps.close();
            ResetCount();
         } catch (SQLException e) {
            System.out.println(e.getMessage());
         }
      }
      scanner.close();
      System.out.println("All tables dropped");

   }

   public void ResetCount() {
      pid = 0;

      contact_id = 100;

      room_num = 0;

      admission_id = 0;
   }

   public void ListTables() {
      PrintStart("BEGIN TABLE LIST");
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
      PrintEnd("END TABLE LIST");
   }

   public void ListColumns(String table) {
      PrintStart("START " + table + " COLUMN LIST");
      String sql = "SELECT * from " + table + ";";
      try (Connection conn = this.connect();) {
         Statement statement = conn.createStatement();
         ResultSet results = statement.executeQuery(sql);
         ResultSetMetaData metadata = results.getMetaData();
         int columnCount = metadata.getColumnCount();
         for (int i = 1; i <= columnCount; i++) {
            String columnName = metadata.getColumnName(i);
            System.out.println(columnName);
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END " + table + " COLUMN LIST");
   }

   // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** PERSON QUERIES **********************
   // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   public void InsertPerson(String first_name, String last_name) {
      PrintStart("ADD PERSON");
      pid++;
      String id = Integer.toString(pid);
      String sql = "INSERT INTO person(person_id, first_name, last_name) VALUES(?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id); // First ? in sql
         ps.setString(2, first_name); // Second ? in sql
         ps.setString(3, last_name); // Fourth ? in sql
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println(first_name + " " + last_name + " ADDED with id: " + id);
      PrintEnd("ADDED PERSON");
   }

   public boolean PersonExists(String last_name) {
      PrintStart("START PERSONS CHECK");
      String sql = "SELECT * FROM person WHERE last_name like '" + last_name + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // loop through the result set
         System.out.println("Check for name: " + rs.getString("last_name"));
         if (rs.getString("last_name").equals(last_name)) {
            System.out.println(rs.getString("last_name") + " ALREADY EXISTS");
            PrintEnd("END PERSONS CHECK");
            return true;
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("Person with last name " + last_name + " can be added");
      PrintEnd("END PERSONS CHECK");
      return false;
   }

   public String[] PersonInfo(String last_name) {
      PrintStart("GET PERSON INFO");
      String[] data = new String[3];
      String id = "";
      String first = "";
      String last = "";
      String sql = "SELECT * FROM person WHERE last_name like '" + last_name + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         System.out.println(rs.getString("first_name") + " " + rs.getString("last_name") +  " id: "
               + rs.getString("person_id"));
         id = rs.getString("person_id");
         data[0] = id;
         first = rs.getString("first_name");
         data[1] = first;
         last = rs.getString("last_name");
         data[2] = last;
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END GET PERSON INFO");
      return data;
   }

   public String[] PersonInfoByID(String pid) {
      PrintStart("GET PERSON INFO");
      String[] data = new String[3];
      String id = "";
      String first = "";
      String last = "";
      String sql = "SELECT * FROM person WHERE person_id like '" + pid + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         System.out.println(rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t" + " id: "
               + rs.getString("person_id") + "\t");
         id = rs.getString("person_id");
         data[0] = id;
         System.out.println("GOT THE ID: " + id);
         first = rs.getString("first_name");
         data[1] = first;
         last = rs.getString("last_name");
         data[2] = last;
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END GET PERSON INFO");
      return data;
   }

   public void ListPersons() {
      PrintStart("LIST PERSONS");
      String sql = "SELECT person_id, first_name, last_name FROM person;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(rs.getString("first_name") + " " + rs.getString("last_name")  + " id: "
                  + rs.getString("person_id") + "\t");
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END PERSONS LIST");
   }

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** PATIENT QUERIES **********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertPatient(String last_name, String pol_name, String pol_num) {
      PrintStart("ADD PATIENT");
      String[] data = PersonInfo(last_name);
      int id = Integer.valueOf(data[0]);
      String sql = "INSERT INTO patient(person_id, first_name, last_name, ins_name, ins_policy) VALUES(?,?,?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         // System.out.pring
         
         ps.setInt(1, id);
         ps.setString(2, data[1]);
         ps.setString(3, data[2]);
         ps.setString(4, pol_name);
         ps.setString(5, pol_num);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println(data[1] + " " + last_name + " ADDED with id: " + data[0]);
      PrintEnd("ADDED PATIENT");
   }

   public void ListPatients() {
      PrintStart("LIST PATIENTS");
      String sql = "SELECT person_id, first_name, last_name, ins_name, ins_policy FROM patient;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\n" + rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t" + " id: "
                  + rs.getString("person_id") + "\n" + "Insurance: " + rs.getString("ins_name") + "\nPolicy number: "
                  + rs.getString("ins_policy"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END PATIENTS LIST");
   }

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** EMERGENCY CONTACT QUERIES **********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertEContact(String name, String phone, String plname) {
      PrintStart("ADD EMERGENCY CONTACT");
      // String[] data = PersonInfo(last_name);
      String[] patient = PersonInfo(plname);
      contact_id++;
      String id = Integer.toString(contact_id);

      String sql = "INSERT INTO emergency_contact(contact_id, name, patient_id, phone_number) VALUES(?,?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id);
         ps.setString(2, name);
         ps.setString(3, patient[0]);
         ps.setString(4, phone);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println(name + " ADDED with id: " + id + " EMERGENCY CONTACT FOR PATIENT ID: " + patient[0]);
      PrintEnd("ADDED EMERGENCY CONTACT");
   }

   public void ListEContacts() {
      PrintStart("LIST EMERGENCY CONTACTS");
      String sql = "SELECT contact_id, name, patient_id, phone_number FROM emergency_contact;";
      // String[] pdata;
      // String[] data = PersonInfo(last_name);
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // pdata = PersonInfoByID(rs.getString("person_id"));
         while (rs.next()) {
            System.out.println("\n" + rs.getString("name") + "\t"
            // + pdata[2] + "\t"
                  + " id: " + rs.getString("contact_id") + "\t" + " phone number: " + rs.getString("phone_number")
                  + "\n" + "Contact for patient id: " + rs.getString("patient_id"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END EMERGENCY CONTACTS LIST");
   }

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** ROOM QUERIES **********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertRoom(String plname, String room) {
      PrintStart("ADD ROOM");
      String[] patient = PersonInfo(plname);
      // String id = Integer.toString(room);
      boolean roomCheck = CheckOccupiedRoom(room);
      String sql = "INSERT INTO room(room_id, occupied, patient_id) VALUES(?,?,?);";
      if (Integer.valueOf(room) > 20) {
         System.out.println("Invalid Room Number: " + room);
      }
      else if(roomCheck == false){
         try (Connection conn = this.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
               ps.setString(1, room);
               ps.setString(2, "TRUE");
               ps.setString(3, patient[0]);
               ps.executeUpdate();
               ps.close();
               System.out.println("Room number: " + room 
               + " OCCUPIED by PATIENT: " + patient[1] 
               + " " + patient[2]);
         } catch (SQLException e) {
               System.out.println(e.getMessage());
         }

      }
      else{
         System.out.println("Room Number: " + room + " is occupied");
      }
      PrintEnd("END ADD ROOM");
   }


   public void ListRooms() {
      PrintStart("LIST ROOMS");
      String sql = "SELECT room_id, patient_id, occupied FROM room;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
        
         while (rs.next()) {
            System.out.println("\n Patient with id: " 
            + rs.getString("patient_id") + " in room: "
                  + rs.getString("room_id") 
                  + "\n Room is occupied? "  
                  + rs.getString("occupied"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END EMERGENCY CONTACTS LIST");
   }

   public boolean CheckOccupiedRoom( String room) {
      String sql = "SELECT * FROM room WHERE room_id like '" + room + "';";
      boolean occupied = false;
      PrintStart("CHECK FOR OCCUPIED ROOM");
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {        
            if(rs.getInt("occupied") == 1){
               occupied = true;
               System.out.println("Room number " + room + " IS occupied");  
            }   
            else{
               System.out.println("Room number " + room + " IS NOT occupied");  
            } 
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END CHECK FOR OCCUPIED ROOM");
      return occupied;
      
   }

   public boolean CheckRoom( String room) {
      String sql = "SELECT * FROM room WHERE room_id like '" + room + "';";
      boolean occupied = false;
      PrintStart("CHECK FOR OCCUPIED ROOM");
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(rs.getString("room_id") + "\t" + " is occupied?" + rs.getString("occupied"));  
            if(rs.getString("occupied") == "true"){
               occupied = true;
            }     
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END CHECK FOR OCCUPIED ROOM");
      return occupied;
      
   }

   public boolean CheckRooms() {
      String sql = "SELECT COUNT(*) AS total FROM room WHERE occupied;";
      boolean occupied = false;
      int total = 0;
      PrintStart("CHECK FOR OCCUPIED ROOM");
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(rs.getInt("total") + " rooms are occupied");  
           total = rs.getInt("total");
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      if(total == 20){
         occupied = true;
         System.out.println("All rooms occupied");
         return occupied;
      }
      PrintEnd("END CHECK FOR OCCUPIED ROOM");
      return occupied;
      
   }




   // public boolean CheckMaxRooms() {
   //    if (room_num >= 20) {
   //       System.out.println("CANNOT ADD PATIENT TO ROOM \nMAX NUMBER OF ROOMS REACHED");
   //       return true;
   //    }
   //    return false;
   // }

   public boolean RoomMaxOut() {
      String sql = "SELECT MAX(room_id) FROM room;";
      int room = 0;
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(rs.getString("room_id") + "\t" + rs.getString("last_name") + "\t" + " id: "
                  + rs.getString("person_id") + "\t");
                  room = Integer.parseInt(rs.getString("room_id"));
                
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      if(room >= 20){
         return true;
      }
      return false;
   }

   private Hashtable intiateRooms(){
      PrintStart("INTIATE ROOMS");
      int count = 1;
      while(count <= 20 ){
         rooms.put(count, false);
         count++;
      }
      Set<Integer> keys = rooms.keySet();
      for(Integer v: keys){
         System.out.println("ROOM: "  + v + " OCCUPIED? " + rooms.get(v));
      }
      PrintStart("END INTIATE ROOMS");
      return rooms;
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

   private void updateRoomTableTrue(Integer room){
      rooms.replace(room, false, true);
   }

   private void updateRoomTableFalse(Integer room){
      rooms.replace(room, false, true);
   }




   public String RoomByPatient(String last_name) {
      PrintStart("GET ROOM PATIENT");
      String[] data = PersonInfo(last_name);
      String id = data[0];
      String room = "";
      String sql = "SELECT * FROM room WHERE patient_id like '" + id + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         System.out.println("Patient: " + last_name + " in room number: " + rs.getString("room_id"));
         room = rs.getString("room_id");

      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END GET ROOM PATIENT");
      return room;
   }

   // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** DOCTOR QUERIES ***********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertDoctor(String name) {
      PrintStart("ADD DOCTOR");
      // String[] data = PersonInfo(last_name);
      // String[] patient = PersonInfo(plname);
      doctor_id++;
      String id = Integer.toString(doctor_id);

      String sql = "INSERT INTO doctor(doctor_id, name) VALUES(?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id);
         ps.setString(2, name);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("Doctor " + name + " ADDED with id: " + id);
      PrintEnd("ADDED DOCTOR");
   }

   public boolean DoctorExists(String doctor) {
      PrintStart("START DOCTOR CHECK");
      String sql = "SELECT * FROM doctor WHERE name like '" + doctor + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // loop through the result set
         System.out.println("Check for name: " + rs.getString("name"));
         if (rs.getString("name").equals(doctor)) {
            System.out.println("Doctor " + rs.getString("name") + " ALREADY EXISTS");
            PrintEnd("END DOCTOR CHECK");
            return true;
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("Doctor with name " + doctor + " can be added");
      PrintEnd("END DOCTOR CHECK");
      return false;
   }

   public void ListDoctors() {
      PrintStart("LIST DOCTORS");
      String sql = "SELECT doctor_id, name FROM doctor;";
      // String[] pdata;
      // String[] data = PersonInfo(last_name);
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // pdata = PersonInfoByID(rs.getString("person_id"));
         while (rs.next()) {
            System.out.println("\nDoctor: " + rs.getString("name") + "\t"
            // + pdata[2] + "\t"
                  + " id: " + rs.getString("doctor_id"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END DOCTOR LIST");
   }

   public String GetDoctorID(String doctor) {
      PrintStart("DOCTOR ID BY NAME");
      String id = "";
      String sql = "SELECT * FROM doctor WHERE name like '" + doctor + "';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // pdata = PersonInfoByID(rs.getString("person_id"));
         while (rs.next()) {
            System.out.println("\nDoctor: " + rs.getString("name") + "\t"
            // + pdata[2] + "\t"
                  + " id: " + rs.getString("doctor_id"));
         }
         id = rs.getString("doctor_id");

      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END DOCTOR ID BY NAME");
      return id;

   }

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** ADDMISSION QUERIES *******************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertAdmission(String pname, String dname, String diagnosis, String date_start, String date_end, String room) {
      // String room = RoomByPatient(pname);
      PrintStart("ADD ADMISSION RECORD");
      String[] patient = PersonInfo(pname);
      // String doc_id = GetDoctorID(dname);
      admission_id++;

      String id = Integer.toString(admission_id);
      String sql = "INSERT INTO admission_records(admission_record_id, date_admitted, date_discharged, diagnosis, patient_id,  doctor_name, room_number, patient_lastname, discharged) VALUES(?,?,?,?,?,?,?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id);
         ps.setString(2, date_start);
         ps.setString(3, date_end);
         ps.setString(4, diagnosis);
         ps.setString(5, patient[0]);
         ps.setString(6, dname);
         ps.setString(7, room);
         ps.setString(8, pname);
         if (!date_end.isEmpty()) {
            ps.setString(9, "TRUE");
         } else {
            ps.setString(8, "FALSE");
         }
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      // System.out.println(name + " ADDED with id: " + id + " EMERGENCY CONTACT FOR
      // PATIENT ID: " + patient[0]);
      PrintEnd("END ADD ADMISSION RECORD");
   }

   public void ListAdmissions() {
      PrintStart("LIST ADMIN RECORDS");
      String sql = "SELECT * FROM admission_records;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // pdata = PersonInfoByID(rs.getString("person_id"));
         while (rs.next()) {
            System.out.println("\n Record number: " + rs.getString("admission_record_id") + "\n Admit date: "
                  + rs.getString("date_admitted") + "\n Discharge date: " + rs.getString("date_discharged")
                  + "\n Diagnosis: " + rs.getString("diagnosis") + "\n Discharged: " + rs.getString("discharged")
                  + "\n Patient id: " + rs.getString("patient_id") + "\n Patient name: "
                  + rs.getString("patient_lastname") + "\n Doctor name: " + rs.getString("doctor_name")
                  + "\n Room number: " + rs.getString("room_number"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END LIST ADMIN RECORDS");
   }

   public void GetAdmission() {
      PrintStart("GET ADMISSION");
      String sql = "SELECT * FROM admission_records;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         // pdata = PersonInfoByID(rs.getString("person_id"));
         while (rs.next()) {
            System.out.println("\n Record number: " + rs.getString("admission_record_id") + "\n Admit date: "
                  + rs.getString("date_admitted") + "\n Discharge date: " + rs.getString("date_discharged")
                  + "\n Diagnosis: " + rs.getString("diagnosis") + "\n Discharged: " + rs.getString("discharged")
                  + "\n Patient id: " + rs.getString("patient_id") + "\n Patient name: "
                  + rs.getString("patient_lastname") + "\n Doctor name: " + rs.getString("doctor_name")
                  + "\n Room number: " + rs.getString("room_number"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END LIST ADMIN RECORDS");
   }

   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   // ******************** TREATMENT QUERIES ********************
   // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   public void InsertTreatment(String pname, String dname, String date, String proc, String med) {
      PrintStart("ADD TREATMENT RECORD");
      treatment_id++;

      String id = Integer.toString(treatment_id);
      String sql = "INSERT INTO treatments(treatment_id,  patient_lastname, doctor_name, treatment_date, procedure_type, medication, treatment_completed) VALUES(?,?,?,?,?,?, ?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id);
         ps.setString(2, pname);
         ps.setString(3, dname);
         ps.setString(4, date);
         ps.setString(5, proc);
         ps.setString(6, med);
         ps.setString(7, "TRUE");
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      // System.out.println(proc + " ADDED with id: " + id + " EMERGENCY CONTACT FOR
      // PATIENT ID: " + patient[0]);
      PrintEnd("END ADD TREATMENT RECORD");
   }

   // public void ListAdmissions() {
   // PrintStart("LIST ADMIN RECORDS");
   // String sql = "SELECT * FROM admission_records;";
   // try (Connection conn = this.connect();
   // Statement stmt = conn.createStatement();
   // ResultSet rs = stmt.executeQuery(sql)) {
   // //pdata = PersonInfoByID(rs.getString("person_id"));
   // while (rs.next()) {
   // System.out.println(
   // "\n Record number: " + rs.getString("admission_record_id")
   // + "\n Admit date: " + rs.getString("date_admitted")
   // + "\n Discharge date: " + rs.getString("date_discharged")
   // + "\n Diagnosis: " + rs.getString("diagnosis")
   // + "\n Discharged: " + rs.getString("discharged")
   // + "\n Patient id: " + rs.getString("patient_id")
   // + "\n Patient name: " + rs.getString("patient_lastname")
   // + "\n Doctor name: " + rs.getString("doctor_name")
   // + "\n Room number: " + rs.getString("room_number"));
   // }
   // } catch (SQLException e) {
   // System.out.println(e.getMessage());
   // }
   // PrintEnd("END LIST ADMIN RECORDS");
   // }

   public static void main(String[] args) throws FileNotFoundException {
      // Connect app = new Connect();
      // app.DropAllTables();
      // // app.insertPerson("123", "Michael", "Auburn");
      // // System.out.println("After Insert");
      // // app.selectSample();
      // app.CreateTables();
      // //app.ListColumns("patient");
      // app.InsertPerson("Don", "Johnson");
      // app.InsertPerson("Moe", "Joe");
      // app.ListPersons();
      // //app.selectSample();
      // app.ListTables();
   }
}
