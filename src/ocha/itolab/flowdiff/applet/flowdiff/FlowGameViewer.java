package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

public class FlowGameViewer extends JApplet {

	// GUI element
	MenuBar menuBar;
	ViewingPanel viewingPanel = null; 
	CursorListener cl;
	Canvas canvas;
	Container windowContainer;
	
	
	/**
	 * applet を初期化し、各種データ構造を初期化する
	 */
	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}

	/**
	 * applet の各イベントの受付をスタートする
	 */
	public void start() {
	}

	/**
	 * applet の各イベントの受付をストップする
	 */
	public void stop() {
	}

	/**
	 * applet等を初期化する
	 */
	private void buildGUI() {

		
		// Canvas
		canvas = new Canvas(512, 512);
		canvas.requestFocus();
		GLCanvas glc = canvas.getGLCanvas();
		
		// ViewingPanel
		viewingPanel = new ViewingPanel();
		viewingPanel.setCanvas(canvas);
		
		// MenuBar
		menuBar = new MenuBar();
		menuBar.setCanvas(canvas);
		
		// CursorListener
		cl = new CursorListener();
		cl.setCanvas(canvas, glc);
		cl.setViewingPanel(viewingPanel);
		canvas.addCursorListener(cl);
		
		// CanvasとViewingPanelのレイアウト
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// ウィンドウ上のレイアウト
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);
		
	}

	/**
	 * main関数
	 * @param args 実行時の引数
	 */
	public static void main(String[] args) {
		Window window = new Window(
				"Flow Game Viewer", 800, 600, Color.LIGHT_GRAY);
		FlowGameViewer fgv = new FlowGameViewer();

		fgv.init();
		window.getContentPane().add(fgv);
		window.setVisible(true);

		fgv.start(); 
	}
		
}

