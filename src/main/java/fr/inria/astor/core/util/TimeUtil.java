package fr.inria.astor.core.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @author matias
 *
 */
public class TimeUtil {

	public static long getDateDiff(Date dateInit, Date dateEnd, TimeUnit timeUnit) {
	    long diffInMillies = dateEnd.getTime() - dateInit.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static long deltaInMinutes(Date date){
		return getDateDiff(date,new Date(),TimeUnit.MINUTES);
	}
	
}
