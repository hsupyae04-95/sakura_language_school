package com.sakura.languageschool.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.ClassBatch;
import com.sakura.languageschool.model.Course;
import com.sakura.languageschool.model.TeacherProfile;
import com.sakura.languageschool.model.dto.ClassBatchRequest;
import com.sakura.languageschool.model.dto.ClassBatchResponse;
import com.sakura.languageschool.model.dto.EnrollmentResponse;
import com.sakura.languageschool.model.enums.BatchStatus;
import com.sakura.languageschool.model.enums.EnrollmentStatus;
import com.sakura.languageschool.repository.ClassBatchDao;
import com.sakura.languageschool.repository.CourseDao;
import com.sakura.languageschool.repository.EnrollmentDao;
import com.sakura.languageschool.repository.TeacherDao;
import com.sakura.languageschool.service.ClassBatchService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassBatchServiceImpl implements ClassBatchService {
	private final ClassBatchDao classBatchDao;
	private final CourseDao courseDao;
	private final TeacherDao teacherDao;
	private final EnrollmentDao enrollmentDao;
	private final Mapper mapper;

	public ClassBatchServiceImpl(
			ClassBatchDao classBatchDao,
			CourseDao courseDao,
			TeacherDao teacherDao,
			EnrollmentDao enrollmentDao,
			Mapper mapper) {
		this.classBatchDao = classBatchDao;
		this.courseDao = courseDao;
		this.teacherDao = teacherDao;
		this.enrollmentDao = enrollmentDao;
		this.mapper = mapper;
	}

    @Override
    @Transactional
    public List<ClassBatchResponse> getAllBatches() {

        return classBatchDao.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<ClassBatchResponse> getBatchesByCourse(Long courseId) {

        // Verify that the course exists and is active
        findActiveCourse(courseId);

        return classBatchDao.findByCourse_Id(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClassBatchResponse getBatchById(Long id) {

        ClassBatch batch = findBatch(id);

        return mapToResponse(batch);
    }

    @Override
    @Transactional
    public ClassBatchResponse createBatch(ClassBatchRequest request) {

        // 1. Find active course
        Course course = findActiveCourse(request.getCourseId());

        // 2. Check duplicate batch name within the course
        if (classBatchDao.existsByCourse_IdAndBatchName(
                request.getCourseId(), request.getBatchName())) {

            throw new IllegalArgumentException(
                    "Batch name already exists for this course");
        }

        // 3. Validate dates
        validateBatchDates(
                request.getStartDate(), request.getEndDate());

        // 4. Create batch
        ClassBatch batch = new ClassBatch();

        batch.setCourse(course);
        batch.setBatchName(request.getBatchName());
        batch.setClassType(request.getClassType());
        batch.setStartDate(request.getStartDate());
        batch.setEndDate(request.getEndDate());
        batch.setSchedule(request.getSchedule());
        batch.setCapacity(request.getCapacity());

        // 5. Assign teacher if provided
        if (request.getTeacherId() != null) {
            TeacherProfile teacher =
                    findActiveTeacher(request.getTeacherId());

            batch.setTeacher(teacher);
        }

        // 6. Set initial status
        batch.setStatus(BatchStatus.OPEN);

        ClassBatch savedBatch = classBatchDao.save(batch);

        return mapToResponse(savedBatch);
    }

    @Override
    @Transactional
    public ClassBatchResponse updateBatch(
            Long id, ClassBatchRequest request) {

        // 1. Find existing batch
        ClassBatch batch = findBatch(id);

        // 2. Find active course
        Course course = findActiveCourse(request.getCourseId());

        // 3. Check duplicate name, excluding current batch
        if (classBatchDao.existsByCourse_IdAndBatchNameAndIdNot(
                request.getCourseId(),
                request.getBatchName(),
                id)) {

            throw new IllegalArgumentException(
                    "Batch name already exists for this course");
        }

        // 4. Validate dates
        validateBatchDates(
                request.getStartDate(), request.getEndDate());

        // 5. Update batch properties
        batch.setCourse(course);
        batch.setBatchName(request.getBatchName());
        batch.setClassType(request.getClassType());
        batch.setStartDate(request.getStartDate());
        batch.setEndDate(request.getEndDate());
        batch.setSchedule(request.getSchedule());
        batch.setCapacity(request.getCapacity());

        // 6. Update teacher only when teacherId is provided
        if (request.getTeacherId() != null) {
            TeacherProfile teacher =
                    findActiveTeacher(request.getTeacherId());

            batch.setTeacher(teacher);
        }

        ClassBatch updatedBatch = classBatchDao.save(batch);

        return mapToResponse(updatedBatch);
    }

    @Override
    @Transactional
    public void assignTeacher(Long batchId, Long teacherId) {

        ClassBatch batch = findBatch(batchId);

        TeacherProfile teacher = findActiveTeacher(teacherId);

        batch.setTeacher(teacher);

        classBatchDao.save(batch);
    }

    @Override
    @Transactional
    public void updateBatchStatus(Long id, BatchStatus status) {

        ClassBatch batch = findBatch(id);

        validateStatusTransition(batch.getStatus(), status);

        batch.setStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassBatchResponse> getTeacherAssignedBatches(String username) {
        return classBatchDao.findByTeacher_User_Username(username)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getApprovedStudents(
            Long batchId, String teacherUsername) {
        classBatchDao.findByIdAndTeacher_User_Username(batchId, teacherUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Assigned batch not found with id: " + batchId));

        return enrollmentDao.findByBatch_IdAndStatus(
                    batchId, com.sakura.languageschool.model.enums.EnrollmentStatus.APPROVED)
                .stream()
                .map(this::mapEnrollmentToResponse)
                .toList();
    }

    // Find batch
    private ClassBatch findBatch(Long id) {

        return classBatchDao.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Batch not found with id: " + id));
    }

    // Find active course
    private Course findActiveCourse(Long courseId) {

        return courseDao.findByIdAndActiveTrue(courseId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Active course not found with id: "
                                        + courseId));
    }

    // Find active teacher
    private TeacherProfile findActiveTeacher(Long teacherId) {

        return teacherDao.findByIdAndActiveTrue(teacherId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Active teacher not found with id: "
                                        + teacherId));
    }

    // Validate batch dates
    private void validateBatchDates(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }
    }

    // Validate allowed status transitions
    private void validateStatusTransition(
            BatchStatus current, BatchStatus next) {

        if (current == next) {
            return;
        }

        boolean allowed = switch (current) {
            case OPEN -> next == BatchStatus.CLOSED
                    || next == BatchStatus.ONGOING
                    || next == BatchStatus.CANCELLED;

            case CLOSED -> next == BatchStatus.OPEN
                    || next == BatchStatus.CANCELLED;

            case ONGOING -> next == BatchStatus.COMPLETED
                    || next == BatchStatus.CANCELLED;

            case COMPLETED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Cannot change batch status from "
                            + current + " to " + next);
        }
    }

    // Map entity to response
    private ClassBatchResponse mapToResponse(ClassBatch batch) {

        ClassBatchResponse response =
                mapper.map(batch, ClassBatchResponse.class);

        response.setCourseId(batch.getCourse().getId());
        response.setCourseName(batch.getCourse().getName());

        if (batch.getTeacher() != null) {
            response.setTeacherId(batch.getTeacher().getId());
            response.setTeacherName(
                    batch.getTeacher().getFullName());
        }

        long enrolledCount = enrollmentDao.countByBatch_IdAndStatus(
                batch.getId(), EnrollmentStatus.APPROVED);
        response.setEnrolledCount(enrolledCount);
        response.setAvailableSeats(Math.max(0L, batch.getCapacity() - enrolledCount));
        response.setAvailable(
                batch.getStatus() == BatchStatus.OPEN
                        && enrolledCount < batch.getCapacity());

        return response;
    }

    private EnrollmentResponse mapEnrollmentToResponse(
            com.sakura.languageschool.model.Enrollment enrollment) {
        EnrollmentResponse response = mapper.map(enrollment, EnrollmentResponse.class);
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(enrollment.getStudent().getFullName());
        response.setBatchId(enrollment.getBatch().getId());
        response.setBatchName(enrollment.getBatch().getBatchName());
        response.setCourseId(enrollment.getBatch().getCourse().getId());
        response.setCourseName(enrollment.getBatch().getCourse().getName());
        return response;
    }
}
