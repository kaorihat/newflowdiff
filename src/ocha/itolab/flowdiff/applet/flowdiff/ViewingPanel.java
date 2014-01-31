
package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ocha.itolab.flowdiff.core.data.*;
import ocha.itolab.flowdiff.core.streamline.*;


public class ViewingPanel extends JPanel {

	// TODO:ファイルのパスが固定になっている
	static String url1 = "file:C:/itot/projects/VolVis/flowdiff/data/kassoro/ari/";
	static String url2 = "file:C:/itot/projects/VolVis/flowdiff/data/kassoro/nashi/";

	public JButton  openDataButton, viewResetButton, generateButton;
	public JRadioButton viewRotateButton, viewScaleButton, viewShiftButton, easyButton, hardButton;
	public JLabel xText, yText, zText;
	public JSlider sliderX, sliderY, sliderZ;
	public Container container;
	File currentDirectory;

	/* Selective canvas */
	Canvas canvas;

	/* Cursor Sensor */
	boolean cursorSensorFlag = false;

	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	CheckBoxListener cbl = null;
	SliderListener sl = null;

	/* Data */
	Grid grid1 = null;
	Grid grid2 = null;
	
	public ViewingPanel() {
		// super class init
		super();
		setSize(200, 800);

		// パネル1
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(7,1));
		openDataButton = new JButton("最初から始める");
		viewResetButton = new JButton("元に戻す");
		p1.add(openDataButton);
		p1.add(viewResetButton);
		ButtonGroup group1 = new ButtonGroup();
		viewRotateButton = new JRadioButton("回転する");
		group1.add(viewRotateButton);
		p1.add(viewRotateButton);
		viewScaleButton = new JRadioButton("大きく・小さく", true);
		group1.add(viewScaleButton);
		p1.add(viewScaleButton);
		viewShiftButton = new JRadioButton("移動する");
		group1.add(viewShiftButton);
		p1.add(viewShiftButton);
		ButtonGroup group2 = new ButtonGroup();
		easyButton = new JRadioButton("ちょいむず", true);
		group2.add(easyButton);
		p1.add(easyButton);
		hardButton = new JRadioButton("かなりむず");
		group2.add(hardButton);
		p1.add(hardButton);

