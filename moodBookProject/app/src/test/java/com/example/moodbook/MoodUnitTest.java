package com.example.moodbook;
import android.graphics.Bitmap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MoodUnitTest {

    // Test valid mood
    @Mock
    Bitmap bitmap; // mock bitmap

    @Mock
    MoodLocation location;

    Mood mood = new Mood("2019-11-24 01:23:00","sad","Passed 301",null,"Alone",null);
    Mood mood1 = new Mood("2019-11-24 01:23:00","angry","Coffee spilled",bitmap,"With a crowd",null); // Same date but not equal to Mood
    Mood mood2 = new Mood("2019-11-24 01:23:01","sad","Failed 301",null,"With one person",null); // Should be equal to mood5 -- 1secs after mood1
    Mood mood3 = new Mood("2019-11-25 01:23:00","happy","Passed 301",null,"With two or more people",location); //One day after mood3
    Mood mood4 = new Mood("2019-11-24 01:23:01","afraid","I might fail",null,"Alone",null); // invalid Mood
    Mood mood5 = new Mood("2019-11-24 01:23:01","sad","Failed 301",null,"With one person",null); // Should be equal to mood2

    /**
     * Constructor for MoodUnitTest Class
     * @throws MoodInvalidInputException
     */
    public MoodUnitTest() throws MoodInvalidInputException {
    }

    /**
     * Testing get date
     * @throws MoodInvalidInputException
     */

    @Test
    public void get_set_date() throws MoodInvalidInputException {
        String date_time_new ="1999-10-25 01:23:00";
        String date1 = "2019-11-24";
        String date2 = "1999-10-25";
        assertEquals(mood.getDateText(), date1);
        mood.setDateTime(date_time_new);
        assertEquals(mood.getDateText(), date2);
        assertFalse(mood.getDateText() ==date1);
    }

    /**
     * Testing for that date Exception is thrown
     * @throws MoodInvalidInputException
     */
    @Test(expected = MoodInvalidInputException.class)
    public void dateExceptionTest() throws MoodInvalidInputException {
        mood.setDateTime("2019-11-24-39");
    }


    /**
     * Testing get and set time
     * @throws MoodInvalidInputException
     */

    @Test
    public void set_get_time() throws MoodInvalidInputException {
        String date_time_new ="1999-10-25 09:09:30";
        String time1 = "01:23:00";
        String time2 = "09:09:30";
        assertEquals(mood.getTimeText(), time1);
        mood.setDateTime(date_time_new);
        assertEquals(mood.getTimeText(), time2);
    }


    /**
     * Testing get and set emotion
     * @throws MoodInvalidInputException
     */

    @Test
    public void set_get_emotion() throws MoodInvalidInputException {
        assertEquals(mood.getEmotionText(), "sad");

        mood.setEmotion("HAPPY");
        assertEquals(mood.getEmotionText(),"happy");

        mood.setEmotion("aNGRy");
        assertEquals(mood.getEmotionText(),"angry");

        mood.setEmotion("afraid");
        assertEquals(mood.getEmotionText(),"afraid");
    }

    /**
     * Testing that emotion Exception is thrown
     * @throws MoodInvalidInputException
     */

   @Test(expected = MoodInvalidInputException.class)
   public void emotionExceptionTest()throws MoodInvalidInputException{
        mood4.setEmotion("sleepy"); //Not a mood
    }

    /**
     * Testing get and set reason
     */

    @Test
    public void set_get_reason() throws MoodInvalidInputException {
        assertEquals(mood.getReasonText(), "Passed 301");

        mood.setReasonText("Failed 301");
        assertEquals(mood.getReasonText(),"Failed 301");

    }

    /**
     * Testing that reason Exception is thrown
     * @throws MoodInvalidInputException
     */

    @Test(expected = MoodInvalidInputException.class)
    public void reasonExceptionTest()throws MoodInvalidInputException{
        mood4.setReasonText("I just saw a cat, a dog, a cow, a monkey, a goat and a lion . "); // To long
    }

    /**
     * Testing  set and get reason photo
     */

    @Test
    public void set_get_reason_photo(){
        assertEquals(mood.getReasonPhoto(),null);
        mood.setReasonPhoto(bitmap);
        assertEquals(mood.getReasonPhoto(), bitmap);
    }


    /**
     * Testing set and get location
     */

    @Test
    public void set_get_location(){
        assertEquals(mood.getLocation(),null);
        MoodLocation l = new MoodLocation("11325 89 Ave NW, Edmonton, AB T6G 2J5\n");
        mood.setLocation(l);
        assertEquals(mood.getLocation(), l);
    }



    /**
     * Testing  set and  get situation
     */

    @Test
    public void set_get_situation() {
        assertEquals(mood.getSituation(), "Alone");
        mood.setSituation("With a crowd");
        assertEquals(mood.getSituation(), "With a crowd");
    }




    /**
     * Testing  set get docID
     */

    @Test
    public void set_get_docID(){
        assertFalse(mood.getDocId()== "LWUu86D7yyRYaFDPQTUl9G8k1r33");
        assertTrue(mood.getDocId()==null);
        mood.setDocId("LWUu86D7yyRYaFDPQTUl9G8k1r33");
        assertEquals(mood.getDocId(), "LWUu86D7yyRYaFDPQTUl9G8k1r33");
    }


    /**
     * Testing  set get docID
     */

    @Test
    public void compareToTest() {
        assertEquals(mood2.compareTo(mood5),0);
        assertTrue(mood1.compareTo(mood)== 0);
        assertTrue(mood2.compareTo(mood1)== 1);
        assertTrue(mood1.compareTo(mood2)== -1);
        assertEquals(mood3.compareTo(mood2),1);
    }
}
