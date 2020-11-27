public class QueryBuilder{
    
    public static void main (String[] args){
      QueryBuilder build = new QueryBuilder();
      
      System.out.print(build.insertPatient("1", "@323", "45545"));
    
    }

    public String insertPatient(String id, String polnum, String polname){
       String query = "INSERT INTO patient (person_id, ins_policy, ins_name)" 
        + " VALUES (" + fv(id)  + fv(polnum) + fvs(polname) + ");";
         return query;
      
    }
    
    public String fv(String value){
         String query = value + ", ";
         return query; 
    
    }
    
    public String fvs(String value){
         String query = "'" +  value + "'";
         return query; 
    
    }


}