package gr.gnoome.Utility;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import gr.gnoome.Domain.Person;
import java.util.*;

public class Database_Manager {

    private static final String url = "jdbc:mysql://localhost:3306/";
    private static final String user = "root";
    private static final String password = "";
    private static final String dbName = "civilian_database";


    private static Connection getConnection(String dbName) {
        String URL = url;
        if (dbName != null)
            URL += dbName;
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, user, password);
        } catch (Exception e) {
            System.out.println("Could not establish connection to the database");
            System.out.println(e.getMessage());
        }
        return con;
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed())
                con.close();
        } catch (Exception e) {
            System.out.println("Error occurred while closing database connection");
            System.out.println(e.getMessage());
        }

    }

    public static boolean existsDatabase() {
        boolean found = false;
        Connection con = getConnection(null);
        if (con != null) {
            try {
                ResultSet rs = con.getMetaData().getCatalogs();
                while (rs.next()) {
                    String catalogs = rs.getString(1);
                    if (catalogs.equals(dbName)) {
                        found = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error occurred while checking database existence");
                System.out.println(e.getMessage());
            } finally {
                closeConnection(con);
            }
        }
        if (!found){
            if(createDatabase()){
                found= true;
            }
        }
        return found;
    }

    private static boolean createDatabase() {
        boolean created = false;

        Connection con = getConnection(null);
        if (con != null) {
            try {
                String SQL = "create database " + dbName + ";";
                Statement st = con.createStatement();
                st.execute(SQL);
                created = true;
            } catch (Exception e) {
                System.out.println("Error occurred while creating database");
                System.out.println(e.getMessage());
            } finally {
                closeConnection(con);
            }
        }
        if (created)
            return createTables();

        return created;
    }

    private static String getFileContent(String resourcePath) {
        try (var inputStream = Database_Manager.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "SQL resource not found: " + resourcePath);
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while reading SQL resource: " + resourcePath, e);
        }
    }

    private static void Rollback(Connection con) {
        try {
            if (con != null)
                con.rollback();
        } catch (Exception e) {
            System.out.println("Error occurred while rolling back");
            System.out.println(e.getMessage());
        }
    }

    private static boolean createTables() {
        boolean created = false;

        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                con.setAutoCommit(false);
                String SQL = getFileContent("SQL_Scripts/create_tables.sql");
                Statement st = con.createStatement();
                st.execute(SQL);

                con.commit();
                created = true;

            } catch (Exception e) {
                System.out.println("Error occurred while creating tables");
                System.out.println(e.getMessage());
                Rollback(con);
            } finally {
                closeConnection(con);
            }

        }
        return created;
    }

    public static boolean addperson(Person person) {
        System.out.println("person id: " + person.getId());
        boolean added = false;
        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                con.setAutoCommit(false);
                String SQL = getFileContent("SQL_Scripts/add_person.sql");
                try (PreparedStatement pst = con.prepareStatement(SQL)) {
                    pst.setString(1, person.getId());
                    pst.setString(2, person.getName());
                    pst.setString(3, person.getSurname());
                    pst.setString(4, person.getBirthdate());
                    pst.setString(5, person.getGender());
                    pst.setString(6, person.getAddress());
                    pst.setString(7, person.getTax());
                    int rows = pst.executeUpdate();

                    if (rows == 1) {
                        con.commit();
                        added = true;
                    }

                }
            } catch (Exception e) {
                System.out.println("Error occurred while adding person");
                System.out.println(e.getMessage());
                Rollback(con);
            } finally {
                closeConnection(con);
            }
        }
        return added;
    }

    public static boolean existperson(String id) {
        boolean exists = false;
        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                String SQL = getFileContent("SQL_Scripts/exist_person.sql");
                try (PreparedStatement pst = con.prepareStatement(SQL)) {
                    pst.setString(1, id);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        exists = true;
                    }
                    rs.close();
                    pst.close();
                }
            } catch (Exception e) {
                System.out.println("Error occurred while checking person existence");
                System.out.println(e.getMessage());
            } finally {
                closeConnection(con);
            }
        }
        return exists;
    }

    public static boolean deleteperson(String id) {
        boolean deleted = false;
        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                con.setAutoCommit(false);
                String SQL = getFileContent("SQL_Scripts/remove_person.sql");
                try (PreparedStatement pst = con.prepareStatement(SQL)) {
                    pst.setString(1, id);
                    int rows = pst.executeUpdate();
                    if (rows == 1) {
                        con.commit();
                        deleted = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error occurred while deleting person");
                System.out.println(e.getMessage());
                Rollback(con);
            } finally {
                closeConnection(con);
            }
        }
        return deleted;
    }

    public static List<Person> ViewSelectedCivilians(Person person) {
        Connection con = getConnection(dbName);
        List <Person> persons = new ArrayList<>();
        if (con != null) {
            try {
                StringBuilder SQL = new StringBuilder("SELECT * FROM Civilian_Registry WHERE 1=1");
                if (person.getId() != null) {
                    SQL.append(" AND ID = ?");
                }
                if (person.getName() != null) {
                    SQL.append(" AND First_Name = ?");
                }
                if (person.getSurname() != null) {
                    SQL.append(" AND Last_Name = ?");
                }
                if (person.getBirthdate() != null) {
                    SQL.append(" AND Date_of_Birth = ?");
                }
                if (person.getGender() != null) {
                    SQL.append(" AND Gender = ?");
                }
                if (person.getAddress() != null) {
                    SQL.append(" AND Address = ?");

                }
                if (person.getTax() != null) {
                    SQL.append(" AND Tax_Identification_Number = ?");
                }
                PreparedStatement pst = con.prepareStatement(SQL.toString());
                int index = 1;
                if (person.getId() != null) {
                    pst.setString(index++, person.getId());
                }
                if (person.getName() != null) {
                    pst.setString(index++, person.getName());
                }
                if (person.getSurname() != null) {
                    pst.setString(index++, person.getSurname());
                }
                if (person.getBirthdate() != null) {
                    pst.setString(index++, person.getBirthdate());
                }
                if (person.getGender() != null) {
                    pst.setString(index++, person.getGender());
                }
                if (person.getAddress() != null) {
                    pst.setString(index++, person.getAddress());
                }
                if (person.getTax() != null) {
                    pst.setString(index++, person.getTax());
                }

                ResultSet rs = pst.executeQuery();
                boolean found = false;
                while (rs.next()) {
                    found = true;

                    person.setid(rs.getString("ID"));
                    person.setName(rs.getString("First_Name"));
                    person.setSurname(rs.getString("Last_Name"));
                    person.setBirthdate(rs.getString("Date_of_Birth"));
                    person.setGender(rs.getString("Gender"));
                    person.setAddress(rs.getString("Address"));
                    person.setTax(rs.getString("Tax_Identification_Number"));

                    persons.add(person);

                }
                if (!found) {
                    System.out.println("No records found matching the search criteria.");
                }
                rs.close();
                pst.close();
            } catch (Exception e) {
                System.out.println("Error occurred while searching for person");
                System.out.println(e.getMessage());
            } finally {
                closeConnection(con);
            }
            
            
        }
        return persons;
    }

    public static List<Person> viewallpersons() {
        List <Person> persons = new ArrayList<>();
        
        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                String SQL = "SELECT * FROM Civilian_Registry WHERE 1=1";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(SQL);

                while (rs.next()) {

                    Person person = new Person();

                    person.setid(rs.getString("ID"));
                    person.setName(rs.getString("First_Name"));
                    person.setSurname(rs.getString("Last_Name"));
                    person.setBirthdate(rs.getString("Date_of_Birth"));
                    person.setGender(rs.getString("Gender"));
                    person.setAddress(rs.getString("Address"));
                    person.setTax(rs.getString("Tax_Identification_Number"));

                    persons.add(person);
                }
            } catch (Exception e) {
                System.out.println("Error occurred while viewing all persons");
                System.out.println(e.getMessage());
               
            } finally {
                closeConnection(con);
            }
        }
        return persons;
    }

    public static Boolean updateperson(String id, String address, String tax) {
        Boolean flag = false;

        Connection con = getConnection(dbName);
        if (con != null) {
            try {
                con.setAutoCommit(false);
                String SQL = getFileContent("SQL_Scripts/update_person.sql");
                try (PreparedStatement pst = con.prepareStatement(SQL)) {
                    pst.setString(1, id);
                    pst.setString(2, address);
                    pst.setString(3, tax);
                    int rows = pst.executeUpdate();
                    if (rows == 1) {
                        con.commit();
                        flag = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error occurred while updating person");
                System.out.println(e.getMessage());
                Rollback(con);
            } finally {
                closeConnection(con);
            }
        }
        return flag;
    }
}