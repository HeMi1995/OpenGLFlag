package com.opengl.zouyu;

import com.opengl.util.Vector3f;

public class Collision {
	int r;			//��ײ��������
	int c;
	Vector3f n;		//�������Գ�������ײ������
	
	public Collision()
	{
		r = -1;
		c = -1;
		n = new Vector3f(0,0,0);
	}
}
