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
    private Collection<Employee> colleagues;

    public Employee(String name, Employee manager) {
        this.name = name;
        this.manager = manager;
    }

    public String getId() { return id; }

    public void setColleagues(Collection<Employee> colleagues) {
        this.colleagues = colleagues;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        colleagues.forEach(e -> builder.append((builder.length() == 0 ? "" : ", ") + "'" + e.getId() + "'"));
        return String.format("Employee: {id: '%s', name: '%s', manager(id): '%s', colleagues(ids): [%s]}",
                id, name, manager == null ? "null" : manager.getId(), builder.toString());
    }
}
