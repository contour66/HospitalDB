public class PatientData{
    private String title;
    private String pFirstName;
    private String pLastName;
    private String room;
    private String eContact;
    private String ePhone;
    private String ipNumber;
    private String ipName;
    private String doctor;
    private String diagnosis;
    private String admitDate;
    private String discDate;  


    public static void main (String args[]){
        PatientData patient  = new PatientData();
        patient.setTitle("words");
        System.out.print(patient.getTitle());
    
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setFirstName(String fname){
        this.pFirstName = fname;
    }

    public String getFirstName(){
        return pFirstName;
    }


    public void setLastName(String lname){
        this.pLastName = lname;
    }

    public String getLastName(){
        return pLastName;
    }


    public void setRoom(String room){
        this.room = room;
    }

    public String getRoom(){
        return room;
    }


    public void setEmContact(String emcontact){
        this.eContact= emcontact;
    }

    public String getEmContact(){
        return eContact;
    }


    public void setEmPhone(String emphone){
        this.ePhone= emphone;
    }

    public String getEmPhone(){
        return ePhone;
    }

    public void setIpNumber(String ipnum){
        this.ipNumber= ipnum;
    }

    public String getIpNumber(){
        return ipNumber;
    }

    public void setIpName(String ipname){
        this.ipName = ipname;
    }

    public String getIpName(){
        return ipName;
    }

    public void setDoctor(String doctor){
        this.doctor = doctor;
    }

    public String getDoctor(){
        return doctor;
    }

    public void setDiagnosis(String d){
        this.diagnosis = d;
    }

    public String getDiagnosis(){
        return diagnosis;
    }

    public void setAdmitDate(String admit){
        this.admitDate = admit;
    }

    public String getAdmitDate(){
        return admitDate;
    }

    public void setDiscDate(String discdate){
        this.discDate = discdate;
    }

    public String getDiscDate(){
        return discDate;
    }





}