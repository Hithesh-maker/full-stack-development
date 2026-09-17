package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find upcoming events ordered by date ascending
    List<Event> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);

    // Filter by department
    List<Event> findByDepartmentIgnoreCase(String department);

    // Filter by event type
    List<Event> findByTypeIgnoreCase(String type);

    // Filter by department and event type
    List<Event> findByDepartmentIgnoreCaseAndTypeIgnoreCase(
            String department,
            String type
    );

    // Search by keyword, department, type, and date
    @Query("SELECT e FROM Event e WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.venue) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:department IS NULL OR :department = '' OR LOWER(e.department) = LOWER(:department)) AND " +
           "(:type IS NULL OR :type = '' OR LOWER(e.type) = LOWER(:type)) AND " +
           "(:date IS NULL OR e.date = :date) " +
           "ORDER BY e.date ASC")
    List<Event> searchEvents(
            @Param("keyword") String keyword,
            @Param("department") String department,
            @Param("type") String type,
            @Param("date") LocalDate date
    );

    // Search events without date filter (backward compatibility)
    default List<Event> searchEvents(String keyword, String department, String type) {
        return searchEvents(keyword, department, type, null);
    }

    long countByDateGreaterThanEqual(LocalDate date);
}