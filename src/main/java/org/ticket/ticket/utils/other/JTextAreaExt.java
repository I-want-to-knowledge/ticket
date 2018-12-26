package org.ticket.ticket.utils.other;

import javax.swing.JTextArea;

public class JTextAreaExt extends JTextArea {
	private static final long serialVersionUID = 3496268324617843183L;

	@Override
	public void append(String str) {
		super.append(str);
		super.paintImmediately(super.getBounds());
		super.setCaretPosition(super.getDocument().getLength());
	}
}
