package viewer;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private static final SQLiteDataSource dataSource = new SQLiteDataSource();
    private static final String URL = "jdbc:sqlite:";
    private static final String SHOW_TABLES = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%'";

    public static List<String> findPublicTables(String database) {
        dataSource.setUrl(URL + database);
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SHOW_TABLES)) {
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString("name"));
            }
            return tables;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    public static String fillTable(String database, String query, DataBaseTableModel model) {
        dataSource.setUrl(URL + database);
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            String[] cols = new String[metaData.getColumnCount()];
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                cols[i - 1] = metaData.getColumnLabel(i);
            }

            List<Object[]> rows = new ArrayList<>();
            Object[] row;
            while (resultSet.next()) {
                 row = new Object[metaData.getColumnCount()];
                 for (int i = 1; i <= metaData.getColumnCount(); i++) {
                     row[i - 1] = resultSet.getObject(i);
                 }
                 rows.add(row);
            }
            model.setTableData(cols, rows);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

}
