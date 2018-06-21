package test.java.dao;

import com.debugTeam.dao.TagDao;
import com.debugTeam.entity.Project;
import com.debugTeam.util.testutil.TestHelper;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

public class TagDaoTest {

    TagDao tagDao = TestHelper.TAG_DAO;

    Project project = new Project("asda", 1, "20180618205012", 0.75,
            1, "aaaaaaa", "321321", 1,
            new ArrayList<String>(), new ArrayList<String>(), "sdsd", 1);

    @Test
    public void addTag() {
        try {
            tagDao.addTag(project.getId(), "21321",
                    project.getOwner(), "545454", 3);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTag() {
        try {
            tagDao.updateTag(project.getId(), "21321",
                    project.getOwner(), "545454", 3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTag() {
        try {
            tagDao.getTag("554545", "56dsd", "xxx.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTagList() {
        try {
            tagDao.getTagList("213221");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}