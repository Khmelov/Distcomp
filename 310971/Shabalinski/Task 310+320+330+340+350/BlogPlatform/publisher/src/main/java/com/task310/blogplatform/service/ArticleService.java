package com.task310.blogplatform.service;

import com.task310.blogplatform.dto.ArticleRequestTo;
import com.task310.blogplatform.dto.ArticleResponseTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.exception.DuplicateException;
import com.task310.blogplatform.exception.EntityNotFoundException;
import com.task310.blogplatform.exception.ValidationException;
import com.task310.blogplatform.mapper.ArticleMapper;
import com.task310.blogplatform.model.Article;
import com.task310.blogplatform.model.Label;
import com.task310.blogplatform.model.User;
import com.task310.blogplatform.repository.ArticleRepository;
import com.task310.blogplatform.repository.LabelRepository;
import com.task310.blogplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final ArticleMapper mapper;

    @Autowired
    public ArticleService(
            ArticleRepository articleRepository,
            UserRepository userRepository,
            LabelRepository labelRepository,
            ArticleMapper mapper) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.mapper = mapper;
    }

    @Caching(evict = {
        @CacheEvict(value = "articles", allEntries = true),
        @CacheEvict(value = "articlesByUser", allEntries = true),
        @CacheEvict(value = "articlesByLabel", allEntries = true),
        @CacheEvict(value = "articlesByFilter", allEntries = true)
    })
    public ArticleResponseTo create(ArticleRequestTo dto) {
        validateArticleRequest(dto);
        
        // Check for duplicate title
        if (articleRepository.findByTitle(dto.getTitle().trim()).isPresent()) {
            throw new DuplicateException("Article with title '" + dto.getTitle() + "' already exists");
        }
        
        Article article = mapper.toEntity(dto);
        
        // Validate and set user
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));
            article.setUser(user);
            article.setUserId(user.getId());
        }
        
        // Validate and set labels
        List<Label> labels = new ArrayList<>();
        
        // Process labelIds if provided
        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            for (Long labelId : dto.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new EntityNotFoundException("Label not found with id: " + labelId));
                labels.add(label);
            }
        }
        
        // Process labelNames if provided - create labels if they don't exist
        List<String> labelNamesToProcess = new ArrayList<>();
        if (dto.getLabelNames() != null && !dto.getLabelNames().isEmpty()) {
            labelNamesToProcess.addAll(dto.getLabelNames());
        }
        // Also support "labels" field as alias for "labelNames"
        if (dto.getLabels() != null && !dto.getLabels().isEmpty()) {
            labelNamesToProcess.addAll(dto.getLabels());
        }
        
        if (!labelNamesToProcess.isEmpty()) {
            for (String labelName : labelNamesToProcess) {
                if (labelName != null && !labelName.trim().isEmpty()) {
                    String trimmedName = labelName.trim();
                    // Validate label name length
                    if (trimmedName.length() < 2) {
                        throw new ValidationException("Label name must be at least 2 characters long");
                    }
                    if (trimmedName.length() > 32) {
                        throw new ValidationException("Label name must not exceed 32 characters");
                    }
                    Label label = labelRepository.findByName(trimmedName)
                            .orElseGet(() -> {
                                // Create new label if it doesn't exist
                                Label newLabel = new Label();
                                newLabel.setName(trimmedName);
                                return labelRepository.save(newLabel);
                            });
                    // Avoid duplicates by ID
                    boolean alreadyExists = labels.stream()
                            .anyMatch(l -> l.getId() != null && l.getId().equals(label.getId()));
                    if (!alreadyExists) {
                        labels.add(label);
                    }
                }
            }
        }
        
        if (!labels.isEmpty()) {
            article.setLabels(labels);
        }
        
        Article saved = articleRepository.save(article);
        return mapper.toResponseDto(saved);
    }

    @Cacheable(value = "articles", key = "'all'")
    public List<ArticleResponseTo> findAll() {
        return mapper.toResponseDtoList(articleRepository.findAll());
    }

    @Cacheable(value = "articles", key = "#id")
    public ArticleResponseTo findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid article id");
        }
        return articleRepository.findById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    @Caching(evict = {
        @CacheEvict(value = "articles", key = "#id"),
        @CacheEvict(value = "articles", key = "'all'"),
        @CacheEvict(value = "articlesByUser", allEntries = true),
        @CacheEvict(value = "articlesByLabel", allEntries = true),
        @CacheEvict(value = "articlesByFilter", allEntries = true)
    })
    public ArticleResponseTo update(Long id, ArticleRequestTo dto) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid article id");
        }
        validateArticleRequest(dto);
        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
        
        // Check for duplicate title (excluding current article)
        articleRepository.findByTitle(dto.getTitle().trim())
                .ifPresent(article -> {
                    if (!article.getId().equals(id)) {
                        throw new DuplicateException("Article with title '" + dto.getTitle() + "' already exists");
                    }
                });
        
        mapper.updateEntityFromDto(dto, existing);
        
        // Update user relationship
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));
            existing.setUser(user);
            existing.setUserId(user.getId());
        }
        
        // Update labels relationship
        List<Label> labels = new ArrayList<>();
        
        // Process labelIds if provided
        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            for (Long labelId : dto.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new EntityNotFoundException("Label not found with id: " + labelId));
                labels.add(label);
            }
        }
        
        // Process labelNames if provided - create labels if they don't exist
        List<String> labelNamesToProcess = new ArrayList<>();
        if (dto.getLabelNames() != null && !dto.getLabelNames().isEmpty()) {
            labelNamesToProcess.addAll(dto.getLabelNames());
        }
        // Also support "labels" field as alias for "labelNames"
        if (dto.getLabels() != null && !dto.getLabels().isEmpty()) {
            labelNamesToProcess.addAll(dto.getLabels());
        }
        
        if (!labelNamesToProcess.isEmpty()) {
            for (String labelName : labelNamesToProcess) {
                if (labelName != null && !labelName.trim().isEmpty()) {
                    String trimmedName = labelName.trim();
                    // Validate label name length
                    if (trimmedName.length() < 2) {
                        throw new ValidationException("Label name must be at least 2 characters long");
                    }
                    if (trimmedName.length() > 32) {
                        throw new ValidationException("Label name must not exceed 32 characters");
                    }
                    Label label = labelRepository.findByName(trimmedName)
                            .orElseGet(() -> {
                                // Create new label if it doesn't exist
                                Label newLabel = new Label();
                                newLabel.setName(trimmedName);
                                return labelRepository.save(newLabel);
                            });
                    // Avoid duplicates by ID
                    boolean alreadyExists = labels.stream()
                            .anyMatch(l -> l.getId() != null && l.getId().equals(label.getId()));
                    if (!alreadyExists) {
                        labels.add(label);
                    }
                }
            }
        }
        
        // Only update labels if at least one of labelIds, labelNames, or labels was provided
        if (dto.getLabelIds() != null || dto.getLabelNames() != null || dto.getLabels() != null) {
            existing.setLabels(labels);
        }
        
        Article updated = articleRepository.save(existing);
        return mapper.toResponseDto(updated);
    }

    @Caching(evict = {
        @CacheEvict(value = "articles", key = "#id"),
        @CacheEvict(value = "articles", key = "'all'"),
        @CacheEvict(value = "articlesByUser", allEntries = true),
        @CacheEvict(value = "articlesByLabel", allEntries = true),
        @CacheEvict(value = "articlesByFilter", allEntries = true)
    })
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid article id");
        }
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
        
        // Get labels before deleting the article
        List<Label> labelsToCheck = new ArrayList<>(article.getLabels());
        
        // Delete the article
        articleRepository.deleteById(id);
        
        // Check and delete labels that are no longer associated with any article
        for (Label label : labelsToCheck) {
            if (label.getId() != null) {
                long articleCount = labelRepository.countArticlesByLabelId(label.getId());
                if (articleCount == 0) {
                    // Label is no longer associated with any article, delete it
                    labelRepository.deleteById(label.getId());
                }
            }
        }
    }

    @Cacheable(value = "articlesByUser", key = "#articleId")
    public UserResponseTo getUserByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            throw new ValidationException("Invalid article id");
        }
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + articleId));
        if (article.getUser() == null) {
            throw new EntityNotFoundException("User not found for article with id: " + articleId);
        }
        // We need UserMapper here, but to avoid circular dependency, let's create a simple conversion
        User user = article.getUser();
        UserResponseTo response = new UserResponseTo();
        response.setId(user.getId());
        response.setLogin(user.getLogin());
        response.setPassword(user.getPassword());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setCreated(user.getCreated());
        response.setModified(user.getModified());
        return response;
    }

    @Cacheable(value = "articlesByLabel", key = "#articleId")
    public List<LabelResponseTo> getLabelsByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            throw new ValidationException("Invalid article id");
        }
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + articleId));
        return article.getLabels().stream()
                .map(label -> {
                    LabelResponseTo response = new LabelResponseTo();
                    response.setId(label.getId());
                    response.setName(label.getName());
                    response.setCreated(label.getCreated());
                    response.setModified(label.getModified());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Cacheable(value = "articlesByFilter", key = "#labelName + '_' + (#labelIds != null ? #labelIds.toString() : 'null') + '_' + (#userLogin != null ? #userLogin : 'null') + '_' + (#title != null ? #title : 'null') + '_' + (#content != null ? #content : 'null')")
    public List<ArticleResponseTo> findByFilters(String labelName, List<Long> labelIds, String userLogin, String title, String content) {
        List<Article> allArticles = articleRepository.findAll();
        return allArticles.stream()
                .filter(article -> {
                    if (labelName != null && !labelName.trim().isEmpty()) {
                        boolean hasLabel = article.getLabels().stream()
                                .anyMatch(label -> label.getName().equalsIgnoreCase(labelName));
                        if (!hasLabel) return false;
                    }
                    if (labelIds != null && !labelIds.isEmpty()) {
                        boolean hasLabelId = article.getLabels().stream()
                                .anyMatch(label -> labelIds.contains(label.getId()));
                        if (!hasLabelId) return false;
                    }
                    if (userLogin != null && !userLogin.trim().isEmpty()) {
                        if (article.getUser() == null || !userLogin.equalsIgnoreCase(article.getUser().getLogin())) {
                            return false;
                        }
                    }
                    if (title != null && !title.trim().isEmpty()) {
                        if (article.getTitle() == null || !article.getTitle().toLowerCase().contains(title.toLowerCase())) {
                            return false;
                        }
                    }
                    if (content != null && !content.trim().isEmpty()) {
                        if (article.getContent() == null || !article.getContent().toLowerCase().contains(content.toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void validateArticleRequest(ArticleRequestTo dto) {
        if (dto == null) {
            throw new ValidationException("Article data is required");
        }
        if (dto.getId() != null) {
            throw new ValidationException("Id must not be provided in request body");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title is required");
        }
        if (dto.getTitle().trim().length() < 2) {
            throw new ValidationException("Title must be at least 2 characters long");
        }
        if (dto.getTitle().trim().length() > 64) {
            throw new ValidationException("Title must not exceed 64 characters");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
        // Validate that content is not just a number (when converted from JSON number to string)
        String contentStr = dto.getContent().trim();
        if (contentStr.matches("^-?\\d+$")) {
            throw new ValidationException("Content must be a text string, not a number");
        }
        if (dto.getUserId() == null || dto.getUserId() <= 0) {
            throw new ValidationException("Valid userId is required");
        }
    }
}

