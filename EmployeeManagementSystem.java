package assignment;

import java.sql.*;
import java.util.*;

public class EmployeeManagementSystem {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Employee";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTableIfNotExists(connection);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Employee Management System");
                System.out.println("1. Add new employee");
                System.out.println("2. Update employee details");
                System.out.println("3. Delete an employee");
                System.out.println("4. Display all employees");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addEmployee(connection, scanner);
                        break;
                    case 2:
                        updateEmployee(connection, scanner);
                        break;
                    case 3:
                        deleteEmployee(connection, scanner);
                        break;
                    case 4:
                        displayAllEmployees(connection);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "department VARCHAR(255)," +
                "salary DOUBLE PRECISION" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }

    private static void addEmployee(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter employee ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter employee name: ");
        String name = scanner.nextLine();
        System.out.print("Enter employee department: ");
        String department = scanner.nextLine();
        System.out.print("Enter employee salary: ");
        double salary = scanner.nextDouble();

        String insertSQL = "INSERT INTO employees (id, name, department, salary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, department);
            preparedStatement.setDouble(4, salary);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee added successfully.");
            } else {
                System.out.println("Failed to add employee.");
            }
        }
    }

    private static void updateEmployee(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter employee ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new name (leave blank to skip): ");
        String newName = scanner.nextLine();
        System.out.print("Enter new department (leave blank to skip): ");
        String newDepartment = scanner.nextLine();
        System.out.print("Enter new salary (0 to skip): ");
        double newSalary = scanner.nextDouble();

        StringBuilder updateSQL = new StringBuilder("UPDATE employees SET ");
        if (!newName.isEmpty()) {
            updateSQL.append("name = ?, ");
        }
        if (!newDepartment.isEmpty()) {
            updateSQL.append("department = ?, ");
        }
        if (newSalary > 0) {
            updateSQL.append("salary = ?, ");
        }
        updateSQL.delete(updateSQL.length() - 2, updateSQL.length()); // Remove the last comma and space
        updateSQL.append(" WHERE id = ?");

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL.toString())) {
            int parameterIndex = 1;
            if (!newName.isEmpty()) {
                preparedStatement.setString(parameterIndex++, newName);
            }
            if (!newDepartment.isEmpty()) {
                preparedStatement.setString(parameterIndex++, newDepartment);
            }
            if (newSalary > 0) {
                preparedStatement.setDouble(parameterIndex++, newSalary);
            }
            preparedStatement.setInt(parameterIndex, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee details updated successfully.");
            } else {
                System.out.println("Failed to update employee details.");
            }
        }
    }

    private static void deleteEmployee(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter employee ID to delete: ");
        int id = scanner.nextInt();

        String deleteSQL = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        }
    }

    private static void displayAllEmployees(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM employees";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            System.out.println("ID\tName\tDepartment\tSalary");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String department = resultSet.getString("department");
                double salary = resultSet.getDouble("salary");
                System.out.println(id + "\t" + name + "\t" + department + "\t" + salary);
            }
        }
    }
}
