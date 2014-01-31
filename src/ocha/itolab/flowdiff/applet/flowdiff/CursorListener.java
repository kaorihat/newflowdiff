package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.media.opengl.awt.GLCanvas;

public class CursorListener implements MouseListener, MouseMotionListener {

	Canvas canvas = null;
	GLCanvas glcanvas = null;
	ViewingPanel  viewingPanel = null;
	int initX = 0, initY = 0;

	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c, Object glc) {
		canvas = (Canvas) c;
		glcanvas = (GLCanvas) glc;
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
	}
	

	/**
	 * ViewingPanelをセットする
	 * @param v ViewingPanel
	 */
	public void setViewingPanel(ViewingPanel v) {
		viewingPanel = v;
	}
	

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * マウスのクリックを検出するリスナー
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * マウスボタンが押されたことを検出するリスナー
	 */
	public void mousePressed(MouseEvent e) {
		
		if(canvas == null) return;

		initX = e.getX();
		initY = e.getY();
		canvas.mousePressed();
	}

	/**
	 * マウスボタンが離されたことを検出するリスナー
	 */
	public void mouseReleased(MouseEvent e) {
		
		if(canvas == null) return;
		
		canvas.mouseReleased();
		canvas.display();
	}

	/**
	 * マウスカーソルが動いたことを検出するリスナー
	 */
	public void mouseMoved(MouseEvent e) {
		
		if(canvas == null) return;
		if(viewingPanel.getCursorSensorFlag() == false) return;

	}

	/**
	 * マウスカーソルをドラッグしたことを検出するリスナー
	 */
	public void mouseDragged(MouseEvent e) {

		if(canvas == null) return;
		
		int cX = e.getX();
		int cY = e.getY();
		
		canvas.drag(initX, cX, initY, cY);
		canvas.display();
	}
}
