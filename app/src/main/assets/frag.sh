precision mediump float;
//varying  vec4 vColor; //���մӶ�����ɫ�������Ĳ���
varying vec2 vTextureCoord;//���մӶ�����ɫ�������Ĳ���
uniform sampler2D sTexture;//������������
void main()                         
{          
	//vec4 finalColor = vColor;      
	vec4 finalColor =  texture2D(sTexture, vTextureCoord);    
	gl_FragColor = finalColor;
}