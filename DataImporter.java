import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class DataImporter {
   String title;
   String pFirstName;
   String pLastName;
   String room;
   String eContact;
   String ePhone;
   String ipNumber;
   String ipName;
   String doctor;
   String diagnosis;
   String admitDate;
   String discDate;
   private static String[] pdata;
   private static String[] titles = { "V", "D", "A", "N", "T", "P" };
   private static String[] tdata;
   private static Connect app = new Connect();

   public static void main(String[] args) throws FileNotFoundException {
      // ***********************************************************
      // Clear DB to intialize data.
      // ***********************************************************
      System.out.println("Program Intialized");
      app.DropAllTables();
      app.CreateTables();
      // ***********************************************************
      // Create new Scanner objects to scan data from
      // document,lines,words.
      // ***********************************************************
      System.out.println("Enter a filename to scan");
      Scanner input = new Scanner(System.in);
      String data = input.nextLine();
      // File file = new File("data.txt");
      File file = new File(data);
      Scanner scan = new Scanner(file);
      Scanner check = new Scanner(file);
      String lineCheck = check.nextLine();
      Scanner scanLineCheck = new Scanner(lineCheck);
      scanLineCheck.useDelimiter(",");
      int numCheck = 0;
      String checkWord = "";
      // ***********************************************************
      // Scans line to check if data is treatment or patient 
      // info by counting words and stores in numcheck.
      // ***********************************************************
      while (scanLineCheck.hasNext()) {
         checkWord = scanLineCheck.next();
         checkWord = checkWord.trim();
         numCheck++;
         // System.out.println("Word count " + numCheck + ": " + checkWord);
      }
      scanLineCheck.close();
      // ***********************************************************
      // Scans each line in data document.
      // ***********************************************************
      while (scan.hasNextLine()) {
         String line = scan.nextLine();
         Scanner scanLine = new Scanner(line);
         scanLine.useDelimiter(",");
         String word = "";
         pdata = new String[12];
         tdata = new String[5];
         PatientData patient = new PatientData();
         int count = 0;
         // ***********************************************************
         // Scans each word in line and checks
         // if it should add to patient data
         // or treatment data..
         // ***********************************************************
         while (scanLine.hasNext()) {
            word = scanLine.next();
            word = word.trim();
            pdata[count] = word;
            if (numCheck > 5) {
               if (count <= 12) {
                  pdata[count] = word;
               }
            } else {
               if (count <= 5) {
                  tdata[count] = word;
               }
            }
            count++;
            // System.out.println(word);
         }
         // ***********************************************************
         // Checks if discharge date is empty/null and discharge date 
         // with "Not Discharged" so patient can be added to room.
         // Populates patient data.
         // ***********************************************************
         if (pdata[11] == null) {
            pdata[11] = "Not Discharged";
            patient = patientInfo(pdata);
            PopulateDB_PatientData(patient);
            System.out.println(
                  "Patient " + patient.getFirstName() + " " + patient.getLastName() + " " + patient.getDiscDate());
            // for (String s : tdata){
            // System.out.println("Treatment: " + s);
         }
         scanLine.close();
      }
      scan.close();
      app.ListPersons();
      app.ListRooms();
   }

   // ***********************************************************
   // Method to print out/check data from patient data array.
   // ***********************************************************
   private static void printLineData() {
      int count = 1;
      for (String s : pdata) {
         System.out.print(count + ". " + s + "\n");
         count++;
      }
   }

   // ***********************************************************
   // Method to intially populate DB from scanned data.
   // ***********************************************************
   private static void PopulateDB_PatientData(PatientData patient) {
      // Checks to make sure the person isn't already in the DB.
      if (!app.PersonExists(patient.getLastName()) && checkTitle(patient.getTitle())) {
         app.GetDoctorID(patient.getDoctor());
         // Inserts patient data if letter in data starts with "P".
         if ((patient.getTitle()).equals("P")) {
            app.InsertPerson(patient.getFirstName(), patient.getLastName());
            app.InsertPatient(patient.getLastName(), patient.getIpName(), patient.getIpNumber());
            app.InsertEContact(patient.getEmContact(), patient.getEmPhone(), patient.getLastName());
            // Inserts patient to room if they have not been discharged.
            if ((patient.getDiscDate()).equals("Not Discharged")) {
               app.InsertRoom(patient.getLastName(), patient.getRoom());
            }
         }
         // Inserts doctor info.
         if (!app.DoctorExists(patient.getDoctor())) {
            app.InsertDoctor(patient.getDoctor());
            app.RoomByPatient(patient.getLastName());
         }
         app.GetDoctorID(patient.getDoctor());
         if (patient.getDiscDate().isEmpty() || patient.getDiscDate() == null || patient.getDiscDate() == "") {
            // patient.setDiscDate("empty");
         }
         // app.InsertAdmission(patient.getLastName(), patient.getDoctor(),
         // patient.getDiagnosis(), patient.getAdmitDate(),
         // patient.getDiscDate(), patient.getRoom());
      }
      // app.InsertRoom(patient.getLastName(), patient.getRoom());
   }

   // ***********************************************************
   // Method to create a new PatientData object.
   // ***********************************************************
   private static PatientData patientInfo(String[] data) {
      PatientData patient = new PatientData();
      patient.setTitle(data[0]);
      patient.setFirstName(data[1]);
      patient.setLastName(data[2]);
      patient.setRoom(data[3]);
      patient.setEmContact(data[4]);
      patient.setEmPhone(data[5]);
      patient.setIpNumber(data[6]);
      patient.setIpName(data[7]);
      patient.setDoctor(data[8]);
      patient.setDiagnosis(data[9]);
      patient.setAdmitDate(data[10]);
      patient.setDiscDate(data[11]);
      return patient;
   }

   // ***********************************************************
   // Method to verify type of person based on first letter in data.
   // ***********************************************************
   private static boolean checkTitle(String t) {
      t = t.toUpperCase();
      for (int i = 0; i < titles.length; i++) {
         if (t.equals(titles[i])) {
            System.out.println("Title " + t + " is valid");
            return true;
         }
      }
      System.out.println("Title not valid");
      return false;
   }

   // private static boolean PersonExists(String name){
   // if(app.PersonExists(name)){
   // return true;
   // }
   // return false;
   // }

}