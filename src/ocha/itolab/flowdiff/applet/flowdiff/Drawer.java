package ocha.itolab.flowdiff.applet.flowdiff;


import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import ocha.itolab.flowdiff.core.data.Element;
import ocha.itolab.flowdiff.core.data.Grid;
import ocha.itolab.flowdiff.core.streamline.Streamline;
import ocha.itolab.flowdiff.core.streamline.StreamlineGenerator;

import com.sun.opengl.util.gl2.GLUT;
//import com.jogamp.opengl.util.gl2.GLUT;

/**
 * 描画処理のクラス
 * 
 * @author itot
 */
public class Drawer implements GLEventListener {

	private GL gl;
	private GL2 gl2;
	private GLU glu;
	private GLUT glut;
	GLAutoDrawable glAD;
	GLCanvas glcanvas;

	Transformer trans = null;

	DoubleBuffer modelview, projection, p1, p2, p3, p4;
	IntBuffer viewport;
	int windowWidth, windowHeight;

	boolean isMousePressed = false, isAnnotation = true;
	boolean isImage = true, isWireframe = true;

	double linewidth = 1.0;
	long datemin, datemax;
	int authmax;

	int dragMode = 1;

	private double angleX = 0.0;
	private double angleY = 0.0;
	private double shiftX = 0.0;
	private double shiftY = 0.0;
	private double scale = 1.0;
	private double centerX = 0.5;
	private double centerY = 0.5;
	private double centerZ = 0.0;
	private double size = 0.25;

	Grid grid1 = null, grid2 = null;
	Streamline sl1 = null, sl2 = null;
	
	/**
	 * Constructor
	 * 
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public Drawer(int width, int height, GLCanvas c) {
		glcanvas = c;
		windowWidth = width;
		windowHeight = height;

		viewport = IntBuffer.allocate(4);
		modelview = DoubleBuffer.allocate(16);
		projection = DoubleBuffer.allocate(16);

		p1 = DoubleBuffer.allocate(3);
		p2 = DoubleBuffer.allocate(3);
		p3 = DoubleBuffer.allocate(3);
		p4 = DoubleBuffer.allocate(3);

		glcanvas.addGLEventListener((javax.media.opengl.GLEventListener) this);
	}

	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * ダミーメソッド
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Transformerをセットする
	 * 
	 * @param transformer
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
	}

	
	/**
	 * Gridをセットする
	 */
	public void setGrid1(Grid g) {
		grid1 = g;
		double minmax[] = grid1.getMinmaxPos();
		centerX = (minmax[0] + minmax[1]) * 0.5;
		centerY = (minmax[2] + minmax[3]) * 0.5;
		centerZ = (minmax[4] + minmax[5]) * 0.5;
	}
	
	/**
	 * Gridをセットする
	 */
	public void setGrid2(Grid g) {
		grid2 = g;
		double minmax[] = grid2.getMinmaxPos();
		centerX = (minmax[0] + minmax[1]) * 0.5;
		centerY = (minmax[2] + minmax[3]) * 0.5;
		centerZ = (minmax[4] + minmax[5]) * 0.5;
	}
	
	/**
	 * Streamlineをセットする
	 */
	public void setStreamline1(Streamline s) {
		sl1 = s;
	}

	/**
	 * Streamlineをセットする
	 */
	public void setStreamline2(Streamline s) {
		sl2 = s;
	}
	
	/**
	 * 描画領域のサイズを設定する
	 * 
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		windowWidth = width;
		windowHeight = height;
	}

	/**
	 * マウスボタンのON/OFFを設定する
	 * 
	 * @param isMousePressed
	 *            マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * 線の太さをセットする
	 * 
	 * @param lw
	 *            線の太さ（画素数）
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
	}

	/**
	 * Imageの可否をセットする
	 */
	public void isImage(boolean is) {
		isImage = is;
	}

	/**
	 * Wireframeの可否をセットする
	 */
	public void isWireframe(boolean is) {
		isWireframe = is;
	}


	/**
	 * マウスドラッグのモードを設定する
	 * 
	 * @param dragMode
	 *            (1:ZOOM 2:SHIFT 3:ROTATE)
	 */
	public void setDragMode(int newMode) {
		dragMode = newMode;
	}

	/**
	 * 初期化
	 */
	public void init(GLAutoDrawable drawable) {

		gl = drawable.getGL();
		gl2= drawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();
		this.glAD = drawable;

		gl.glEnable(GL.GL_RGBA);
		gl.glEnable(GL2.GL_DEPTH);
		gl.glEnable(GL2.GL_DOUBLE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	}

	/**
	 * 再描画
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		windowWidth = width;
		windowHeight = height;

		// ビューポートの定義
		gl.glViewport(0, 0, width, height);

		// 投影変換行列の定義
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, -height / 200.0,
				height / 200.0, -1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);

	}

	/**
	 * 描画を実行する
	 */
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// 視点位置を決定
		gl2.glLoadIdentity();
		glu.gluLookAt(centerX, centerY, (centerZ + 20.0), centerX, centerY,
				centerZ, 0.0, 1.0, 0.0);

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScaleY() * windowHeight / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// 行列をプッシュ
		gl2.glPushMatrix();

		// いったん原点方向に物体を動かす
		gl2.glTranslated(centerX, centerY, centerZ);

		// マウスの移動量に応じて回転
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0);

