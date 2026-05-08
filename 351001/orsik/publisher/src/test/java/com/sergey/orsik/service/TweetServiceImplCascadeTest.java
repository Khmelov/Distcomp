package com.sergey.orsik.service;

import com.sergey.orsik.client.DiscussionCommentsClient;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.TweetMapper;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.repository.LabelRepository;
import com.sergey.orsik.repository.TweetRepository;
import com.sergey.orsik.service.impl.TweetDeletionHelper;
import com.sergey.orsik.service.impl.TweetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetServiceImplCascadeTest {

    @Mock
    private TweetRepository tweetRepository;
    @Mock
    private CreatorRepository creatorRepository;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private TweetMapper tweetMapper;
    @Mock
    private DiscussionCommentsClient discussionCommentsClient;
    @Mock
    private TweetDeletionHelper tweetDeletionHelper;

    @Test
    void deleteByIdCallsDiscussionBeforeDbDelete() {
        TweetServiceImpl service = new TweetServiceImpl(
                tweetRepository,
                creatorRepository,
                labelRepository,
                tweetMapper,
                discussionCommentsClient,
                tweetDeletionHelper);
        when(tweetRepository.existsById(10L)).thenReturn(true);

        service.deleteById(10L);

        verify(discussionCommentsClient).deleteAllForTweet(10L);
        verify(tweetDeletionHelper).deleteTweetAndOrphanLabels(10L);
    }

    @Test
    void deleteByIdDoesNotCallDiscussionWhenTweetMissing() {
        TweetServiceImpl service = new TweetServiceImpl(
                tweetRepository,
                creatorRepository,
                labelRepository,
                tweetMapper,
                discussionCommentsClient,
                tweetDeletionHelper);
        when(tweetRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(99L)).isInstanceOf(EntityNotFoundException.class);
        verifyNoInteractions(discussionCommentsClient);
        verifyNoInteractions(tweetDeletionHelper);
    }
}
