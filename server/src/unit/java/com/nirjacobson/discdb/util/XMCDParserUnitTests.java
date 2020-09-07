package com.nirjacobson.discdb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.nirjacobson.discdb.model.Disc;
import org.junit.Test;

public class XMCDParserUnitTests {

  @Test
  public void testParse() {
    final Disc disc = TestFactory.getDisc(false);
    final String xmcd = TestFactory.getXMCD();

    try {
      assertEquals(disc, XMCDParser.parse(xmcd));
    } catch (final Exception pE) {
      fail();
    }

    try {
      XMCDParser.parse("This is not XMCD");
      fail();
    } catch (final Exception pE) {
      assertTrue(pE instanceof XMCDParser.ParseException);
    }
  }
}
