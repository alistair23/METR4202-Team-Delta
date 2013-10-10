package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Benjamin Rose & Ben Merange
 *
 * This class creates a console based off JPanel for implementation in a GUI.
 * 
 */

public class ConsolePanel extends JPanel{
	
	static GridBagConstraints gc = new GridBagConstraints();
	GridBagLayout g = new GridBagLayout();
	
	JTextArea con = new JTextArea();
	JScrollPane sp = new JScrollPane();
	
    public ConsolePanel(){
    	
    	this.setLayout(g);
    	
		con.setLineWrap(true);
		con.setWrapStyleWord(true);
	
		sp.getViewport().setView(con);
	
		gc.fill = GridBagConstraints.BOTH;
	    gc.anchor = GridBagConstraints.CENTER;
	    gc.gridx = 0;
	    gc.gridy = 0;
	    gc.weightx = 1;
	    gc.weighty = 1;
	    gc.gridwidth = 1;
	    gc.gridheight = 1;
	    gc.ipadx = 100;
	    gc.ipady = 100;
		
		this.add(sp,gc);
    }
    
    public static void main(String s[]) {
    	
    	//initialise the Console
    	ConsolePanel cp = new ConsolePanel();
    	
    	//add some example text    	
    	cp.addln("Adding a new line.");
    	cp.add("Adding a text on same line.");
    	cp.newln();
    	cp.newln();
    	cp.add("two new lines added above.");
    	
    	//create a jframe to display the console
    	JFrame jf = new JFrame();
    	jf.setSize(new Dimension(500,500));
    	jf.add(cp,gc);
    	jf.setVisible(true);
    }
    
    public void addln(String str){
    	con.append(str+"\n");
    	con.setCaretPosition(con.getDocument().getLength());
    }

    public void add(String str){
    	con.append(str);
    	con.setCaretPosition(con.getDocument().getLength());
    }
    
    public void newln(){
    	con.append("\n");
    	con.setCaretPosition(con.getDocument().getLength());
    }
    
    public void wipe(){
    	con.setText("");
    	con.setCaretPosition(con.getDocument().getLength());
    }
}
