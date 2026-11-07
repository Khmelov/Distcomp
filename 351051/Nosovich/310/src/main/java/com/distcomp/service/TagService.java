package com.distcomp.service;

import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;

import java.util.List;

/**
 * Business operations over {@link com.distcomp.model.Tag} entities.
 */
public interface TagService {

    /**
     * Creates a new tag.
     *
     * @param request validated payload
     * @return the stored tag
     */
    TagResponseTo create(TagRequestTo request);

    /**
     * @param id identifier of the tag
     * @return the tag with this identifier
     */
    TagResponseTo findById(Long id);

    /**
     * @return every stored tag
     */
    List<TagResponseTo> findAll();

    /**
     * Replaces the fields of an existing tag.
     *
     * @param id      identifier of the tag to change
     * @param request validated payload
     * @return the updated tag
     */
    TagResponseTo update(Long id, TagRequestTo request);

    /**
     * Removes a tag and detaches it from every issue that used it.
     *
     * @param id identifier of the tag to remove
     */
    void delete(Long id);

    /**
     * @param issueId identifier of the issue
     * @return every tag attached to this issue
     */
    List<TagResponseTo> findByIssueId(Long issueId);
}
