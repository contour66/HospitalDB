public class TreatmentData{
    private String pLastName;
    private String doctor;
    private String type;
    private String treatment;
    private String date;

    public static void main (String args[]){
       TreatmentData tr_data  = new TreatmentData();
        tr_data.setTreatment("medicines");
        System.out.print(tr_data.getTreatment());
    }

    public void setLastName(String lname){
        this.pLastName = lname;
    }

    public String getLastName(){
        return pLastName;
    }

    public void setDoctor(String doctor){
        this.doctor = doctor;
    }

    public String getDoctor(){
        return doctor;
    }

    public void setType(String t){
        this.type = t;
    }

    public String getTreatment(){
        return treatment;
    }
    public void setTreatment(String t){
        this.treatment = t;
    }

    public String getType(){
        return type;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }


}