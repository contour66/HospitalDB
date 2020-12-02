public class WorkerData{
    private String first_name;
    private String last_name;
    private String title;
    private static String[] titles = { "V", "D", "A", "N", "T" };

    public static void main (String args[]){
       WorkerData data  = new WorkerData();
        System.out.print(data.CheckTitle("T"));
    }

    public void setFirstName(String name){
        this.first_name = name;
    }

    public String getFirstName(){
        return first_name;
    }

    public void setLastName(String name){
        this.last_name = name;
    }

    public String getLastName(){
        return last_name;
    }

    public void setTitle(String t){
        this.title = t;
    }

    public String getTitle(){
        return title;
    }

    public String CheckTitle(String t){
        String title = "";
        for(int i = 0; i < titles.length; i++){
            if(t.equals(titles[i])){
                title = titles[i];
            }
        }
        return title;
    }

}