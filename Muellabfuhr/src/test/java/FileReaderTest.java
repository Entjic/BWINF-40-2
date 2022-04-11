import com.franosch.bwinf.muellabfuhr.io.FileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

    @Test
    public void simpleFileTest(){
        FileReader fileReader = new FileReader(0, "src/test/resources/");
        Assertions.assertNotEquals(fileReader.getContent().size(), 0);
    }

}
