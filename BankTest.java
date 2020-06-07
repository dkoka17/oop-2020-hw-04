import com.sun.tools.javac.Main;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.*;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    public void testMain() {
        String res = "acct:0 bal:999 trans:1\n" +
                "acct:1 bal:1001 trans:1\n" +
                "acct:2 bal:999 trans:1\n" +
                "acct:3 bal:1001 trans:1\n" +
                "acct:4 bal:999 trans:1\n" +
                "acct:5 bal:1001 trans:1\n" +
                "acct:6 bal:999 trans:1\n" +
                "acct:7 bal:1001 trans:1\n" +
                "acct:8 bal:999 trans:1\n" +
                "acct:9 bal:1001 trans:1\n" +
                "acct:10 bal:999 trans:1\n" +
                "acct:11 bal:1001 trans:1\n" +
                "acct:12 bal:999 trans:1\n" +
                "acct:13 bal:1001 trans:1\n" +
                "acct:14 bal:999 trans:1\n" +
                "acct:15 bal:1001 trans:1\n" +
                "acct:16 bal:999 trans:1\n" +
                "acct:17 bal:1001 trans:1\n" +
                "acct:18 bal:999 trans:1\n" +
                "acct:19 bal:1001 trans:1\n";
        String [] args = new String[]{ "small.txt", "4" };
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        Bank.main(args);
        System.out.flush();
        System.setOut(old);
        String s = new String(baos.toByteArray(), Charset.defaultCharset());
        assertEquals(s,res);
    }
    @Test
    public void testTrans(){
        Transaction transaction1 = new Transaction(100,100,100);
        Transaction transaction2 = new Transaction(100,100,100);

        Transaction transaction21 = new Transaction(100,100,10);
        Transaction transaction22 = new Transaction(100,10,10);

        Transaction transaction31 = new Transaction(10,100,100);
        Transaction transaction32 = new Transaction(10,100,10);
        Transaction transaction33 = new Transaction(100,10,100);
        Transaction transaction34 = new Transaction(10,10,10);

        Transaction transaction4 = new Transaction(100,2,10);

        assertTrue(transaction1.equals(transaction2));

        assertFalse(transaction1.equals(transaction21));
        assertFalse(transaction1.equals(transaction22));

        assertFalse(transaction1.equals(transaction31));
        assertFalse(transaction1.equals(transaction32));
        assertFalse(transaction1.equals(transaction33));
        assertFalse(transaction1.equals(transaction34));
    }
    @Test
    public void testMainWithotthread() {
        String res = "acct:0 bal:999 trans:1\n" +
                "acct:1 bal:1001 trans:1\n" +
                "acct:2 bal:999 trans:1\n" +
                "acct:3 bal:1001 trans:1\n" +
                "acct:4 bal:999 trans:1\n" +
                "acct:5 bal:1001 trans:1\n" +
                "acct:6 bal:999 trans:1\n" +
                "acct:7 bal:1001 trans:1\n" +
                "acct:8 bal:999 trans:1\n" +
                "acct:9 bal:1001 trans:1\n" +
                "acct:10 bal:999 trans:1\n" +
                "acct:11 bal:1001 trans:1\n" +
                "acct:12 bal:999 trans:1\n" +
                "acct:13 bal:1001 trans:1\n" +
                "acct:14 bal:999 trans:1\n" +
                "acct:15 bal:1001 trans:1\n" +
                "acct:16 bal:999 trans:1\n" +
                "acct:17 bal:1001 trans:1\n" +
                "acct:18 bal:999 trans:1\n" +
                "acct:19 bal:1001 trans:1\n";
        String [] args = new String[]{ "small.txt"};
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        Bank.main(args);
        System.out.flush();
        System.setOut(old);
        String s = new String(baos.toByteArray(), Charset.defaultCharset());
        assertEquals(s,res);
    }
    @Test
    public void testMainFalse() {
        String res = "Args: transaction-file [num-workers [limit]]";
        String [] args = new String[]{ };
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        Bank.main(args);
        System.out.flush();
        System.setOut(old);
        String s = new String(baos.toByteArray(), Charset.defaultCharset());

    }





    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testZhenExceptionThrown_thenRuleIsApplied() {
        exceptionRule.expect(java.io.FileNotFoundException.class);
        exceptionRule.expectMessage("safasf");
        try {
            Bank.processFile("safasf",2);
        }catch (Exception e){

        }


    }
}