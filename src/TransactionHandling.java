import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TransactionHandling {
    private static final String url = "jdbc:mysql://localhost:3306/bank";
    private static final String user = "root";
    private static final String password = "database2025";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);
        con.setAutoCommit(false);
        int choice;
        do {
            System.out.println("=================\nWelcome to the Bank");
            System.out.println(
                    "1.Open Bank Account\n2.Login Bank Account\n3.Deactivate Account\n4.List of Accounts Holders\n5.Exit Portal\n===================");
            System.out.print("Please enter your choice: ");
            choice = scan.nextInt();
            switch (choice) {
                case 1:
                    openAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    deleteAccount();
                    break;
                case 4:
                    listAccountHolder();
                    break;
                case 5:
                    System.out.println("Bank Closed..");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);
    }

    public static void openAccount() throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);
        System.out.println("Create Account-");
        System.out.print("Name: ");
        String name = scan.nextLine();
        System.out.print("Choose Password (4 digits): ");
        int password = scan.nextInt();
        String sql = "INSERT INTO infoHolder(password, name) VALUES(?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, password);
        preparedStatement.setString(2, name);
        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int account = generatedKeys.getInt(1);
                String sql2 = "SELECT * FROM infoHolder WHERE ac_number = ?";
                PreparedStatement preparedStatement2 = con.prepareStatement(sql2);
                preparedStatement2.setInt(1, account);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                if (resultSet2.next()) {
                    System.out.println("Name: " + resultSet2.getString("name") +
                            "\n Account Number: " + resultSet2.getInt("ac_number") +
                            "\n Password: " + resultSet2.getInt("password") +
                            "\n Balance: ₹" + resultSet2.getInt("balance"));
                }
                System.out.println("Account Created Successfully.");
            }
        } else {
            System.out.println("Account Creation Failed.");
        }
    }

    public static void login() throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);
        System.out.println("Login Account.");
        System.out.print("Account number: ");
        int account = scan.nextInt();
        System.out.print("Password: ");
        int password = scan.nextInt();
        String sql = "select * from infoHolder where ac_number=? and password=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, account);
        preparedStatement.setInt(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println("Login Successfully.");
            System.out.println("Account number- " + resultSet.getInt("ac_number"));
            System.out.println("Account Password- " + resultSet.getInt("password"));
            System.out.println("Account Holder Name- " + resultSet.getString("name"));
            System.out.println("Total Account Balance- ₹" + resultSet.getInt("balance"));

            System.out.println("You are logged in.");
            System.out.println("1.Debit Account\n2.Credit Account\n3.Check Balance\n4.Exit Portal");
            System.out.println("Please enter your choice: ");
            int choice = scan.nextInt();
            switch (choice) {
                case 1:
                    debit(account);
                    break;
                case 2:
                    credit(account);
                    break;
                case 3:
                    checkBa(account);
                    break;
                case 4:
                    System.out.println("LogOut...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } else {
            System.out.println("Login Failed or Account not found.");
        }
    }

    public static void debit(int ac_number1) throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);

        String infoCredit = "Select * from infoHolder where ac_number=?";
        System.out.print("Enter Credit Account Number.");
        int creditAccount = scan.nextInt();
        PreparedStatement preparedStatement = con.prepareStatement(infoCredit);
        preparedStatement.setInt(1, creditAccount);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println(
                    "Name - " + resultSet.getString("name") + "\nAccount Number - " + resultSet.getInt("ac_number"));
            System.out.print("Is information correct(Y/N?- ");
            String correct = scan.next();
            if (correct.toUpperCase().equals("Y")) {
                System.out.print("Enter Amount:- ₹");
                int amount = scan.nextInt();
                // String sql = "select * from infoHolder where ac_number=?";
                if (isSufficient(con, ac_number1, amount)) {
                    String debitSql = "Update infoHolder set balance=balance-? where ac_number=?";
                    String creditSql = "Update infoHolder set balance=balance+? where ac_number=?";
                    PreparedStatement pDebit = con.prepareStatement(debitSql);
                    pDebit.setInt(1, amount);
                    pDebit.setInt(2, ac_number1);
                    PreparedStatement pCredit = con.prepareStatement(creditSql);
                    pCredit.setInt(2, creditAccount);
                    pCredit.setInt(1, amount);
                    con.setAutoCommit(true);
                    pDebit.executeUpdate();
                    pCredit.executeUpdate();
                    System.out.println("Debit Successfully");
                } else {
                    System.out.println("Debit Failed");
                }
            } else {
                System.out.println("Transaction failed");
            }
        } else {
            System.out.println("Transaction failed");
        }

    }

    public static void credit(int ac_number2) throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);
        String creditSql = "Update infoHolder set balance=balance+? where ac_number=?";
        System.out.print("Enter Amount to credit: ₹");
        int amount = scan.nextInt();
        if (amount < 0) {
            System.out.println("Amount cannot be negative");
            return;
        }
        PreparedStatement preparedStatement = con.prepareStatement(creditSql);
        preparedStatement.setInt(1, amount);
        preparedStatement.setInt(2, ac_number2);
        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            System.out.println("Credit Failed.");
        } else {
            System.out.println("Credit Failed.");
        }
    }

    public static void deleteAccount() throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        Scanner scan = new Scanner(System.in);
        String deleteSql = "Delete from infoHolder where ac_number=? AND password=?";
        PreparedStatement preparedStatement = con.prepareStatement(deleteSql);
        System.out.print("Enter Account Number- ");
        int ac_number = scan.nextInt();
        System.out.print("Enter Password- ");
        int password = scan.nextInt();
        preparedStatement.setInt(1, ac_number);
        preparedStatement.setInt(2, password);
        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            System.out.println("Account Deactivate Successfully");
        } else {
            System.out.println("Account Deactivate Failed");
        }
    }

    public static void checkBa(int ac_number3) throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        String sql = "Select balance from infoHolder where ac_number=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, ac_number3);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            System.out.println("Current Balance - ₹" + resultSet.getString("balance"));
        }
    }

    public static void listAccountHolder() throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        String sql = "Select * from infoHolder";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        int number = 1;
        while (resultSet.next()) {
            System.out.println("============================\n" + number);
            System.out.println("Name: " + resultSet.getString("name") + "\nAccount Number: "
                    + resultSet.getInt("ac_number") + "\nBalance: ₹" + resultSet.getInt("balance"));
            number++;
        }
        System.out.println("=============================");
    }

    public static boolean isSufficient(Connection con, int number, int balance) throws Exception {
        String sql = "select balance from infoHolder where ac_number=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, number);
        ResultSet resultSet = pst.executeQuery();
        if (resultSet.next()) {
            int currentBalance = resultSet.getInt("balance");
            if (currentBalance < balance) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}