		// パネル2
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(7,1));
		sliderX = new JSlider(0, 100, 10);
		sliderX.setMajorTickSpacing(10);
		sliderX.setMinorTickSpacing(5);
		sliderX.setPaintTicks(true);
		sliderX.setLabelTable(sliderX.createStandardLabels(20));
	    sliderX.setPaintLabels(true);
	    xText = new JLabel(" よこ: " + sliderX.getValue());
		p2.add(sliderX);
		p2.add(xText);
		sliderY = new JSlider(0, 100, 10);
		sliderY.setMajorTickSpacing(10);
		sliderY.setMinorTickSpacing(5);
		sliderY.setPaintTicks(true);
		sliderY.setLabelTable(sliderY.createStandardLabels(20));
	    sliderY.setPaintLabels(true);
	    yText = new JLabel(" たて: " + sliderY.getValue());
		p2.add(sliderY);
		p2.add(yText);
		sliderZ = new JSlider(0, 100, 10);
		sliderZ.setMajorTickSpacing(10);
		sliderZ.setMinorTickSpacing(5);
		sliderZ.setPaintTicks(true);
		sliderZ.setLabelTable(sliderZ.createStandardLabels(20));
	    sliderZ.setPaintLabels(true);
	    zText = new JLabel(" たかさ: " + sliderZ.getValue());
		p2.add(sliderZ);
		p2.add(zText);
		generateButton = new JButton("決定する");
		p2.add(generateButton);
		
		
		//
		// パネル群のレイアウト
		//
		this.setLayout(new GridLayout(2,1));
		this.add(p1);
		this.add(p2);
		
		//
		// リスナーの追加
		//
		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);

		if (cbl == null)
			cbl = new CheckBoxListener();
		addCheckBoxListener(cbl);

		if (sl == null)
			sl = new SliderListener();
		addSliderListener(sl);
	}

	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}


	/**
	 * Cursor Sensor の ON/OFF を指定するフラグを返す
	 * @return cursorSensorFlag
	 */
	public boolean getCursorSensorFlag() {
		return cursorSensorFlag;
	}


	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		viewRotateButton.addActionListener(actionListener);
		viewScaleButton.addActionListener(actionListener);
		viewShiftButton.addActionListener(actionListener);
		easyButton.addActionListener(actionListener);
		hardButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		openDataButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		generateButton.addActionListener(actionListener);
	}

	/**
	 * チェックボックスのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addCheckBoxListener(CheckBoxListener checkBoxListener) {
	}

	/**
	 * スライダのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addSliderListener(ChangeListener changeListener) {
		sliderX.addChangeListener(changeListener);
		sliderY.addChangeListener(changeListener);
		sliderZ.addChangeListener(changeListener);
	}
	
	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();

			if (buttonPushed == openDataButton) {
				grid1 = FileReader.getGrid(url1);
				grid2 = FileReader.getGrid(url2);
				sliderX.setValue(10);
				sliderY.setValue(10);
				sliderZ.setValue(10);
				canvas.setGrid1(grid1);
				canvas.setGrid2(grid2);
				canvas.setStreamline1(null);
				canvas.setStreamline2(null);
			}
			
			if (buttonPushed == viewResetButton) {
				grid1.setStartPoint(10, 10, 10);
				grid2.setStartPoint(10, 10, 10);
				sliderX.setValue(10);
				sliderY.setValue(10);
				sliderZ.setValue(10);
				canvas.viewReset();
			}
			
			if (buttonPushed == generateButton) {
				Streamline sl1 = new Streamline();
				Streamline sl2 = new Streamline();
				int eIjk[] = new int[3];
				int numg[] = grid1.getNumGridPoint();
				eIjk[0] = sliderX.getValue() * numg[0] / 100;
				eIjk[1] = sliderY.getValue() * numg[1] / 100;
				eIjk[2] = sliderZ.getValue() * numg[2] / 100;
				StreamlineGenerator.generate(grid1, sl1, eIjk, null);
				System.out.println("    target:" + grid1.intersectWithTarget(sl1)); 
				canvas.setStreamline1(sl1);
				StreamlineGenerator.generate(grid2, sl2, eIjk, null);
				System.out.println("    target:" + grid1.intersectWithTarget(sl2)); 
				canvas.setStreamline2(sl2);
				
			}
			
			canvas.display();
		}
	}


	/**
	 * ファイルダイアログにイベントがあったときに、対応するディレクトリを特定する
	 * @return ファイル
	 */
	String getDirectory() {
		JFileChooser dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int selected = dirChooser.showOpenDialog(container);
		if (selected == JFileChooser.APPROVE_OPTION) { // open selected
			return dirChooser.getSelectedFile().getAbsolutePath();
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		} 
		
		return null;
	}

	
	/**
	 * 拡張子がJPGであるファイルの名前一式を配列に確保して返す
	 */
	String[] getJpegFilenames(String dirname) {
	
		File directory = new File(dirname);
		String[] filelist = directory.list();
		int num = 0;
		for(int i = 0; i < filelist.length; i++) {
			if(filelist[i].endsWith("JPG") || filelist[i].endsWith("jpg"))
				num++;
		}
		
		String jpeglist[] = new String[num];
		num = 0;
		for(int i = 0; i < filelist.length; i++) {
			if(filelist[i].endsWith("JPG") || filelist[i].endsWith("jpg"))
				jpeglist[num++] = filelist[i];
		}
		
		return jpeglist;
	}
			
			
	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if (buttonPushed == viewRotateButton) {
				canvas.setDragMode(3);
			}
			if (buttonPushed == viewScaleButton) {
				canvas.setDragMode(1);
			}
			if (buttonPushed == viewShiftButton) {
				canvas.setDragMode(2);
			}
			if (buttonPushed == easyButton){
				grid1.levelMode = 1;
				grid2.levelMode = 1;
			}
			if (buttonPushed == hardButton){
				grid1.levelMode = 0;
				grid2.levelMode = 0;
			}

			canvas.display();
		}
	}

	/**
	 * チェックボックスのアクションを検知するItemListener
	 * @author itot
	 */
	class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox stateChanged = (JCheckBox) e.getSource();

			// 再描画
			canvas.display();
		}
	}

	/**
	 * スライダのアクションを検知するActionListener
	 * @author itot
	 */
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			int numg[] = grid1.getNumGridPoint();
			JSlider changedSlider = (JSlider) e.getSource();
			if (changedSlider == sliderX) {
				xText.setText(" よこ:" + sliderX.getValue());
				grid1.startPoint[0] = sliderX.getValue() * numg[0] / 100;
				grid2.startPoint[0] = sliderX.getValue() * numg[0] / 100;
			}
			else if (changedSlider == sliderY) {
				yText.setText(" たて:" + sliderY.getValue());
				grid1.startPoint[1] = sliderY.getValue() * numg[1] / 100;
				grid2.startPoint[1] = sliderY.getValue() * numg[1] / 100;
			}
			else if (changedSlider == sliderZ) {
				zText.setText(" たかさ:" + sliderZ.getValue());
				grid1.startPoint[2] = sliderZ.getValue() * numg[2] / 100;
				grid2.startPoint[2] = sliderZ.getValue() * numg[2] / 100;
			}
			canvas.display();
		}
	}
}
