package com.bbchen.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityList {
	 
	public ActivityList() {
	
	}
	
	//建立一个public static的list用来放activity
	public static List<Activity> activityListAll = new ArrayList<Activity>(); 

	//finish所有list中的activity 
	public static void killall(List<Activity> activityList){     
		int siz = activityList.size();     
		for(int i=0;i<siz;i++){         
			if(activityList.get(i)!=null){             
				activityList.get(i).finish();         
			}     
		} 
	}
		
}
