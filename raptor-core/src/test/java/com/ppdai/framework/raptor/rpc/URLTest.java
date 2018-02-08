package com.ppdai.framework.raptor.rpc;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class URLTest {

    private String str1 = "test.com";
    private URL url1 = URL.valueOf(str1);

    private String str2 = "http://test.com";
    private URL url2 = URL.valueOf(str2);

    private String str3 = "http://test.com:8080/";
    private URL url3 = URL.valueOf(str3);

    private String str4 = "http://test.com:8080/raptor/";
    private URL url4 = URL.valueOf(str4);

    private String str5 = "http://test.com:8080/raptor?a=1";
    private URL url5 = URL.valueOf(str5);

    private String str6 = "http://test.com/raptor?a=1";
    private URL url6 = URL.valueOf(str6);

    private String str7 = "test.com/raptor?a=1";
    private URL url7 = URL.valueOf(str7);

    private String str8 = "test.com?a=1";
    private URL url8 = URL.valueOf(str8);

    private String str9 = "http://?a=1";
    private URL url9 = URL.valueOf(str9);

    @Test
    public void testValueOf() {
        Assert.assertTrue(StringUtils.isBlank(url1.getProtocol()));

        Assert.assertEquals(0, url2.getPort());

        Assert.assertTrue(StringUtils.isBlank(url3.getPath()));

        Assert.assertEquals(0, url4.getParameters().size());

        Assert.assertEquals("http", url5.getProtocol());
        Assert.assertEquals("test.com", url5.getHost());
        Assert.assertEquals(8080, url5.getPort());
        Assert.assertEquals("raptor", url5.getPath());
        Assert.assertEquals("1", url5.getParameter("a"));
        Assert.assertEquals(str5, url5.toFullStr());

        Assert.assertEquals(0, url6.getPort());


        Assert.assertTrue(StringUtils.isBlank(url7.getProtocol()));
        Assert.assertEquals(0, url7.getPort());

        Assert.assertTrue(StringUtils.isBlank(url8.getPath()));

        Assert.assertTrue(StringUtils.isBlank(url9.getHost()));
    }

    @Test
    public void testGetUri() {
        Assert.assertEquals(str1, url1.getUri());
        Assert.assertEquals(str2, url2.getUri());
        Assert.assertEquals(StringUtils.removeEnd(str3, "/"), url3.getUri());
        Assert.assertEquals(str4, url4.getUri());
        Assert.assertEquals(StringUtils.substringBefore(str5, "?"), url5.getUri());
        Assert.assertEquals(StringUtils.substringBefore(str6, "?"), url6.getUri());
        Assert.assertEquals(StringUtils.substringBefore(str7, "?"), url7.getUri());
        Assert.assertEquals(StringUtils.substringBefore(str8, "?"), url8.getUri());
        Assert.assertEquals(StringUtils.substringBefore(str9, "?"), url9.getUri());

    }

    @Test
    public void testToFullStr() {
        Assert.assertEquals(str1, url1.toFullStr());
        Assert.assertEquals(str2, url2.toFullStr());
        Assert.assertEquals(StringUtils.removeEnd(str3, "/"), url3.toFullStr());
        Assert.assertEquals(str4, url4.toFullStr());
        Assert.assertEquals(str5, url5.toFullStr());
        Assert.assertEquals(str6, url6.toFullStr());
        Assert.assertEquals(str7, url7.toFullStr());
        Assert.assertEquals(str8, url8.toFullStr());
        Assert.assertEquals(str9, url9.toFullStr());
    }
}
