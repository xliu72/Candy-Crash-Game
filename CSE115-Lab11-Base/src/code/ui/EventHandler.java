package code.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;

import code.model.Model;

public class EventHandler implements ActionListener {

	private Model _model;
	private int _row;
	private int _col;
	private ArrayList<ArrayList<JButton>> _viewBoard;
	
	public EventHandler(Model m, int r, int c) {
		_model = m;
		_row = r;
		_col = c;

	}
	
	@Override public void actionPerformed(ActionEvent e) {
		_model.select(_row,_col);
	}

}
