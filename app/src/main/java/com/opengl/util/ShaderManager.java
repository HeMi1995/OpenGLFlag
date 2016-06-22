package com.opengl.util;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
/**
 * �����Ǳ��벢������ɫ���ű���Դ�Ĺ����ࡣɫ�����ű���Ҫ����assets�ļ����¡�ʹ��ʱ��<br>
 * 1.��Ҫ�õ�����ɫ���ű��ļ�������shaderName�Ķ�ά�����С�<br>
 * 2.Ȼ������GL���������е��� ShaderManager.loadShaderScriptAndCompiled()�������ɱ��������Ҫ����ɫ������<br>
 * 3.ʹ��ʱͨ������ShaderManager.getShaderProgram(int i)��ñ���õ�program ID��<br>
 * @author fuxp
 */
public class ShaderManager
{
	public static final String SHADER_SCRIPT_1 = "vertex.sh";
	public static final String SHADER_SCRIPT_2 = "frag.sh";
	public static final String SHADER_SCRIPT_3 = "vertexlight.sh";
	public static final String SHADER_SCRIPT_4 = "fraglight.sh";
	
	private ShaderManager(){}
	/**�ö�ά�����ʾ������Ҫ�õ�����ɫ���ű�*/
	private static String[][] shaderName=
	{
		{"vertex.sh","frag.sh"},
		{"vertexlight.sh","fraglight.sh"},
//		{getInternalScript("vertex.sh"),getInternalScript("frag.sh")},//0
//		{getInternalScript("vertexlight.sh"),getInternalScript("fraglight.sh")},//1
	};
	
	private final static int shaderCount=shaderName.length;
	private final static int[] program=new int[shaderCount];
	private final static String[]mVertexShader=new String[shaderCount];
	private final static String[]mFragmentShader=new String[shaderCount];

	/**��ñ���õ�shader program ��id*/
	public static int getShaderProgram(int i) {
		return program[i];
	}
	/**������Ƕ��ɫ�����ȱ������ɫ����*/
	public static void loadShaderScriptAndCompiled(Resources r){
		loadCodeFromFile(r);
		compileShader();
	}
	/**���ز��ȱ������ɫ����*/
	public static void loadShaderScriptAndCompiled(Resources r,String[][]script){
		shaderName = script;
		loadCodeFromFile(r);
		compileShader();
	}
	/**����shader*/
	private static void loadCodeFromFile(Resources r) {
		for (int i = 0; i < shaderCount; i++) {
			mVertexShader[i] = loadFromAssetsFile(shaderName[i][0], r);
			mFragmentShader[i] = loadFromAssetsFile(shaderName[i][1], r);
		}
	}
	
	/**����shader*/
	private static void compileShader() {
		for (int i = 0; i < shaderCount; i++) {
			program[i] = createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	/**
     * ����shaderProgram����ķ���
     * @param vertexSource ����ű�
     * @param fragmentSource ƬԪ�ű�
     * @return shaderProgram��id
     */
	public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     *  ����shader�ķ���
     * @param shaderType shader������  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
     * @param source shader�Ľű��ַ���
     * @return shader��id
     */
    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {   
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * ���ÿһ�������Ƿ��д���ķ���
     * @param op
     */
    private static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);

            throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * ��IO��AssetsĿ¼�¶�ȡ�ļ�(��ȡ*.sh�ַ���)
     * @param fname �ļ���
     * @param r ��Դ
     * @return *.sh�ַ���
     */
    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        try {
            InputStream           in   = r.getAssets().open(fname);
            int                   ch   = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * ��ȡ�ű��ַ���
     * @param name 
     * 		{"frag.sh","vertex.sh"}
     * 		{"fraglight.sh","vertexlight.sh"}
     * @return
     */
    public static String getInternalScript(String name){
    	try {
    		InputStream in = ClassLoader.getSystemResourceAsStream("com/fuxp/openwater/resource/" + name);
		    int                   ch   = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            String result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
            return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
    }
}
