package org.infernus.idea.checkstyle.service.cmd;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.infernus.idea.checkstyle.CheckstyleProjectService;
import org.infernus.idea.checkstyle.checker.CheckStyleChecker;
import org.infernus.idea.checkstyle.csapi.CheckstyleActions;
import org.infernus.idea.checkstyle.csapi.TabWidthAndBaseDirProvider;
import org.infernus.idea.checkstyle.exception.CheckStylePluginException;
import org.infernus.idea.checkstyle.model.ConfigurationLocation;
import org.infernus.idea.checkstyle.service.CheckstyleActionsImpl;
import org.infernus.idea.checkstyle.service.FileUtil;
import org.infernus.idea.checkstyle.service.StringConfigurationLocation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;


public class OpCreateCheckerTest
{
    private static final Project PROJECT = Mockito.mock(Project.class);

    private static final String CONFIG_FILE = "config-ok.xml";

    private static Module sModuleMock = null;

    private static TabWidthAndBaseDirProvider sConfigurationsMock = null;


    @BeforeClass
    public static void setUp() throws URISyntaxException {

        sModuleMock = Mockito.mock(Module.class);
        Mockito.when(sModuleMock.getProject()).thenReturn(PROJECT);

        sConfigurationsMock = Mockito.mock(TabWidthAndBaseDirProvider.class);
        Mockito.when(sConfigurationsMock.tabWidth()).thenReturn(2);
        Mockito.when(sConfigurationsMock.baseDir()).thenReturn(  //
                Optional.of(new File(OpCreateCheckerTest.class.getResource(CONFIG_FILE).toURI()).getParent()));

        CheckstyleProjectService csServiceMock = Mockito.mock(CheckstyleProjectService.class);
        Mockito.when(csServiceMock.getCheckstyleInstance()).thenReturn(Mockito.mock(CheckstyleActions.class));
        CheckstyleProjectService.activateMock4UnitTesting(csServiceMock);
    }

    @AfterClass
    public static void tearDown() {
        CheckstyleProjectService.activateMock4UnitTesting(null);
    }


    @Test
    public void testCreateCheckerWithConfigsMock() throws IOException, URISyntaxException {

        final ConfigurationLocation configLoc = new StringConfigurationLocation( //
                FileUtil.readFile("cmd/" + CONFIG_FILE));

        final CheckStyleChecker checker = new CheckstyleActionsImpl(PROJECT).createChecker(sModuleMock, configLoc,
                Collections.emptyMap(), sConfigurationsMock, getClass().getClassLoader());
        Assert.assertNotNull(checker);
    }


    @Test(expected = CheckStylePluginException.class)
    public void testCreateChecker_noConfigLoc() throws IOException, URISyntaxException {

        //noinspection ConstantConditions
        new CheckstyleActionsImpl(PROJECT).createChecker(sModuleMock, null, Collections.emptyMap(),
                sConfigurationsMock, getClass().getClassLoader());
        Assert.fail("expected exception was not thrown");
    }


    @Test
    public void testCreateChecker_noModule() throws IOException, URISyntaxException {

        final ConfigurationLocation configLoc = new StringConfigurationLocation( //
                FileUtil.readFile("cmd/" + CONFIG_FILE));

        //noinspection ConstantConditions
        CheckStyleChecker checker = new CheckstyleActionsImpl(PROJECT).createChecker(null, configLoc, Collections
                .emptyMap(), sConfigurationsMock, getClass().getClassLoader());
        Assert.assertNotNull(checker);
    }
}
