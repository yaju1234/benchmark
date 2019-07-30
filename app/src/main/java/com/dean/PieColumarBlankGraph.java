package com.dean;

import java.util.Random;

import org.zeroxlab.zeroxbenchmark.R;

import com.kisai.bt.android.Kisai_BT_Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PieColumarBlankGraph extends Activity {
	ImageView stats,graph,map,options;
	public static boolean BAR = true;
	public static boolean BAR1 = true;
	public static boolean LINE = false;
	public float[] values;
	public float[] values1;
	public float[] values2;
	public float[] values3;
	DrawView myView;
	public static float x;
	private boolean runnable = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
			values = new float[] {};
			values1 = new float[] {};
			  String[] verlabels = new String[] { "150","140","130","120","110","100","90","80","70","60" };
		    String[] verlabels1 = new String[] {/* "10","9","8","7","6","5","4","3","2","1" */""};
		    String[] horlabels = new String[] {"2","4","6","8","10","12","14","16","18"};
		    DrawView myView = new DrawView( PieColumarBlankGraph.this,values, values1,
		      horlabels, verlabels,verlabels1,  false);		   
		    setContentView(R.layout.main1);
		    LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);
	    	chartContainer.addView(myView);
		    stats=(ImageView)findViewById(R.id.imageView30);
		    stats.setVisibility(View.GONE);
		    graph=(ImageView)findViewById(R.id.imageView31);
		    stats.setVisibility(View.GONE);
		    map=(ImageView)findViewById(R.id.imageView32);
		    stats.setVisibility(View.GONE);
		    options=(ImageView)findViewById(R.id.imageView33);
		    stats.setVisibility(View.GONE);
		    graph.setBackgroundResource(R.drawable.cardio_progress_box_down);
		    runnable = true;
	        //startDraw.start();
		    stats.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
                    /*Intent intent=new Intent(getApplicationContext(), main.class);
		            startActivity(intent);*/
		            }
	        });
		    
	        map.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	/*Intent intent=new Intent(getApplicationContext(), RouteOverlay.class);
		            startActivity(intent);*/
	            	
	            }
	        });
	        options.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	/*Intent intent=new Intent(getApplicationContext(), Options.class);
		            startActivity(intent);
	            	*/
	            }
	        });
	        
	}
