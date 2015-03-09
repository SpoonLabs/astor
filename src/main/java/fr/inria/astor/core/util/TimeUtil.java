package fr.inria.astor.core.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;
/**
 * 
 * @author matias
 *
 */
public class TimeUtil {

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static long delta(Date date){
		return getDateDiff(date,new Date(),TimeUnit.MINUTES);
	}
	
}
