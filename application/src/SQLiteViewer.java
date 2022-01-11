import javax.swing.*;
import org.sqlite.SQLiteDataSource;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * SWING GUI Application that let the user specify database path
 * then the application loads the database and the database relations names
 * then the application let the user execute SQL queries
 * after executing the query the results will be presented in tabular form
 *
 * @author razlevi
 */

public class SQLiteViewer extends JFrame {
    /**
     * Instantiates a new SQLite Viewer
     */
    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setTitle("SQLite Database Viewer");
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        // Fixed Paddings
        Insets topPadding = new Insets(10, 0, 0, 0);
        Insets leftPadding = new Insets(0, 10, 0, 0);
        Insets topLeftPadding = new Insets(10, 10, 0, 0);

        // The Main GridBagLayout Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        mainPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Label before the input text field
        JLabel dataBaseName = new JLabel("Path");
        {
            con.weightx = 0.0;
            con.gridx = 0;
            con.gridy = 0;
            mainPanel.add(dataBaseName, con);
        }

        // Database name Input text field
        JTextField fileNameTextField = new JTextField();
        {
            fileNameTextField.setName("FileNameTextField");
            fileNameTextField.setColumns(20);
            con.fill = GridBagConstraints.BOTH;
            con.insets = leftPadding;
            con.weightx = 1.0;
            con.gridx = 1;
            mainPanel.add(fileNameTextField, con);
        }

        // Open the database button
        JButton openFileButton = new JButton("Open");
        {
            openFileButton.setName("OpenFileButton");
            con.fill = GridBagConstraints.HORIZONTAL;
            con.weightx = 0.0;
            con.gridx = 2;
            mainPanel.add(openFileButton, con);
        }

        // Table label before choosing a label
        JLabel tableLabel = new JLabel("Table");
        {
            con.fill = GridBagConstraints.NONE;
            con.insets = topPadding;
            con.gridx = 0;
            con.gridy = 1;
            mainPanel.add(tableLabel, con);
        }

        // Adding the tablesComboBox
        JComboBox<String> tablesComboBox = new JComboBox<>();
        {
            tablesComboBox.setName("TablesComboBox");
            con.fill = GridBagConstraints.BOTH;
            con.insets = topLeftPadding;
            con.weightx = 1.0;
            con.gridx = 1;
            mainPanel.add(tablesComboBox, con);
        }

        // Adding query area label
        JLabel queryLabel = new JLabel("Query");
        {
            con.fill = GridBagConstraints.NONE;
            con.anchor = GridBagConstraints.NORTH;
            con.insets = topPadding;
            con.weightx = 0.0;
            con.gridx = 0;
            con.gridy = 2;
            mainPanel.add(queryLabel, con);
        }

        // Adding the TextArea component
        JTextArea queryTextArea = new JTextArea();
        {
            queryTextArea.setName("QueryTextArea");
            queryTextArea.setRows(5);
            queryTextArea.setForeground(Color.BLACK);
            queryTextArea.setWrapStyleWord(true);
            con.fill = GridBagConstraints.BOTH;
            con.anchor = GridBagConstraints.CENTER;
            con.insets = topLeftPadding;
            con.weightx = 1.0;
            con.gridx = 1;
            JScrollPane queryTextScroll = new JScrollPane(queryTextArea);
            mainPanel.add(queryTextScroll, con);
        }

        // Adding Execute query button
        JButton executeQueryButton = new JButton("Execute");
        {
            executeQueryButton.setName("ExecuteQueryButton");
            executeQueryButton.setEnabled(false);
            con.fill = GridBagConstraints.HORIZONTAL;
            con.anchor = GridBagConstraints.NORTH;
            con.insets = topLeftPadding;
            con.weightx = 0.0;
            con.gridx = 2;
            mainPanel.add(executeQueryButton, con);
        }

        // Adding All Components to the main panel
        add(mainPanel, BorderLayout.PAGE_START);

        // Adding Table for Outputting Queries
        JTable dataTable = new JTable();
        {
            dataTable.setName("Table");
            dataTable.setFillsViewportHeight(true);
            JScrollPane tableScrollPane = new JScrollPane(dataTable);
            tableScrollPane.setBorder(new EmptyBorder(8, 8, 8, 8));
            add(tableScrollPane, BorderLayout.CENTER);
        }

        // Loading the database names
        openFileButton.addActionListener(event -> {
            String fileName = fileNameTextField.getText();
            if (Files.exists(Paths.get(fileName))) {
                new TableNamesLoader(fileName, tablesComboBox).execute();
                queryTextArea.setEnabled(true);
                executeQueryButton.setEnabled(true);
                queryTextArea.setText(String.format(DatabaseConnector.BASE_QUERY, tablesComboBox.getSelectedItem()));
            } else {
                tablesComboBox.removeAllItems();
                queryTextArea.setText(null);
                queryTextArea.setEnabled(false);
                executeQueryButton.setEnabled(false);
                dataTable.setModel(new DefaultTableModel());
                JOptionPane.showMessageDialog(new Frame(), "Database doesn't exist!", "File Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tablesComboBox.addItemListener(event -> queryTextArea.setText(
                String.format(DatabaseConnector.BASE_QUERY, event.getItem().toString())));


        // Execute query section
        executeQueryButton.addActionListener(event -> new DataLoader(
                fileNameTextField.getText(),
                queryTextArea.getText(),
                dataTable).execute());
    }
}
