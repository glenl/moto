import java.io.File;
import java.io.IOException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import static java.nio.file.attribute.PosixFilePermission.*;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

public class Moto {

    public static void main(String[] args) throws IOException {
        Logger log = LoggerFactory.getLogger(Moto.class);

        OptionParser parser = new OptionParser();
        parser.accepts("number", "number of piece in opus")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(0);
        parser.accepts("time", "time signature of piece")
            .withRequiredArg()
            .defaultsTo("4/4");
        parser.accepts("key", "key signature")
            .withRequiredArg()
            .defaultsTo("c \\major");
        parser.accepts("template", "template to use for processing")
            .withRequiredArg()
            .ofType(File.class)
            .defaultsTo(new File("template.ftl"));
        parser.accepts("help", "this help message");

        OptionSet options;
        try {
            options = parser.parse(args);
            if (options.has("help")) {
                parser.printHelpOn(System.out);
                return;
            }
        }
        catch (OptionException ex) {
            System.out.println(ex.getMessage());
            parser.printHelpOn(System.out);
            return;
        }

        // Make sure the template file exists before continuting
        File t_file = (File)options.valueOf("template");
        String fn = t_file.getName();
        if (!t_file.exists()) {
            Path default_p = FileSystems.getDefault().getPath(".", "tempate.ftl");
            fn = default_p.toString();
            log.warn("No template, using default");
        }
        Set<PosixFilePermission> perms =
            EnumSet.of(OWNER_READ,
                       OWNER_WRITE,
                       OWNER_EXECUTE,
                       GROUP_READ,
                       GROUP_EXECUTE,
                       OTHERS_READ,
                       OTHERS_EXECUTE
                       );

        TemplateMap tmap = new TemplateMap(options);

        // Eventually the output file will go in a folder named for
        // the piece we are about to create so we need to fail if this
        // folder already exists.
        int ndot = fn.lastIndexOf('.');
        if (ndot != -1) {
            String lybase = fn.substring(0, ndot) + tmap.getSuffix();
            Path d = FileSystems.getDefault().getPath(".", lybase);
            if (d.toFile().exists()) {
                log.error("Output folder already exists - {}", d.toString());
                return;
            }
            try {
                Path p = tmap.translate(fn);
                if (p != null) {
                    Path folder = Files.createDirectory(d);
                    Path newp = folder.resolve(lybase + ".ly");
                    Files.copy(p, newp);
                    Files.setPosixFilePermissions(newp, perms);
                    log.info("created {}", newp.toString());
                }
            }
            catch (IOException ioe) {
                log.warn(ioe.getMessage());
            }
        }
    }

}
