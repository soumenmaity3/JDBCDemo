import java.sql.*;
import java.util.*;

public class Main {
    private static final String url="jdbc:mysql://127.0.0.1:3306/collage";
//private static final String url="jdbc:mysql://127.0.0.1:3306/bank";

    private static final String username="root";
    private static final String password="database2025";
    public static void main(String[] args)throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, username, password);
        con.setAutoCommit(false);
        Scanner scan=new Scanner(System.in);

//        This is all For Statement
//        Statement stmt=con.createStatement();
//        System.out.println("Enter the student name");
//        String name=scan.nextLine();
//        System.out.println("Enter the student id");
//        int id=scan.nextInt();
//        System.out.println("Enter the student marks");
//        int marks=scan.nextInt();
//        scan.nextLine();
//        System.out.println("Enter the student city");
//        String city=scan.nextLine();
//        String query1=String.format("INSERT INTO student (id, name, marks, city) VALUES (%d, '%s', %d, '%s')",id,name,marks,city);//This is for insert row i
//        String query2=String.format("UPDATE student SET id=%d WHERE id=%d",106,107);  //This is for update specific rows
//        String query3=String.format("DELETE FROM student WHERE id=%d",106);  //This is delete statement
//        String sql="select * from student";        //show the table from ->
//        ResultSet result1=stmt.executeQuery(sql);
//        while(result1.next()) {
//            int id = result1.getInt("id");
//            String name = result1.getString("name");
//            int marks = result1.getInt("marks");
//            String address = result1.getString("city");
//            System.out.println("id:" + id + " name:" + name + " marks:" + marks + " address:" + address);
//        }//<-
//        int result2=stmt.executeUpdate(query1);
//        if(result2>0){
//            con.commit();
//            System.out.println("Data Inserted Successfully");
//        }else {
//            System.out.println("Data Insertion Failed");
//        }

//        This is all For preparedStatement
//        String quary1 = "INSERT INTO student(id, name, marks, city) VALUES (?, ?, ?, ?)";  // This is for  insert data using preparedStatement
//        String quary2 = "Select * FROM student ";//This is only read
//        PreparedStatement ps=con.prepareStatement(quary2);
//        ps.setInt(1,id);//for update
//        ps.setString(2,name);  //for update
//        ps.setInt(3,marks);//for update
//        ps.setString(4,city);//for update
//        int result=ps.executeUpdate();//for update
//        if(result>0){//for update
//        con.commit();
//            System.out.println("Data Inserted Successfully");//for update
//        }else {//for update
//            System.out.println("Data Insertion Failed");//for update
//        }
//        ResultSet rs=ps.executeQuery();//for read only
//
//        while(rs.next()){//for read all
//            System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getInt(3)+rs.getString(4));//for read all
//        }//for read all

        //Batch processing

        //Using Statement
//        Statement stmt=con.createStatement();
//        while (true){
//            System.out.print("Enter Name ;- ");
//            String name=scan.next();
//            System.out.print("Enter id:- ");
//            int id=scan.nextInt();
//            System.out.print("Enter marks:- ");
//            int marks=scan.nextInt();
//            System.out.print("Enter city:- ");
//            String city=scan.next();
//            System.out.print("Enter more data(Y/N):- ");
//            String query=String.format("INSERT INTO student (id,name,marks,city) VALUES(%d,'%s', %d,'%s');",id,name,marks,city);
//            stmt.addBatch(query);
//            String choice=scan.next();
//            if(choice.toUpperCase().equals("N")){
//                break;
//            }
//        }
//        int[] arr=stmt.executeBatch();
//        for(int i=0;i<arr.length;i++){
//            if(arr[i]==0){
//                System.out.println("Query: "+i+" failed");
//            }
//        }
//
//
// Using PreparedStatement
//        String query="INSERT INTO student (id,name,marks,city) VALUES(?, ?, ?, ?)";
//        PreparedStatement pst=con.prepareStatement(query);
//        while (true){
//            System.out.print("Enter Name ;- ");
//            String name=scan.next();
//            System.out.print("Enter id:- ");
//            int id=scan.nextInt();
//            System.out.print("Enter marks:- ");
//            int marks=scan.nextInt();
//            System.out.print("Enter city:- ");
//            String city=scan.next();
//            System.out.print("Enter more data(Y/N):- ");
//            String choice=scan.next();
//            pst.setInt(1,id);
//            pst.setString(2,name);
//            pst.setInt(3,marks);
//            pst.setString(4,city);
//            pst.addBatch();
//            if(choice.toUpperCase().equals("N")){
//                break;
//            }
//        }
//        int[] arr=pst.executeBatch();


        //Transaction Handling
        String debitQuery="UPDATE infoHolder SET balance =balance - ? WHERE ac_number=?";
        String updateQuery="UPDATE infoHolder SET balance =balance + ? WHERE ac_number=?";
        Scanner scan2=new Scanner(System.in);
        System.out.println("Enter account number");
        int number=scan2.nextInt();
        System.out.println("Enter balance");
        int balance=scan2.nextInt();
        System.out.println("Enter credit account number");
        int number2=scan2.nextInt();

        PreparedStatement debPst=con.prepareStatement(debitQuery);
        PreparedStatement cradPst=con.prepareStatement(updateQuery);
        debPst.setInt(1, balance);
        debPst.setInt(2,number);
        cradPst.setInt(1, balance);
        cradPst.setInt(2,number2);

        debPst.executeUpdate();
        cradPst.executeUpdate();

        if(isSufficient(con,number,balance)){
//            int affectedRows=debPst.executeUpdate();
//            int affectedRows2=cradPst.executeUpdate();
            con.commit();
            System.out.println("Debit successfully");
        }else{
            con.rollback();
            System.out.println("Transaction failed");
        }






    }
    //this is for Transaction Handling
    static boolean isSufficient(Connection con,int number,int balance)throws Exception{
        String sql="select balance from infoHolder where ac_number=?";
        PreparedStatement pst=con.prepareStatement(sql);
        pst.setInt(1, number);
        ResultSet rs=pst.executeQuery();
        if(rs.next()){
            int currentBalance=rs.getInt("balance");
            if(balance>currentBalance){
                return false;
            }
            else {
                return true;
            }
        }else {
            return false;
        }
    }
}