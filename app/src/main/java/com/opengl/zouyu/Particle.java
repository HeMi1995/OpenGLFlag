package com.opengl.zouyu;

import com.opengl.util.Vector3f;

public class Particle {
	float pfMass;//��������
	float pfInvMass;//��������������Ϊ�������
	Vector3f pvPosition;//����λ��
	Vector3f pvVelocity;//�����ٶ�
	Vector3f pvAcceleration;//���Ӽ��ٶ�
	Vector3f pvForces;//��������
	boolean bLocked;//�Ƿ�����
	public Particle(){
		this.pvPosition = new Vector3f(0,0,0);
		this.pvVelocity = new Vector3f(0,0,0);
		this.pvAcceleration = new Vector3f(0,0,0);
		this.pvForces = new Vector3f(0,0,0);
}}
