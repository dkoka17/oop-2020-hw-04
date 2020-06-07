import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrackerTest {

    @Test
    public void mainTest() {
        String res = "molly\n" +
                "All Done!";
        String [] args = new String[]{ "4181eecbd7a755d19fdf73887c54837cbecf63fd", "5","8" };
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        Cracker.main(args);
        System.out.flush();
        System.setOut(old);
        String s = new String(baos.toByteArray(), Charset.defaultCharset());

    }

    @Test
    public void mainTest2() {
        String res = "4181eecbd7a755d19fdf73887c54837cbecf63fd";
        String [] args = new String[]{ "molly"};
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        Cracker.main(args);
        System.out.flush();
        System.setOut(old);
        String s = new String(baos.toByteArray(), Charset.defaultCharset());

    }

    @Test
    public void testPairs() {
        ArrayList<Cracker.pair> pairsToCompare = new ArrayList<Cracker.pair>();

        Cracker.pair p = new Cracker.pair(0,4);
        pairsToCompare.add(p);
        p = new Cracker.pair(5,9);
        pairsToCompare.add(p);
        p = new Cracker.pair(10,14);
        pairsToCompare.add(p);
        p = new Cracker.pair(15,19);
        pairsToCompare.add(p);
        p = new Cracker.pair(20,24);
        pairsToCompare.add(p);
        p = new Cracker.pair(25,29);
        pairsToCompare.add(p);
        p = new Cracker.pair(30,34);
        pairsToCompare.add(p);
        p = new Cracker.pair(35,39);
        pairsToCompare.add(p);

        ArrayList<Cracker.pair> pairs = Cracker.generatePairsWithReturn(8);

        for(int i=0; i<pairs.size(); i++){
            assertTrue(pairs.get(i).equals(pairsToCompare.get(i)));
        }

        Cracker.pair p2 = new Cracker.pair(0,4);
        Cracker.pair p3 = new Cracker.pair(0,2);
        Cracker.pair p5 = new Cracker.pair(4,2);
        assertFalse(p2.equals(p3));
        assertFalse(p5.equals(p3));

    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @org.junit.jupiter.api.Test
    public void testZhenExceptionThrown_thenRuleIsApplied() {
        exceptionRule.expect(java.lang.Exception.class);
        try {
            Cracker.connectDigest("");
        }catch (Exception e){

        }
    }
}