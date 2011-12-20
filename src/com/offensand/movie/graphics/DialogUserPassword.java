package com.offensand.movie.graphics;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DialogUserPassword extends JDialog implements ActionListener {

  private static final long serialVersionUID = - 1728069940703804132L;
  private JTextField        tfUser;
  private JPasswordField    tfPassword;
  private JButton           confirm;
  private boolean           cancelled        = true;

  public DialogUserPassword(Window window) {
    super(window, "Input Required", JDialog.DEFAULT_MODALITY_TYPE);
    tfUser = new JTextField();
    tfPassword = new JPasswordField();
    confirm = new JButton("Confirm");
    JButton cancel = new JButton("Cancel");
    confirm.addActionListener(this);
    cancel.addActionListener(this);
    JPanel panel1 = new JPanel(new FlowLayout());
    JPanel panel2 = new JPanel(new FlowLayout());
    JPanel panel3 = new JPanel(new GridLayout(1, 2));
    setLayout(new GridLayout(3, 1));
    JLabel labeluser = new JLabel("User:");
    JLabel labelpassword = new JLabel("Password:");
    panel1.add(labeluser);
    panel1.add(tfUser);
    panel2.add(labelpassword);
    panel2.add(tfPassword);
    panel3.add(confirm);
    panel3.add(cancel);
    add(panel1);
    add(panel2);
    add(panel3);
  }

  private void requirePassword() {
    setVisible(true);
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public String getUser() {
    if ((tfUser.getText() == null ) || tfUser.getText().equals("")) {
      requirePassword();
    } else {
      cancelled = false;
    }
    return tfUser.getText();
  }

  public String getPassword() {
    if ((tfPassword.getPassword() == null )
        || String.copyValueOf(tfPassword.getPassword()).equals("")) {
      requirePassword();
    } else {
      cancelled = false;
    }
    return String.copyValueOf(tfPassword.getPassword());
  }

  public void setUser(String user) {
    tfUser.setText(user);
  }

  public void setPassword(String password) {
    tfPassword.setText(password);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    setVisible(false);
    if (e.getSource() == confirm) {
      cancelled = false;
    } else {
      cancelled = true;
    }
  }
}
