import java.util.Hashtable;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class QueryMenu {
    private static Hashtable<String, String> options = new Hashtable<String, String>();
    private static DataImporter import_data = new DataImporter();
    private static Connect app = new Connect();

    public static void ImportData() throws FileNotFoundException {
        try {
            import_data.RunImport();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public static void PrintMenu() throws FileNotFoundException {
        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++\n"
                + "\nEnter a new query selection or type end to stop program\n"
                + "\n+++++++++++++++++++++++++++++++++++++++++++++++++\n");
        File file = new File("query_selection.txt");
        Scanner scan = new Scanner(file);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();

            String option_num[] = line.split("]", 2);
            //String option_string = scanLine.next();
            //options.put(option_num, option_string);
            System.out.println(option_num[0] + " " + option_num[1]);

        }
        scan.close();
        //System.out.println("Enter a new query selection or type END to end program");
        //System.out.print(options.get("3.1"));

    }

    private static void MenuSelection() throws FileNotFoundException {
        //PrintMenu();
        String query = "";
        while (!(query).equals("end") &&  !(query).equals("n")) {
            PrintMenu();
            Scanner input = new Scanner(System.in);
            query = input.nextLine();
            switch (query) {
                case "1.1":
                    app.Query_1_1();
                    break;
                case "1.2":
                    app.Query_1_2();
                    break;
                case "1.3":
                    app.Query_1_3();
                    break;
                case "2.1":
                    app.Query_2_1();
                    break;
                case "2.2":
                    app.Query_2_2();
                    break;
                case "2.3":
                    app.Query_2_3();
                    break;
                case "2.4":
                    app.Query_2_4();
                    break;
                case "2.5":
                    app.Query_2_5();
                    break;
                case "2.6":
                    app.Query_2_6();
                    break;
                case "2.7":
                    app.Query_2_7();
                    break;
                case "2.8":
                    app.Query_2_8();
                    break;
                case "3.1":
                    app.Query_3_1();
                    break;
                case "3.2":
                    app.Query_3_2();
                    break;
                case "3.3":
                    app.Query_3_3();
                    break;
                case "3.4":
                    app.Query_3_4();
                    break;
                case "3.5":
                    app.Query_3_5();
                    break;
                case "3.6":
                    app.Query_3_6();
                    break;
                case "3.7":
                    app.Query_3_7();
                    break;
                case "3.8":
                    app.Query_3_8();
                    break;
                case "4.1":
                    app.Query_4_1();
                    break;
                case "4.2":
                    app.Query_4_2();
                    break;
                case "4.3":
                    app.Query_4_3();
                    break;
                case "4.4":
                    app.Query_4_4();
                    break;
                case "4.5":
                    app.Query_4_5();
                    break;
                case "end":
                    break;
                default:
                    System.out.println("Invalid Option. Select Again");
                    break;
            }
            if(query.equals("end")){
                System.out.println("ENDING PROGRAM");
            }else{
                System.out.println("Select new query?\nEnter y for yes or n for no");
                query = input.nextLine();
                if(query.equals("n")){
                    System.out.println("ENDING PROGRAM");
                }
                if(!query.equals("y") || !query.equals("n")){
                    System.out.println("Invalid Entry");
                }
            }

        }
    }

    private static String UserInput() {
        Scanner input = new Scanner(System.in);
        String data = "";
        while (input.hasNext()) {
            data = input.next();
        }
        input.close();
        return data;
    }

    private static void DelayTimer() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //app.DropAllTables();
       //ImportData();
        //  app.ListPersons();
        //  app.ListRooms();
        //  app.ListAdmissions();
        MenuSelection();
        //app.DropAllTables();
        //DelayTimer();
        //    app.Query_1_3();
        // MenuSelection("1.2");
    }

}