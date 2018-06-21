package test.java.service;

import com.debugTeam.service.TagService;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class TagServiceImplTest {

    TagService tagService = TestHelper.TAG_SERVICE;

    @Test
    public void addTag() {
        try {
            tagService.addTag("1","11","111","!1",100);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTag() {
        try {
            tagService.updateTag("1","11","!1","!11",222);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTag() {
        try {
            tagService.getTag("!","111","11");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}