import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;


import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.PrintStream;


public class MainTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Before
    public void setup() {
        System.setOut(new PrintStream(output));
    }



    @Test
    public void testLowerCase () throws  Exception{
        String [] args = {"C:\\Users\\NoorZ\\Documents\\testDir"};
        MainClass.main(args);
        String testString = "okiaest";
        StringBuilder expectedOutput = new StringBuilder();
        for(int i = 'a';i<='z';i++){
            if(testString.indexOf((char)i)!=-1){
                expectedOutput.append((char)i+"\t"+40+'\n');
            }
            else {
                expectedOutput.append((char)i+"\t"+0+'\n');
            }
        }
        Assert.assertEquals(expectedOutput.toString(),output.toString());

    }

    @Test
    public void testLowerCaseWithExtra () throws  Exception{
        String [] args = {"C:\\Users\\NoorZ\\Documents\\testDir2"};
        MainClass.main(args);
        String testString = "okiaest";
        StringBuilder expectedOutput = new StringBuilder();
        for(int i = 'a';i<='z';i++){
            if(testString.indexOf((char)i)!=-1){
                expectedOutput.append((char)i+"\t"+40+'\n');
            }
            else {
                expectedOutput.append((char)i+"\t"+0+'\n');
            }
        }
        Assert.assertEquals(expectedOutput.toString(),output.toString());

    }





    @Test(expected = RuntimeException.class)
    public void testNoPath() throws Exception {
        String [] args = {};
        MainClass.main(args);

    }
    @Test(expected = IOException.class)
    public void invalidPath() throws Exception {

        String []args = {"InvalidPath"};
        MainClass.main(args);

    }


    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

}
