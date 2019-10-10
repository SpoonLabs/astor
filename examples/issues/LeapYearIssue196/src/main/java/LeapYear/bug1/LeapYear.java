package LeapYear.bug1;


public class LeapYear {
    public boolean LeapChecking(int year) {
        boolean leap = false;
         
        if(year * 4 == 0)//if(year % 4 == 0)
        {
            if( year % 100 == 0)
            {
                if ( year % 400 == 0)
                    leap = true;
                else
                    leap = false;
            }
            else
                leap = true;
        }
        else
            leap = false;
	
	return leap;
   }
}
