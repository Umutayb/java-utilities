package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides utility methods for working with a database.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
public class DBUtilities {
    private final Printer log = new Printer(DBUtilities.class);

    /**
     * Establishes a connection to the database using the provided credentials.
     *
     * @param user the username to use for the connection
     * @param password the password to use for the connection
     * @param url the URL of the database to connect to
     * @return a Connection representing the database connection
     * @throws RuntimeException if there is an error establishing the connection
     */
    public Connection getConnection(String user, String password, String url) {
        try {
            Connection connection = DriverManager.getConnection(url,user,password);
            log.success("Connection established!");
            return connection;
        }
        catch (SQLException e) {throw new RuntimeException(e);}
    }

    /**
     * Retrieves the results of an SQL query and maps them to a list of maps.
     *
     * @param connection the database connection to use for the query
     * @param sqlQuery the SQL query to execute
     * @param includeNullValues whether to include null values in the resulting map
     * @return a list of maps, where each map represents a row in the result set and maps column names to values
     * @throws RuntimeException if there is an error executing the SQL query
     */
    public List<Map<String, Object>> getResults(Connection connection, String sqlQuery, Boolean includeNullValues){
        log.info("Mapping result set...");
        List<Map<String, Object>> database = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ResultSetMetaData md = resultSet.getMetaData();
            int columns = md.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>(columns);
                for(int i=1; i<=columns; ++i) {
                    if (includeNullValues) row.put(md.getColumnName(i), resultSet.getObject(i));
                    else if (resultSet.getObject(i) != null && resultSet.getObject(i).toString().length()>0)
                        row.put(md.getColumnName(i), resultSet.getObject(i));
                }
                database.add(row);
            }
            if (database.size() == 0) log.warning("No entries were found for the given query!");
            else if (database.size() == 1) log.success(database.size() + " entry successfully mapped!");
            else log.success(database.size() + " entries successfully mapped!");
            return database;
        }
        catch (SQLException e) {throw new RuntimeException(e);}
    }
}
