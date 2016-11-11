// Template map

import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import freemarker.template.*;
import joptsimple.OptionSet;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.IOException;

public class TemplateMap {

    static Configuration t_config = new Configuration(Configuration.VERSION_2_3_23);

    private Map t_table;

    static {
        Logger log = LoggerFactory.getLogger(TemplateMap.class);
        try {
            t_config.setDefaultEncoding("UTF-8");
            t_config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            t_config.setDirectoryForTemplateLoading(new File("."));
        } catch (IOException ioe) {
            log.error("At configuration: {}", ioe.getMessage());
        }
    }

    TemplateMap(OptionSet options) {
        t_table = new HashMap<String,String>();

        // Yes, I know I get this as an integer and converted to a
        // string for the map but at least it gets verified.
        int number = (Integer)options.valueOf("number");
        try {
            t_table.put("number", String.valueOf(number));
            t_table.put("key", options.valueOf("key"));
            t_table.put("time", options.valueOf("time"));
        }
        catch (java.lang.RuntimeException ex) {
            // ignore caught exception
        }
    }

    String getSuffix() {
        try {
            String num = (String)t_table.get("number");
            return String.format("n%02d", Integer.parseInt(num));
        }
        catch (java.lang.RuntimeException ex) {
            return null;
        }
    }

    Path translate(String templateFN) throws IOException {
        Logger log = LoggerFactory.getLogger(TemplateMap.class);
        Path p = Files.createTempFile("M", ".ly");
        BufferedWriter out = Files.newBufferedWriter(p, StandardCharsets.UTF_8);
        try {
            Template t = t_config.getTemplate(templateFN);
            t.process(t_table, out);
            out.close();
            return p;
        } catch (TemplateNotFoundException | TemplateException tnf) {
            log.warn("Template failure - {}", tnf.getMessage());
            return null;
        }
    }
}
