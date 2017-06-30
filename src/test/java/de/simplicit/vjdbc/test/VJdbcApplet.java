// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class VJdbcApplet extends Applet {
    private static final long serialVersionUID = 3257850974046533684L;
    
    private JButton _addButton;
    private JButton _changeButton;
    private JButton _deleteButton;
    private JTable _tableOfAddresses;
    private AddressTableModel _modelOfAddresses = new AddressTableModel();

    public void init() {
        try {
            Class.forName("de.simplicit.vjdbc.VirtualDriver").newInstance();

            setLayout(new GridBagLayout());
            setBackground(Color.GRAY);

            _addButton = new JButton("Add");
            add(_addButton, new GridBagConstraints(0, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            _changeButton = new JButton("Change");
            add(_changeButton, new GridBagConstraints(1, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
            _deleteButton = new JButton("Delete");
            add(_deleteButton, new GridBagConstraints(2, 0, 1, 1, 0.3, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

            _addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addAddress();
                }
            });
            
            _deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteAddresses();
                }
            });
            
            _tableOfAddresses = new JTable(_modelOfAddresses);
            add(_tableOfAddresses, new GridBagConstraints(0, 1, 3, 10, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

            refreshAdresses();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(HeadlessException e) {
            e.printStackTrace();
        }
    }

    private void addAddress() {
        AddressDialog adrDialog = new AddressDialog();

        adrDialog.setSize(400, 200);
        adrDialog.setModal(true);
        adrDialog.show();

        if(!adrDialog.isCancelled()) {
            Connection conn = null;
            try {
                conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("insert into Address (Name, Street, City) values (?, ?, ?)");
                stmt.setString(1, adrDialog.getName());
                stmt.setString(2, adrDialog.getStreet());
                stmt.setString(3, adrDialog.getCity());
                stmt.executeUpdate();
                stmt.close();
            } catch(SQLException e) {
                e.printStackTrace();
            } finally {
                if(conn != null) {
                    try {
                        conn.close();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }

                refreshAdresses();
            }
        }
    }
    
    private void deleteAddresses() {
        if(_tableOfAddresses.getSelectedRowCount() > 0) {
            int[] selrows = _tableOfAddresses.getSelectedRows();
            Connection conn = null;
            try {
                conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("delete from address where id = ?");
                for(int i = 0; i < selrows.length; i++) {
                    Object[] row = _modelOfAddresses.getSelectedItem(selrows[i]);
                    Integer id = (Integer)row[0];
                    stmt.setInt(1, id.intValue());
                    stmt.executeUpdate();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally {
                if(conn != null) {
                    try {
                        conn.close();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
                
                refreshAdresses();
            }
        }
    }

    private Connection getConnection() throws SQLException {
        URL codebase = getCodeBase();
        String vjdbcurl = "jdbc:vjdbc:servlet:" + codebase.toString() + "vjdbc";
        return DriverManager.getConnection(vjdbcurl);
    }

    private void refreshAdresses() {
        Connection conn = null;
        try {
            _modelOfAddresses.clear();
            conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from address");
            while(rs.next()) {
                _modelOfAddresses.addAddress(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
            stmt.close();
            _modelOfAddresses.fireTableDataChanged();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class AddressDialog extends JDialog {
        private static final long serialVersionUID = 3258416127268042038L;
        
        private JTextField _name;
        private JTextField _street;
        private JTextField _city;
        private JButton _ok;
        private JButton _cancel;

        private boolean _cancelled;

        AddressDialog() {
            Container cont = getContentPane();
            cont.setLayout(new GridBagLayout());

            _name = new JTextField();
            _street = new JTextField();
            _city = new JTextField();

            _ok = new JButton("OK");
            _cancel = new JButton("Cancel");

            Insets insets = new Insets(2, 5, 2, 5);

            cont.add(new JLabel("Name:"),
                     new GridBagConstraints(0, 0, 1, 1, 0.2, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            cont.add(_name,
                     new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            cont.add(new JLabel("Street:"),
                     new GridBagConstraints(0, 1, 1, 1, 0.2, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            cont.add(_street,
                     new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            cont.add(new JLabel("City:"),
                     new GridBagConstraints(0, 2, 1, 1, 0.2, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            cont.add(_city,
                     new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(_ok);
            buttonPanel.add(_cancel);

            cont.add(buttonPanel,
                     new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            _ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _cancelled = false;
                    AddressDialog.this.dispose();
                }
            });

            _cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _cancelled = true;
                    AddressDialog.this.dispose();
                }
            });
        }

        public boolean isCancelled() {
            return _cancelled;
        }

        public String getName() {
            return _name.getText();
        }

        public String getStreet() {
            return _street.getText();
        }

        public String getCity() {
            return _city.getText();
        }
    }
    
    private static class AddressTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 3689627012692391472L;
        
        private static final int COLUMN_ID = 0;
        private static final int COLUMN_NAME = 1;
        private static final int COLUMN_STREET = 2;
        private static final int COLUMN_CITY = 3;
        private static final int COLUMN_COUNT = 4;
        private ArrayList _addresses;
        
        public AddressTableModel() {
            _addresses = new ArrayList();
        }
        
        public void clear() {
            _addresses.clear();
        }
        
        public Object[] getSelectedItem(int index) {
            return (Object[])_addresses.get(index);
        }
        
        public void addAddress(int id, String name, String street, String city) {
            Object[] address = new Object[COLUMN_COUNT];
            address[COLUMN_ID] = new Integer(id);
            address[COLUMN_NAME] = name;
            address[COLUMN_STREET] = street;
            address[COLUMN_CITY] = city;
            _addresses.add(address);
        }

        public String getColumnName(int column) {
            switch(column) {
                case COLUMN_ID:
                    return "Id";
                case COLUMN_NAME:
                    return "Name";
                case COLUMN_STREET:
                    return "Street";
                case COLUMN_CITY:
                    return "City";
            }
            return "";
        }

        public int getColumnCount() {
            return COLUMN_COUNT;
        }

        public int getRowCount() {
            return _addresses.size();
        }

        public Object getValueAt(int row, int column) {
            Object[] address = (Object[])_addresses.get(row);
            return address[column];
        }
    }
}
