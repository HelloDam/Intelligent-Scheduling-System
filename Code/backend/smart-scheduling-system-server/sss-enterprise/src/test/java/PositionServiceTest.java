import com.dam.EnterpriseApplication;
import com.dam.service.PositionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EnterpriseApplication.class})
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositionServiceTest {

    @Autowired
    private PositionService positionService;

    @Test
    public void copyPositionToOtherStoreTest(){
        positionService.copyPositionToOtherStore(4L);
    }

}
