package shikiri.tool;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tool")
@EqualsAndHashCode(of = "id")
@Builder @Getter @Setter @Accessors(chain = true, fluent = true)
@NoArgsConstructor @AllArgsConstructor
public class ToolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tool")
    private String id;

    @Column(name = "tx_name")
    private String name;

    @Column(name = "tx_category")
    private String category;

    @Column(name = "tx_description")
    private String description;

    @Column(name = "tx_userId")
    private String userId;

    public ToolModel(Tool o) {
        this.id = o.id();
        this.name = o.name();
        this.category = o.category();
        this.description = o.description();
        this.userId = o.userId();
    }
    
    public Tool toDTO() {
        return Tool.builder()
            .id(id)
            .name(name)
            .category(category)
            .description(description)
            .userId(userId)
            .build();
    }
}
