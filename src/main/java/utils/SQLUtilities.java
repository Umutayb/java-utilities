package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLUtilities {
    private final Printer log = new Printer(SQLUtilities.class);

    public Connection getConnection(String user, String password, String url, String database) {
        log.new Info("Establishing database connection...");
        if (database!=null) url += ";database=" + database;
        try {
            Connection connection = DriverManager.getConnection(url,user,password);
            log.new Success("Connection established!");
            return connection;
        }
        catch (SQLException e) {throw new RuntimeException(e);}
    }

    public List<Map<String, Object>> getResults(Connection connection, String sqlQuery, Boolean includeNullValues){
        log.new Info("Mapping result set...");
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
            if (database.size() == 0) log.new Warning("No entries were found for the given query!");
            else if (database.size() == 1) log.new Success(database.size() + " entry successfully mapped!");
            else log.new Success(database.size() + " entries successfully mapped!");
            return database;
        }
        catch (SQLException e) {throw new RuntimeException(e);}
    }
}