		// マウスの移動量に応じて移動
		gl2.glTranslated(shiftX, shiftY, 0.0);

		// マウスの移動量に応じて拡大縮小
		gl2.glScaled(scale, scale, scale);

		// 物体をもとの位置に戻す
		gl2.glTranslated(-centerX, -centerY, -centerZ);

		// 変換行列とビューポートの値を保存する
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);

		drawBox();
		
		if(grid1 != null && sl1 != null) {
			drawTarget(grid1, sl1);
			drawStartGrid(grid1);
			drawStreamline(sl1, 1);
			drawEndGrid(grid1);
		}
		if(grid2 != null && sl2 != null) {
			drawTarget(grid2, sl2);
			drawStartGrid(grid2);
			drawStreamline(sl2, 2);
			drawEndGrid(grid2);
		}
		
		// 行列をポップ
		gl2.glPopMatrix();

	}

	
	/**
	 * 格子領域を箱で描画する
	 */
	void drawBox() {
		if(grid1 == null) return;
		double minmax[] = grid1.getMinmaxPos();
		
		// 6本のループを描く
		gl2.glColor3d(0.5, 0.5, 0.5);
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();
	}
	
	/**
	 * 始点を描画する
	 */
	void drawStartGrid(Grid grid){
		int i, j, k;
		if(grid == null) return;
		i = grid.startPoint[0];
		j = grid.startPoint[1];
		k = grid.startPoint[2];

		double minmax[] = new double[6];
		minmax[0] = minmax[2] = minmax[4] = 1.0e+30;
		minmax[1] = minmax[3] = minmax[5] = -1.0e+30;
		
		Element element = grid.getElement(grid.calcElementId(i, j, k));
		for (int d = 0; d < 8; d++){
			double pos[] = element.gp[d].getPosition();
			for (int loop = 0; loop < 3; loop++){
				minmax[loop*2] = (minmax[loop*2] > pos[loop] ? pos[loop] : minmax[loop*2]);
				minmax[loop*2 + 1] = (minmax[loop*2 + 1] < pos[loop] ? pos[loop] : minmax[loop*2 + 1]);
			}
		}
		
		// 6本のループを描く
		gl2.glColor3d(1.0, 1.0, 1.0);
		
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();
	}
	
	
	/**
	 * 的を描画する
	 */
	void drawTarget(Grid grid, Streamline sl){
		int i, j, k;
		if(grid == null) return;
		i = grid.target[0];
		j = grid.target[1];
		k = grid.target[2];

		double minmax[] = new double[6];
		minmax[0] = minmax[2] = minmax[4] = 1.0e+30;
		minmax[1] = minmax[3] = minmax[5] = -1.0e+30;
		
		Element element = grid.getElement(grid.calcElementId(i, j, k));
		for (int d = 0; d < 8; d++){
			double pos[] = element.gp[d].getPosition();
			for (int loop = 0; loop < 3; loop++){
				minmax[loop*2] = (minmax[loop*2] > pos[loop] ? pos[loop] : minmax[loop*2]);
				minmax[loop*2 + 1] = (minmax[loop*2 + 1] < pos[loop] ? pos[loop] : minmax[loop*2 + 1]);
			}
		}
		
		// 6本のループを描く
		if (sl == null) {
			gl2.glColor3d(1.0, 0.0, 0.0);
		}
		else if (grid.intersectWithTarget(sl)) {
			gl2.glColor3d(1.0, 1.0, 0.0);
		}
		else {
			gl2.glColor3d(1.0, 0.0, 0.0);
		}
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();
	}
	
	/**
	 * 流線の行き着いた先の格子を描く
	 */
	public void drawEndGrid(Grid grid){
		if(grid == null) return;
		
		double minmax[] = new double[6];
		minmax[0] = minmax[2] = minmax[4] = 1.0e+30;
		minmax[1] = minmax[3] = minmax[5] = -1.0e+30;
		
		Element element;
		int lastId = StreamlineGenerator.lastElementId();
		if (lastId > 0) {
			element = grid.getElement(lastId);
		}
		else {
			return;
		}
		// System.out.println("    lastElementId=" + StreamlineGenerator.lastElementId());
		for (int d = 0; d < 8; d++){
			double pos[] = element.gp[d].getPosition();
			for (int loop = 0; loop < 3; loop++){
				minmax[loop*2] = (minmax[loop*2] > pos[loop] ? pos[loop] : minmax[loop*2]);
				minmax[loop*2 + 1] = (minmax[loop*2 + 1] < pos[loop] ? pos[loop] : minmax[loop*2 + 1]);
			}
		}
		
		// 6本のループを描く

		gl2.glColor3d(0.0, 1.0, 0.0);
		
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();
	
	}
	
	
	/**
	 * 流線を描く
	 */
	void drawStreamline(Streamline sl, int id) {
		
		// 折れ線を描く
		if(id == 1)
			gl2.glColor3d(1.0, 0.0, 1.0);
		if(id == 2)
			gl2.glColor3d(0.0, 1.0, 1.0);
		gl2.glBegin(GL2.GL_LINE_STRIP);
		for(int i = 0; i < sl.getNumVertex(); i++) {
			double pos[] = sl.getPosition(i);
			gl2.glVertex3d(pos[0], pos[1], pos[2]);
		}
		gl2.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}
