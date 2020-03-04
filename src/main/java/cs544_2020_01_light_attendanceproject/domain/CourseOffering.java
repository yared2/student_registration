package cs544_2020_01_light_attendanceproject.domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(SummaryView.class)
    private Long id;

    @ManyToOne
    @NotNull(message = "please specify a course")
    @JsonIgnoreProperties("courseOfferings")
    @JsonView(SummaryView.class)
    private Course course;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone="CST")
    @NotNull(message = "please specify startDate")
    @JsonView(SummaryView.class)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone="CST")
    @NotNull(message = "please specify endDate")
    @JsonView(SummaryView.class)
    private Date endDate;
    @JsonIgnoreProperties("courseOffering")
    @OneToMany(mappedBy = "courseOffering",cascade = CascadeType.REMOVE)
    @JsonView(DetailView.class)
    private List<Session> sessions;
    @JsonIgnoreProperties("courseOfferings")
    @ManyToMany(mappedBy = "courseOfferings")
    @JsonView(DetailView.class)
    private List<User> students;

    @JsonIgnoreProperties("courseOfferings")
    @ManyToOne
    @NotNull(message = "please specify a location")
    @JsonView(SummaryView.class)
    private Location location;

    public CourseOffering() {}

    public CourseOffering(Long id, Course course, Date startDate, Date endDate, Location location, List<Session> sessions) {
        this.id = id;
        this.course = course;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessions = sessions;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseOffering that = (CourseOffering) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(course, that.course) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, course, startDate, endDate, location);
    }

    @PreRemove
    private void removeAssociationsWithStudents() {
        for (User u : this.getStudents()) {
            u.removeCourseOffering(this);
        }
    }
    public void removeuser(User user) {
        this.students.remove(user);
    }

    public interface SummaryView extends Course.SummaryView, Location.SummaryView {}
    public interface DetailView extends SummaryView, Session.SummaryView, User.SummaryView {}
}
