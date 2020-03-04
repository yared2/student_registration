package cs544_2020_01_light_attendanceproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(SummaryView.class)
    private Long id;
    @NotEmpty(message = "Please provide a description.")
    @JsonView(SummaryView.class)
    private String description;

    @OneToMany(mappedBy = "location")
    @JsonView(DetailView.class)
    @JsonIgnoreProperties("location")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "location")
    @JsonView(DetailView.class)
    private List<CourseOffering> courseOfferings;

    public Location() { }

    public Location(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<CourseOffering> getCourseOfferings() {
        return courseOfferings;
    }

    public void setCourseOfferings(List<CourseOffering> courseOfferings) {
        this.courseOfferings = courseOfferings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) &&
                Objects.equals(description, location.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    public interface SummaryView {}
    public interface DetailView extends SummaryView, Attendance.SummaryView, CourseOffering.SummaryView {}
}
