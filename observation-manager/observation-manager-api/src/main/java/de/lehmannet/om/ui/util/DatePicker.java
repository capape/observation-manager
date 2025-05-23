/*
 * ====================================================================
 * /util/DatePicker.java
 * (c) by Dirk Lehmann
 * ====================================================================
 */

package de.lehmannet.om.ui.util;

import de.lehmannet.om.util.DateManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class DatePicker extends JDialog {

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private final JButton[] fields = new JButton[38];
    private final DateManager dateManager;

    private JLabel monthYearLabel = null;

    private ZonedDateTime date;

    private int day;
    private int month;
    private int year;

    public DatePicker(JFrame parent, String title, DateManager dateManager) {

        super(parent, true);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
        this.dateManager = dateManager;
        createDatePicker(title, now);
    }

    public DatePicker(JFrame parent, String title, ZonedDateTime date, DateManager dateManager) {

        super(parent, true);
        this.dateManager = dateManager;

        createDatePicker(title, date);
    }

    private void createDatePicker(String title, ZonedDateTime date) {

        this.date = date;
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();
        this.initDialog();

        this.setDates();

        this.setTitle(title);
        this.setSize(500, 250);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public String getDateString() {

        return this.dateManager.zonedDateTimeToStringWithHour(this.date);
    }

    private void initDialog() {

        // Set table header
        String[] header = {
            this.bundle.getString("datePicker.sun"),
            this.bundle.getString("datePicker.mon"),
            this.bundle.getString("datePicker.tue"),
            this.bundle.getString("datePicker.wed"),
            this.bundle.getString("datePicker.thu"),
            this.bundle.getString("datePicker.fri"),
            this.bundle.getString("datePicker.sat")
        };

        JPanel headerPanel = new JPanel(new GridLayout(1, 7));
        for (String s : header) {
            headerPanel.add(new JLabel(s, SwingConstants.CENTER));
        }
        this.getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Set day matrix
        JPanel centerPanel = new JPanel(new GridLayout(6, 7));

        for (int x = 0; x < this.fields.length; x++) {
            JButton currentButton = createDayButton();
            this.fields[x] = currentButton;
            centerPanel.add(this.fields[x]);
        }
        this.getContentPane().add(centerPanel, BorderLayout.CENTER);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel footerPanel = new JPanel(gridbag);

        JButton prevYearButton = createPreviousYearButton(gridbag, constraints);
        footerPanel.add(prevYearButton);

        JButton prevButton = createPreviousMonthButton(gridbag, constraints);
        footerPanel.add(prevButton);

        this.monthYearLabel = createMonthYearLabel(gridbag, constraints);
        footerPanel.add(monthYearLabel);

        JButton nextButton = createNextMonthButton(gridbag, constraints);
        footerPanel.add(nextButton);

        JButton nextYearButton = createNextYearButton(gridbag, constraints);
        footerPanel.add(nextYearButton);

        JButton julianDateButton = createJulianDateButton(gridbag, constraints, this.dateManager);
        footerPanel.add(julianDateButton);

        this.getContentPane().add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createDayButton() {
        JButton currentButton = new JButton();
        currentButton.setFocusPainted(false);
        currentButton.addActionListener(dayButtonActionListener());
        return currentButton;
    }

    private ActionListener dayButtonActionListener() {
        return event -> {
            if (event.getSource() instanceof JButton) {
                DatePicker.this.day = Integer.parseInt(((JButton) event.getSource()).getText());

                // @formatter:off
                DatePicker.this.date = DatePicker.this
                        .date
                        .withDayOfMonth(DatePicker.this.day)
                        .withMonth(DatePicker.this.month)
                        .withYear(DatePicker.this.year);
                // @formatter:on

                DatePicker.this.dispose();
            }
        };
    }

    private JLabel createMonthYearLabel(GridBagLayout gridbag, GridBagConstraints constraints) {
        final JLabel label = new JLabel("" + this.month + "/" + this.year, SwingConstants.CENTER);
        ConstraintsBuilder.buildConstraints(constraints, 2, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(label, constraints);
        return label;
    }

    private JButton createJulianDateButton(
            GridBagLayout gridbag, GridBagConstraints constraints, DateManager dateManager) {
        JButton julianDateButton = new JButton(this.bundle.getString("datePicker.button.julianDate"));
        julianDateButton.addActionListener(ae -> {
            JulianDateDialog jdd = new JulianDateDialog(DatePicker.this, dateManager);
            DatePicker.this.date = jdd.getZonedDateTime();
            if (DatePicker.this.date != null) {
                DatePicker.this.dispose();
            }
        });
        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 5, 1, 100, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(julianDateButton, constraints);
        return julianDateButton;
    }

    private JButton createNextYearButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        JButton nextYearButton = new JButton(this.bundle.getString("datePicker.button.nextYear"));
        nextYearButton.addActionListener(ae -> {
            DatePicker.this.year++;
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + DatePicker.this.month + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 4, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(nextYearButton, constraints);
        return nextYearButton;
    }

    private JButton createNextMonthButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        JButton nextButton = new JButton(this.bundle.getString("datePicker.button.next"));
        nextButton.addActionListener(ae -> {
            if (DatePicker.this.month == 12) {
                DatePicker.this.month = 1;
                DatePicker.this.year++;
            } else {
                DatePicker.this.month++;
            }
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + DatePicker.this.month + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 3, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(nextButton, constraints);
        return nextButton;
    }

    private JButton createPreviousYearButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        JButton prevYearButton = new JButton(this.bundle.getString("datePicker.button.previousYear"));
        prevYearButton.addActionListener(ae -> {
            DatePicker.this.year--;
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + DatePicker.this.month + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(prevYearButton, constraints);
        return prevYearButton;
    }

    private JButton createPreviousMonthButton(GridBagLayout gridbag, GridBagConstraints constraints) {
        JButton prevButton = new JButton(this.bundle.getString("datePicker.button.previous"));
        prevButton.addActionListener(ae -> {
            if (DatePicker.this.month == 1) {
                DatePicker.this.month = 12;
                DatePicker.this.year--;
            } else {
                DatePicker.this.month--;
            }
            DatePicker.this.setDates();
            DatePicker.this.monthYearLabel.setText("" + DatePicker.this.month + "/" + DatePicker.this.year);
        });
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 20, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(prevButton, constraints);
        return prevButton;
    }

    private void setDates() {

        for (JButton field : fields) {
            field.setText("");
            field.setEnabled(false);
        }

        YearMonth yearMonth = YearMonth.of(this.year, this.month);
        int daysInMonth = yearMonth.lengthOfMonth();
        int dayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();

        ZonedDateTime now = ZonedDateTime.now();

        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        int currentDay = now.getDayOfMonth();

        this.day = 1;
        for (int buttonDayIndex = dayOfWeek; day <= daysInMonth; buttonDayIndex++, day++) {
            if ((this.year == currentYear) && (this.month == currentMonth) && (this.day == currentDay)) {
                fields[buttonDayIndex].setForeground(Color.RED);
            } else {
                fields[buttonDayIndex].setForeground(Color.BLACK);
            }
            fields[buttonDayIndex].setText(String.valueOf(this.day));
            fields[buttonDayIndex].setEnabled(true);
        }

        this.getContentPane().repaint();
    }
}

class JulianDateDialog extends JDialog implements ActionListener {

    private final ResourceBundle bundle =
            LocaleToolsFactory.appInstance().getBundle("ObservationManager", Locale.getDefault());

    private ZonedDateTime zonedDateTime;
    private JButton cancel;
    private JButton ok;
    private JTextField jdString;
    private DatePicker datePicker;
    private DateManager dateManager;

    public JulianDateDialog(DatePicker dp, DateManager dateManager) {

        super(dp, true);

        this.datePicker = dp;
        this.dateManager = dateManager;

        this.setTitle(this.bundle.getString("julianDateDialog.title"));
        this.setSize(350, 90);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(dp);

        this.initDialog();

        this.setVisible(true);
    }

    public ZonedDateTime getZonedDateTime() {

        return this.zonedDateTime;
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
                    double jd = this.dateManager.parseAstronomicalJulianDate(jdString);
                    this.zonedDateTime = this.dateManager.fromAstronomicalJulianDate(jd, ZoneId.systemDefault());

                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(
                            this,
                            this.bundle.getString("julianDateDialog.warning.wrongFormat"),
                            this.bundle.getString("title.warning"),
                            JOptionPane.WARNING_MESSAGE);
                } catch (IllegalArgumentException nfe) {
                    JOptionPane.showMessageDialog(
                            this,
                            this.bundle.getString("julianDateDialog.warning.wrongFormat"),
                            this.bundle.getString("title.warning"),
                            JOptionPane.WARNING_MESSAGE);
                } catch (ParseException e1) {
                    JOptionPane.showMessageDialog(
                            this,
                            this.bundle.getString("julianDateDialog.warning.wrongFormat"),
                            this.bundle.getString("title.warning"),
                            JOptionPane.WARNING_MESSAGE);
                }
                this.dispose();
            }
        }
    }

    private void initDialog() {

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.getContentPane().setLayout(gridbag);

        ConstraintsBuilder.buildConstraints(constraints, 0, 0, 1, 1, 10, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        JLabel jdLabel = new JLabel(this.bundle.getString("julianDateDialog.label.JDField"));
        jdLabel.setToolTipText(this.bundle.getString("julianDateDialog.tooltip.JDField"));
        gridbag.setConstraints(jdLabel, constraints);
        this.getContentPane().add(jdLabel);
        ConstraintsBuilder.buildConstraints(constraints, 1, 0, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.jdString = new JTextField();

        String julianDate = this.dateManager.formatAsAstronomicalJulianDate(datePicker.getDate());

        this.jdString.setText(julianDate);
        jdString.setEditable(true);
        jdString.setToolTipText(this.bundle.getString("julianDateDialog.tooltip.JDField"));
        gridbag.setConstraints(jdString, constraints);
        this.getContentPane().add(jdString);

        ConstraintsBuilder.buildConstraints(constraints, 0, 1, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.ok = new JButton(this.bundle.getString("dialog.button.ok"));
        this.ok.addActionListener(this);
        gridbag.setConstraints(this.ok, constraints);
        this.getContentPane().add(this.ok);

        ConstraintsBuilder.buildConstraints(constraints, 1, 1, 1, 1, 30, 50);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        this.cancel = new JButton(this.bundle.getString("dialog.button.cancel"));
        this.cancel.addActionListener(this);
        gridbag.setConstraints(this.cancel, constraints);
        this.getContentPane().add(this.cancel);
    }
}
