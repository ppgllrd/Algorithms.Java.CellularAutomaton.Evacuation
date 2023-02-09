package gui;

import javax.swing.*;

public class Frame extends JFrame {
  public Frame(Canvas canvas) {
    super();
    this.add(canvas);
    this.pack();
    this.setResizable(false);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}