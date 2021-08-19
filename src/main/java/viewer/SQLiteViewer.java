package viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;

public class SQLiteViewer extends JFrame {

    private static final String SELECT_QUERY = "SELECT * FROM %s;";
    private JComboBox<String> tablesBox;
    private JTextArea queryArea;
    private JTextField dbNameField;
    private DataBaseTableModel tableModel;
    private JButton executeButton;

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        dbNameField = new JTextField();
        dbNameField.setName("FileNameTextField");
        dbNameField.setBounds(10, 10, 400, 30);
        add(dbNameField);

        tablesBox = new JComboBox<>();
        tablesBox.setName("TablesComboBox");
        tablesBox.setBounds(10, 50, 400, 30);
        tablesBox.addItemListener(e ->
                checkEventDispatchThread(() -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        queryArea.setText(String.format(SELECT_QUERY, tablesBox.getSelectedItem()));
                    }
                })
        );
        add(tablesBox);

        queryArea = new JTextArea();
        queryArea.setName("QueryTextArea");
        queryArea.setBounds(10, 90, 400, 100);
        add(queryArea);

        tableModel = new DataBaseTableModel();
        JTable dataTable = new JTable(tableModel);
        dataTable.setName("Table");
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBounds(10, 200, 660, 200);
        add(scrollPane);

        executeButton = new JButton("Execute");
        executeButton.setName("ExecuteQueryButton");
        executeButton.setBounds(420, 90, 100, 30);
        executeButton.addActionListener(e ->
                checkEventDispatchThread(() -> {
                    String error = DataBase.fillTable(dbNameField.getText(), queryArea.getText(), tableModel);
                    if (error != null) {
                        JOptionPane.showMessageDialog(new Frame(), error);
                    }
                })
        );
        add(executeButton);

        JButton openButton = new JButton("Open");
        openButton.setName("OpenFileButton");
        openButton.setBounds(420, 10, 100, 30);
        openButton.addActionListener(e -> {
            Runnable runnable;
            if (new File(dbNameField.getText()).isFile()) {
                runnable = () -> {
                    if (tablesBox.getItemCount() > 0) {
                        tablesBox.removeAllItems();
                    }
                    DataBase.findPublicTables(dbNameField.getText())
                            .forEach(t -> tablesBox.addItem(t));
                    if (tablesBox.getItemCount() > 0) {
                        tablesBox.setSelectedIndex(0);
                    }
                    enableQueryExecute();
                };
            } else {
                runnable = () -> {
                    JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
                    disableQueryExecute();
                };
            }
            checkEventDispatchThread(runnable);
        });
        add(openButton);

        disableQueryExecute();
    }

    private void enableQueryExecute() {
        if (!queryArea.isEnabled()) {
            queryArea.setEnabled(true);
            executeButton.setEnabled(true);
        }
    }

    private void disableQueryExecute() {
        if (queryArea.isEnabled()) {
            queryArea.setEnabled(false);
            executeButton.setEnabled(false);
        }
    }
    
    private void checkEventDispatchThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

}
