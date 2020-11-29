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


public class MenuQueries {
   
   // ******************** PRINT FORMATTING **********************

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

   //*** 1.1) List the rooms that are occupied, along with the associated
   //*** patient names and the date the patient was admitted. 

   public void ListRoom_1_1() {
    PrintStart("1.1) LIST ROOMS");
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

   

    // 1.2. List the rooms that are currently unoccupied.

    // 1.3. List all rooms in the hospital along with patient 
    // names and admission dates for those that are occupied.

   
   
   // ******************** ROOM QUERIES **********************
   

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

   // ******************** HELPER QUERIES **********************
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

   public void GetOccupiedRooms() {
    String sql = "SELECT * FROM room WHERE occupied like 1';";
    
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



   public static void main(String[] args) throws FileNotFoundException {
     
   }
}



//    // ******************** POPULATE PERSON QUERIES **********************
   
//    public void InsertPerson(String first_name, String last_name) {
//     PrintStart("ADD PERSON");
//     pid++;
//     String id = Integer.toString(pid);
//     String sql = "INSERT INTO person(person_id, first_name, last_name) VALUES(?,?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, id); // First ? in sql
//        ps.setString(2, first_name); // Second ? in sql
//        ps.setString(3, last_name); // Fourth ? in sql
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println(first_name + " " + last_name + " ADDED with id: " + id);
//     PrintEnd("ADDED PERSON");
//  }

//  public boolean PersonExists(String last_name) {
//     PrintStart("START PERSONS CHECK");
//     String sql = "SELECT * FROM person WHERE last_name like '" + last_name + "';";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // loop through the result set
//        System.out.println("Check for name: " + rs.getString("last_name"));
//        if (rs.getString("last_name").equals(last_name)) {
//           System.out.println(rs.getString("last_name") + " ALREADY EXISTS");
//           PrintEnd("END PERSONS CHECK");
//           return true;
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println("Person with last name " + last_name + " can be added");
//     PrintEnd("END PERSONS CHECK");
//     return false;
//  }

//  public String[] PersonInfo(String last_name) {
//     PrintStart("GET PERSON INFO");
//     String[] data = new String[3];
//     String id = "";
//     String first = "";
//     String last = "";
//     String sql = "SELECT * FROM person WHERE last_name like '" + last_name + "';";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        System.out.println(rs.getString("first_name") + " " + rs.getString("last_name") +  " id: "
//              + rs.getString("person_id"));
//        id = rs.getString("person_id");
//        data[0] = id;
//        first = rs.getString("first_name");
//        data[1] = first;
//        last = rs.getString("last_name");
//        data[2] = last;
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END GET PERSON INFO");
//     return data;
//  }

//  public String[] PersonInfoByID(String pid) {
//     PrintStart("GET PERSON INFO");
//     String[] data = new String[3];
//     String id = "";
//     String first = "";
//     String last = "";
//     String sql = "SELECT * FROM person WHERE person_id like '" + pid + "';";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        System.out.println(rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t" + " id: "
//              + rs.getString("person_id") + "\t");
//        id = rs.getString("person_id");
//        data[0] = id;
//        System.out.println("GOT THE ID: " + id);
//        first = rs.getString("first_name");
//        data[1] = first;
//        last = rs.getString("last_name");
//        data[2] = last;
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END GET PERSON INFO");
//     return data;
//  }

//  public void ListPersons() {
//     PrintStart("LIST PERSONS");
//     String sql = "SELECT person_id, first_name, last_name FROM person;";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        while (rs.next()) {
//           System.out.println("\n" + rs.getString("first_name") + " " + rs.getString("last_name")  + " id: "
//                 + rs.getString("person_id") + "\t");
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END PERSONS LIST");
//  }

 
//  // ******************** POPULATE PATIENT QUERIES **********************
 

//  public void InsertPatient(String last_name, String pol_name, String pol_num) {
//     PrintStart("ADD PATIENT");
//     String[] data = PersonInfo(last_name);
//     int id = Integer.valueOf(data[0]);
//     String sql = "INSERT INTO patient(person_id, first_name, last_name, ins_name, ins_policy) VALUES(?,?,?,?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        // System.out.pring
       
//        ps.setInt(1, id);
//        ps.setString(2, data[1]);
//        ps.setString(3, data[2]);
//        ps.setString(4, pol_name);
//        ps.setString(5, pol_num);
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println(data[1] + " " + last_name + " ADDED with id: " + data[0]);
//     PrintEnd("ADDED PATIENT");
//  }

//  public void ListPatients() {
//     PrintStart("LIST PATIENTS");
//     String sql = "SELECT person_id, first_name, last_name, ins_name, ins_policy FROM patient;";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        while (rs.next()) {
//           System.out.println("\n" + rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t" + " id: "
//                 + rs.getString("person_id") + "\n" + "Insurance: " + rs.getString("ins_name") + "\nPolicy number: "
//                 + rs.getString("ins_policy"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END PATIENTS LIST");
//  }

 
//  // ******************** POPULATE EMERGENCY CONTACT QUERIES **********************
 

//  public void InsertEContact(String name, String phone, String plname) {
//     PrintStart("ADD EMERGENCY CONTACT");
//     // String[] data = PersonInfo(last_name);
//     String[] patient = PersonInfo(plname);
//     contact_id++;
//     String id = Integer.toString(contact_id);

//     String sql = "INSERT INTO emergency_contact(contact_id, name, patient_id, phone_number) VALUES(?,?,?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, id);
//        ps.setString(2, name);
//        ps.setString(3, patient[0]);
//        ps.setString(4, phone);
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println(name + " ADDED with id: " + id + " EMERGENCY CONTACT FOR PATIENT ID: " + patient[0]);
//     PrintEnd("ADDED EMERGENCY CONTACT");
//  }

//  public void ListEContacts() {
//     PrintStart("LIST EMERGENCY CONTACTS");
//     String sql = "SELECT contact_id, name, patient_id, phone_number FROM emergency_contact;";
//     // String[] pdata;
//     // String[] data = PersonInfo(last_name);
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\n" + rs.getString("name") + "\t"
//           // + pdata[2] + "\t"
//                 + " id: " + rs.getString("contact_id") + "\t" + " phone number: " + rs.getString("phone_number")
//                 + "\n" + "Contact for patient id: " + rs.getString("patient_id"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END EMERGENCY CONTACTS LIST");
//  }


//   // ******************** POPULATE DOCTOR QUERIES ***********************
   
//   public void InsertDoctor(String name) {
//     PrintStart("ADD DOCTOR");
//     // String[] data = PersonInfo(last_name);
//     // String[] patient = PersonInfo(plname);
//     doctor_id++;
//     String id = Integer.toString(doctor_id);

//     String sql = "INSERT INTO doctor(doctor_id, name) VALUES(?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, id);
//        ps.setString(2, name);
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println("Doctor " + name + " ADDED with id: " + id);
//     PrintEnd("ADDED DOCTOR");
//  }

//  public boolean DoctorExists(String doctor) {
//     PrintStart("START DOCTOR CHECK");
//     String sql = "SELECT * FROM doctor WHERE name like '" + doctor + "';";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // loop through the result set
//        System.out.println("Check for name: " + rs.getString("name"));
//        if (rs.getString("name").equals(doctor)) {
//           System.out.println("Doctor " + rs.getString("name") + " ALREADY EXISTS");
//           PrintEnd("END DOCTOR CHECK");
//           return true;
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println("Doctor with name " + doctor + " can be added");
//     PrintEnd("END DOCTOR CHECK");
//     return false;
//  }

//  public void ListDoctors() {
//     PrintStart("LIST DOCTORS");
//     String sql = "SELECT doctor_id, name FROM doctor;";
//     // String[] pdata;
//     // String[] data = PersonInfo(last_name);
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\nDoctor: " + rs.getString("name") + "\t"
//           // + pdata[2] + "\t"
//                 + " id: " + rs.getString("doctor_id"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END DOCTOR LIST");
//  }

//  public String GetDoctorID(String doctor) {
//     PrintStart("DOCTOR ID BY NAME");
//     String id = "";
//     String sql = "SELECT * FROM doctor WHERE name like '" + doctor + "';";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\nDoctor: " + rs.getString("name") + "\t"
//           // + pdata[2] + "\t"
//                 + " id: " + rs.getString("doctor_id"));
//        }
//        id = rs.getString("doctor_id");

//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END DOCTOR ID BY NAME");
//     return id;

//  }
 
//  // ******************** POPULATE ADDMISSION QUERIES *******************

//  public void InsertAdmission(String pname, String dname, String diagnosis, String date_start, String date_end, String room) {
//     // String room = RoomByPatient(pname);
//     PrintStart("ADD ADMISSION RECORD");
//     String[] patient = PersonInfo(pname);
//     // String doc_id = GetDoctorID(dname);
//     admission_id++;

//     String id = Integer.toString(admission_id);
//     String sql = "INSERT INTO admission_records(admission_record_id, date_admitted, date_discharged, diagnosis, patient_id,  doctor_name, room_number, patient_lastname, discharged) VALUES(?,?,?,?,?,?,?,?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, id);
//        ps.setString(2, date_start);
//        ps.setString(3, date_end);
//        ps.setString(4, diagnosis);
//        ps.setString(5, patient[0]);
//        ps.setString(6, dname);
//        ps.setString(7, room);
//        ps.setString(8, pname);
//        if (!date_end.isEmpty()) {
//           ps.setString(9, "TRUE");
//        } else {
//           ps.setString(8, "FALSE");
//        }
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     // System.out.println(name + " ADDED with id: " + id + " EMERGENCY CONTACT FOR
//     // PATIENT ID: " + patient[0]);
//     PrintEnd("END ADD ADMISSION RECORD");
//  }

//  public void ListAdmissions() {
//     PrintStart("LIST ADMIN RECORDS");
//     String sql = "SELECT * FROM admission_records;";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\n Record number: " + rs.getString("admission_record_id") + "\n Admit date: "
//                 + rs.getString("date_admitted") + "\n Discharge date: " + rs.getString("date_discharged")
//                 + "\n Diagnosis: " + rs.getString("diagnosis") + "\n Discharged: " + rs.getString("discharged")
//                 + "\n Patient id: " + rs.getString("patient_id") + "\n Patient name: "
//                 + rs.getString("patient_lastname") + "\n Doctor name: " + rs.getString("doctor_name")
//                 + "\n Room number: " + rs.getString("room_number"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END LIST ADMIN RECORDS");
//  }

//  public void GetAdmission() {
//     PrintStart("GET ADMISSION");
//     String sql = "SELECT * FROM admission_records;";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\nRecord number: " + rs.getString("admission_record_id") + "\nAdmit date: "
//                 + rs.getString("date_admitted") + "\nDischarge date: " + rs.getString("date_discharged")
//                 + "\nDiagnosis: " + rs.getString("diagnosis") + "\nDischarged: " + rs.getString("discharged")
//                 + "\nPatient id: " + rs.getString("patient_id") + "\nPatient name: "
//                 + rs.getString("patient_lastname") + "\nDoctor name: " + rs.getString("doctor_name")
//                 + "\nRoom number: " + rs.getString("room_number"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END LIST ADMIN RECORDS");
//  }

 
//  // ******************** POPULATE TREATMENT QUERIES ********************
 
//  public void InsertTreatment(String pname, String dname, String date, String proc, String med) {
//     PrintStart("ADD TREATMENT RECORD");
//     treatment_id++;
//     String id = Integer.toString(treatment_id);
//     String sql = "INSERT INTO treatments(treatment_id, patient_lastname, doctor_name, procedure_type, treatment, treatment_date) VALUES(?,?,?,?,?,?);";
//     try (Connection conn = this.connect();) {
//        PreparedStatement ps = conn.prepareStatement(sql);
//        ps.setString(1, id);
//        ps.setString(2, pname);
//        ps.setString(3, dname);
//        ps.setString(4, date);
//        ps.setString(5, proc);
//        ps.setString(6, med);
//        // ps.setString(7, "TRUE");
//        ps.executeUpdate();
//        ps.close();
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     System.out.println("Patient: " + pname 
//     + "\nDoctor: " + dname
//        + "\nProcedure: " + proc
//        + "\nTreatment: " + med
//        + "\nDate: " + date);
//     PrintEnd("END ADD TREATMENT RECORD");
//  }

//  public void ListTreatments() {
//     PrintStart("LIST TREAMENTS RECORDS");
//     String sql = "SELECT * FROM treatments;";
//     try (Connection conn = this.connect();
//           Statement stmt = conn.createStatement();
//           ResultSet rs = stmt.executeQuery(sql)) {
//        // pdata = PersonInfoByID(rs.getString("person_id"));
//        while (rs.next()) {
//           System.out.println("\n Record number: " + rs.getString("admission_record_id") + "\n Admit date: "
//                 + rs.getString("date_admitted") + "\n Discharge date: " + rs.getString("date_discharged")
//                 + "\n Diagnosis: " + rs.getString("diagnosis") + "\n Discharged: " + rs.getString("discharged")
//                 + "\n Patient id: " + rs.getString("patient_id") + "\n Patient name: "
//                 + rs.getString("patient_lastname") + "\n Doctor name: " + rs.getString("doctor_name")
//                 + "\n Room number: " + rs.getString("room_number"));
//        }
//     } catch (SQLException e) {
//        System.out.println(e.getMessage());
//     }
//     PrintEnd("END LIST ADMIN RECORDS");
//  }
