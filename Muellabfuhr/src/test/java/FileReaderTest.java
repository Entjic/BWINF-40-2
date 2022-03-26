import com.franosch.bwinf.muellabfuhr.io.FileReader;
import org.junit.Assert;
import org.junit.Test;

public class FileReaderTest {

    @Test
    public void simpleFileTest(){
        FileReader fileReader = new FileReader(0, "src/test/resources/");
        Assert.assertNotEquals(fileReader.getContent().size(), 0);
    }

}
