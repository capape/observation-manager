/* ====================================================================
 * /util/DatePicker.java
 * 
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import de.lehmannet.om.util.DateConverter;

public class DatePicker extends JDialog {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private final JButton[] fields = new JButton[37];

    private JLabel monthYearLabel = null;

    private int day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
    private int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    private int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

    private Calendar calendar = null;
    private TimeZone timeZone = null;

    public DatePicker(JFrame parent, String title) {

        super(parent, true);

        this.initDialog();
        this.setDates();

        super.setTitle(title);
        super.setSize(500, 330);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(null);

        super.setVisible(true);
        super.setResizable(false);

    }

    public DatePicker(JFrame parent, String title, TimeZone timeZone) {

        super(parent, true);

        this.timeZone = timeZone;

        this.initDialog();
        this.setDates();

        super.setTitle(title);
        super.setSize(500, 250);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(null);

        super.setVisible(true);
        super.setResizable(false);

    }

    public DatePicker(JFrame parent, String title, Calendar cal) {

        super(parent, true);

        this.day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        this.month = cal.get(java.util.Calendar.MONTH);
        this.year = cal.get(java.util.Calendar.YEAR);

        this.initDialog();
        this.setDates();

        super.setTitle(title);
        super.setSize(500, 250);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(null);

        super.setVisible(true);
        super.setResizable(false);

    }

    public DatePicker(JFrame parent, String title, Calendar cal, TimeZone timeZone) {

        super(parent, true);

        this.timeZone = timeZone;

        this.day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        this.month = cal.get(java.util.Calendar.MONTH);
        this.year = cal.get(java.util.Calendar.YEAR);

        this.initDialog();
        this.setDates();

        super.setTitle(title);
        super.setSize(500, 250);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(null);

        super.setVisible(true);
        super.setResizable(false);

    }

    public Calendar getDate() {

        // Calendar might be created by JulianDateDialog
        if (this.calendar != null) {
            return this.calendar;
        }

        return new GregorianCalendar(this.year, this.month, this.day);

    }

    public String getDateString() {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        df.setCalendar(this.getDate());
        return df.format(this.getDate().getTime());

    }

    private void initDialog() {

        // Set table header
        String[] header = { this.bundle.getString("datePicker.sun"), this.bundle.getString("datePicker.mon"),
                this.bundle.getString("datePicker.tue"), this.bundle.getString("datePicker.wed"),
                this.bundle.getString("datePicker.thu"), this.bundle.getString("datePicker.fri"),
                this.bundle.getString("datePicker.sat") };

        JPanel headerPanel = new JPanel(new GridLayout(1, 7));
        for (String s : header) {
            headerPanel.add(new JLabel(s, SwingConstants.CENTER));
        }
        super.getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Set day matrix
        JPanel centerPanel = new JPanel(new GridLayout(6, 7));
        JButton currentButton = null;
        for (int x = 0; x < this.fields.length; x++) {
            currentButton = new JButton();
            currentButton.setFocusPainted(false);
            currentButton.addActionListener(event -> {
                if (event.getSource() instanceof JButton) {
                    DatePicker.this.day = Integer.parseInt(((JButton) event.getSource()).getText());
                    DatePicker.this.dispose();
                }
            });
            this.fields[x] = currentButton;
            centerPanel.add(this.fields[x]);
        }
        super.getContentPane().add(centerPanel, BorderLayout.CENTER);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel footerPanel = new JPanel(gridbag);

        JButton prevYearButton = new JButton(this.bundle.getString("datePicker.button.previousYear"));
        prevYearButton.addActionListener(ae -> {
            DatePicker.this.year--;
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + (DatePicker.this.month + 1) + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(prevYearButton, constraints);
        footerPanel.add(prevYearButton);

        JButton prevButton = new JButton(this.bundle.getString("datePicker.button.previous"));
        prevButton.addActionListener(ae -> {
            if (DatePicker.this.month == 0) {
                DatePicker.this.month = 11;
                DatePicker.this.year--;
            } else {
                DatePicker.this.month--;
            }
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + (DatePicker.this.month + 1) + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(prevButton, constraints);
        footerPanel.add(prevButton);

        this.monthYearLabel = new JLabel("" + (this.month + 1) + "/" + this.year, SwingConstants.CENTER);
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(this.monthYearLabel, constraints);
        footerPanel.add(monthYearLabel);

        JButton nextButton = new JButton(this.bundle.getString("datePicker.button.next"));
        nextButton.addActionListener(ae -> {
            if (DatePicker.this.month == 11) {
                DatePicker.this.month = 0;
                DatePicker.this.year++;
            } else {
                DatePicker.this.month++;
            }
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + (DatePicker.this.month + 1) + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(nextButton, constraints);
        footerPanel.add(nextButton);

        JButton nextYearButton = new JButton(this.bundle.getString("datePicker.button.nextYear"));
        nextYearButton.addActionListener(ae -> {
            DatePicker.this.year++;
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + (DatePicker.this.month + 1) + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(nextYearButton, constraints);
        footerPanel.add(nextYearButton);

        JButton julianDateButton = new JButton(this.bundle.getString("datePicker.button.julianDate"));
        julianDateButton.addActionListener(ae -> {

            JulianDateDialog jdd = new JulianDateDialog(DatePicker.this, DatePicker.this.timeZone);
            DatePicker.this.calendar = jdd.getCalendar();
            if (DatePicker.this.calendar != null) {
                DatePicker.this.dispose();
            }

        });
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 5, 1, 100, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(julianDateButton, constraints);
        footerPanel.add(julianDateButton);

        super.getContentPane().add(footerPanel, BorderLayout.SOUTH);

        super.pack();

    }

    private void setDates() {

        for (JButton field : fields) {
            field.setText("");
            field.setEnabled(false);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(this.year, this.month, 1);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        Calendar currentDate = Calendar.getInstance();
        int currentMonth = currentDate.get(java.util.Calendar.MONTH);
        int currentYear = currentDate.get(java.util.Calendar.YEAR);
        int currentDay = currentDate.get(java.util.Calendar.DAY_OF_MONTH);

        this.day = 1;
        for (int x = dayOfWeek - 1; day <= daysInMonth; x++, day++) {
            if ((this.year == currentYear) && (this.month == currentMonth) && (this.day == currentDay)) {
                fields[x].setForeground(Color.RED);
            } else {
                fields[x].setForeground(Color.BLACK);
            }
            fields[x].setText(String.valueOf(this.day));
            fields[x].setEnabled(true);
        }

        super.getContentPane().repaint();

    }

}

class JulianDateDialog extends JDialog implements ActionListener {

    private final PropertyResourceBundle bundle = (PropertyResourceBundle) ResourceBundle
            .getBundle("ObservationManager", Locale.getDefault());

    private Calendar calendar = null;
    private TimeZone timeZone = null;

    private JButton cancel = null;
    private JButton ok = null;
    private JTextField jdString = null;

    public JulianDateDialog(DatePicker dp, TimeZone tz) {

        super(dp, true);

        this.timeZone = tz;

        super.setTitle(this.bundle.getString("julianDateDialog.title"));
        super.setSize(350, 90);
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        super.setLocationRelativeTo(dp);

        this.initDialog();

        this.setVisible(true);
        this.pack();

    }

    public Calendar getCalendar() {

        return this.calendar;

    }

    // --------------
    // ActionListener ---------------------------------------------------------
    // --------------

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if (source instanceof JButton) {
            if (source.equals(this.cancel)) {
                this.dispose();
            } else if (source.equals(this.ok)) {
                String jdString = this.jdString.getText();
                try {
                    double jd = Double.parseDouble(jdString);
                    this.calendar = DateConverter.toGregorianDate(jd, this.timeZone);
                } catch (IllegalArgumentException nfe) {
                    JOptionPane.showMessageDialog(this, this.bundle.getString("julianDateDialog.warning.wrongFormat"),
                            this.bundle.getString("title.warning"), JOptionPane.WARNING_MESSAGE);
                }
                this.dispose();
            }
        }

    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        super.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel jdLabel = new JLabel(this.bundle.getString("julianDateDialog.label.JDField"));
        jdLabel.setToolTipText(this.bundle.getString("julianDateDialog.tooltip.JDField"));
        gridbag.setConstraints(jdLabel, constraints);
        super.getContentPane().add(jdLabel);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.jdString = new JTextField();
        String now = "" + DateConverter.toJulianDate(Calendar.getInstance());
        now = now.substring(0, now.indexOf('.'));
        this.jdString.setText(now);
        jdString.setEditable(true);
        jdString.setToolTipText(this.bundle.getString("julianDateDialog.tooltip.JDField"));
        gridbag.setConstraints(jdString, constraints);
        super.getContentPane().add(jdString);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(this.bundle.getString("dialog.button.ok"));
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        super.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.bundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        super.getContentPane().add(this.cancel);

    }

}