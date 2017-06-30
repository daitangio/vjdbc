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

public class QuizApplet extends Applet {
    private static final long serialVersionUID = 3834594309310789936L;
    
    private CardLayout _cardLayout;
    private static final String CARD_HIGHSCORE = "highscore";
    private static final String CARD_QUESTIONS = "questions";
    private static final String CARD_ERROR = "error";

    // The Questions panel
    private JPanel _pnlQuestions;
    private JLabel _lblQuestion;
    private JRadioButton[] _rbAnswer = new JRadioButton[4];
    private ButtonGroup _grpAnswers;
    private JButton _btnAnswer;

    // The Highscore panel
    private JPanel _pnlHighscore;
    private JTable _tblHighscoreEntries;
    private HighscoreTableModel _tmHighscores;

    // The Error panel
    private JPanel _pnlError;
    private JLabel _lblError;

    private String _username;

    private boolean _initialized;

    private java.util.List _questions = new ArrayList();
    private int _questionPointer = 0;

    private class Question {
        Question(String q, String[] answers, int ca) {
            _question = q;
            _answers = answers;
            _correctAnswer = ca;
        }

        String _question;
        String _answers[] = new String[4];
        int _correctAnswer;
    }

    private class HighscoreEntry {
        HighscoreEntry(String user, int correctanswers) {
            _user = user;
            _correctAnswers = new Integer(correctanswers);
        }

        String _user;
        Integer _correctAnswers;
    }

    private class HighscoreTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 3689352126128272949L;
        
        private static final int COL_USER = 0;
        private static final int COL_CORRECTANSWERS = 1;
        private static final int COL_COUNT = 2;

