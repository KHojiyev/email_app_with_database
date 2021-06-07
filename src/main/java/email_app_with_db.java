import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class email_app_with_db {
    public static Scanner scanner = new Scanner(System.in);
    public static Connection connection = ConnectDB.ConnectionDb("db_email");
    public static User activeUser;
    public static int receiver_id;


    public static void main(String[] args) {
        while (true) {
            showMainMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    System.out.println("bye");
                    return;
                default:
                    break;
            }


        }
    }

    private static void login() {
        System.out.println("---------login----------");
        System.out.print("Email: ");
        scanner = new Scanner(System.in);
        String login_email = scanner.nextLine();
        System.out.print("password: ");
        String login_password = scanner.nextLine();
        String sql = "select * from users";
        boolean notFound = true;
        boolean inCorrectPassword = true;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                //String id = resultSet.getString("user_id");
                int id = resultSet.getInt("user_id");
                String email = resultSet.getString("email").trim();
                String password = resultSet.getString("password").trim();
                String first_name = resultSet.getString("first_name").trim();
                String last_name = resultSet.getString("last_name").trim();
                if (email.equals(login_email)) {
                    notFound = false;
                    if (password.equals(login_password)) {
                        inCorrectPassword = false;
                        activeUser = new User(id, first_name, last_name, email, password);
                        System.out.println("\n--Welcome to mail " + activeUser.getFirst_name() + " " + activeUser.getLast_name() + "--");
                        checkNewMessage();
                        mainOperations();
                    }
                }
            }
            preparedStatement.close();

            if (notFound) {
                System.out.println("user not found");
            }
            if (inCorrectPassword) {
                System.out.println("incorrect password");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void checkNewMessage() {
        String sql = "select * from email where is_read_receiver = 'unread' and receiver_id =" + activeUser.getUser_id();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count++;
            }
            if (count > 0) {
                System.out.println("\nYou have " + count + " unread messages\n");
                count = 0;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void mainOperations() {
        while (true) {
            scanner = new Scanner(System.in);
            operationMenu();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    sendMessage();
                    break;
                case 2:
                    viewInbox();
                    break;
                case 3:
                    viewOutBox();
                    break;
                case 4:
                    recycleBin();
                    break;
                case 5:
                    settings();
                    break;
                case 6:
                    return;
                default:
                    break;
            }
        }
    }

    private static void operationMenu() {
        checkDeleteForever();
        System.out.println("Select operations:\n" +
                "---------------------\n" +
                "1.Send message\n" +
                "2.View inbox\n" +
                "3.View outbox\n" +
                "4.Recycle Bin\n" +
                "5.Settings\n" +
                "6.Log out\n" +
                "---------------------");

    }

    private static void checkDeleteForever() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from email");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("is_deleted_sender").startsWith("deleted_forever")) {
                    if (resultSet.getString("is_deleted_sender").startsWith("deleted_forever")) {
                        PreparedStatement preparedStatement1 = connection.prepareStatement(
                                "delete from email where email_id=" + resultSet.getInt("email_id"));
                        preparedStatement1.executeUpdate();
                        preparedStatement1.close();

                    }
                }
            }
            preparedStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void settings() {
        settingsMenu();
        scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:

                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                return;
        }
    }

    private static void settingsMenu() {
        System.out.println("-------Settings------");
        System.out.println("1.Change email\n" +
                "2.Change password\n" +
                "3.Change First name\n" +
                "4.Change Last name\n" +
                "5.Delete account\n" +
                "6.Exit");
        System.out.println("---------------------");
    }

    private static void recycleBin() {
        System.out.println("-------RecycleBin--------");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from email where (is_deleted_receiver='deleted' and receiver_id=" + activeUser.getUser_id() +
                            ") or (sender_id=" + activeUser.getUser_id() + " and is_deleted_sender='deleted')");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                String receiver_email = "";
                PreparedStatement preparedStatement1 = connection.prepareStatement(
                        "select * from users where user_id = " + resultSet.getInt("receiver_id"));
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while (resultSet1.next()) {
                    receiver_email = resultSet1.getString("email");
                }
                preparedStatement1.close();

                String sender_email = "";
                try {
                    PreparedStatement preparedStatement2 = connection.prepareStatement("select email from users where user_id="
                            + resultSet.getInt("sender_id"));
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    while (resultSet2.next()) {
                        sender_email = resultSet2.getString("email");
                    }
                    resultSet2.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


                if (resultSet.getInt("sender_id") == activeUser.getUser_id()) {
                    try {
                        System.out.println("id: " + resultSet.getInt("email_id") + "\n" +
                                "Receiver: " + receiver_email + "\n" +
                                "Subject: " + resultSet.getString("subject") + "\n" +
                                "Message: " + resultSet.getString("message") + "\n" +
                                "Time: " + resultSet.getTimestamp("created_at"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("id: " + resultSet.getInt("email_id") + "\n" +
                            "Sender: " + sender_email + "\n" +
                            "Subject: " + resultSet.getString("subject") + "\n" +
                            "Message: " + resultSet.getString("message") + "\n" +
                            "Time: " + resultSet.getTimestamp("created_at"));
                }
                System.out.println("----------------------");
            }
            deleteForeverCancelButtons();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteForeverCancelButtons() throws SQLException {
        System.out.println("<|| d - delete forever || r - recover || c - cancel ||>");
        scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from email where (is_deleted_receiver='deleted' and receiver_id=" + activeUser.getUser_id() +
                            ") or (sender_id=" + activeUser.getUser_id() + " and is_deleted_sender='deleted')");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                switch (choice) {
                    case "d":
                        System.out.print("id: ");
                        scanner = new Scanner(System.in);
                        int del = scanner.nextInt();
                        if (resultSet.getInt("sender_id") == activeUser.getUser_id()) {
                            PreparedStatement preparedStatement1 = connection.prepareStatement(
                                    "update email set is_deleted_sender='deleted_forever' where email_id=" + del);
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        }
                        if (resultSet.getInt("receiver_id") == activeUser.getUser_id()) {
                            PreparedStatement preparedStatement1 = connection.prepareStatement(
                                    "update email set is_deleted_receiver='deleted_forever' where email_id=" + del);
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        }
                        System.out.println("deleted");
                        break;
                    case "c":

                        System.out.println("exiting...");
                        return;
                    case "r":
                        System.out.print("id: ");
                        scanner = new Scanner(System.in);
                        int dell = scanner.nextInt();
                        if (resultSet.getInt("sender_id") == activeUser.getUser_id()) {
                            PreparedStatement preparedStatement1 = connection.prepareStatement(
                                    "update email set is_deleted_sender='notDeleted' where email_id=" + dell);
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        } else {
                            PreparedStatement preparedStatement1 = connection.prepareStatement(
                                    "update email set is_deleted_receiver='notDeleted' where email_id=" + dell);
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        }
                        System.out.println("recovered!!!");
                        break;
                    default:
                        break;
                }
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage() {
        System.out.print("To whom: ");
        scanner = new Scanner(System.in);
        String toWhom = scanner.nextLine();
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        System.out.print("Message: ");
        String message = scanner.nextLine();
        String receiverId = "select * from users";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(receiverId);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                if (resultSet1.getString("email").trim().equals(toWhom)) {
                    receiver_id = resultSet1.getInt("user_id");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String sql = "insert into email (subject,message,sender_id,receiver_id,created_at,is_read_sender,is_read_receiver,is_deleted_sender,is_deleted_receiver)values(?,?,?,?,?,?,?,?,?)";
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subject);
            preparedStatement.setString(2, message);
            preparedStatement.setInt(3, activeUser.getUser_id());
            preparedStatement.setInt(4, receiver_id);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(localDateTime));
            preparedStatement.setString(6, "unread");
            preparedStatement.setString(7, "unread");
            preparedStatement.setString(8, "notDeleted");
            preparedStatement.setString(9, "notDeleted");

            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println(" sent!!\n");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void viewOutBox() {
        System.out.println("------OutBox------");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select * from email where sender_id=" + activeUser.getUser_id());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean notFound = true;
            while (resultSet.next()) {
                if (resultSet.getString("is_deleted_sender").startsWith("notDeleted")) {
                    notFound = false;
                    String receiver_email = "";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(
                            "select * from users where user_id = " + resultSet.getInt("receiver_id"));
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    while (resultSet1.next()) {
                        receiver_email = resultSet1.getString("email");
                    }
                    preparedStatement1.close();
                    System.out.println("id: " + resultSet.getInt("email_id") +
                            " Status: " + resultSet.getString("is_read_receiver") + "\n" +
                            "To whom: " + receiver_email + "\n" +
                            "Subject: " + resultSet.getString("subject") + "\n" +
                            "Message: " + resultSet.getString("message") + "\n" +
                            "Time: " + resultSet.getTimestamp("created_at"));
                    System.out.println("----------------------");
                }

            }
            preparedStatement.close();
            if (notFound) {
                System.out.println(" \nempty\n ");
            }
            deleteCancelButtons("out");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void viewInbox() {
        System.out.println("-------InBox--------");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from email where receiver_id="
                    + activeUser.getUser_id());
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean notFound = true;
            while (resultSet.next()) {
                if (resultSet.getString("is_deleted_receiver").trim().equals("notDeleted")) {
                    if (resultSet.getString("is_read_receiver").trim().equals("unread")) {
                        notFound = false;
                        String sender_email = "";
                        try {
                            PreparedStatement preparedStatement1 = connection.prepareStatement("select email from users where user_id="
                                    + resultSet.getInt("sender_id"));
                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            while (resultSet1.next()) {
                                sender_email = resultSet1.getString("email");
                            }
                            resultSet1.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        System.out.println("id: " + resultSet.getInt("email_id") +
                                " Status: " + resultSet.getString("is_read_receiver") + "\n" +
                                "Sender: " + sender_email + "\n" +
                                "Subject: " + resultSet.getString("subject") + "\n" +
                                "Message: " + resultSet.getString("message") + "\n" +
                                "Time: " + resultSet.getTimestamp("created_at"));
                        System.out.println("----------------------");
                    }
                    if (resultSet.getString("is_read_receiver").trim().equals("read")) {
                        notFound = false;
                        String sender_email = "";
                        try {
                            PreparedStatement preparedStatement1 = connection.prepareStatement("select email from users where user_id="
                                    + resultSet.getInt("sender_id"));
                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            while (resultSet1.next()) {
                                sender_email = resultSet1.getString("email");
                            }
                            resultSet1.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        System.out.println("id: " + resultSet.getInt("email_id") +
                                " Status: " + resultSet.getString("is_read_receiver") + "\n" +
                                "Sender: " + sender_email + "\n" +
                                "Subject: " + resultSet.getString("subject") + "\n" +
                                "Message: " + resultSet.getString("message") + "\n" +
                                "Time: " + resultSet.getTimestamp("created_at"));
                        System.out.println("----------------------");
                    }
                }
            }
            while (resultSet.next()) {
                if (resultSet.getString("is_deleted_receiver").trim().equals("notDeleted")) {
                    if (resultSet.getString("is_read_receiver").trim().equals("read")) {
                        notFound = false;
                        String sender_email = "";
                        try {
                            PreparedStatement preparedStatement1 = connection.prepareStatement("select email from users where user_id="
                                    + resultSet.getInt("sender_id"));
                            ResultSet resultSet1 = preparedStatement1.executeQuery();
                            while (resultSet1.next()) {
                                sender_email = resultSet1.getString("email");
                            }
                            resultSet1.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        System.out.println("----------------------");
                        System.out.println("id: " + resultSet.getInt("email_id") +
                                " Status: " + resultSet.getString("is_read_receiver") + "\n" +
                                "Sender: " + sender_email + "\n" +
                                "Subject: " + resultSet.getString("subject") + "\n" +
                                "Message: " + resultSet.getString("message") + "\n" +
                                "Time: " + resultSet.getTimestamp("created_at"));
                        System.out.println("----------------------");
                    }
                }
            }
            preparedStatement.close();
            if (notFound) {
                System.out.println(" \nempty\n ");
            }
            deleteCancelButtons("in");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void deleteCancelButtons(String which) {
        System.out.println("<||d - delete || c - cancel ||>");
        scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        switch (choice) {
            case "d":
                System.out.print("id: ");
                scanner = new Scanner(System.in);
                int del = scanner.nextInt();
                if (which.equals("in")) {
                    try {
                        PreparedStatement preparedStatement1 = connection.prepareStatement(
                                "update email set is_deleted_receiver='deleted' where email_id=" + del);
                        preparedStatement1.executeUpdate();
                        preparedStatement1.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else if (which.equals("out")) {
                    try {
                        PreparedStatement preparedStatement1 = connection.prepareStatement(
                                "update email set is_deleted_sender='deleted' where email_id=" + del);
                        preparedStatement1.executeUpdate();
                        preparedStatement1.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                System.out.println("deleted");
                break;
            case "c":
                if (which.equals("in")) {
                    try {
                        PreparedStatement preparedStatement2 = connection.prepareStatement(
                                "update email set is_read_receiver ='read' where receiver_id=" + activeUser.getUser_id());
                        preparedStatement2.executeUpdate();
                        preparedStatement2.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                System.out.println("exiting...");
                return;
            default:
                break;
        }
    }

    private static void registerUser() {
        scanner = new Scanner(System.in);
        System.out.println("--------registering-------");
        System.out.print("First Name: ");
        String first_name = scanner.nextLine();
        System.out.print("Last Name: ");
        String last_name = scanner.nextLine();
        System.out.print("email address: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String sql = "insert into users(email,password,first_name,last_name) values(?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, first_name);
            preparedStatement.setString(4, last_name);
            System.out.println("register successfully!!!");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void showMainMenu() {
        System.out.println("\nMain menu:");
        System.out.println("1.Sign in");
        System.out.println("2.Sign up");
        System.out.println("3.Exit");

    }
}
