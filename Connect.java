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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect {
   private int pid = 0;
   private int contact_id = 1000;
   private int admission_id = 0;
   private int doctor_id = 100;
   private int treatment_id = 0;
   private Hashtable<Integer, Boolean> rooms = new Hashtable<Integer, Boolean>();

   // ******************** INTIATE DB **********************
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

   // ******************** POPULATE PERSON QUERIES **********************
   public void InsertPerson(String first_name, String last_name) {
      PrintStart("ADD PERSON");
      pid++;
      // String id = Integer.toString(pid);
      String sql = "INSERT INTO person(person_id, first_name, last_name) VALUES(?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setInt(1, pid); // First ? in sql
         ps.setString(2, first_name); // Second ? in sql
         ps.setString(3, last_name); // Fourth ? in sql
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println(first_name + " " + last_name + " ADDED with id: " + pid);
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
         System.out.println(
               rs.getString("first_name") + " " + rs.getString("last_name") + " id: " + rs.getString("person_id"));
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
            System.out.println("\n" + rs.getString("first_name") + " " + rs.getString("last_name") + " id: "
                  + rs.getString("person_id") + "\t");
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END PERSONS LIST");
   }

   // ******************** POPULATE PATIENT QUERIES **********************

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

   // ******************** POPULATE EMERGENCY CONTACT QUERIES **********************

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

   // ******************** POPULATE ROOM QUERIES **********************

   public void InsertRoom(String plname, String room) {
      PrintStart("ADD ROOM");
      String[] patient = PersonInfo(plname);
      // String id = Integer.toString(room);
      boolean roomCheck = CheckOccupiedRoom(room);
      String sql = "INSERT INTO room(room_id, occupied, patient_id, patient_lastname) VALUES(?,?,?,?);";
      if (Integer.valueOf(room) > 20) {
         System.out.println("Invalid Room Number: " + room);
      } else if (roomCheck == false) {
         try (Connection conn = this.connect();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, room);
            ps.setString(2, "TRUE");
            ps.setString(3, patient[0]);
            ps.setString(4, plname);
            ps.executeUpdate();
            ps.close();
            System.out.println("Room number: " + room + "\nOCCUPIED by PATIENT: " + plname + "\nPatient id: "
                  + patient[1] + " " + patient[2]);
         } catch (SQLException e) {
            System.out.println(e.getMessage());
         }

      } else {
         System.out.println("Room Number: " + room + " is occupied");
      }
      PrintEnd("END ADD ROOM");
   }

   public void ListRooms() {
      PrintStart("LIST ROOMS");
      String sql = "SELECT room_id, patient_id, occupied, patient_lastname FROM room;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out
                  .println("\nPatient " + rs.getString("patient_lastname") + " with id: " + rs.getString("patient_id")
                        + " in room: " + rs.getString("room_id") + "\n Room is occupied? " + rs.getString("occupied"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END EMERGENCY CONTACTS LIST");
   }

   public boolean CheckOccupiedRoom(String room) {
      String sql = "SELECT * FROM room WHERE room_id like '" + room + "';";
      boolean occupied = false;
      PrintStart("CHECK FOR OCCUPIED ROOM");
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            if (rs.getInt("occupied") == 1) {
               occupied = true;
               System.out.println("Room number " + room + " IS occupied");
            } else {
               System.out.println("Room number " + room + " IS NOT occupied");
            }
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END CHECK FOR OCCUPIED ROOM");
      return occupied;

   }

   private Hashtable intiateRooms() {
      PrintStart("INTIATE ROOMS");
      int count = 1;
      while (count <= 20) {
         rooms.put(count, false);
         count++;
      }
      Set<Integer> keys = rooms.keySet();
      for (Integer v : keys) {
         System.out.println("ROOM: " + v + " OCCUPIED? " + rooms.get(v));
      }
      PrintStart("END INTIATE ROOMS");
      return rooms;
   }

   private void updateRoomTableTrue(Integer room) {
      rooms.replace(room, false, true);
   }

   private void updateRoomTableFalse(Integer room) {
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

   // ******************** POPULATE DOCTOR QUERIES ***********************

   public void InsertDoctor(String name) {
      PrintStart("ADD DOCTOR");
      // String[] data = PersonInfo(last_name);
      // String[] patient = PersonInfo(plname);
      doctor_id++;
      // String id = Integer.toString(doctor_id);

      String sql = "INSERT INTO doctor(doctor_id, name) VALUES(?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setInt(1, doctor_id);
         ps.setString(2, name);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("Doctor " + name + " ADDED with id: " + doctor_id);
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

   // ******************** POPULATE ADDMISSION QUERIES *******************

   public void InsertAdmission(String pname, String dname, String diagnosis, String date_start, String date_end,
         String room) {
      // String room = RoomByPatient(pname);
      PrintStart("ADD ADMISSION RECORD");
      String[] patient = PersonInfo(pname);
      // String doc_id = GetDoctorID(dname);
      admission_id++;

      // String id = Integer.toString(admission_id);
      String sql = "INSERT INTO admission_records(admission_record_id, date_admitted, date_discharged, diagnosis, patient_id,  doctor_name, room_number, patient_lastname, discharged) VALUES(?,?,?,?,?,?,?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setInt(1, admission_id);
         ps.setString(2, date_start);
         ps.setString(3, date_end);
         ps.setString(4, diagnosis);
         ps.setString(5, patient[0]);
         ps.setString(6, dname);
         ps.setString(7, room);
         ps.setString(8, pname);
         if (date_end != null) {
            ps.setString(9, "TRUE");
         } else {
            ps.setString(9, "FALSE");
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
            System.out.println("\nRecord number: " + rs.getString("admission_record_id") + "\nAdmit date: "
                  + rs.getString("date_admitted") + "\nDischarge date: " + rs.getString("date_discharged")
                  + "\nDiagnosis: " + rs.getString("diagnosis") + "\nDischarged: " + rs.getString("discharged")
                  + "\nPatient id: " + rs.getString("patient_id") + "\nPatient name: "
                  + rs.getString("patient_lastname") + "\nDoctor name: " + rs.getString("doctor_name")
                  + "\nRoom number: " + rs.getString("room_number"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END LIST ADMIN RECORDS");
   }

   // ******************** POPULATE TREATMENT QUERIES ********************

   public void InsertTreatment(String pname, String dname, String date, String proc, String med) {
      PrintStart("ADD TREATMENT RECORD");
      treatment_id++;
      String id = Integer.toString(treatment_id);
      String sql = "INSERT INTO treatments(treatment_id, patient_lastname, doctor_name, procedure_type, treatment, treatment_date) VALUES(?,?,?,?,?,?);";
      try (Connection conn = this.connect();) {
         PreparedStatement ps = conn.prepareStatement(sql);
         ps.setString(1, id);
         ps.setString(2, pname);
         ps.setString(3, dname);
         ps.setString(4, date);
         ps.setString(5, proc);
         ps.setString(6, med);
         // ps.setString(7, "TRUE");
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      System.out.println("Patient: " + pname + "\nDoctor: " + dname + "\nProcedure: " + proc + "\nTreatment: " + med
            + "\nDate: " + date);
      PrintEnd("END ADD TREATMENT RECORD");
   }

   public void ListTreatments() {
      PrintStart("LIST TREAMENTS RECORDS");
      String sql = "SELECT * FROM treatments;";
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

   //! ====================================================================
   //? ********************************************************************
   //? ############### QUERY SELECTION METHODS FOR MENU ###################
   //? ********************************************************************
   //! ====================================================================

   //*** 1.1) List the rooms that are occupied, along with the associated
   //*** patient names and the date the patient was admitted. 
   public void Query_1_1() {
      PrintStart("Query_1_1");
      //String rooms = "SELECT room_id, patient_lastname FROM room;";
      String admit = "SELECT room.room_id, room.occupied, admission_records.patient_lastname, admission_records.date_admitted"
            + " FROM admission_records INNER JOIN room ON room.patient_lastname = admission_records.patient_lastname";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(admit)) {
         while (rs.next()) {
            System.out.println("\nPatient: " + rs.getString("patient_lastname") + "\nDate admitted: "
                  + rs.getString("date_admitted") + "\nRoom number: " + rs.getString("room_id"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_1_1");

   }

   //*** 1.2) List the rooms that are currently unoccupied.
   public void Query_1_2() {
   }
   //    String sql = "SELECT * FROM room;";   

   //    PrintStart("LIST UNOCCUPIED ROOM");
   //       try (Connection conn = this.connect();
   //          Statement stmt = conn.createStatement();
   //          ResultSet rs = stmt.executeQuery(roomTable.get(j))) {
   //           while (rs.next()) {   
   //             // System.out.println("ROOM NOT EXISTS " + Integer.valueOf(rs.getString("room_id")));
   //            boolean empty = false;
   //            int room = Integer.valueOf(rs.getString("room_id"));
   //             if(rs.getInt("occupied") == 0){
   //                empty = true;
   //             }
   //             roomOccupied(room, empty);
   //          }
   //       } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   // }
   //    PrintEnd("END LIST UNOCCUPIED");
   // }

   // }

   // public void Query_1_2() {
   // int count = 0; 
   // String[] roomsearch = RoomArrayHelper();
   // boolean[] occupied = new boolean[20];
   // Hashtable<Integer, String> roomTable = new Hashtable<Integer,String>();
   // //Hashtable<Integer, Integer> roomOccupied = new Hashtable<Integer,Integer>();
   // Hashtable<Integer, Boolean> roomOccupied = new Hashtable<Integer,Boolean>();
   // String sql = "SELECT * FROM room WHERE occupied like 'TRUE';";   
   //    PrintStart("LIST UNOCCUPIED ROOM");

   //       try (Connection conn = this.connect();
   //          Statement stmt = conn.createStatement();
   //          ResultSet rs = stmt.executeQuery(roomTable.get(j))) {
   //           while (rs.next()) {   
   //             // System.out.println("ROOM NOT EXISTS " + Integer.valueOf(rs.getString("room_id")));
   //            boolean empty = false;
   //            int room = Integer.valueOf(rs.getString("room_id"));
   //             if(rs.getInt("occupied") == 0){
   //                empty = true;
   //             }
   //             roomOccupied(room, empty);
   //          }
   //       } catch (SQLException e) {
   //       System.out.println(e.getMessage());
   // }
   //    PrintEnd("END LIST UNOCCUPIED");
   // }

   //***  1.3) List all rooms in the hospital along with patient names and admission dates for 
   //*** those that are occupied.
   public void Query_1_3() {
      PrintStart("Query_1_3");
      //String rooms = "SELECT room_id, patient_lastname FROM room;";
      String sql = "SELECT room.room_id, room.occupied, admission_records.patient_lastname, admission_records.date_admitted"
            + " FROM admission_records INNER JOIN room ON room.patient_lastname = admission_records.patient_lastname";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\nPatient: " + rs.getString("patient_lastname") + "\nDate admitted: "
                  + rs.getString("date_admitted") + "\nRoom number: " + rs.getString("room_id"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_1_3");
   }

   //*** 2.1) List all patients in the database, with full personal information.
   public void Query_2_1() {
      PrintStart("Query_2_1");
      String sql = "SELECT person_id, first_name, last_name, ins_name, ins_policy FROM patient;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\n" + rs.getString("first_name") + " " + rs.getString("last_name") + " " + " id: "
                  + rs.getString("person_id") + "\n" + "Insurance: " + rs.getString("ins_name") + "\nPolicy number: "
                  + rs.getString("ins_policy"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_2_1");
   }

   //*** 2.2) List all patients currently admitted to the hospital. List only patient 
   //*** identification number and name.
   public void Query_2_2() {
      PrintStart("Query_2_2");
      // String sql = "SELECT * FROM admission_records WHERE date_discharged='Not Discharged';";
      String sql = "SELECT patient.person_id, patient.first_name, patient.last_name, admission_records.date_discharged"
            + " FROM admission_records INNER JOIN patient ON patient.last_name = admission_records.patient_lastname WHERE admission_records.date_discharged='Not Discharged';";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\nPatient: " + rs.getString("first_name") + " " + rs.getString("last_name") + "\nId: "
                  + rs.getString("person_id"));
            // + "\nDischarged status: " + rs.getString("date_discharged"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_2_2");

   }

   //*** 2.3) List all patients who were discharged in a given date range. List only 
   //*** patient identification number and name.
   public void Query_2_3() {
   }

   //*** 2.4) List all patients who have been admitted within a given date range. 
   //*** List only patient identification number and name.
   public void Query_2_4() {
   }

   //*** 2.5) For a given patient, list all admissions to the hospital along with 
   //*** the diagnosis for each admission.
   public void Query_2_5() {
      String last_name = PatientPicker();
      PrintStart("Query_2_5");
      String sql = "SELECT * FROM admission_records WHERE patient_lastname like '" + last_name + "';";
      System.out.println("\nAddmission Records for: " + last_name);
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\nAdmission number:" + rs.getString("admission_record_id") + "\nDate admitted: "
                  + rs.getString("date_admitted") + "\nDate discharged: " + rs.getString("date_discharged")
                  + "\nDiagnosis: " + rs.getString("diagnosis"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_2_5");
   }

   //*** 2.6) For a given patient, list all treatments that were administered. 
   //*** Group treatments by admissions. List admissions in descending chronological order, 
   //*** and list treatments in ascending chronological order within each admission.
   public void Query_2_6() {

   }

   //*** 2.7) List patients who were admitted to the hospital within 30 days of their 
   //*** last discharge date. For each patient list their patient identification number, 
   //*** name, diagnosis, and admitting doctor.
   public void Query_2_7() {
      // String last_name = PatientPicker();
      // PrintStart("Query_2_7");
      // String sql = "SELECT patient.person_id, patient.first_name, patient.last_name, admission_records.diagnosis, "
      // + "admission_records.date_admitted, admission_records_id, admission_records.date_discharged, admission_records.doctor_name"
      // + " FROM admission_records INNER JOIN patient ON patient.last_name = admission_records.patient_lastname "
      // + "WHERE admission_records.date_discharged='Not Discharged';";
      // System.out.println("\nAddmission Records for: " + last_name);
      // try (Connection conn = this.connect();
      //       Statement stmt = conn.createStatement();
      //       ResultSet rs = stmt.executeQuery(sql)) {
      //    while (rs.next()) {

      //       // if(StringToDate(rs.getString("date_admitted"),rs.getString("date_discharged"))
      //       System.out.println(
      //          "\nAdmission number:" + rs.getString("admission_record_id") 
      //          + "\nDate admitted: " + rs.getString("date_admitted") 
      //          + "\nDate discharged: "  + rs.getString("date_discharged")
      //          + "\nDiagnosis: "   +rs.getString("diagnosis"));
      //    }
      // } catch (SQLException e) {
      //    System.out.println(e.getMessage());
      // }
      // PrintEnd("END Query_2_5");
   }

   //*** 2.8) For each patient that has ever been admitted to the hospital, list their 
   //*** total number of admissions, average duration of each admission, longest 
   //*** span between admissions, shortest span between admissions, and average span 
   //*** between admissions.
   public void Query_2_8() {
   }

   //*** 3.1) List the diagnoses given to admitted patients, in descending order of 
   //*** occurrences. List diagnosis identification number, name, and total occurrences of 
   //*** each diagnosis.
   public void Query_3_1() {
         PrintStart("Query_3_1");
         String sql = "SELECT *, COUNT(*) AS total FROM admission_records " 
         + "WHERE date_discharged='Not Discharged' GROUP BY diagnosis, patient_lastname "
         + "ORDER BY admission_record_id DESC;";
         try (Connection conn = this.connect();
         // WHERE date_discharged='Not Discharged' 
               // Hashtable <String, Integer> diag = new Hashtable<String,Integer>();
               Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
               System.out.println(
               "\nDiagnosis ID: "+ rs.getInt("admission_record_id")
              + "\nPatient Last Name: "+ rs.getString("patient_lastname")
              + "\nDiagnosis: " + rs.getString("diagnosis")
              + "\nOccurences: " + rs.getInt("total"));
            }
         } catch (SQLException e) {
            System.out.println(e.getMessage());
         }
         PrintEnd("END Query_3_1");
   }

   //*** 3.2) List the diagnoses given to all (admitted and discharged) patients, in descending 
   //*** order of occurrences. List diagnosis identification number, name, and total occurrences 
   //*** of each diagnosis.

   public void Query_3_2() {
      PrintStart("Query_3_2");
      String sql = "SELECT *, COUNT(*) AS total FROM admission_records " 
      + "GROUP BY diagnosis, patient_lastname "
      + "ORDER BY admission_record_id DESC;";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(
            "\nDiagnosis ID: "+ rs.getInt("admission_record_id")
           + "\nPatient Last Name: "+ rs.getString("patient_lastname")
           + "\nDiagnosis: " + rs.getString("diagnosis")
           + "\nOccurences: " + rs.getInt("total"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_3_2");
}

   //*** 3.3) List the treatments performed at the hospital, in descending order of occurrences. 
   //*** List treatment identification number, name, and total number of occurrences of each treatment.
   public void Query_3_3() {
   }

   //*** 3.4) List the treatments performed on admitted patients, in descending order of occurrences. 
   //*** List treatment identification number, name, and total number of occurrences of each treatment.
   public void Query_3_4() {
   }

   //*** 3.5) List the top 5 most administered medications.
   public void Query_3_5() {
      PrintStart("Query_3_5");
      int count = 1;
      String sql = "SELECT treatment, "
      + "COUNT(treatment) AS total FROM treatments "
      + "WHERE procedure_type= 'M' "
      + "GROUP BY treatment "
      + "ORDER BY total DESC LIMIT 5 ;";
      System.out.println("\nTop 5 Medications\n");
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println(
           count++ + ") " + rs.getString("treatment"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_3_5");
   }

   //*** 3.6) List the most common procedure administered at the hospital. Also, list all doctors that 
   //*** performed that procedure.
   public void Query_3_6() {
   }

   //*** 3.7) List the most recent procedure administered at the hospital. Also, list all doctors that 
   //*** performed that procedure.
   public void Query_3_7() {
      PrintStart("Query_3_7");
      //String rooms = "SELECT room_id, patient_lastname FROM room;";
      String admit = "SELECT room.room_id, room.occupied, admission_records.patient_lastname, admission_records.date_admitted"
            + " FROM admission_records INNER JOIN room ON room.patient_lastname = admission_records.patient_lastname";
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(admit)) {
         while (rs.next()) {
            System.out.println("\nPatient: " + rs.getString("patient_lastname") + "\nDate admitted: "
                  + rs.getString("date_admitted") + "\nRoom number: " + rs.getString("room_id"));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Query_1_1");
   }

   //*** 3.8) List the diagnoses associated with the top 5 patients who have the highest occurrences of 
   //*** admissions to the hospital, in ascending order or correlation.
   public void Query_3_8() {
   }

   //*** 4.1) List all workers at the hospital, in ascending last name, first name order. For each worker, 
   //*** list their, name, and job category.
   public void Query_4_1() {
   }

   //*** 4.2) List the primary doctors of patients with a high admission rate (at least 4 admissions within 
   //*** a one-year time frame).
   public void Query_4_2() {
   }

   //*** 4.3) For a given doctor, list all associated diagnoses in descending order of occurrence. For each
   //*** diagnosis, list the total number of occurrences for the given doctor.
   public void Query_4_3() {
   }

   //*** 4.4) For a given doctor, list all treatments that they ordered in descending order of occurrence. 
   //*** For each treatment, list the total number of occurrences for the given doctor.
   public void Query_4_4() {
   }

   //*** 4.5) List doctors who have been involved in the treatment of every admitted patient.
   public void Query_4_5() {
   }

   //! ====================================================================
   //? ********************************************************************
   //? ###################### HELPER METHODS FOR QUERIES ###################
   //? ********************************************************************
   //! ====================================================================

   //*** Room Helper 
   private String[] RoomArrayHelper() {
      String[] rooms = new String[20];
      for (int i = 1; i < rooms.length; i++) {
         rooms[i] = Integer.toString(i);
      }
      return rooms;
   }

   //***  Helper to get user input to search patient records.
   private static String PatientPicker() {
      System.out.println("\n********************************\n" + "Enter the patients last name: "
            + "\n*******************************\n");

      Scanner input = new Scanner(System.in);
      String data = input.nextLine();
      return data;
   }

   //***  Helper to compare dates.
   private static void StringToDate(String start_date, String end_date) {
      // SimpleDateFormat converts the string format to date object 
      SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
      try {
         Date d1 = sdf.parse(start_date);
         Date d2 = sdf.parse(end_date);
         // Calucalte time difference 
         // in milliseconds 
         long difference_In_Time = d2.getTime() - d1.getTime();
         long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

         System.out.print("Difference " + "between two dates is: ");

         System.out.println(difference_In_Days + " days, ");
      }
      // Catch the Exception 
      catch (ParseException e) {
         e.printStackTrace();
      }
   }

   //***  Helper to caclulate if date is over 30 days.
   private static boolean Under30Days(String start_date, String end_date) {
      // SimpleDateFormat converts the string format to date object 
      SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
      boolean under30 = false;
      try {
         Date d1 = sdf.parse(start_date);
         Date d2 = sdf.parse(end_date);
         // Calucalte time difference 
         // in milliseconds 
         long difference_In_Time = d2.getTime() - d1.getTime();
         long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
         if (difference_In_Days <= 30) {
            under30 = true;
            System.out.println(difference_In_Days + " days.");
         }
      }
      // Catch the Exception 
      catch (ParseException e) {
         e.printStackTrace();
      }
      return under30;
   }

   //***  Helper to get the last admission day.
   public void MaxAdmissionID(String last_name) {
      PrintStart("GET MaxAdmissionID");
      String sql = "SELECT MAX(admission_record_id) FROM admission_records WHERE patient_lastname LIKE  '" + last_name + "';";
      System.out.println("\nAddmission Records for: " + last_name);
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\nAdmission number: " + rs.getInt(1));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Get MaxAdmissionID");
   }

    //***  Helper to get the last procedure.
    public void LastProcedure() {
      PrintStart("GET Last Procedure");
      String sql = "SELECT MAX(treatment_id) FROM treatments WHERE procedure_type LIKE 'P';";
      // System.out.println("\nLast procedure: " + last_name);
      try (Connection conn = this.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         while (rs.next()) {
            System.out.println("\nTreatment type: " + rs.getInt(1));
         }
      } catch (SQLException e) {
         System.out.println(e.getMessage());
      }
      PrintEnd("END Get Last Procedure");
   }

   public static void main(String[] args) throws FileNotFoundException {
      //StringToDate("07-01-2020", "07-31-2020");
      //MaxAdmissionID();
      Connect app = new Connect();
      //app.Query_3_1();
      app.Query_3_5();
      //app.LastProcedure();
      // app.MaxAdmissionID("Bush");
      // app.DropAllTables();
      // app.insertPerson("123", "Michael", "Auburn");
      // System.out.println("After Insert");
      // app.selectSample();
      // app.CreateTables();
      //app.ListColumns("patient");
      // app.InsertPerson("Don", "Johnson");
      // app.InsertPerson("Moe", "Joe");
      // app.ListPersons();
      //app.selectSample();
      // app.ListTables();
   }
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

// public boolean CheckRoom( String room) {
//    String sql = "SELECT * FROM room WHERE room_id like '" + room + "';";
//    boolean occupied = false;
//    PrintStart("CHECK FOR OCCUPIED ROOM");
//    try (Connection conn = this.connect();
//          Statement stmt = conn.createStatement();
//          ResultSet rs = stmt.executeQuery(sql)) {
//       while (rs.next()) {
//          System.out.println(rs.getString("room_id") + "\t" + " is occupied?" + rs.getString("occupied"));  
//          if(rs.getString("occupied") == "true"){
//             occupied = true;
//          }     
//       }
//    } catch (SQLException e) {
//       System.out.println(e.getMessage());
//    }
//    PrintEnd("END CHECK FOR OCCUPIED ROOM");
//    return occupied;

// }

// public boolean CheckRooms() {
//    String sql = "SELECT COUNT(*) AS total FROM room WHERE occupied;";
//    boolean occupied = false;
//    int total = 0;
//    PrintStart("CHECK FOR OCCUPIED ROOM");
//    try (Connection conn = this.connect();
//          Statement stmt = conn.createStatement();
//          ResultSet rs = stmt.executeQuery(sql)) {
//       while (rs.next()) {
//          System.out.println(rs.getInt("total") + " rooms are occupied");  
//         total = rs.getInt("total");
//       }
//    } catch (SQLException e) {
//       System.out.println(e.getMessage());
//    }
//    if(total == 20){
//       occupied = true;
//       System.out.println("All rooms occupied");
//       return occupied;
//    }
//    PrintEnd("END CHECK FOR OCCUPIED ROOM");
//    return occupied;

// }