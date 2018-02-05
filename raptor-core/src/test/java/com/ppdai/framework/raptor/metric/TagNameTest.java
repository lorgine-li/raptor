package com.ppdai.framework.raptor.metric;

import com.ppdai.framework.raptor.util.TagNameUtil;
import org.junit.Assert;
import org.junit.Test;

public class TagNameTest {

    @Test
    public void testTagName() {
        TagName tagName = TagName.name("myName").addTag("tag1", "1").addTag("tag3", "3").addTag("tag2", "2");
        System.out.println(tagName.toString());

        TagName tagName1 = TagNameUtil.parse(tagName.toString());
        System.out.println(tagName1.toString());

        Assert.assertEquals(tagName.toString(), tagName1.toString());

    }
}
