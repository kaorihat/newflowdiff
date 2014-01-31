package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.event.*;
import javax.swing.*;



/*
 * MenuBarを構築する
 * @author itot
 */
public class MenuBar extends JMenuBar  {

	/* var */
	/*
	 * Note for programmer: better avoid using 'public' access here, 
	 * rather preferred to use get*() methods with 'public' access.
	 */
	// file menu 
	public JMenu fileMenu;	//file
	public JMenuItem openMenuItem;	//open...
	public JMenuItem optionMenuItem;	//
	public JMenuItem outputTemplateMenuItem;	//outputTemplete...
	public JMenuItem readTemplateMenuItem;	//readTemplete...
	public JMenuItem exitMenuItem;	//exit

	// appearance menu 
	public JMenu appearanceMenu;	//appearance
	public JMenuItem appearanceMenuItem;	//appearance...
	public JMenuItem tableAttributeMenuItem;	//tableAttribute...
	public JMenuItem parametersMenuItem;

	// Listener
	MenuItemListener ml;
	
	// component
	Canvas canvas = null;

	
	/**
	 * Constructor
	 * @param withReadyMadeMenu 通常はtrue
	 */
	public MenuBar(boolean withReadyMadeMenu) {
		super();
		if (withReadyMadeMenu) {
			buildFileMenu();
			buildAppearanceMenu();
		}
		
		ml = new MenuItemListener();
		this.addMenuListener(ml);
	}

	/**
	 * Constructor
	 */
	public MenuBar() {
		this(true);
	}

	/**
	 * Fileに関するメニューを構築する
	 */
	public void buildFileMenu() {

		// create file menu
		fileMenu = new JMenu("File");
		add(fileMenu);

		// add menu item
		openMenuItem = new JMenuItem("Open");
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();
		optionMenuItem = new JMenuItem("Option");
		fileMenu.addSeparator();
		outputTemplateMenuItem = new JMenuItem("Output Template");
		fileMenu.add(outputTemplateMenuItem);
		fileMenu.addSeparator();
		readTemplateMenuItem = new JMenuItem("Read Template");
		fileMenu.add(readTemplateMenuItem);
		fileMenu.addSeparator();
		exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(exitMenuItem);
	}



	/**
	 * Appearance に関するメニューを構築する
	 */
	public void buildAppearanceMenu() {

		// create appearance menu
		appearanceMenu = new JMenu("Appearance");
		add(appearanceMenu);

		// add menu item
		appearanceMenuItem = new JMenuItem("Appearance");
		appearanceMenu.add(appearanceMenuItem);
		// add menu item
		tableAttributeMenuItem = new JMenuItem("Table attribute");
		appearanceMenu.add(tableAttributeMenuItem);
		// add menu item
		parametersMenuItem = new JMenuItem("Parameters");
		appearanceMenu.add(parametersMenuItem);
	}

	
	/**
	 * Canvas をセットする
	 */
	public void setCanvas(Canvas c) {
		canvas = c;;
	}
	
	
	/**
	 * 選択されたメニューアイテムを返す
	 * @param name 選択されたメニュー名
	 * @return JMenuItem 選択されたメニューアイテム
	 */
	public JMenuItem getMenuItem(String name) {

		// file menu
		if (openMenuItem.getText().equals(name))
			return openMenuItem;
		if (optionMenuItem.getText().equals(name))
			return optionMenuItem;
		if (outputTemplateMenuItem.getText().equals(name))
			return outputTemplateMenuItem;
		if (readTemplateMenuItem.getText().equals(name))
			return readTemplateMenuItem;
		if (exitMenuItem.getText().equals(name))
			return exitMenuItem;


		// appearance menu
		if (appearanceMenuItem.getText().equals(name))
			return appearanceMenuItem;
		if (tableAttributeMenuItem.getText().equals(name))
			return tableAttributeMenuItem;
		if (parametersMenuItem.getText().equals(name))
			return parametersMenuItem;
		// other
		return null;
	}

	/**
	 * メニューに関するアクションの検知を設定する
	 * @param actionListener ActionListener
	 */
	public void addMenuListener(ActionListener actionListener) {

		// file menu
		openMenuItem.addActionListener(actionListener);
		optionMenuItem.addActionListener(actionListener);
		outputTemplateMenuItem.addActionListener(actionListener);
		readTemplateMenuItem.addActionListener(actionListener);
		exitMenuItem.addActionListener(actionListener);

		// appearance menu
		appearanceMenuItem.addActionListener(actionListener);
		tableAttributeMenuItem.addActionListener(actionListener);
		parametersMenuItem.addActionListener(actionListener);
	}
	
	/**
	 * メニューの各イベントを検出し、それに対応するコールバック処理を呼び出す
	 * 
	 * @author itot
	 */
	class MenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JMenuItem menuItem = (JMenuItem) e.getSource();

		}
	}



}
