/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qspin.qtaste.javagui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 * @author lvboque
 */
public class JavaGUI extends JMXAgent implements JavaGUIMBean,
		KeyEventDispatcher {

	private static int count = 0;
	private Frame frame;
	private Window window;
	private JPanel panel;
	private JButton b;
	private MouseEventsCatcher catcher;

	public JavaGUI() {
		System.out.println("Initializing javagui agent!");
		init();
		catcher = new MouseEventsCatcher();
	}

	private Component getComponentByName(String name) {
		Frame[] frames = Frame.getFrames();
		for (int f = 0; f < frames.length; f++) {
			Frame frame = frames[f];
			if (frame.getName().equals(name)) {
				return frame;
			}
			Window[] windows = frame.getWindows();
			for (int w = 0; w < windows.length; w++) {
				Window window = windows[w];
				if (window.getName().equals(name)) {
					return window;
				}
				Component c = lookForComponent(name, window.getComponents());
				if (c != null) {
					// System.out.println("getComponentByName is returning " +
					// c);

					return c;
				}
			}
		}
		return null;
	}

	private Component lookForComponent(String name, Component[] components) {
		for (int c = 0; c < components.length; c++) {

			String componentName = components[c].getName();
			// System.out.println("browsing " + components[c].toString());
			// System.out.println("name=" + componentName);
			if (componentName != null && componentName.equals(name)) {
				System.out.println("Component:" + name + " is found!");
				return components[c];
			} else {
				if (components[c] instanceof Container) {
					Component result = lookForComponent(name,
							((Container) components[c]).getComponents());
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	/*
	 * public boolean clickOnButton(String name) { Component c =
	 * getComponentByName(name); if (c == null) { return false; } if (c
	 * instanceof AbstractButton) { ((AbstractButton) c).doClick(); } return
	 * true; }
	 */

	public String[] listComponents() {
		ArrayList<String> list = new ArrayList<String>();

		Frame[] frames = Frame.getFrames();
		for (int f = 0; f < frames.length; f++) {
			Frame frame = frames[f];
			if (frame.getName() != null) {
				list.add(frame.getName());
			}
			Window[] windows = frame.getWindows();
			for (int w = 0; w < windows.length; w++) {
				Window window = windows[w];
				if (window.getName() != null) {
					list.add(window.getName());
				}
				list.addAll(browseComponent(window.getComponents()));
			}
		}
		String[] result = (String[]) list.toArray(new String[0]);
		return result;
	}

	private ArrayList<String> browseComponent(Component[] components) {
		ArrayList<String> list = new ArrayList<String>();
		for (int c = 0; c < components.length; c++) {
			String componentName = components[c].getName();
			// System.out.println("browsing " + components[c].toString());
			// System.out.println("name=" + componentName);
			if (componentName != null) {
				System.out.println("Component:" + componentName + " is found!");
				if (!componentName.startsWith("null."))
					list.add(componentName);
			}
			if (components[c] instanceof Container) {
				list.addAll(browseComponent(((Container) components[c])
						.getComponents()));
			}
		}
		return list;
	}

	public boolean keyPressedOnComponent(String componentName, int vkEvent) {
		Component c = getComponentByName(componentName);
		if (c == null) {
			return false;
		}

		System.out.println("Location on screen:" + c.getLocationOnScreen());
		KeyEvent event = new KeyEvent(c, KeyEvent.KEY_PRESSED,
				System.currentTimeMillis(), 0, vkEvent);
		try {

			java.lang.reflect.Field f = AWTEvent.class
					.getDeclaredField("focusManagerIsDispatching");
			f.setAccessible(true);
			f.set(event, Boolean.TRUE);
			c.dispatchEvent(event);
		} catch (Exception exc) {
			System.out.println("Exception sending event" + exc);
			return false;
		}

		return true;
	}

	public boolean clickOnButton(String componentName) {
		return clickOnButton(componentName, 1);
	}

	public boolean clickOnButton(String componentName, int pressTime) {
		Component c = getComponentByName(componentName);
		if (c == null) {
			return false;
		}

		System.out.println("Location on screen:" + c.getLocationOnScreen());
		// KeyEvent event = new KeyEvent(c, KeyEvent.KEY_PRESSED,
		// System.currentTimeMillis(), 0, KeyEvent.VK_SPACE);
		try {
			// c.dispatchEvent(event);
			AbstractButton btn = (AbstractButton) c;
			btn.doClick(pressTime);
			// java.lang.reflect.Field f =
			// AWTEvent.class.getDeclaredField("focusManagerIsDispatching");
			// f.setAccessible(true);
			// f.set(event, Boolean.TRUE);
			// c.dispatchEvent(event);
		} catch (Exception exc) {
			System.out.println("Exception sending event" + exc);
			return false;
		}

		return true;
	}

	public boolean isEnabled(String componentName) {
		Component c = getComponentByName(componentName);
		return c.isEnabled();
	}

	public String getButtonText(String componentName) {
		AbstractButton c = (AbstractButton) getComponentByName(componentName);
		return c.getText();
	}

	public void takeSnapShot(String componentName, String fileName) {
		Component c = getComponentByName(componentName);

		Dimension size = c.getSize();
		BufferedImage myImage = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = myImage.createGraphics();
		c.paint(g2);
		try {
			File file = new File(fileName);
			file.createNewFile();
			System.out.println("creating empty file");
			ImageIO.write(myImage, "jpg", file);
		} catch (Exception e) {
			System.out.println("Error saving snapshot " + fileName + ":" + e);
		}
	}

	public String getText(String componentName) {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JLabel) {
				return ((JLabel) c).getText();
			} else if (c instanceof JTextComponent) {
				return ((JTextComponent) c).getText();
			}
		}
		return null;
	}

	public boolean setText(String componentName, String value) {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JLabel) {
				((JLabel) c).setText(value);
				return true;
			} else if (c instanceof JTextComponent) {
				((JTextComponent) c).setText(value);
				return true;
			}
		}
		return false;
	}

	public boolean selectComponant(String componentName, boolean value) {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JCheckBox) {
				((JCheckBox) c).setSelected(value);
				return true;
			} else if (c instanceof JRadioButton) {
				((JRadioButton) c).setSelected(value);
				return true;
			}
		}
		return false;
	}

	public boolean selectValue(String componentName, String value) {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JCheckBox || c instanceof JRadioButton) {
				return selectComponant(componentName,
						Boolean.parseBoolean(value));
			} else if (c instanceof JComboBox) {
				JComboBox combo = (JComboBox) c;
				for (int i = 0; i < combo.getItemCount(); i++) {
					if (combo.getItemAt(i).toString().equals(value)) {
						combo.setSelectedIndex(i);
						return true;
					}
				}
			} else if (c instanceof JList) {
				JList list = (JList) c;
				for (int i = 0; i < list.getModel().getSize(); i++) {
					if (list.getModel().getElementAt(i).toString()
							.equals(value)) {
						list.setSelectedIndex(i);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean selectIndex(String componentName, int index) {
		Component c = getComponentByName(componentName);
		if (c != null) {
			if (c instanceof JComboBox) {
				JComboBox combo = (JComboBox) c;
				if (combo.getItemCount() > index) {
					combo.setSelectedIndex(index);
					return true;
				}
			} else if (c instanceof JList) {
				JList list = (JList) c;
				if (list.getModel().getSize() > index) {
					list.setSelectedIndex(index);
					return true;
				}
			}
		}
		return false;
	}

	private void getAllKeys() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(this);

	}

	public static void premain(String agentArgs, Instrumentation inst) {
		JavaGUI javagui = new JavaGUI();

	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		System.out.println("JavaGUI received: " + e);
		return true;
	}

	public class MouseEventsCatcher extends MouseAdapter {

		public void mousePressed(MouseEvent event) {
			System.out.println(event.toString());
		}
	}

	@Override
	public boolean selectNode(String componentName, String nodeName, String nodeSeparator) {
		String[] nodeNames = nodeName.split(nodeSeparator);
		Component c = getComponentByName(componentName);
		System.out.println("main condition");
		if (c != null && c instanceof JTree && nodeNames.length > 0) {
			TreeModel model = ((JTree) c).getModel();
			Object node = model.getRoot();
			Object[] path = new Object[nodeNames.length];
			System.out.println("root : " + node);
			System.out.println("root expected : " + nodeNames[0]);
			if (node.toString().equals(nodeNames[0]) ) {
				System.out.println("Root found!!");
				path[0] = node;

				for (int i = 1; i < nodeNames.length; i++) {
					System.out.println("Search " + nodeNames[i] + " in " + node.toString() );
					for ( int childIndex = 0; childIndex < model.getChildCount(node); childIndex++)
					{
						Object child = model.getChild(node, childIndex);
						if ( child.toString().equals(nodeNames[i])) {
							node = child;
							path[i] = node;
							break;
						}
					}
					if ( path[i] == null )
					{
						return false;
					}
				}
				((JTree)c).setSelectionPath(new TreePath(path));
				((JTree)c).expandPath(new TreePath(path));
				((JTree)c).setExpandsSelectedPaths(true);
				
				return true;
			}
		}
		return false;
	}
	// Todo: getColor, awt?

}