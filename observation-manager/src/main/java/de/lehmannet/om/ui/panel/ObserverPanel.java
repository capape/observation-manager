/* ====================================================================
 * /panel/ObserverPanel.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import de.lehmannet.om.IObserver;
import de.lehmannet.om.ISchemaElement;
import de.lehmannet.om.Observer;
import de.lehmannet.om.ui.util.ConstraintsBuilder;
import de.lehmannet.om.ui.util.EditPopupHandler;
import de.lehmannet.om.ui.util.OMLabel;
import de.lehmannet.om.util.FloatUtil;

public class ObserverPanel extends AbstractPanel implements MouseListener, ActionListener {

    private static final long serialVersionUID = -7193577368053769290L;

    private IObserver observer = null;

    private JTextField name = null;
    private JTextField surname = null;
    private JTextArea contacts = null;
    private JTable accounts = null;
    private JButton addAccountRow = null;
    private JButton deleteAccountRow = null;
    private JTextField fstOffset = null;

    public ObserverPanel(IObserver observer, boolean editable) {

        super(editable);

        this.observer = observer;

        this.createPanel();

        if (observer != null) {
            this.loadSchemaElement();
        }

    }

    private void loadSchemaElement() {

        this.name.setText(this.observer.getName());
        this.name.setEditable(this.isEditable());

        this.surname.setText(this.observer.getSurname());
        this.surname.setEditable(this.isEditable());

        List<String> contacts = this.observer.getContacts();
        ListIterator<String> iterator = contacts.listIterator();
        StringBuilder contactString = new StringBuilder();
        while (iterator.hasNext()) {
            contactString.append(iterator.next());
            if (iterator.hasNext()) {
                contactString.append("\n");
            }
        }
        this.contacts.setText(contactString.toString());
        /*
         * if( (this.observer != null) && !(this.isEditable()) ) { this.contacts.setBackground(Color.LIGHT_GRAY); }
         */
        this.contacts.setEditable(this.isEditable());

        if (!Float.isNaN(this.observer.getFSTOffset())) {
            // Output format
            DecimalFormat df = new DecimalFormat("0.00");
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            this.fstOffset.setText(df.format(this.observer.getFSTOffset()));
        }
        this.fstOffset.setEditable(this.isEditable());

        // Accounts is initialized in createPanel due to TableModel

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        // Check only button
        // Source component is always the JTextArea
        if (e.getButton() == MouseEvent.BUTTON3) {
            new EditPopupHandler(e.getX(), e.getY(), this.contacts);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }

    @Override
    public ISchemaElement getSchemaElement() {

        return this.observer;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(this.addAccountRow)) {
            ((AccountTableModel) this.accounts.getModel()).addNewRow();
        } else if (e.getSource().equals(this.deleteAccountRow)) {
            // This is strange...when deleting a row (which is not the last row in the
            // table)
            // the TableCellEditor for column 0 gets lost
            // Therefore retrieve TableCellEditor first, and reset the CellEditor after we
            // deleted the row.
            TableCellEditor editor = this.accounts.getColumnModel().getColumn(0).getCellEditor();
            ((AccountTableModel) this.accounts.getModel()).deleteRow(this.accounts.getSelectedRow());
            this.accounts.getColumnModel().getColumn(0).setCellEditor(editor);
        }

    }

    @Override
    public ISchemaElement updateSchemaElement() {

        if (this.observer == null) {
            return null;
        }

        // Check mandatory fields
        // Get mandatory fields
        String name = this.getObserverName();
        if (name == null) {
            return null;
        }
        this.observer.setName(name);

        String surname = this.getSurname();
        if (surname == null) {
            return null;
        }
        this.observer.setSurname(surname);

        String contacts = this.contacts.getText();
        this.observer.setContacts(new ArrayList<>()); // Clear contacts
        if ((contacts != null) && !("".equals(contacts))) {
            this.observer.addContact(contacts); // Add all contacts again
        }

        // Add optional fields
        Map<String, String> accounts = ((AccountTableModel) this.accounts.getModel()).getAllEntries();
        // if( !accounts.isEmpty() ) {
        this.observer.setAccounts(accounts);
        // }

        String fstO = this.fstOffset.getText();
        if ((fstO != null) && !("".equals(fstO))) {
            try {
                float fo = FloatUtil.parseFloat(fstO);
                this.observer.setFSTOffset(fo);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observer.warning.fstOffsetNumeric"));
                return null;
            }
        }

        return this.observer;

    }

    @Override
    public ISchemaElement createSchemaElement() {

        // Get mandatory fields
        String name = this.getObserverName();
        if (name == null) {
            return null;
        }

        String surname = this.getSurname();
        if (surname == null) {
            return null;
        }

        this.observer = new Observer(name, surname);

        String contacts = this.contacts.getText();
        if ((contacts != null) && !("".equals(contacts))) {
            this.observer.addContact(contacts);
        }

        Map<String, String> accounts = ((AccountTableModel) this.accounts.getModel()).getAllEntries();
        if (!accounts.isEmpty()) {
            this.observer.setAccounts(accounts);
        }

        String fstO = this.fstOffset.getText();
        if ((fstO != null) && !("".equals(fstO))) {
            try {
                float fo = FloatUtil.parseFloat(fstO);
                this.observer.setFSTOffset(fo);
            } catch (NumberFormatException nfe) {
                this.createWarning(AbstractPanel.bundle.getString("panel.observer.warning.fstOffsetNumeric"));
                return null;
            }
        }

        return this.observer;

    }

    private String getObserverName() {

        String name = this.name.getText();
        if ((name == null) || ("".equals(name))) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observer.warning.noName"));
            return null;
        }

        return name;

    }

    private String getSurname() {

        String surname = this.surname.getText();
        if ((surname == null)
        // || ("".equals(surname)) DeepSkyLog requires this :-)
        ) {
            this.createWarning(AbstractPanel.bundle.getString("panel.observer.warning.noSurename"));
            return null;
        }

        return surname;

    }

    private void createPanel() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 5, 1);
        OMLabel Lname = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.name"), true);
        Lname.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.name"));
        gridbag.setConstraints(Lname, constraints);
        this.add(Lname);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 45, 1);
        this.name = new JTextField();
        this.name.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.name"));
        gridbag.setConstraints(this.name, constraints);
        this.add(this.name);

        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 5, 1);
        OMLabel Lsurname = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.surname"),
                SwingConstants.RIGHT, true);
        Lsurname.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.surname"));
        gridbag.setConstraints(Lsurname, constraints);
        this.add(Lsurname);
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 45, 1);
        this.surname = new JTextField();
        this.surname.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.surname"));
        gridbag.setConstraints(this.surname, constraints);
        this.add(this.surname);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 5, 1);
        OMLabel Lcontacts = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.contacts"), false);
        Lcontacts.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.contacts"));
        gridbag.setConstraints(Lcontacts, constraints);
        this.add(Lcontacts);
        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 3, 1, 45, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.contacts = new JTextArea(40, 3);
        this.contacts.addMouseListener(this);
        this.contacts.setLineWrap(true);
        this.contacts.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.contacts"));
        JScrollPane contactScroll = new JScrollPane(this.contacts);
        contactScroll.setMinimumSize(new Dimension(300, 60));
        gridbag.setConstraints(contactScroll, constraints);
        this.add(contactScroll);

        ConstraintsBuilder.buildConstraints(constraints, 0, 2, 1, 1, 5, 1);
        OMLabel LfstOffset = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.fstOffset"), false);
        LfstOffset.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.fstOffset"));
        gridbag.setConstraints(LfstOffset, constraints);
        this.add(LfstOffset);
        ConstraintsBuilder.buildConstraints(constraints, 1, 2, 1, 1, 30, 1);
        this.fstOffset = new JTextField();
        this.fstOffset.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.fstOffset"));
        gridbag.setConstraints(this.fstOffset, constraints);
        this.add(this.fstOffset);
        ConstraintsBuilder.buildConstraints(constraints, 2, 2, 1, 1, 15, 1);
        OMLabel LfstUnit = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.fstUnit"), false);
        LfstOffset.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.fstUnit"));
        gridbag.setConstraints(LfstUnit, constraints);
        this.add(LfstUnit);

        ConstraintsBuilder.buildConstraints(constraints, 0, 3, 1, 1, 50, 1);
        OMLabel Laccounts = new OMLabel(AbstractPanel.bundle.getString("panel.observer.label.accounts"), false);
        Laccounts.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.accounts"));
        gridbag.setConstraints(Laccounts, constraints);
        this.add(Laccounts);
        ConstraintsBuilder.buildConstraints(constraints, 0, 4, 4, 2, 50, 90);
        constraints.fill = GridBagConstraints.BOTH;
        String[] accountBoxItems = null;
        JComboBox accountBox = null;
        if ((this.observer == null) || (this.observer.getAccounts() == null)) {
            accountBoxItems = this.getAccountBoxItems(new HashMap<>());
            accountBox = new JComboBox(accountBoxItems);
            this.accounts = new JTable(new AccountTableModel(new HashMap<>(), this.isEditable(), accountBox));
        } else {
            Map<String, String> a = new HashMap<>(this.observer.getAccounts());
            accountBoxItems = this.getAccountBoxItems(a);
            accountBox = new JComboBox(accountBoxItems);
            this.accounts = new JTable(new AccountTableModel(a, this.isEditable(), accountBox));
        }
        TableColumn col = this.accounts.getColumnModel().getColumn(0);
        accountBox.setRenderer(new AccountListRenderer());
        col.setCellEditor(new DefaultCellEditor(accountBox));
        this.accounts.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.accounts"));
        this.accounts.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        JScrollPane scrollPane = new JScrollPane(this.accounts);
        gridbag.setConstraints(scrollPane, constraints);
        this.add(scrollPane);

        if (this.isEditable()) { // Only show buttons in edit mode
            ConstraintsBuilder.buildConstraints(constraints, 0, 6, 2, 1, 25, 1);
            this.addAccountRow = new JButton(AbstractPanel.bundle.getString("panel.observer.button.newAccount"));
            this.addAccountRow.addActionListener(this);
            this.addAccountRow.setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.newAccount"));
            gridbag.setConstraints(this.addAccountRow, constraints);
            this.add(this.addAccountRow);

            ConstraintsBuilder.buildConstraints(constraints, 2, 6, 2, 1, 25, 1);
            this.deleteAccountRow = new JButton(AbstractPanel.bundle.getString("panel.observer.button.deleteAccount"));
            this.deleteAccountRow.addActionListener(this);
            this.deleteAccountRow
                    .setToolTipText(AbstractPanel.bundle.getString("panel.observer.tooltip.deleteAccount"));
            gridbag.setConstraints(this.deleteAccountRow, constraints);
            this.add(this.deleteAccountRow);
        } /*
           * else { ConstraintsBuilder.buildConstraints(constraints, 0, 6, 4, 1, 45, 100); JLabel Lfill = new
           * JLabel(""); gridbag.setConstraints(Lfill, constraints); this.add(Lfill); }
           */

    }

    private String[] getAccountBoxItems(Map<String, String> accounts) {

        List<String> items = new ArrayList<>();

        // Add default items
        items.add(AccountListRenderer.EMPTY_LIST_ENTRY);

        items.add(Observer.ACCOUNT_AAVSO);
        items.add(Observer.ACCOUNT_DEEPSKYLOG);
        items.add(Observer.ACCOUNT_DSL);

        // Add user items
        String[] accountArray = (String[]) accounts.keySet().toArray(new String[] {});
        for (String s : accountArray) {

            // Filter out generic entires
            if (Observer.ACCOUNT_AAVSO.equals(s) || Observer.ACCOUNT_DEEPSKYLOG.equals(s)
                    || Observer.ACCOUNT_DSL.equals(s)) {
                continue;
            }

            // Found user created entry
            items.add(s);

        }

        return (String[]) items.toArray(new String[] {});

    }

}

class AccountTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 8689281626651639913L;

    private String[] accounts = new String[] {};
    private String[] userNames = new String[] {};

    private boolean tableEditable = false;
    private JComboBox box = null;

    public AccountTableModel(Map<String, String> map, boolean tableEditable, JComboBox box) {

        // Make sure both lists indicies are always equal
        if (map.size() > this.accounts.length) {
            this.accounts = new String[map.size()];
            this.userNames = new String[map.size()];
        }
        Iterator<String> iterator = map.keySet().iterator();
        String currentAccount = null;
        int i = 0;
        while (iterator.hasNext()) {
            currentAccount = iterator.next();
            accounts[i] = currentAccount;
            userNames[i++] = map.get(currentAccount);
        }

        this.tableEditable = tableEditable;
        this.box = box;

    }

    @Override
    public int getColumnCount() {

        return 2;

    }

    @Override
    public int getRowCount() {

        // Make sure both lists indicies are always equal
        return this.accounts.length;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
        case 0: {
            String value = this.accounts[rowIndex];
            if (("".equals(value)) || (value == null)) {
                this.box.setEditable(true);
                return AccountListRenderer.EMPTY_LIST_ENTRY;
            }
            return value;
        }
        case 1: {
            String value = this.userNames[rowIndex];
            if ("".equals(value)) {
                return "";
            }
            return value;
        }
        }

        return "";

    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        // Make sure both lists are always the same size
        if (col == 0) {
            if (!Arrays.asList(this.accounts).contains(value)) {
                this.accounts[row] = (String) value;
            }
        } else {
            this.userNames[row] = (String) value;
        }

        fireTableCellUpdated(row, col);

    }

    @Override
    public String getColumnName(int column) {

        String name = "";

        switch (column) {
        case 0: {
            name = AbstractPanel.bundle.getString("table.header.observerAccount.account");
            break;
        }
        case 1: {
            name = AbstractPanel.bundle.getString("table.header.observerAccount.username");
            break;
        }
        }

        return name;

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return this.tableEditable;

    }

    public Map<String, String> getAllEntries() {

        Map<String, String> result = new HashMap<>();

        String account = null;
        String user = null;
        for (int i = 0; i < this.accounts.length; i++) {
            account = this.accounts[i];
            if ((account == null) // Skip empty lines
                    || ("".equals(account)) || AccountListRenderer.EMPTY_LIST_ENTRY.equals(account)) {
                continue;
            }
            user = this.userNames[i];
            if ((user == null) // Skip empty lines
                    || ("".equals(user))) {
                continue;
            }

            result.put(account, user);
        }

        return result;

    }

    public void addNewRow() {

        // Expand arrays
        String[] newAccount = new String[this.accounts.length + 1];
        String[] newUsernames = new String[this.userNames.length + 1];
        System.arraycopy(this.accounts, 0, newAccount, 0, this.accounts.length);
        System.arraycopy(this.userNames, 0, newUsernames, 0, this.userNames.length);
        this.accounts = newAccount;
        this.userNames = newUsernames;

        // Remove all existing entries from box
        for (String account : this.accounts) {
            box.removeItem(account);
        }

        fireTableRowsInserted(this.accounts.length - 1, this.accounts.length);

    }

    public void deleteRow(int row) {

        if (row == -1) {
            return;
        }

        // Delete entry with help of ArrayList
        List<String> list = new ArrayList<>(Arrays.asList(this.accounts));
        list.remove(row);
        this.accounts = (String[]) list.toArray(new String[] {});
        List<String> list2 = new ArrayList<>(Arrays.asList(this.userNames));
        list2.remove(row);
        this.userNames = (String[]) list2.toArray(new String[] {});

        fireTableRowsDeleted(row - 1, row);

    }

}

class AccountListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 2594810794131546967L;

    public static final String EMPTY_LIST_ENTRY = AbstractPanel.bundle
            .getString("panel.observer.account.comboBox.enterValue");

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        Font f = null;
        if (value != null) {
            if (value.equals(AccountListRenderer.EMPTY_LIST_ENTRY)) {
                f = new Font("sansserif", Font.ITALIC, 12);
            } else {
                f = new Font("sansserif", Font.BOLD, 12);
            }
        }

        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        c.setFont(f);

        return c;

    }

}