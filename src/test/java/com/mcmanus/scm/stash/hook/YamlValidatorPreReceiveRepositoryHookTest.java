package com.mcmanus.scm.stash.hook;

import com.atlassian.bitbucket.commit.Commit;
import com.atlassian.bitbucket.commit.CommitService;
import com.atlassian.bitbucket.commit.MinimalCommit;
import com.atlassian.bitbucket.content.*;
import com.atlassian.bitbucket.idx.CommitIndex;
import com.atlassian.bitbucket.property.PropertyMap;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.user.Person;
import com.atlassian.bitbucket.util.Page;
import com.atlassian.bitbucket.util.PageRequest;
import com.atlassian.bitbucket.util.PageUtils;
import com.mcmanus.scm.stash.hook.YamlValidatorPreReceiveRepositoryHook;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YamlValidatorPreReceiveRepositoryHookTest {

    // Required testing holes:
    // 1) Need to make sure the temp files/directory is deleted once the validation is complete
    // 2) Need to check that a commit with an incorrect file and then a commit with a fix to that file works
    // 3) Need to check if a non yaml file is allowed
    // 4) Need to check if a commit with a yaml file and a text file works
    // 5) Need to check if a commit with a incorrect yaml file and a text file works
    // 6) Need to check if a delete of an incorrect yaml file works

    @Test
    public void shouldCheckAndAddFilesWithParticularExtension() {

        CommitService commitServiceMock = mock(CommitService.class);
        ContentService contentServiceMock = mock(ContentService.class);
        CommitIndex commitIndexMock = mock(CommitIndex.class);

        Commit commitMock = mock(Commit.class);
        Repository repositoryMock = mock(Repository.class);

        ChangesRequest.Builder builderMock = mock(ChangesRequest.Builder.class);
        ChangesRequest changesRequestMock = mock(ChangesRequest.class);

        Change change = mock(Change.class);
        ArrayList<Change> changes = new ArrayList<>();
        changes.add(change);

        Page<Change> pagedChanges = PageUtils.createPage(changes, PageUtils.newRequest(1, 1));

        when(commitMock.getId()).thenReturn("asdfh329fhpehguh");
        when(builderMock.build()).thenReturn(changesRequestMock);
        when(commitServiceMock.getChanges(any(ChangesRequest.class), any(PageRequest.class))).thenReturn(pagedChanges);
        when(change.getType()).thenReturn(ChangeType.ADD);
        when(change.getPath()).thenReturn(new SimplePath("/right/here.yaml"));

        YamlValidatorPreReceiveRepositoryHook hook = new YamlValidatorPreReceiveRepositoryHook(commitServiceMock,
                contentServiceMock, commitIndexMock);

        ConcurrentMap<String, Commit> testPathChanges = new ConcurrentHashMap<>();

        hook.addFileChangesOnCommit(testPathChanges, repositoryMock, commitMock, "yaml");

        assertThat(testPathChanges.size(), is(1));
    }
}
