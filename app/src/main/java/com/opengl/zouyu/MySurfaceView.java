package com.opengl.zouyu;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.opengl.util.LoadUtil;
import com.opengl.util.LoadedObjectVertexNormalTexture;
import com.opengl.util.MatrixState;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView{
	SceneRenderer mRenderer;
	int textureFlagId[] = new int[3];
	int nowId;
	int groundId; 
	float scan = -15;
	float scana = 0;
	float downy;
	LoadedObjectVertexNormalTexture bg;
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(2);
		mRenderer=new SceneRenderer();
		this.setRenderer(mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				downy = y;
				break;
			case MotionEvent.ACTION_MOVE:
				scana = (y - downy)*0.02f;
				break;
			case MotionEvent.ACTION_UP:
				scan += scana;
				scana = 0;
		}
		return true;
	}
	class SceneRenderer implements Renderer
	{
		TextureRect flag;  
		ParticleControl pc;
		CalThread ct;
		
		long start;
		public void onDrawFrame(GL10 gl)
		{
			FloatBuffer fbt=null;
			synchronized(Constant.lockA)
    		{
				 fbt = Constant.mVertexBufferForFlag;//��ȡ��ǰ����֡��������
    		}	
			//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setInitStack();
            MatrixState.translate(-0.5f, 0, scan+scana);
            
            MatrixState.pushMatrix();
            flag.drawSelf(fbt,textureFlagId[nowId]); 
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, Constant.COLLISIONTOLERANCE-0.05f, 0);//-0.05fΪ�����Ŷ�����ֹ����Ҳ�ڵ�����ʱ˺��
            bg.drawSelf(groundId, 0);
            MatrixState.popMatrix();
		}
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height);
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio*0.4f, ratio*0.4f, -1*0.4f, 1*0.4f, 1, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0,1f,3,0f,0f,-1f,0f,1.0f,0.0f);
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//������Ļ����ɫRGBA
            GLES20.glClearColor(0.3921f,0.5843f,0.9294f,1.0f);
            MatrixState.setLightLocation(10, 30, 10);
            //���������ζԶ��� 
            flag = new TextureRect(MySurfaceView.this);   
            pc = new ParticleControl();
            textureFlagId[0]=initTexture(R.drawable.openglflag);
            textureFlagId[1]=initTexture(R.drawable.hzflag);
            textureFlagId[2]=initTexture(R.drawable.android_flag);
            groundId = initTexture(R.drawable.t);
            bg = LoadUtil.loadFromFile("ground.obj",MySurfaceView.this.getResources(), MySurfaceView.this);
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            ct=new CalThread(pc);
            ct.start();
		}			
	}

	
	@Override
	public void onPause() {
		super.onPause();
		mRenderer.ct.flag = false;
	}
	public int initTexture(int drawableId)//textureId
	{
		//��������ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        
        //ͨ������������ͼƬ===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        return textureId;
	}
}
