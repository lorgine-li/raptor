package com.ppdai.framework.raptor.codegen.core.utils.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by zhongyi on 2018/1/15.
 */
public class MustacheProcessor {

    private static final MustacheFactory mf = new DefaultMustacheFactory();

    public static void process(String mustachePath,String outputDes,Object parameter)throws Exception{
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputDes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Error happens at MustacheProcessor.");
        }
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(mustachePath);
        mustache.execute(writer, parameter);
        writer.flush();
    }

}
