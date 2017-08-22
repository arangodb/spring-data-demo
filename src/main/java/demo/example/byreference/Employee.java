package demo.example.byreference;

import com.arangodb.springframework.annotation.Ref;
import org.springframework.data.annotation.Id;

import java.util.Collection;

public class Employee {

    @Id
    private String id;

    private String name;

    @Ref
    private Employee manager;

    @Ref
    private Collection<Assignment> assignments;

    public Employee(String name, Employee manager) {
        this.name = name;
        this.manager = manager;
    }

    public String getId() { return id; }

    public void setName(String name) { this.name = name; }

    public void setAssignments(Collection<Assignment> assignments) { this.assignments = assignments; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (assignments != null) {
            assignments.forEach(a -> {
                builder.append((builder.length() == 0 ? "" : ", ") + "'" + (a == null ? "null" : a.getDescription()) + "'");
            });
        }
        return String.format("\nEmployee: {id: '%s', name: '%s', manager(id): '%s', assignments: [%s]}",
                id, name, manager == null ? "null" : manager.getId(), builder.toString());
    }
}