@Override
	
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
        }
        onBackPressed();
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(PieColumarBlankGraph.this, Kisai_BT_Main.class);
        
        setIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(setIntent); 

        return;
    }
	class DrawView extends View{
		private Paint paint;
		private float[] values;
		private float[] values1;
		private String[] horlabels;
		private String[] verlabels;
		private String[] verlabels1;
		private boolean type;
		Context context;
		private float[] values2;
		private float[] values3;

		public DrawView(Context context, float[] values,float[] values1,  String[] horlabels,String[] verlabels,String[] verlabels1, boolean type) {

		    super(context);
		    
		    if (values == null)
		    	
		        values = new float [0];
		    else
		        this.values = values;
		    
		    if (values1== null)
		    	
		        values1 = new float [0];
		    else
		        this.values1 = values1;
		    
		   
		   
		    if (horlabels == null)
		    	
		        this.horlabels = new String[0];
		    else
		        this.horlabels = horlabels;
		    
		    if (verlabels == null)
		    	
		        this.verlabels = new String[0];
		    else
		        this.verlabels = verlabels;
		    
		    if (verlabels1 == null)
		    	
		        this.verlabels1 = new String[0];
		    else
		        this.verlabels1 = verlabels1;
		    
		    this.type = type;
		    
		    paint = new Paint();
		    
		}

		@SuppressLint("DrawAllocation")
		@Override
		protected void onDraw(final Canvas canvas) {
		    context=getContext();
		    float border = 20;
		    float horstart = border*2;
		    float height = getHeight();
		    float width = getWidth()-20;
		    float max = getMax();
		    Log.w("max", ""+max);
		    float min = getMin();
		    Log.w("min", ""+min);
		    float diff = max - min;
		    float graphheight = height - (2 * border);
		    float graphwidth = width - ( 2*border);
		    paint.setTextAlign(Align.LEFT);
		    //horizontal label  draw Start here    
		    int hors = horlabels.length-1;
		    for (int i = 0; i < horlabels.length; i++) {
		    	if(i==0){
		    		paint.setColor(Color.DKGRAY);
			        float x = ((graphwidth / hors) * i) + horstart;
			        canvas.drawLine(x, height - border, x, border, paint);
			        canvas.drawText(horlabels[i], x, height , paint);
		    	}
		    	else if(i==8)
		    	{
		    		paint.setColor(Color.DKGRAY);
			        float x = ((graphwidth / hors) * i) + horstart;
			        canvas.drawLine(x, height-border , x, border, paint);
			        canvas.drawText(horlabels[i], x, height , paint);
		    	}
		        paint.setColor(Color.DKGRAY);
		        float x = ((graphwidth / hors) * i) + horstart;
		        paint.setTextAlign(Align.CENTER);
		        if (i==horlabels.length+5)
		            paint.setTextAlign(Align.RIGHT);
		        if (i==0)
		            paint.setTextAlign(Align.LEFT);
		        canvas.drawText(horlabels[i], x, height , paint);
		    }
		//  Vertical label  draw Start here    
		    int vers = verlabels.length-1 ;
		    for (int i = 0; i < verlabels.length; i++) {
		        paint.setColor(Color.BLACK);
		        float y = ((graphheight / vers) * i) + border;
		        canvas.drawLine(horstart, y, width, y, paint);
		        canvas.drawText(verlabels[i], 10, y, paint);
		       }
		    int vers10 = verlabels1.length-1 ;
		    for (int i = 0; i < verlabels1.length; i++) {
		        paint.setColor(Color.BLACK);
		        float y = ((graphheight / vers10) * i) + border;
		        canvas.drawText(verlabels1[i], width+10, y, paint);
		    }

		    int x = 0;
		    int y = 0;
		    Paint paint = new Paint();
		    paint.setStyle(Paint.Style.FILL);
		    String str2rotate = "Rotated!";
		    // draw bounding rect before rotating text
		    Rect rect = new Rect();
		    paint.getTextBounds(str2rotate, 0, str2rotate.length(), rect);
		    canvas.translate(x, y);
		    paint.setColor(Color.GREEN);
		    paint.setStyle(Paint.Style.FILL);
		    paint.setStrokeWidth(1);
		    paint.setStyle(Paint.Style.STROKE);
		    canvas.drawText("!Rotated", 0, 0, paint);
            Random random=new Random();
            String str="12345";
            int ii=random.nextInt(str.length());
            if(ii==1){
            values = new float[] { 0.0f,0.0f,  0.0f, 0.0f , 0.0f ,  0.0f , 0.0f };
    		values1 = new float[] {  0.0f,  0.0f,  0.0f,  0.0f ,  0.0f ,  0.0f , 0.0f };
            }else if(ii==2){
            	values = new float[] { 1.0f,0.0f, 1.0f, 1.0f ,2.0f , 4.0f ,1.0f };
        		values1 = new float[] { 2.0f, 5.0f, 3.0f, 4.0f , 2.0f , 5.0f ,3.0f };
                	
            }else if(ii==3){
            	values = new float[] { 0.0f,1.0f, 0.0f, 1.0f ,3.0f , 5.0f ,2.0f };
        		values1 = new float[] { 3.0f, 5.0f, 2.0f, 3.0f , 4.0f , 5.0f ,2.0f };
                	
            }else if(ii==4){
            	values = new float[] { 0.0f,1.0f, 0.0f, 1.0f ,3.0f , 5.0f ,1.0f };
        		values1 = new float[] { 3.0f, 4.0f, 2.0f, 4.0f , 3.0f , 5.0f ,2.0f };
                
            }else if(ii==5){
            	values = new float[] { 1.0f,0.0f, 1.0f, 0.0f ,3.0f , 5.0f ,1.0f };
        		values1 = new float[] { 2.0f, 5.0f, 2.0f, 4.0f , 3.0f , 5.0f ,2.0f };
                
            }
            values = new float[] { 0.0f,0.0f,  0.0f, 0.0f , 0.0f ,  0.0f , 0.0f };
    		values1 = new float[] {  0.0f,  0.0f,  0.0f,  0.0f ,  0.0f ,  0.0f , 0.0f };
		    int m=0;
             while(m<2){
	          switch(m){
	
	              case 0: if (max != min) 
		            {
		            paint.setColor(Color.BLUE);
		            float datalength = values.length;
		            float colwidth = (width - (2 * border)) / datalength;
		            float halfcol = colwidth / 2;
		            float lasth = 0;
		            for (int i = 0; i < values.length; i++) {
		                float val = values[i] - min;
		                float rat = val / diff;
		                float h = graphheight * rat;
		                if (i > 0)
		                canvas.drawLine(((i-1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
		                lasth = h;
		                m++;
		                
		            }}
		            case 1: if(type == false ){
		            	 paint.setColor(Color.BLACK);
		            	 float datalength1 = values1.length;
				            float colwidth1 = (width - (2 * border)) / datalength1;
				            float halfcol1 = colwidth1 / 2;
				            float lasth1 = 0;
				            for (int i1 = 0; i1 < values1.length; i1++) {
				                float val = values1[i1] - min;
				                float rat = val / diff;
				                float h = graphheight * rat;
				                if (i1 > 0)
				                canvas.drawLine(((i1-1) * colwidth1) + (horstart + 1) + halfcol1, (border - lasth1) + graphheight, (i1 * colwidth1) + (horstart + 1) + halfcol1, (border - h) + graphheight, paint);
				                lasth1 = h;
				                m++;
				             
		            }}
		            case 2: if(type == false ){}
		            case 3: if(type == false ){}
		    }}
		}

		private float getMax() {
		    float largest = Integer.MIN_VALUE;
		    for (int i = 0; i < values.length; i++)
		        if (values[i] > largest)
		            largest = values[i];
		    return largest;
		}

		private float getMin() {
		    float smallest = Integer.MAX_VALUE;
		    for (int i = 0; i < values.length; i++)
		        if (values[i] < smallest)
		            smallest = values[i];
		    return smallest;
		}
		

	    public Handler handler = new Handler(){
	        @Override
	        public void handleMessage(android.os.Message msg){
	            switch(msg.what){
	            case 0x01:
	                int testValue = (int)(Math.random() * 600)+1;
	              //setGraph(testValue);
	                break;
	            };
	        }
	    };

	    public Thread startDraw = new Thread(){
	        @Override
	        public void run(){
	            while(runnable){
	                handler.sendEmptyMessage(0x01);
	                try{
	                    Thread.sleep(1000);
	                } catch (Exception e){
	                     e.printStackTrace();
	                }
	            }
	        }
	    };

	}
		}