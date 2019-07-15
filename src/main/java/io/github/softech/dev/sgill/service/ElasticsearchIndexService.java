package io.github.softech.dev.sgill.service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.softech.dev.sgill.domain.*;
import io.github.softech.dev.sgill.repository.*;
import io.github.softech.dev.sgill.repository.search.*;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.indices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToMany;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ElasticsearchIndexService {

    private static final Lock reindexLock = new ReentrantLock();

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final CartRepository cartRepository;

    private final CartSearchRepository cartSearchRepository;

    private final CertificateRepository certificateRepository;

    private final CertificateSearchRepository certificateSearchRepository;

    private final ChoiceRepository choiceRepository;

    private final ChoiceSearchRepository choiceSearchRepository;

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    private final CourseRepository courseRepository;

    private final CourseSearchRepository courseSearchRepository;

    private final CourseCartBridgeRepository courseCartBridgeRepository;

    private final CourseCartBridgeSearchRepository courseCartBridgeSearchRepository;

    private final CourseHistoryRepository courseHistoryRepository;

    private final CourseHistorySearchRepository courseHistorySearchRepository;

    private final CustomerRepository customerRepository;

    private final CustomerSearchRepository customerSearchRepository;

    private final OrdersRepository ordersRepository;

    private final OrdersSearchRepository ordersSearchRepository;

    private final QuestionRepository questionRepository;

    private final QuestionSearchRepository questionSearchRepository;

    private final QuestionHistoryRepository questionHistoryRepository;

    private final QuestionHistorySearchRepository questionHistorySearchRepository;

    private final QuizRepository quizRepository;

    private final QuizSearchRepository quizSearchRepository;

    private final QuizHistoryRepository quizHistoryRepository;

    private final QuizHistorySearchRepository quizHistorySearchRepository;

    private final SectionRepository sectionRepository;

    private final SectionSearchRepository sectionSearchRepository;

    private final SectionHistoryRepository sectionHistoryRepository;

    private final SectionHistorySearchRepository sectionHistorySearchRepository;

    private final TimeCourseLogRepository timeCourseLogRepository;

    private final TimeCourseLogSearchRepository timeCourseLogSearchRepository;

    private final TopicRepository topicRepository;

    private final TopicSearchRepository topicSearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final BookmarkRepository bookmarkRepository;

    private final BookmarkSearchRepository bookmarkSearchRepository;

    private final CompanyRequestRepository companyRequestRepository;

    private final CompanyRequestSearchRepository companyRequestSearchRepository;

    private final LegacyCoursesRepository legacyCoursesRepository;

    private final LegacyCoursesSearchRepository legacyCoursesSearchRepository;

    private final MergeFunctionRepository mergeFunctionRepository;

    private final MergeFunctionSearchRepository mergeFunctionSearchRepository;

    private final QuizAppRepository quizAppRepository;

    private final QuizAppSearchRepository quizAppSearchRepository;

    private final ServicelistRepository servicelistRepository;

    private final ServicelistSearchRepository servicelistSearchRepository;

    private final TagsRepository tagsRepository;

    private final TagsSearchRepository tagsSearchRepository;

    private JestElasticsearchTemplate jestElasticsearchTemplate;

    public ElasticsearchIndexService(CartRepository cartRepository, CartSearchRepository cartSearchRepository,
                                     CertificateRepository certificateRepository, CertificateSearchRepository certificateSearchRepository,
                                     ChoiceRepository choiceRepository, ChoiceSearchRepository choiceSearchRepository, CompanyRepository companyRepository,
                                     CompanySearchRepository companySearchRepository, CourseRepository courseRepository,
                                     CourseSearchRepository courseSearchRepository, CourseCartBridgeRepository courseCartBridgeRepository,
                                     CourseCartBridgeSearchRepository courseCartBridgeSearchRepository, CourseHistoryRepository courseHistoryRepository,
                                     CourseHistorySearchRepository courseHistorySearchRepository, CustomerRepository customerRepository,
                                     CustomerSearchRepository customerSearchRepository, OrdersRepository ordersRepository, OrdersSearchRepository ordersSearchRepository,
                                     QuestionRepository questionRepository, QuestionSearchRepository questionSearchRepository, QuestionHistoryRepository questionHistoryRepository,
                                     QuestionHistorySearchRepository questionHistorySearchRepository, QuizRepository quizRepository, QuizSearchRepository quizSearchRepository,
                                     QuizHistoryRepository quizHistoryRepository, QuizHistorySearchRepository quizHistorySearchRepository, SectionRepository sectionRepository,
                                     SectionSearchRepository sectionSearchRepository, SectionHistoryRepository sectionHistoryRepository,
                                     SectionHistorySearchRepository sectionHistorySearchRepository, TimeCourseLogRepository timeCourseLogRepository,
                                     TimeCourseLogSearchRepository timeCourseLogSearchRepository, TopicRepository topicRepository, TopicSearchRepository topicSearchRepository,
                                     UserRepository userRepository, UserSearchRepository userSearchRepository, BookmarkRepository bookmarkRepository, BookmarkSearchRepository bookmarkSearchRepository,
                                     CompanyRequestRepository companyRequestRepository, CompanyRequestSearchRepository companyRequestSearchRepository, LegacyCoursesRepository legacyCoursesRepository,
                                     LegacyCoursesSearchRepository legacyCoursesSearchRepository, MergeFunctionRepository mergeFunctionRepository,
                                     MergeFunctionSearchRepository mergeFunctionSearchRepository, QuizAppRepository quizAppRepository,
                                     QuizAppSearchRepository quizAppSearchRepository, ServicelistRepository servicelistRepository,
                                     ServicelistSearchRepository servicelistSearchRepository, TagsRepository tagsRepository, TagsSearchRepository tagsSearchRepository,
                                     JestElasticsearchTemplate jestElasticsearchTemplate) {
        this.cartRepository = cartRepository;
        this.cartSearchRepository = cartSearchRepository;
        this.certificateRepository = certificateRepository;
        this.certificateSearchRepository = certificateSearchRepository;
        this.choiceRepository = choiceRepository;
        this.choiceSearchRepository = choiceSearchRepository;
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
        this.courseRepository = courseRepository;
        this.courseSearchRepository = courseSearchRepository;
        this.courseCartBridgeRepository = courseCartBridgeRepository;
        this.courseCartBridgeSearchRepository = courseCartBridgeSearchRepository;
        this.courseHistoryRepository = courseHistoryRepository;
        this.courseHistorySearchRepository = courseHistorySearchRepository;
        this.customerRepository = customerRepository;
        this.customerSearchRepository = customerSearchRepository;
        this.ordersRepository = ordersRepository;
        this.ordersSearchRepository = ordersSearchRepository;
        this.questionRepository = questionRepository;
        this.questionSearchRepository = questionSearchRepository;
        this.questionHistoryRepository = questionHistoryRepository;
        this.questionHistorySearchRepository = questionHistorySearchRepository;
        this.quizRepository = quizRepository;
        this.quizSearchRepository = quizSearchRepository;
        this.quizHistoryRepository = quizHistoryRepository;
        this.quizHistorySearchRepository = quizHistorySearchRepository;
        this.sectionRepository = sectionRepository;
        this.sectionSearchRepository = sectionSearchRepository;
        this.sectionHistoryRepository = sectionHistoryRepository;
        this.sectionHistorySearchRepository = sectionHistorySearchRepository;
        this.timeCourseLogRepository = timeCourseLogRepository;
        this.timeCourseLogSearchRepository = timeCourseLogSearchRepository;
        this.topicRepository = topicRepository;
        this.topicSearchRepository = topicSearchRepository;
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkSearchRepository = bookmarkSearchRepository;
        this.companyRequestRepository = companyRequestRepository;
        this.companyRequestSearchRepository = companyRequestSearchRepository;
        this.legacyCoursesRepository = legacyCoursesRepository;
        this.legacyCoursesSearchRepository = legacyCoursesSearchRepository;
        this.mergeFunctionRepository = mergeFunctionRepository;
        this.mergeFunctionSearchRepository = mergeFunctionSearchRepository;
        this.quizAppRepository = quizAppRepository;
        this.quizAppSearchRepository = quizAppSearchRepository;
        this.servicelistRepository = servicelistRepository;
        this.servicelistSearchRepository = servicelistSearchRepository;
        this.tagsRepository = tagsRepository;
        this.tagsSearchRepository = tagsSearchRepository;
        this.jestElasticsearchTemplate = jestElasticsearchTemplate;
    }

    @Async
    @Timed
    public void reindexAll() {
        if (reindexLock.tryLock()) {
            try {
                reindexForClass(Cart.class, cartRepository, cartSearchRepository);
                reindexForClass(Certificate.class, certificateRepository, certificateSearchRepository);
                reindexForClass(Choice.class, choiceRepository, choiceSearchRepository);
                reindexForClass(Company.class, companyRepository, companySearchRepository);
                reindexForClass(CompanyRequest.class, companyRequestRepository, companyRequestSearchRepository);
                reindexForClass(Course.class, courseRepository, courseSearchRepository);
                reindexForClass(CourseCartBridge.class, courseCartBridgeRepository, courseCartBridgeSearchRepository);
                reindexForClass(CourseHistory.class, courseHistoryRepository, courseHistorySearchRepository);
                reindexForClass(Customer.class, customerRepository, customerSearchRepository);
                reindexForClass(LegacyCourses.class, legacyCoursesRepository, legacyCoursesSearchRepository);
                reindexForClass(MergeFunction.class, mergeFunctionRepository, mergeFunctionSearchRepository);
                reindexForClass(Orders.class, ordersRepository, ordersSearchRepository);
                reindexForClass(Question.class, questionRepository, questionSearchRepository);
                reindexForClass(QuestionHistory.class, questionHistoryRepository, questionHistorySearchRepository);
                reindexForClass(Quiz.class, quizRepository, quizSearchRepository);
                reindexForClass(QuizApp.class, quizAppRepository, quizAppSearchRepository);
                reindexForClass(QuizHistory.class, quizHistoryRepository, quizHistorySearchRepository);
                reindexForClass(Section.class, sectionRepository, sectionSearchRepository);
                reindexForClass(SectionHistory.class, sectionHistoryRepository, sectionHistorySearchRepository);
                reindexForClass(Servicelist.class, servicelistRepository, servicelistSearchRepository);
                reindexForClass(Tags.class, tagsRepository, tagsSearchRepository);
                reindexForClass(TimeCourseLog.class, timeCourseLogRepository, timeCourseLogSearchRepository);
                reindexForClass(Topic.class, topicRepository, topicSearchRepository);
                reindexForClass(User.class, userRepository, userSearchRepository);
                reindexForClass(Bookmark.class, bookmarkRepository, bookmarkSearchRepository);
                log.info("Elasticsearch: Successfully performed reindexing");
            } finally {
                reindexLock.unlock();
            }
        } else {
            log.info("Elasticsearch: concurrent reindexing attempt");
        }
    }

    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        Map settings = jestElasticsearchTemplate.getSetting(entityClass);
        Map newSetting = new HashMap();
        newSetting.put("index.number_of_shards", "1");
        newSetting.put("index.number_of_replicas", "0");
        newSetting.put("index.refresh_interval", "1s");
        newSetting.put("index.auto_expand_replicas", null);
        newSetting.put("index.store.type", "fs");
        newSetting.put("priority", "50");
        jestElasticsearchTemplate.deleteIndex(entityClass);
        try {
            jestElasticsearchTemplate.createIndex(entityClass, newSetting);
            //jestElasticsearchTemplate.createIndex(entityClass);
        } catch (ResourceAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        jestElasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            // if a JHipster entity field is the owner side of a many-to-many relationship, it should be loaded manually
            List<Method> relationshipGetters = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.getType().equals(Set.class))
                .filter(field -> field.getAnnotation(ManyToMany.class) != null)
                .filter(field -> field.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
                .filter(field -> field.getAnnotation(JsonIgnore.class) == null)
                .map(field -> {
                    try {
                        return new PropertyDescriptor(field.getName(), entityClass).getReadMethod();
                    } catch (IntrospectionException e) {
                        log.error("Error retrieving getter for class {}, field {}. Field will NOT be indexed",
                            entityClass.getSimpleName(), field.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            int size = 100;
            for (int i = 0; i <= jpaRepository.count() / size; i++) {
                Pageable page = new PageRequest(i, size);
                log.info("Indexing page {} of {}, size {}", i, jpaRepository.count() / size, size);
                Page<T> results = jpaRepository.findAll(page);
                results.map(result -> {
                    // if there are any relationships to load, do it now
                    relationshipGetters.forEach(method -> {
                        try {
                            // eagerly load the relationship set
                            ((Set) method.invoke(result)).size();
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                        }
                    });
                    return result;
                });
                elasticsearchRepository.saveAll(results.getContent());
            }
        }
        log.info("Elasticsearch: Indexed all rows for {}", entityClass.getSimpleName());
    }
}