        private ArrayList _entries = new ArrayList();

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < _entries.size()) {
                HighscoreEntry entry = (HighscoreEntry) _entries.get(rowIndex);
                switch (columnIndex) {
                    case COL_USER:
                        return entry._user;
                    case COL_CORRECTANSWERS:
                        return entry._correctAnswers;
                }
            }
            return null;
        }

        public int getColumnCount() {
            return COL_COUNT;
        }

        public int getRowCount() {
            return _entries.size();
        }

        public String getColumnName(int column) {
            switch (column) {
                case COL_USER:
                    return "User";
                case COL_CORRECTANSWERS:
                    return "Number of correct answers";
            }
            return null;
        }

        public void clear() {
            _entries.clear();
        }

        public void addEntry(String user, int correctanswers) {
            _entries.add(new HighscoreEntry(user, correctanswers));
        }

        public void updateTable() {
            fireTableDataChanged();
        }
    }

    public void init() {
        try {
            Class.forName("de.simplicit.vjdbc.VirtualDriver").newInstance();

            _cardLayout = new CardLayout();
            setLayout(_cardLayout);

            // Initialize the Questions panel
            _pnlQuestions = new JPanel();
            _pnlQuestions.setLayout(new GridBagLayout());

            _lblQuestion = new JLabel("Question");
            _lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
            _pnlQuestions.add(_lblQuestion, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

            _grpAnswers = new ButtonGroup();

            for (int i = 0; i < _rbAnswer.length; i++) {
                _rbAnswer[i] = new JRadioButton("Answer " + (i + 1));
                _rbAnswer[i].setActionCommand("" + (i + 1));
                _pnlQuestions.add(_rbAnswer[i], new GridBagConstraints(0, i + 1, 1, 1, 0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
                _grpAnswers.add(_rbAnswer[i]);
            }
            _btnAnswer = new JButton("Answer");
            _pnlQuestions.add(_btnAnswer, new GridBagConstraints(0, 5, 1, 1, 0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

            setBackground(_rbAnswer[0].getBackground());

            // Initialize the Highscore panel
            _pnlHighscore = new JPanel();
            _tmHighscores = new HighscoreTableModel();
            _pnlHighscore.setLayout(new BorderLayout());
            _pnlHighscore.add(new JLabel("Highscore"), BorderLayout.NORTH);
            _tblHighscoreEntries = new JTable(_tmHighscores);
            _pnlHighscore.add(_tblHighscoreEntries, BorderLayout.CENTER);

            // Finally the Error panel
            _pnlError = new JPanel();
            _pnlError.setLayout(new BorderLayout());
            _pnlError.add(new JLabel("Error"), BorderLayout.NORTH);
            _lblError = new JLabel("Error message");
            _lblError.setHorizontalAlignment(SwingConstants.CENTER);
            _lblError.setBackground(Color.RED);
            _pnlError.add(_lblError, BorderLayout.CENTER);

            add(_pnlQuestions, CARD_QUESTIONS);
            add(_pnlHighscore, CARD_HIGHSCORE);
            add(_pnlError, CARD_ERROR);
            _cardLayout.show(this, CARD_QUESTIONS);

            _btnAnswer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        answerQuestion();
                    } catch (SQLException e1) {
                        handleException(e1);
                    }
                }
            });

            _initialized = true;
        } catch (Exception e) {
            handleException(e);
        }
    }

    public void start() {
        if (_initialized) {
            _username = JOptionPane.showInputDialog(this, "Please enter your name:");
            try {
                if (checkUserExists()) {
                    JOptionPane.showMessageDialog(this, "You already answered the questions !");
                    displayHighscore();
                } else {
                    getQuestions();
                    displayQuestion();
                }
            } catch (Exception e) {
                handleException(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Applet is not correctly initialized !");
        }
    }

    private Connection openConnection() throws SQLException {
        URL codebase = getCodeBase();
        String vjdbcurl = "jdbc:vjdbc:servlet:" + codebase.toString() + "vjdbc,QuizDB";
        return DriverManager.getConnection(vjdbcurl);
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                handleException(e);
            }
        }
    }

    private boolean checkUserExists() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = openConnection();
            pstmt = conn.prepareStatement("select * from Answer where User = ?");
            pstmt.setString(1, _username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                pstmt = conn.prepareStatement("insert into Answer values (?, 0)");
                pstmt.setString(1, _username);
                pstmt.executeUpdate();
                return false;
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void getQuestions() throws SQLException {
        Connection conn = null;
        try {
            conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet questions = stmt.executeQuery("select question, answer1, answer2, answer3, answer4, correctanswer from Question");
            while (questions.next()) {
                _questions.add(new Question(questions.getString(1),
                        new String[]{
                            questions.getString(2),
                            questions.getString(3),
                            questions.getString(4),
                            questions.getString(5)},
                        questions.getInt(6)));
            }
        } finally {
            closeConnection(conn);
        }
    }

    private void displayQuestion() {
        Question question = (Question) _questions.get(_questionPointer);
        _lblQuestion.setText(question._question);
        for (int i = 0; i < _rbAnswer.length; i++) {
            _rbAnswer[i].setText(question._answers[i]);
        }
    }

    private void answerQuestion() throws SQLException {
        ButtonModel selectedButton = _grpAnswers.getSelection();
        if (selectedButton != null) {
            Question question = (Question) _questions.get(_questionPointer);
            int answer = Integer.parseInt(selectedButton.getActionCommand());
            if (question._correctAnswer == answer) {
                updateUser();
                JOptionPane.showMessageDialog(this, "Correct !");
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect, the correct answer is:\n" + question._answers[question._correctAnswer - 1]);
            }

            _questionPointer++;

            if (hasMoreQuestions()) {
                displayQuestion();
            } else {
                JOptionPane.showMessageDialog(this, "Quiz is over !");
                displayHighscore();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please choose an answer !");
        }
    }

    private boolean hasMoreQuestions() {
        return _questionPointer < _questions.size();
    }

    private void displayHighscore() throws SQLException {
        _btnAnswer.setEnabled(false);
        _cardLayout.show(this, CARD_HIGHSCORE);

        _tmHighscores.clear();

        Connection conn = null;
        try {
            conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select user, correctanswers from Answer order by correctanswers desc");
            while (rs.next()) {
                _tmHighscores.addEntry(rs.getString(1), rs.getInt(2));
            }

            _tmHighscores.updateTable();
        } finally {
            closeConnection(conn);
        }
    }

    private void updateUser() throws SQLException {
        Connection conn = null;
        try {
            conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement("update Answer set correctanswers = correctanswers + 1 where user = ?");
            pstmt.setString(1, _username);
            pstmt.executeUpdate();
        } finally {
            closeConnection(conn);
        }
    }

    private void handleException(Exception e) {
        _cardLayout.show(this, CARD_ERROR);
        _lblError.setText(e.getMessage());
        e.printStackTrace();
    }
}
